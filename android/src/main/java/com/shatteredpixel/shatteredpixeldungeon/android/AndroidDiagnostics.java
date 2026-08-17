/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Local-only diagnostics for development APKs.
 *
 * Captures:
 * - uncaught Java crashes before Android terminates the process;
 * - Android 11+ historical process exits, including ANR metadata/system traces;
 * - stalls of both the Android UI looper and libGDX render loop.
 *
 * Nothing is uploaded. Reports remain in app-private storage until the player clears them.
 */
final class AndroidDiagnostics {

    private static final String REPORT_FILE = "echoes_pending_diagnostic.txt";
    private static final String PREFS = "EchoesDiagnostics";
    private static final String LAST_SYSTEM_EXIT = "last_system_exit_timestamp";

    private static final long PING_INTERVAL_MS = 2_000L;
    private static final long STALL_TIMEOUT_MS = 8_000L;
    private static final int MAX_REPORT_BYTES = 768 * 1024;
    private static final int MAX_SYSTEM_TRACE_BYTES = 192 * 1024;
    private static final int MAX_STACK_FRAMES_PER_THREAD = 96;
    private static final long SESSION_ID = System.currentTimeMillis();

    private static final Object REPORT_LOCK = new Object();

    private static volatile Context appContext;
    private static volatile boolean crashHandlerInstalled;
    private static volatile boolean watchdogStarted;
    private static volatile boolean foreground;

    private static volatile boolean uiPingOutstanding;
    private static volatile boolean renderPingOutstanding;
    private static volatile long uiPingSentAt;
    private static volatile long renderPingSentAt;
    private static volatile boolean uiStallLatched;
    private static volatile boolean renderStallLatched;
    private static volatile Thread renderThread;

    private AndroidDiagnostics() {
    }

    static void install(Context context) {
        if (context == null) return;
        appContext = context.getApplicationContext();
        installCrashHandler();
        collectHistoricalExit(appContext);
    }

    private static synchronized void installCrashHandler() {
        if (crashHandlerInstalled) return;
        crashHandlerInstalled = true;

        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                appendReport(buildReportSection(
                        "JAVA_CRASH",
                        "An uncaught Java exception terminated the process.",
                        thread,
                        throwable));
            } catch (Throwable ignored) {
                // Never allow diagnostics code to suppress Android's normal crash handling.
            }

            if (previous != null) {
                previous.uncaughtException(thread, throwable);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        });
    }

    static void startWatchdogs() {
        if (watchdogStarted) return;
        synchronized (AndroidDiagnostics.class) {
            if (watchdogStarted) return;
            watchdogStarted = true;
        }

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Thread watchdog = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(PING_INTERVAL_MS);
                } catch (InterruptedException ignored) {
                    // Diagnostics must remain alive for the process lifetime.
                }

                if (!foreground) {
                    resetOutstandingPings();
                    continue;
                }

                final long now = SystemClock.uptimeMillis();
                pingUiThread(mainHandler, now);
                pingRenderThread(now);
            }
        }, "Echoes-Diagnostics-Watchdog");
        watchdog.setDaemon(true);
        watchdog.setPriority(Thread.MIN_PRIORITY);
        watchdog.start();
    }

    static void setForeground(boolean active) {
        foreground = active;
        if (!active) resetOutstandingPings();
    }

    private static void resetOutstandingPings() {
        uiPingOutstanding = false;
        renderPingOutstanding = false;
        uiStallLatched = false;
        renderStallLatched = false;
    }

    private static void pingUiThread(Handler mainHandler, long now) {
        if (!uiPingOutstanding) {
            uiPingOutstanding = true;
            uiPingSentAt = now;
            if (!mainHandler.post(() -> {
                uiPingOutstanding = false;
                uiStallLatched = false;
            })) {
                uiPingOutstanding = false;
            }
        } else if (!uiStallLatched && now - uiPingSentAt >= STALL_TIMEOUT_MS) {
            uiStallLatched = true;
            try {
                appendReport(buildReportSection(
                        "ANDROID_UI_STALL",
                        "Android's main looper did not answer the diagnostics heartbeat for at least "
                                + STALL_TIMEOUT_MS + " ms.",
                        Looper.getMainLooper().getThread(),
                        null));
            } catch (Throwable ignored) {
            }
        }
    }

    private static void pingRenderThread(long now) {
        if (Gdx.app == null) return;

        if (!renderPingOutstanding) {
            renderPingOutstanding = true;
            renderPingSentAt = now;
            try {
                Gdx.app.postRunnable(() -> {
                    renderThread = Thread.currentThread();
                    renderPingOutstanding = false;
                    renderStallLatched = false;
                });
            } catch (Throwable ignored) {
                renderPingOutstanding = false;
            }
        } else if (!renderStallLatched && now - renderPingSentAt >= STALL_TIMEOUT_MS) {
            renderStallLatched = true;
            try {
                appendReport(buildReportSection(
                        "LIBGDX_RENDER_STALL",
                        "The libGDX render loop did not execute a posted heartbeat for at least "
                                + STALL_TIMEOUT_MS + " ms.",
                        renderThread,
                        null));
            } catch (Throwable ignored) {
            }
        }
    }

    static boolean hasPendingReport(Context context) {
        return reportFile(context).isFile() && reportFile(context).length() > 0;
    }

    static String readPendingReport(Context context) {
        File file = reportFile(context);
        if (!file.isFile()) return "";

        try (FileInputStream input = new FileInputStream(file)) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            int remaining = MAX_REPORT_BYTES;
            while (remaining > 0 && (read = input.read(buffer, 0, Math.min(buffer.length, remaining))) >= 0) {
                if (read == 0) continue;
                output.write(buffer, 0, read);
                remaining -= read;
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Unable to read diagnostic report: " + e;
        }
    }

    static void clearPendingReport(Context context) {
        File file = reportFile(context);
        if (file.isFile()) file.delete();
    }

    static void showPendingReportIfAny(Activity activity) {
        if (activity == null || !hasPendingReport(activity)) return;
        Intent intent = new Intent(activity, AndroidCrashReportActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    private static File reportFile(Context context) {
        Context safeContext = context != null ? context.getApplicationContext() : appContext;
        if (safeContext == null) throw new IllegalStateException("Android diagnostics context unavailable");
        return new File(safeContext.getFilesDir(), REPORT_FILE);
    }

    private static void appendReport(String report) {
        Context context = appContext;
        if (context == null || report == null || report.isEmpty()) return;

        synchronized (REPORT_LOCK) {
            File file = reportFile(context);
            boolean append = file.isFile() && file.length() > 0 && file.length() < MAX_REPORT_BYTES;
            try (FileOutputStream output = new FileOutputStream(file, append)) {
                if (append) output.write("\n\n".getBytes(StandardCharsets.UTF_8));
                output.write(report.getBytes(StandardCharsets.UTF_8));
                output.flush();
            } catch (IOException ignored) {
            }
        }
    }

    private static String buildReportSection(String type, String note, Thread culprit, Throwable throwable) {
        StringBuilder out = new StringBuilder(24 * 1024);
        out.append("============================================================\n");
        out.append("ECHOES OF YENDOR ANDROID DIAGNOSTIC\n");
        out.append("============================================================\n");
        out.append("report_type: ").append(type).append('\n');
        out.append("session_id: ").append(SESSION_ID).append('\n');
        out.append("timestamp: ").append(timestamp(System.currentTimeMillis())).append('\n');
        if (note != null) out.append("note: ").append(note).append('\n');
        appendAppAndDeviceInfo(out);
        appendGameContext(out);

        if (throwable != null) {
            out.append("\n--- EXCEPTION ---\n");
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            out.append(writer);
        }

        if (culprit != null) {
            out.append("\n--- SUSPECT THREAD ---\n");
            appendThread(out, culprit, culprit.getStackTrace());
        }

        out.append("\n--- ALL THREADS ---\n");
        appendAllThreads(out);
        return out.toString();
    }

    private static void appendAppAndDeviceInfo(StringBuilder out) {
        Context context = appContext;
        out.append("package: ").append(context != null ? context.getPackageName() : "?").append('\n');
        if (context != null) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                out.append("version_name: ").append(info.versionName).append('\n');
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    out.append("version_code: ").append(info.getLongVersionCode()).append('\n');
                } else {
                    out.append("version_code: ").append(info.versionCode).append('\n');
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        out.append("device: ").append(Build.MANUFACTURER).append(' ').append(Build.MODEL).append('\n');
        out.append("android: ").append(Build.VERSION.RELEASE)
                .append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        out.append("abis: ").append(Arrays.toString(Build.SUPPORTED_ABIS)).append('\n');

        Runtime runtime = Runtime.getRuntime();
        out.append("heap_used_mb: ").append((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)).append('\n');
        out.append("heap_max_mb: ").append(runtime.maxMemory() / (1024 * 1024)).append('\n');
    }

    private static void appendGameContext(StringBuilder out) {
        out.append("\n--- GAME CONTEXT ---\n");
        try {
            out.append("level: ").append(Dungeon.level == null ? "null" : Dungeon.level.getClass().getName()).append('\n');
            if (Dungeon.level != null) {
                out.append("level_size: ").append(Dungeon.level.width()).append('x').append(Dungeon.level.height()).append('\n');
            }
            if (Dungeon.hero != null) {
                out.append("hero: ").append(Dungeon.hero.getClass().getName()).append('\n');
                out.append("hero_pos: ").append(Dungeon.hero.pos).append('\n');
            } else {
                out.append("hero: null\n");
            }
        } catch (Throwable e) {
            out.append("game_context_error: ").append(e).append('\n');
        }
    }

    private static void appendAllThreads(StringBuilder out) {
        try {
            List<Map.Entry<Thread, StackTraceElement[]>> threads = new ArrayList<>(Thread.getAllStackTraces().entrySet());
            threads.sort(Comparator.comparing(entry -> entry.getKey().getName()));
            for (Map.Entry<Thread, StackTraceElement[]> entry : threads) {
                appendThread(out, entry.getKey(), entry.getValue());
            }
        } catch (Throwable e) {
            out.append("thread_dump_error: ").append(e).append('\n');
        }
    }

    private static void appendThread(StringBuilder out, Thread thread, StackTraceElement[] stack) {
        if (thread == null) return;
        out.append('\n').append('"').append(thread.getName()).append('"')
                .append(" id=").append(thread.getId())
                .append(" state=").append(thread.getState())
                .append(" daemon=").append(thread.isDaemon())
                .append('\n');
        if (stack == null) return;
        int count = Math.min(stack.length, MAX_STACK_FRAMES_PER_THREAD);
        for (int i = 0; i < count; i++) {
            out.append("    at ").append(stack[i]).append('\n');
        }
        if (stack.length > count) out.append("    ... ").append(stack.length - count).append(" more\n");
    }

    private static String timestamp(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(new Date(millis));
    }

    private static void collectHistoricalExit(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Api30.collectHistoricalExit(context);
            } catch (Throwable ignored) {
            }
        }
    }

    /** Isolated so Android 5-10 never need to resolve API-30-only classes. */
    private static final class Api30 {

        private static void collectHistoricalExit(Context context) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (manager == null) return;

            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            long lastSeen = prefs.getLong(LAST_SYSTEM_EXIT, 0L);
            List<ApplicationExitInfo> exits = manager.getHistoricalProcessExitReasons(null, 0, 8);
            if (exits == null || exits.isEmpty()) return;

            ApplicationExitInfo newestRelevant = null;
            long newestTimestamp = lastSeen;
            for (ApplicationExitInfo exit : exits) {
                newestTimestamp = Math.max(newestTimestamp, exit.getTimestamp());
                if (exit.getTimestamp() > lastSeen && isRelevant(exit.getReason())) {
                    if (newestRelevant == null || exit.getTimestamp() > newestRelevant.getTimestamp()) {
                        newestRelevant = exit;
                    }
                }
            }
            prefs.edit().putLong(LAST_SYSTEM_EXIT, newestTimestamp).apply();
            if (newestRelevant == null) return;

            StringBuilder out = new StringBuilder(16 * 1024);
            out.append("============================================================\n");
            out.append("ANDROID SYSTEM EXIT RECORD\n");
            out.append("============================================================\n");
            out.append("report_type: SYSTEM_EXIT_").append(reasonName(newestRelevant.getReason())).append('\n');
            out.append("timestamp: ").append(timestamp(newestRelevant.getTimestamp())).append('\n');
            out.append("reason_code: ").append(newestRelevant.getReason()).append('\n');
            out.append("status: ").append(newestRelevant.getStatus()).append('\n');
            out.append("importance: ").append(newestRelevant.getImportance()).append('\n');
            out.append("pss_kb: ").append(newestRelevant.getPss()).append('\n');
            out.append("rss_kb: ").append(newestRelevant.getRss()).append('\n');
            if (newestRelevant.getDescription() != null) {
                out.append("description: ").append(newestRelevant.getDescription()).append('\n');
            }

            if (newestRelevant.getReason() == ApplicationExitInfo.REASON_ANR) {
                try (InputStream trace = newestRelevant.getTraceInputStream()) {
                    if (trace != null) {
                        out.append("\n--- ANDROID ANR TRACE ---\n");
                        out.append(readTextLimited(trace, MAX_SYSTEM_TRACE_BYTES));
                    }
                } catch (IOException e) {
                    out.append("\nanr_trace_error: ").append(e).append('\n');
                }
            } else if (newestRelevant.getReason() == ApplicationExitInfo.REASON_CRASH_NATIVE) {
                out.append("\nnote: Android may expose a protobuf native tombstone here; raw binary is intentionally not copied into the text report.\n");
            }

            appendReport(out.toString());
        }

        private static boolean isRelevant(int reason) {
            return reason == ApplicationExitInfo.REASON_ANR
                    || reason == ApplicationExitInfo.REASON_CRASH
                    || reason == ApplicationExitInfo.REASON_CRASH_NATIVE
                    || reason == ApplicationExitInfo.REASON_LOW_MEMORY
                    || reason == ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE;
        }

        private static String reasonName(int reason) {
            switch (reason) {
                case ApplicationExitInfo.REASON_ANR: return "ANR";
                case ApplicationExitInfo.REASON_CRASH: return "JAVA_CRASH";
                case ApplicationExitInfo.REASON_CRASH_NATIVE: return "NATIVE_CRASH";
                case ApplicationExitInfo.REASON_LOW_MEMORY: return "LOW_MEMORY";
                case ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE: return "EXCESSIVE_RESOURCE_USAGE";
                default: return "REASON_" + reason;
            }
        }

        private static String readTextLimited(InputStream input, int limit) throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int remaining = limit;
            while (remaining > 0) {
                int read = input.read(buffer, 0, Math.min(buffer.length, remaining));
                if (read < 0) break;
                if (read == 0) continue;
                output.write(buffer, 0, read);
                remaining -= read;
            }
            if (remaining == 0) output.write("\n[trace truncated by Echoes diagnostics]\n".getBytes(StandardCharsets.UTF_8));
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
