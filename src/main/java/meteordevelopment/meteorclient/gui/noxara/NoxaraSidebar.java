/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.utils.render.DisplayItemUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;

public final class NoxaraSidebar extends NoxaraPanel {
    private final NoxaraDashboard dashboard;

    public NoxaraSidebar(NoxaraDashboard dashboard) {
        super(NoxaraStyle.PANEL, 12, true);
        this.dashboard = dashboard;
        spacing = 4;
    }

    @Override
    public void init() {
        add(new BrandHeader()).expandX().pad(12);
        add(new SectionLabel("WORKSPACE")).padHorizontal(14).padTop(2).padBottom(3);

        add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_ALL, "All modules", DisplayItemUtils.toStack(Items.COMPASS)))
            .expandX().padHorizontal(8).padVertical(1);
        add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_ACTIVE, "Active", DisplayItemUtils.toStack(Items.REDSTONE)))
            .expandX().padHorizontal(8).padVertical(1);
        add(new NavigationButton(dashboard, NoxaraDashboard.PAGE_FAVORITES, "Favorites", DisplayItemUtils.toStack(Items.NETHER_STAR)))
            .expandX().padHorizontal(8).padVertical(1);

        add(new SectionLabel("CATEGORIES")).padHorizontal(14).padTop(11).padBottom(3);

        // Noxara's primary browsing order is deliberately stable and predictable.
        addCategory(Categories.Combat);
        addCategory(Categories.Render);
        addCategory(Categories.Movement);
        addCategory(Categories.Player);
        addCategory(Categories.World);
        addCategory(Categories.Misc);

        add(theme.label("NOXARA CLIENT").color(NoxaraStyle.SUBTLE)).padHorizontal(14).padTop(12);
        add(theme.label("26.3 • TEST BUILD").color(NoxaraStyle.MUTED)).padHorizontal(14).padBottom(12);
    }

    private void addCategory(Category category) {
        add(new NavigationButton(dashboard, category.name, category.name, category.icon.get()))
            .expandX().padHorizontal(8).padVertical(1);
    }

    private static final class BrandHeader extends WWidget {
        private static final Color ICON_BG = new Color(124, 58, 237, 255);

        @Override
        protected void onCalculateSize() {
            width = theme.scale(152);
            height = theme.scale(56);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            double s = theme.scale(36);
            double iconY = y + (height - s) / 2;
            NoxaraStyle.roundedRect(renderer, x, iconY, s, s, theme.scale(10), ICON_BG);
            renderer.quad(x + s * 0.12, iconY + s * 0.12, s * 0.76, s * 0.76, GuiRenderer.CIRCLE, NoxaraStyle.LAVENDER);
            renderer.text("N", x + s / 2 - theme.textWidth("N") / 2, iconY + theme.scale(10), NoxaraStyle.TEXT, true);
            renderer.text("Noxara", x + s + theme.scale(9), y + theme.scale(8), NoxaraStyle.TEXT, true);
            renderer.text("CLIENT", x + s + theme.scale(10), y + theme.scale(31), NoxaraStyle.MUTED, false);
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
            height = theme.scale(34);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean selected = dashboard.page().equals(page);
            double target = selected ? 1 : mouseOver ? 0.48 : 0;
            hoverProgress += (target - hoverProgress) * Math.min(1, delta * 18);

            if (hoverProgress > 0.005) {
                NoxaraStyle.mix(fill, NoxaraStyle.CARD_HOVER, NoxaraStyle.NAV_SELECTED, hoverProgress);
                NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(8), fill);
            }

            if (selected) {
                NoxaraStyle.roundedRect(renderer, x, y + theme.scale(7), theme.scale(3), height - theme.scale(14), theme.scale(2), NoxaraStyle.LAVENDER);
            }

            if (icon != null && !icon.isEmpty()) {
                renderer.item(icon, (int) (x + theme.scale(10)), (int) (y + (height - theme.scale(16)) / 2), 1, false);
            }

            Color labelColor = selected ? NoxaraStyle.TEXT : (mouseOver ? NoxaraStyle.SUBTLE : NoxaraStyle.MUTED);
            renderer.text(label, x + theme.scale(36), y + (height - theme.textHeight()) / 2, labelColor, false);
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
