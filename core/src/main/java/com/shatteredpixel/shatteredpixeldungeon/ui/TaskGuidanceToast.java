/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegionAreaLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

/**
 * Compact persistent objective HUD for the surface chapter.
 *
 * This deliberately is not a Toast: objective information is a permanent HUD layer,
 * not a transient system notification. The existing class name is retained so story
 * code can continue calling showObjective()/dismissObjective() without migration noise.
 */
public class TaskGuidanceToast extends Component {

    // Keep the custom surface HUD below the desktop debug/performance strip and debug buttons.
    private static final float SAFE_TOP = 25f;
    private static final float STACK_GAP = 4f;

    private static final int ACCENT = 0xA88B5C;
    private static final int REGION = 0x9D8D70;
    private static final int OBJECTIVE = 0xF2E7D2;
    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";

    private static TaskGuidanceToast instance;
    private static String shownSignature;

    private final NinePatch bg;
    private final Image accent;
    private final RenderedTextBlock region;
    private final RenderedTextBlock objective;

    private TaskGuidanceToast(String text) {
        super();

        // Keep the objective card visually tied to the 74px-wide minimap instead of spanning the screen.
        // Longer objectives wrap naturally to a second line, preserving a compact right-side HUD stack.
        width = Math.min(PixelScene.landscape() ? 100 : 92,
                Math.max(80, PixelScene.uiCamera.width - 20));

        // Use the same native translucent chrome as the minimap Toast so both read as one HUD family.
        bg = Chrome.get(Chrome.Type.TOAST_TR);
        add(bg);

        accent = new Image(PIXEL);
        accent.hardlight(ACCENT);
        add(accent);

        region = PixelScene.renderTextBlock(regionName(), 5);
        region.hardlight(REGION);
        region.maxWidth((int)width - 10);
        add(region);

        objective = PixelScene.renderTextBlock(cleanObjective(text), 6);
        objective.hardlight(OBJECTIVE);
        objective.maxWidth((int)width - 10);
        add(objective);

        height = Math.max(24, objective.height() + 15);
    }

    public static void showObjective(String text) {
        if (!(Game.scene() instanceof GameScene)) return;

        String display = text == null ? "" : text.trim();
        String signature = regionName() + '|' + display;
        if (signature.equals(shownSignature) && instance != null
                && instance.exists && instance.parent == Game.scene()) {
            SurfaceMiniMapToast.sync();
            return;
        }

        shownSignature = signature;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
        if (display.isEmpty()) {
            SurfaceMiniMapToast.sync();
            return;
        }

        instance = new TaskGuidanceToast(display);
        instance.camera = PixelScene.uiCamera;
        // Share the minimap's right-hand anchor, but stay below the desktop debug/performance strip.
        instance.setPos(Math.max(2, PixelScene.uiCamera.width - instance.width() - 2), SAFE_TOP);
        PixelScene.align(instance);
        Game.scene().addToFront(instance);

        // Build/reposition the minimap only after the objective bar has a real measured bottom edge.
        SurfaceMiniMapToast.sync();
    }

    /** Returns the first safe y-coordinate for the minimap below the objective bar. */
    static float miniMapTop() {
        if (instance != null && instance.exists) {
            return instance.y + instance.height() + STACK_GAP;
        }
        return 38f;
    }

    public static void dismissObjective() {
        shownSignature = null;
        SurfaceMiniMapToast.dismissMiniMap();
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
    }

    private static String regionName() {
        if (Dungeon.level instanceof RegionAreaLevel) {
            return ((RegionAreaLevel) Dungeon.level).regionAreaName();
        }
        if (Dungeon.level instanceof SurfaceEntranceLevel) return "地表入口 · 林缘";
        if (Dungeon.level instanceof OldKingsRoadLevel) return "旧王道";
        if (Dungeon.level instanceof MorningcreekOutskirtsLevel) return "晨溪郊外";
        if (Dungeon.level instanceof MorningcreekMainStreetLevel) return "晨溪 · 主街";
        if (Dungeon.level instanceof OldCrowInnLevel) return "老鸦旅店";
        return "当前目标";
    }

    private static String cleanObjective(String text) {
        if (text == null) return "";
        String result = text.trim();
        if (result.startsWith("当前任务：")) result = result.substring("当前任务：".length());
        result = result.replace("  ◇ ", " · ").replace("◇ ", "");
        return result;
    }

    @Override
    protected void layout() {
        bg.x = x;
        bg.y = y;
        bg.size(width, height);

        accent.x = x + 3;
        accent.y = y + 2;
        accent.scale.set(Math.max(1, width - 6), 1);

        region.setPos(x + 5, y + 4);
        objective.setPos(x + 5, y + 11);
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
