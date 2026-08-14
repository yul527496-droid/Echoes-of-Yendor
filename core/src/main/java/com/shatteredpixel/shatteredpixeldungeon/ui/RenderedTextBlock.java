package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.FontPreviewMode;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class RenderedTextBlock extends Component {

    private int maxWidth = Integer.MAX_VALUE;
    public int nLines;

    private static final RenderedText SPACE = new RenderedText();
    private static final RenderedText NEWLINE = new RenderedText();

    protected String text;
    protected String[] tokens = null;
    protected ArrayList<RenderedText> words = new ArrayList<>();
    protected boolean multiline = false;

    private int size;
    private float zoom;
    private int color = -1;
    private boolean border = true;
    private int hightlightColor = Window.TITLE_COLOR;
    private boolean highlightingEnabled = true;

    // Historical Shattered Pixel Dungeon layout overlap. Kept as the default
    // so existing UI is unchanged. Ledger UI can explicitly opt into a
    // different tracking value without touching the rest of the game.
    private float tracking = -0.667f;

    // Font choice must survive rebuilds triggered by text(), maxWidth(), and
    // highlighting changes. Without this, ledger text briefly used Fusion Pixel
    // during construction and then silently rebuilt with Droid Sans.
    private boolean ledgerPixelFont;

    public static final int LEFT_ALIGN = 1;
    public static final int CENTER_ALIGN = 2;
    public static final int RIGHT_ALIGN = 3;
    private int alignment = LEFT_ALIGN;

    public RenderedTextBlock(int s) {
        this(s, true);
    }

    public RenderedTextBlock(int s, boolean b) {
        size = s;
        border = b;
    }

    public RenderedTextBlock(String t, int s) {
        this(t, s, true);
    }

    public RenderedTextBlock(String t, int s, boolean b) {
        size = s;
        border = b;
        text(t);
    }

    public void text(String t) {
        text = t;
        if (t != null && !t.equals("")) {
            tokens = Game.platform.splitforTextBlock(t, multiline);
            build();
        }
    }

    public String text() {
        return text;
    }

    public void text(String t, int w) {
        maxWidth = w;
        multiline = true;
        text(t);
    }

    public int maxWidth() {
        return maxWidth;
    }

    public void maxWidth(int w) {
        if (maxWidth != w) {
            maxWidth = w;
            multiline = true;
            text(text);
        }
    }

    public void tokens(String... vals) {
        StringBuilder f = new StringBuilder();
        for (String v : vals) f.append(v);
        text = f.toString();
        tokens = vals;
        build();
    }

    /**
     * Keeps Fusion Pixel selected for this block across every future rebuild.
     * If the optional font was not packaged, DesktopPlatformSupport still
     * falls back safely to the normal game font.
     */
    public synchronized void setLedgerPixelFont(boolean enabled) {
        if (ledgerPixelFont != enabled) {
            ledgerPixelFont = enabled;
            build();
        }
    }

    public synchronized boolean ledgerPixelFont() {
        return ledgerPixelFont;
    }

    private synchronized void build() {
        if (tokens == null) return;

        boolean previousFontMode = FontPreviewMode.ledgerPixelFont;
        if (ledgerPixelFont) FontPreviewMode.ledgerPixelFont = true;

        try {
            clear();
            words = new ArrayList<>();
            boolean hi = false;

            for (String s : tokens) {
                if ((s.equals("_") || s.equals("**")) && highlightingEnabled) {
                    hi = !hi;
                } else if (s.equals("\n")) {
                    words.add(NEWLINE);
                } else if (s.equals(" ")) {
                    words.add(SPACE);
                } else {
                    RenderedText w = new RenderedText(s, size, border);
                    if (hi) w.hardlight(hightlightColor);
                    else if (color != -1) w.hardlight(color);
                    w.scale.set(zoom);
                    words.add(w);
                    add(w);
                    if (height < w.height()) height = w.height();
                }
            }
            layout();
        } finally {
            FontPreviewMode.ledgerPixelFont = previousFontMode;
        }
    }

    public synchronized void zoom(float z) {
        zoom = z;
        for (RenderedText w : words) if (w != null) w.scale.set(z);
        layout();
    }

    /**
     * Extra horizontal advance after each rendered token, in logical UI units.
     * The legacy default is -0.667. Ledger screens can use 0 or a small
     * positive value for readable CJK spacing without changing global UI.
     */
    public synchronized void tracking(float value) {
        tracking = value;
        layout();
    }

    public synchronized float tracking() {
        return tracking;
    }

    public synchronized void hardlight(int c) {
        color = c;
        for (RenderedText w : words) if (w != null) w.hardlight(c);
    }

    public synchronized void resetColor() {
        color = -1;
        for (RenderedText w : words) if (w != null) w.resetColor();
    }

    public synchronized void alpha(float a) {
        for (RenderedText w : words) if (w != null) w.alpha(a);
    }

    public synchronized void setHightlighting(boolean e) {
        setHightlighting(e, Window.TITLE_COLOR);
    }

    public synchronized void setHightlighting(boolean e, int c) {
        if (e != highlightingEnabled || c != hightlightColor) {
            hightlightColor = c;
            highlightingEnabled = e;
            build();
        }
    }

    public synchronized void invert() {
        if (words != null) {
            for (RenderedText w : words) {
                if (w != null) {
                    w.ra = .77f;
                    w.ga = .73f;
                    w.ba = .62f;
                    w.rm = -.77f;
                    w.gm = -.73f;
                    w.bm = -.62f;
                }
            }
        }
    }

    public synchronized void align(int a) {
        alignment = a;
        layout();
    }

    @Override
    protected synchronized void layout() {
        super.layout();

        float x = this.x;
        float y = this.y;
        float h = 0;
        nLines = 1;

        ArrayList<ArrayList<RenderedText>> lines = new ArrayList<>();
        ArrayList<RenderedText> cur = new ArrayList<>();
        lines.add(cur);
        width = 0;

        for (int i = 0; i < words.size(); i++) {
            RenderedText w = words.get(i);

            if (w == SPACE) {
                x += 1.667f;
            } else if (w == NEWLINE) {
                y += h + 2;
                x = this.x;
                nLines++;
                cur = new ArrayList<>();
                lines.add(cur);
            } else {
                if (w.height() > h) h = w.height();

                float full = w.width();
                int j = i + 1;
                while (Messages.lang() != Languages.CHI_SMPL
                        && Messages.lang() != Languages.CHI_TRAD
                        && Messages.lang() != Languages.JAPANESE
                        && j < words.size()
                        && words.get(j) != SPACE
                        && words.get(j) != NEWLINE) {
                    full += words.get(j).width() + tracking;
                    j++;
                }

                if ((x - this.x) + full - .001f > maxWidth && !cur.isEmpty()) {
                    y += h + 2;
                    x = this.x;
                    nLines++;
                    cur = new ArrayList<>();
                    lines.add(cur);
                }

                w.x = x;
                w.y = y;
                PixelScene.align(w);
                x += w.width();
                cur.add(w);
                if (x - this.x > width) width = x - this.x;
                x += tracking;
            }
        }

        height = (y - this.y) + h;

        if (alignment != LEFT_ALIGN) {
            for (ArrayList<RenderedText> line : lines) {
                if (line.isEmpty()) continue;
                float lw = line.get(line.size() - 1).width()
                        + line.get(line.size() - 1).x - this.x;
                for (RenderedText w : line) {
                    if (alignment == CENTER_ALIGN) {
                        w.x += (width() - lw) / 2f;
                    } else if (alignment == RIGHT_ALIGN) {
                        w.x += width() - lw;
                    }
                    PixelScene.align(w);
                }
            }
        }
    }
}
