/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Floating title bar with real Meteor tab shortcuts and recognizable built-in item icons. */
public final class NoxaraWindowBar extends WWidget {
    private static final ItemStack MODULES_ICON = new ItemStack(Items.COMPASS);
    private static final ItemStack CONFIG_ICON = new ItemStack(Items.CHEST);
    private static final ItemStack GUI_ICON = new ItemStack(Items.PAINTING);
    private static final ItemStack HUD_ICON = new ItemStack(Items.SPYGLASS);
    private static final ItemStack FRIENDS_ICON = new ItemStack(Items.PLAYER_HEAD);
    private static final ItemStack MACROS_ICON = new ItemStack(Items.REDSTONE_TORCH);
    private static final ItemStack PROFILES_ICON = new ItemStack(Items.BOOK);
    private static final ItemStack PATHS_ICON = new ItemStack(Items.LEAD);

    private final NoxaraDashboard dashboard;
    private final Color navigationFill = new Color(0, 0, 0, 0);

    public NoxaraWindowBar(NoxaraDashboard dashboard) {
        this.dashboard = dashboard;
        tooltip = "Drag to move • Resize from the bottom-right corner • ESC closes";
    }

    @Override
    protected void onCalculateSize() {
        width = theme.scale(500);
        height = theme.scale(36);
    }

    /** Called by the dashboard before title-bar dragging so a tab click is not treated as a drag. */
    public boolean handleTabClick(MouseButtonEvent click) {
        if (click.button() != MOUSE_BUTTON_LEFT || !insideNavigation(click.x(), click.y())) return false;

        List<Tab> tabs = Tabs.get();
        if (tabs.isEmpty()) return false;

        double start = navigationStart();
        double slotWidth = navigationWidth() / tabs.size();
        if (slotWidth <= 0) return false;

        int index = (int) ((click.x() - start) / slotWidth);
        if (index < 0 || index >= tabs.size()) return false;

        tabs.get(index).openScreen(theme);
        return true;
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Tab hovered = tabAt(mouseX, mouseY);
        tooltip = hovered != null ? hovered.name : "Drag to move • Resize from the bottom-right corner • ESC closes";
        return super.render(renderer, mouseX, mouseY, delta);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(10), NoxaraStyle.TOP_BAR);

        double iconSize = theme.scale(21);
        double iconX = x + theme.scale(9);
        double iconY = y + (height - iconSize) / 2;
        NoxaraStyle.roundedRect(renderer, iconX, iconY, iconSize, iconSize, theme.scale(6), NoxaraStyle.VIOLET);
        renderer.quad(iconX + theme.scale(3), iconY + theme.scale(3), iconSize - theme.scale(6),
            iconSize - theme.scale(6), GuiRenderer.CIRCLE, NoxaraStyle.LAVENDER);
        renderer.text("N", iconX + iconSize / 2 - theme.textWidth("N") / 2,
            iconY + theme.scale(4), NoxaraStyle.TEXT, true);
        renderer.text("NOXARA", iconX + iconSize + theme.scale(9), y + theme.scale(5), NoxaraStyle.TEXT, true);
        renderer.text("CONTROL CENTER", iconX + iconSize + theme.scale(10),
            y + theme.scale(22), NoxaraStyle.MUTED, false);

        drawNavigation(renderer, mouseX, mouseY);

        double statusWidth = theme.scale(92);
        double statusHeight = theme.scale(22);
        double statusX = x + width - statusWidth - theme.scale(9);
        double statusY = y + (height - statusHeight) / 2;
        NoxaraStyle.roundedRect(renderer, statusX, statusY, statusWidth, statusHeight,
            statusHeight / 2, NoxaraStyle.CARD_ACTIVE);
        NoxaraStyle.roundedRect(renderer, statusX + theme.scale(8), statusY + theme.scale(8),
            theme.scale(5), theme.scale(5), theme.scale(2.5), NoxaraStyle.LAVENDER);
        renderer.text("26.3 TEST", statusX + theme.scale(18),
            statusY + (statusHeight - theme.textHeight()) / 2, NoxaraStyle.SUBTLE, false);
    }

    private void drawNavigation(GuiRenderer renderer, double mouseX, double mouseY) {
        List<Tab> tabs = Tabs.get();
        if (tabs.isEmpty()) return;

        double start = navigationStart();
        double slotWidth = navigationWidth() / tabs.size();
        if (slotWidth <= 0) return;

        double inset = theme.scale(1.5);
        double buttonHeight = theme.scale(28);
        double buttonY = y + (height - buttonHeight) / 2;

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            double buttonX = start + i * slotWidth + inset;
            double buttonWidth = Math.max(theme.scale(18), slotWidth - inset * 2);
            boolean selected = tab.isScreen(mc.gui.screen());
            boolean hovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth
                && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;

            if (selected || hovered) {
                NoxaraStyle.mix(navigationFill, NoxaraStyle.CARD_HOVER, NoxaraStyle.NAV_SELECTED,
                    selected ? 1 : 0.42);
                NoxaraStyle.roundedRect(renderer, buttonX, buttonY, buttonWidth, buttonHeight,
                    theme.scale(6), navigationFill);
            }

            if (selected) {
                NoxaraStyle.roundedRect(renderer, buttonX + theme.scale(6),
                    buttonY + buttonHeight - theme.scale(2.5),
                    Math.max(theme.scale(5), buttonWidth - theme.scale(12)), theme.scale(1.5),
                    theme.scale(1), NoxaraStyle.LAVENDER);
            }

            ItemStack icon = iconFor(tab);
            if (icon != null && !icon.isEmpty()) {
                boolean showLabel = slotWidth >= theme.scale(62);
                double iconX = showLabel ? buttonX + theme.scale(5) : buttonX + (buttonWidth - theme.scale(16)) / 2;
                renderer.item(icon, (int) iconX, (int) (buttonY + (buttonHeight - theme.scale(16)) / 2), 1, false);

                if (showLabel) {
                    String label = labelFor(tab);
                    String clipped = NoxaraStyle.clipText(label, buttonWidth - theme.scale(26), theme);
                    renderer.text(clipped, buttonX + theme.scale(24),
                        buttonY + (buttonHeight - theme.textHeight()) / 2,
                        selected ? NoxaraStyle.TEXT : NoxaraStyle.MUTED, false);
                }
            }
        }
    }

    private Tab tabAt(double mouseX, double mouseY) {
        if (!insideNavigation(mouseX, mouseY)) return null;

        List<Tab> tabs = Tabs.get();
        if (tabs.isEmpty()) return null;

        double slotWidth = navigationWidth() / tabs.size();
        if (slotWidth <= 0) return null;
        int index = (int) ((mouseX - navigationStart()) / slotWidth);
        return index >= 0 && index < tabs.size() ? tabs.get(index) : null;
    }

    private boolean insideNavigation(double mouseX, double mouseY) {
        return mouseY >= y && mouseY <= y + height
            && mouseX >= navigationStart() && mouseX <= navigationStart() + navigationWidth();
    }

    private double navigationStart() {
        // Leave a dedicated, quiet identity zone for the Noxara logo and name.
        return x + theme.scale(142);
    }

    private double navigationWidth() {
        double statusStart = x + width - theme.scale(92) - theme.scale(9);
        return Math.max(0, statusStart - navigationStart() - theme.scale(5));
    }

    private static String labelFor(Tab tab) {
        return switch (tab.name) {
            case "Path Manager" -> "Paths";
            default -> tab.name;
        };
    }

    private static ItemStack iconFor(Tab tab) {
        return switch (tab.name) {
            case "Modules" -> MODULES_ICON;
            case "Config" -> CONFIG_ICON;
            case "GUI" -> GUI_ICON;
            case "HUD" -> HUD_ICON;
            case "Friends" -> FRIENDS_ICON;
            case "Macros" -> MACROS_ICON;
            case "Profiles" -> PROFILES_ICON;
            case "Path Manager" -> PATHS_ICON;
            default -> null;
        };
    }
}
