package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.FontPreviewMode;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/**
 * V2 step 2B: one-screen font-face A/B test.
 *
 * Geometry, tracking and logical sizes are intentionally identical on both
 * pages. The left page is the current desktop CJK face (Droid Sans); the right
 * page is Fusion Pixel 12px Proportional zh_hans when the build injected it.
 */
public class LedgerFontBaselineScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        LedgerPageGrid.Page right = LedgerEnvironment.rightGrid(book);

        FontPreviewMode.ledgerPixelFont = false;
        buildColumn(left, "当前字体", "Droid Sans");

        FontPreviewMode.ledgerPixelFont = true;
        try {
            buildColumn(right, "像素字体", "Fusion Pixel 12 Proportional");
        } finally {
            // Never let the experiment leak into any other scene or window.
            FontPreviewMode.ledgerPixelFont = false;
        }

        fadeIn();
    }

    private void buildColumn(LedgerPageGrid.Page page, String heading, String faceName) {
        float x = page.content.left + 3f;
        float w = page.content.width() - 6f;
        float y = page.content.top + 2f;

        RenderedTextBlock label = t(heading, 9, LedgerEnvironment.INK, (int) w);
        center(label, page.content, y);
        add(label);

        RenderedTextBlock face = t(faceName, 5, LedgerEnvironment.FADED_INK, (int) w);
        center(face, page.content, label.bottom() + 3f);
        add(face);

        y = face.bottom() + 11f;

        RenderedTextBlock title = t("遗迹下行者登记簿", 11,
                LedgerEnvironment.INK, (int) w);
        title.setPos(x, y);
        add(title);

        RenderedTextBlock section = t("现存记录", 9,
                LedgerEnvironment.INK, (int) w);
        section.setPos(x, title.bottom() + 9f);
        add(section);

        RenderedTextBlock fields = t("姓名　身份　去向", 7,
                LedgerEnvironment.INK, (int) w);
        fields.setPos(x, section.bottom() + 9f);
        add(fields);

        RenderedTextBlock mixed = t("女猎手 Lv.30", 7,
                LedgerEnvironment.INK, (int) w);
        mixed.setPos(x, fields.bottom() + 9f);
        add(mixed);

        RenderedTextBlock place = t("晨溪镇，老鸦旅店", 6,
                LedgerEnvironment.FADED_INK, (int) w);
        place.setPos(x, mixed.bottom() + 8f);
        add(place);

        RenderedTextBlock action = t("登记新的下行者", 7,
                LedgerEnvironment.STAMP, (int) w);
        action.setPos(x, place.bottom() + 10f);
        add(action);

        RenderedTextBlock note = t("同坐标　同字号　零字距", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.setPos(x, page.content.bottom - note.height() - 3f);
        add(note);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width, 0f);
    }

    private void center(RenderedTextBlock block, RectF area, float y) {
        block.setPos(area.left + (area.width() - block.width()) / 2f, y);
    }

    @Override
    public void destroy() {
        FontPreviewMode.ledgerPixelFont = false;
        super.destroy();
    }
}
