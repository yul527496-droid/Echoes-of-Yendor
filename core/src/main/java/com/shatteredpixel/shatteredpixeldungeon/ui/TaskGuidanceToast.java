/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRegionMap;
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

    private static final int BG = 0x221E19;
    private static final int ACCENT = 0xA88B5C;
    private static final int REGION = 0xBCA77D;
    private static final int OBJECTIVE = 0xE6DDC8;
    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";

    private static TaskGuidanceToast instance;
    private static String shownSignature;

    private final NinePatch bg;
    private final Image accent;
    private final RenderedTextBlock region;
    private final RenderedTextBlock objective;
    private final IconButton mapButton;

    private TaskGuidanceToast(String text) {
        super();

        width = Math.min(PixelScene.landscape() ? 180 : 116,
                Math.max(88, PixelScene.uiCamera.width - 20));

        bg = Chrome.get(Chrome.Type.BLANK);
        bg.hardlight(BG);
        bg.alpha(0.86f);
        add(bg);

        accent = new Image(PIXEL);
        accent.hardlight(ACCENT);
        add(accent);

        region = PixelScene.renderTextBlock(regionName(), 5);
        region.hardlight(REGION);
        region.maxWidth((int)width - 30);
        add(region);

        objective = PixelScene.renderTextBlock(cleanObjective(text), 6);
        objective.hardlight(OBJECTIVE);
        objective.maxWidth((int)width - 30);
        add(objective);

        height = Math.max(24, objective.height() + 15);

        mapButton = new IconButton(Icons.get(Icons.MAGNIFY)) {
            @Override protected void onClick() {
                GameScene.show(new WndRegionMap());
            }

            @Override protected String hoverText() {
                return "区域地图";
            }
        };
        add(mapButton);
    }

    public static void showObjective(String text) {
        if (!(Game.scene() instanceof GameScene)) return;
        SurfaceMiniMapToast.sync();

        String display = text == null ? "" : text.trim();
        String signature = regionName() + '|' + display;
        if (signature.equals(shownSignature) && instance != null
                && instance.exists && instance.parent == Game.scene()) return;

        shownSignature = signature;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
        if (display.isEmpty()) return;

        instance = new TaskGuidanceToast(display);
        instance.camera = PixelScene.uiCamera;
        instance.setPos((PixelScene.uiCamera.width - instance.width()) / 2f, 5);
        PixelScene.align(instance);
        Game.scene().addToFront(instance);
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
        mapButton.setRect(x + width - 21, y + 4, 17, Math.max(16, height - 8));
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
