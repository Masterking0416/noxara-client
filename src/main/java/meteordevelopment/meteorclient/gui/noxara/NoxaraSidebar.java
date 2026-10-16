/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.utils.render.DisplayItemUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;

/**
 * Fixed-frame sidebar. The brand and footer stay pinned; only navigation scrolls.
 * Keeping the rounded panel outside WView also prevents the view's scissor rectangle
 * from cutting off the sidebar's rounded corners and shadow.
 */
public final class NoxaraSidebar extends WContainer {
    private final NoxaraDashboard dashboard;

    private BrandHeader brandHeader;
    private NavigationList navigationList;
    private SidebarFooter footer;

    public NoxaraSidebar(NoxaraDashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public void init() {
        brandHeader = add(new BrandHeader()).widget();
        navigationList = add(new NavigationList(dashboard)).widget();
        footer = add(new SidebarFooter()).widget();
    }

    @Override
    protected void onCalculateSize() {
        width = theme.scale(174);
        height = dashboard.contentHeight();
    }

    @Override
    protected void onCalculateWidgetPositions() {
        double inset = theme.scale(7);
        double headerTop = theme.scale(8);

        brandHeader.x = x + inset;
        brandHeader.y = y + headerTop;
        brandHeader.width = Math.max(theme.scale(96), width - inset * 2);
        brandHeader.height = theme.scale(46);

        navigationList.x = x + theme.scale(3);
        navigationList.y = brandHeader.y + brandHeader.height + theme.scale(7);
        navigationList.width = Math.max(theme.scale(90), width - theme.scale(6));
        // NavigationList calculates its own height from its content and maxHeight.
        // Leave that height untouched so WView's scroll range stays internally consistent.

        footer.width = Math.max(theme.scale(90), width - theme.scale(24));
        footer.height = theme.scale(28);
        footer.x = x + theme.scale(12);
        footer.y = y + height - footer.height - theme.scale(7);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        NoxaraStyle.panel(renderer, x, y, width, height, theme.scale(11), NoxaraStyle.PANEL, true);
    }

    private static final class NavigationList extends WView {
        private final NoxaraDashboard dashboard;

        private NavigationList(NoxaraDashboard dashboard) {
            this.dashboard = dashboard;
            spacing = 4;
            hasScrollBar = false;
            scrollOnlyWhenMouseOver = true;
        }

        @Override
        public void init() {
            super.init();

            add(new SectionLabel("WORKSPACE")).padHorizontal(9).padTop(2).padBottom(2);
            add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_ALL, "All modules", DisplayItemUtils.toStack(Items.COMPASS)))
                .expandX().padHorizontal(3).padVertical(1);
            add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_ACTIVE, "Active", DisplayItemUtils.toStack(Items.REDSTONE)))
                .expandX().padHorizontal(3).padVertical(1);
            add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_FAVORITES, "Favorites", DisplayItemUtils.toStack(Items.NETHER_STAR)))
                .expandX().padHorizontal(3).padVertical(1);
            add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_EXPLOITS, "Exploits", DisplayItemUtils.toStack(Items.TNT)))
                .expandX().padHorizontal(3).padVertical(1);

            add(new SectionLabel("CATEGORIES")).padHorizontal(9).padTop(7).padBottom(2);
            addCategory(Categories.Combat);
            addCategory(Categories.Movement);
            addCategory(Categories.Player);
            addCategory(Categories.Render);
            addCategory(Categories.World);
            addCategory(Categories.Misc);
        }

        @Override
        protected void onCalculateSize() {
            // Reserve space for the pinned brand and pinned build-status footer.
            maxHeight = Math.max(theme.scale(42), dashboard.contentHeight() - theme.scale(104));
            super.onCalculateSize();
        }

        private void addCategory(Category category) {
            add(new NavigationButton(dashboard, category.name, category.name, category.icon.get()))
                .expandX().padHorizontal(3).padVertical(1);
        }
    }

    private static final class BrandHeader extends WWidget {
        private static final Color ICON_BG = new Color(124, 58, 237, 255);
        private String title = "Noxara";

        @Override
        protected void onCalculateSize() {
            width = theme.scale(152);
            height = theme.scale(46);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            title = NoxaraStyle.clipText("Noxara", width - theme.scale(49), theme);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            double s = theme.scale(31);
            double iconY = y + (height - s) / 2;
            NoxaraStyle.roundedRect(renderer, x, iconY, s, s, theme.scale(9), ICON_BG);
            renderer.quad(x + s * 0.12, iconY + s * 0.12, s * 0.76, s * 0.76, GuiRenderer.CIRCLE, NoxaraStyle.LAVENDER);
            renderer.text("N", x + s / 2 - theme.textWidth("N") / 2, iconY + theme.scale(8), NoxaraStyle.TEXT, true);
            double textX = x + s + theme.scale(8);
            renderer.text(title, textX, y + theme.scale(5), NoxaraStyle.TEXT, true);
            renderer.text("CLIENT", textX + theme.scale(1), y + theme.scale(26), NoxaraStyle.MUTED, false);
        }
    }

    private static final class SidebarFooter extends WWidget {
        @Override
        protected void onCalculateSize() {
            width = theme.scale(135);
            height = theme.scale(28);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(7), NoxaraStyle.CARD);
            double dot = theme.scale(5);
            renderer.quad(x + theme.scale(8), y + (height - dot) / 2, dot, dot, GuiRenderer.CIRCLE, NoxaraStyle.GREEN);
            renderer.text("26.3 TEST BUILD", x + theme.scale(19),
                y + (height - theme.textHeight()) / 2, NoxaraStyle.SUBTLE, false);
        }
    }

    private static final class SectionLabel extends WWidget {
        private final String title;

        private SectionLabel(String title) {
            this.title = title;
        }

        @Override
        protected void onCalculateSize() {
            width = theme.textWidth(title);
            height = theme.textHeight() + theme.scale(2);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.text(title, x, y, NoxaraStyle.MUTED, false);
        }
    }

    private static final class NavigationButton extends WWidget {
        private final NoxaraDashboard dashboard;
        private final String page;
        private final String label;
        private final ItemStack icon;

        private double hoverProgress;
        private final Color fill = new Color(0, 0, 0, 0);
        private String displayLabel = "";

        private NavigationButton(NoxaraDashboard dashboard, String page, String label, ItemStack icon) {
            this.dashboard = dashboard;
            this.page = page;
            this.label = label;
            this.icon = icon;
            tooltip = label;
        }

        @Override
        protected void onCalculateSize() {
            width = theme.scale(150);
            height = theme.scale(29);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            // Keep labels inside the sidebar at every theme scale and window width.
            displayLabel = NoxaraStyle.clipText(label, width - theme.scale(43), theme);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean selected = dashboard.page().equals(page);
            double target = selected ? 1 : mouseOver ? 0.48 : 0;
            hoverProgress += (target - hoverProgress) * Math.min(1, delta * 18);
            hoverProgress = Math.max(0, Math.min(1, hoverProgress));

            if (hoverProgress > 0.005) {
                NoxaraStyle.mix(fill, NoxaraStyle.CARD_HOVER, NoxaraStyle.NAV_SELECTED, hoverProgress);
                NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(7), fill);
            }

            if (selected) {
                NoxaraStyle.roundedRect(renderer, x, y + theme.scale(6), theme.scale(3),
                    height - theme.scale(12), theme.scale(2), NoxaraStyle.LAVENDER);
            }

            if (icon != null && !icon.isEmpty()) {
                renderer.item(icon, (int) (x + theme.scale(7)),
                    (int) (y + (height - theme.scale(16)) / 2), 1, false);
            }

            Color labelColor = selected ? NoxaraStyle.TEXT : (mouseOver ? NoxaraStyle.SUBTLE : NoxaraStyle.MUTED);
            renderer.text(displayLabel, x + theme.scale(30),
                y + (height - theme.textHeight()) / 2, labelColor, false);
        }

        @Override
        public boolean onMouseClicked(MouseButtonEvent click, boolean doubled) {
            if (mouseOver && click.button() == MOUSE_BUTTON_LEFT) {
                dashboard.selectPage(page);
                return true;
            }
            return false;
        }
    }
}
