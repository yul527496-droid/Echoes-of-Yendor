package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/**
 * Temporary V2 typography acceptance page.
 *
 * Step 2A only compares CJK tracking and visual size hierarchy. Production
 * copy, page-turn animation, candle placement, and final font-face selection
 * are deliberately out of scope here.
 */
public class LedgerTypographyBaselineScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        LedgerPageGrid.Page right = LedgerEnvironment.rightGrid(book);

        buildTrackingPage(left);
        buildSizePage(right);
        fadeIn();
    }

    private void buildTrackingPage(LedgerPageGrid.Page page) {
        outline(page.content, 0xFF4E78A8, 0.25f);

        RenderedTextBlock title = text("字距对照", 10, LedgerEnvironment.INK,
                (int) page.content.width(), 0f);
        center(title, page.content, page.content.top + 2f);
        add(title);

        RenderedTextBlock note = text("同一行文字，只改变字距", 6,
                LedgerEnvironment.FADED_INK, (int) page.content.width(), 0f);
        center(note, page.content, title.bottom() + 3f);
        add(note);

        float y = note.bottom() + 8f;
        y = trackingSample(page, y, "旧字距", -0.667f);
        y = trackingSample(page, y + 5f, "零字距", 0f);
        trackingSample(page, y + 5f, "舒展字距", 0.25f);
    }

    private float trackingSample(LedgerPageGrid.Page page, float y,
                                 String label, float tracking) {
        RenderedTextBlock tag = text(label, 6, LedgerEnvironment.FADED_INK,
                (int) page.content.width(), 0f);
        tag.setPos(page.content.left + 2f, y);
        add(tag);

        RenderedTextBlock sample = text("遗迹下行者登记簿", 8,
                LedgerEnvironment.INK, (int) page.content.width(), tracking);
        sample.setPos(page.content.left + 2f, tag.bottom() + 2f);
        add(sample);

        RenderedTextBlock fields = text("姓名　身份　惯用兵器　去向", 7,
                LedgerEnvironment.INK, (int) page.content.width(), tracking);
        fields.setPos(page.content.left + 2f, sample.bottom() + 2f);
        add(fields);

        return fields.bottom();
    }

    private void buildSizePage(LedgerPageGrid.Page page) {
        outline(page.content, 0xFF4E78A8, 0.25f);

        RenderedTextBlock title = text("字号层级", 10, LedgerEnvironment.INK,
                (int) page.content.width(), 0f);
        center(title, page.content, page.content.top + 2f);
        add(title);

        float x = page.content.left + 3f;
        float w = page.content.width() - 6f;
        float y = title.bottom() + 9f;

        RenderedTextBlock hero = text("遗迹下行者登记簿", 11,
                LedgerEnvironment.INK, (int) w, 0f);
        hero.setPos(x, y);
        add(hero);

        RenderedTextBlock section = text("现存记录", 9,
                LedgerEnvironment.INK, (int) w, 0f);
        section.setPos(x, hero.bottom() + 8f);
        add(section);

        RenderedTextBlock body = text("姓名　身份　去向", 7,
                LedgerEnvironment.INK, (int) w, 0f);
        body.setPos(x, section.bottom() + 8f);
        add(body);

        RenderedTextBlock meta = text("晨溪镇，老鸦旅店", 6,
                LedgerEnvironment.FADED_INK, (int) w, 0f);
        meta.setPos(x, body.bottom() + 7f);
        add(meta);

        RenderedTextBlock mixed = text("女猎手 Lv.30", 7,
                LedgerEnvironment.INK, (int) w, 0f);
        mixed.setPos(x, meta.bottom() + 9f);
        add(mixed);

        RenderedTextBlock safe = text("返回　继续　移除", 7,
                LedgerEnvironment.STAMP, (int) w, 0f);
        safe.setPos(x, mixed.bottom() + 8f);
        add(safe);

        RenderedTextBlock foot = text("本页不使用特殊符号", 6,
                LedgerEnvironment.FADED_INK, (int) w, 0f);
        foot.setPos(x, page.content.bottom - foot.height() - 3f);
        add(foot);
    }

    private RenderedTextBlock text(String value, int size, int color,
                                   int width, float tracking) {
        return LedgerUI.text(value, size, color, width, tracking);
    }

    private void center(RenderedTextBlock block, RectF area, float y) {
        block.setPos(area.left + (area.width() - block.width()) / 2f, y);
    }

    private void outline(RectF r, int color, float alpha) {
        ColorBlock top = new ColorBlock(r.width(), 1f, color);
        top.x = r.left;
        top.y = r.top;
        top.alpha(alpha);
        add(top);

        ColorBlock bottom = new ColorBlock(r.width(), 1f, color);
        bottom.x = r.left;
        bottom.y = r.bottom - 1f;
        bottom.alpha(alpha);
        add(bottom);

        ColorBlock left = new ColorBlock(1f, r.height(), color);
        left.x = r.left;
        left.y = r.top;
        left.alpha(alpha);
        add(left);

        ColorBlock right = new ColorBlock(1f, r.height(), color);
        right.x = r.right - 1f;
        right.y = r.top;
        right.alpha(alpha);
        add(right);
    }
}
