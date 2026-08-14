package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroTalentRules;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.Tweener;

public class LedgerReturnScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;
    private Image book;
    private boolean closing;

    @Override
    public void create() {
        super.create();
        book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);
        buildReturnedHero(left);
        buildConfiguration();
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildReturnedHero(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();
        RenderedTextBlock title = t("归来补录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t("晨溪镇 · 老鸦旅店", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.12f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.76f, 0.34f));

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(2.15f);
        hero.x = page.body.left + (page.body.width() - hero.width()) / 2f;
        hero.y = page.body.top + 7f;
        add(hero);

        RenderedTextBlock who = t(LedgerFlow.draft().name + "\n"
                        + Messages.titleCase(LedgerFlow.draft().heroClass.title()),
                7, LedgerEnvironment.INK, (int) page.body.width());
        who.align(RenderedTextBlock.CENTER_ALIGN);
        who.setPos(page.body.left + (page.body.width() - who.width()) / 2f,
                hero.y + hero.height() + 5f);
        add(who);

        LedgerStamp returned = new LedgerStamp("已归", 7);
        returned.setRect(page.body.left + (page.body.width() - 43f) / 2f,
                Math.min(page.body.bottom - 35f, who.bottom() + 8f), 43f, 21f);
        returned.alpha(0.86f);
        add(returned);

        RenderedTextBlock lead = t("旧页留存，此页补记。", 5,
                LedgerEnvironment.FADED_INK, (int) page.body.width() - 8);
        lead.align(RenderedTextBlock.CENTER_ALIGN);
        lead.setPos(page.body.left + (page.body.width() - lead.width()) / 2f,
                page.body.bottom - lead.height() - 3f);
        add(lead);
    }

    private void buildConfiguration() {
        float x = right.header.left;
        float w = right.header.width();
        RenderedTextBlock title = t("归来时状况", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);

        RenderedTextBlock note = t("已核对的归来记录", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.08f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f), w * 0.84f, 0.36f));

        float bx = right.body.left + 6f;
        float bw = right.body.width() - 12f;
        float y = right.body.top + 8f;

        y = field(bx, bw, y, "所习专精",
                Messages.titleCase(LedgerFlow.draft().subClass().title()));
        y = field(bx, bw, y, "英雄战技", LedgerFlow.draft().armorAbility().name());
        y = field(bx, bw, y, "自由天赋",
                ReturningHeroTalentRules.isComplete(
                        LedgerFlow.draft().talentPlan,
                        LedgerFlow.draft().heroClass,
                        LedgerFlow.draft().subClass(),
                        LedgerFlow.draft().armorAbility())
                        ? "四阶记录完整" : "记录仍不完整");
        field(bx, bw, y, "旧行装", "核对完成");

        RenderedTextBlock locked = t("此页只作归还确认；专精与战技不再在盖印后改写。",
                5, LedgerEnvironment.FADED_INK, (int) bw);
        locked.align(RenderedTextBlock.CENTER_ALIGN);
        locked.setPos(bx + (bw - locked.width()) / 2f,
                right.body.bottom - locked.height() - 5f);
        add(locked);

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.30f));
        LedgerButton start = new LedgerButton(Chrome.Type.BLANK, "合上名册", 6) {
            @Override protected void onClick() {
                super.onClick();
                closeLedger();
            }
        };
        start.textColor(LedgerEnvironment.INK);
        start.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(start);
    }

    private float field(float x, float width, float y, String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 42);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText, 6, LedgerEnvironment.INK, (int) width - 47);
        value.setPos(x + 47f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 10f;
    }

    private void closeLedger() {
        if (closing) return;
        closing = true;

        LedgerAudio.bookClose();
        LedgerAudio.fadeOut(0.92f);

        final ColorBlock leftFold = new ColorBlock(1f, left.paper.height(), 0xFF704724);
        final ColorBlock rightFold = new ColorBlock(1f, right.paper.height(), 0xFF704724);
        final ColorBlock leftEdge = new ColorBlock(1.2f, left.paper.height(), 0xFFE5B96F);
        final ColorBlock rightEdge = new ColorBlock(1.2f, right.paper.height(), 0xFFE5B96F);
        leftFold.y = leftEdge.y = left.paper.top;
        rightFold.y = rightEdge.y = right.paper.top;
        leftFold.alpha(0f);
        rightFold.alpha(0f);
        leftEdge.alpha(0f);
        rightEdge.alpha(0f);
        add(leftFold);
        add(rightFold);
        add(leftEdge);
        add(rightEdge);

        final ColorBlock veil = new ColorBlock(Camera.main.width, Camera.main.height, 0xFF120B07);
        veil.x = 0f;
        veil.y = 0f;
        veil.alpha(0f);
        add(veil);

        final Image closed = LedgerClosedArtwork.image();
        final float closedScale = Math.min(
                (Camera.main.width - 8f) / closed.width,
                (Camera.main.height - 8f) / closed.height);
        closed.scale.set(closedScale * 1.04f);
        closed.x = (Camera.main.width - closed.width()) / 2f;
        closed.y = (Camera.main.height - closed.height()) / 2f;
        closed.alpha(0f);
        add(closed);

        add(new Tweener(this, 1.02f) {
            @Override protected void updateValues(float progress) {
                float p = progress * progress * (3f - 2f * progress);
                float fold = Math.min(1f, p / 0.68f);
                float leftW = left.paper.width() * fold;
                float rightW = right.paper.width() * fold;

                leftFold.x = left.paper.left;
                leftFold.size(Math.max(0.01f, leftW), left.paper.height());
                leftFold.alpha(0.18f + 0.68f * fold);
                leftEdge.x = left.paper.left + leftW - leftEdge.width;
                leftEdge.alpha((float) Math.sin(Math.PI * Math.min(1f, fold)) * 0.55f);

                rightFold.x = right.paper.right - rightW;
                rightFold.size(Math.max(0.01f, rightW), right.paper.height());
                rightFold.alpha(0.18f + 0.68f * fold);
                rightEdge.x = right.paper.right - rightW;
                rightEdge.alpha((float) Math.sin(Math.PI * Math.min(1f, fold)) * 0.55f);

                veil.alpha(Math.max(0f, (p - 0.25f) / 0.75f) * 0.54f);

                float reveal = Math.max(0f, Math.min(1f, (p - 0.42f) / 0.58f));
                closed.alpha(reveal);
                float s = closedScale * (1.04f - reveal * 0.04f);
                closed.scale.set(s);
                closed.x = (Camera.main.width - closed.width()) / 2f;
                closed.y = (Camera.main.height - closed.height()) / 2f;
            }

            @Override protected void onComplete() {
                LedgerAudio.leave();
                SequelGame.start(LedgerFlow.draft());
            }
        });
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
