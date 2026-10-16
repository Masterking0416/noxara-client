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

import java.util.List;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Floating title bar with direct GUI-drawn icons and shortcuts to Meteor tabs. */
public final class NoxaraWindowBar extends WWidget {
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

    /** Called before title-bar dragging so a tab click is not treated as a drag. */
    public boolean handleTabClick(MouseButtonEvent click) {
        if (click.button() != MOUSE_BUTTON_LEFT || !insideNavigation(click.x(), click.y())) return false;

        List<Tab> tabs = Tabs.get();
        if (tabs.isEmpty()) return false;

        double slotWidth = navigationWidth() / tabs.size();
        if (slotWidth <= 0) return false;

        int index = (int) ((click.x() - navigationStart()) / slotWidth);
        if (index < 0 || index >= tabs.size()) return false;

        tabs.get(index).openScreen(theme);
        return true;
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Tab hovered = tabAt(mouseX, mouseY);
        tooltip = hovered != null
            ? hovered.name
            : "Drag to move • Resize from the bottom-right corner • ESC closes";
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
        double iconSize = theme.scale(14);

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

            boolean showLabel = slotWidth >= theme.scale(62);
            double iconX = showLabel ? buttonX + theme.scale(5) : buttonX + (buttonWidth - iconSize) / 2;
            double iconY = buttonY + (buttonHeight - iconSize) / 2;
            drawTabIcon(renderer, tab.name, iconX, iconY, iconSize,
                selected ? NoxaraStyle.TEXT : NoxaraStyle.MUTED);

            if (showLabel) {
                String clipped = NoxaraStyle.clipText(labelFor(tab), buttonWidth - theme.scale(26), theme);
                renderer.text(clipped, buttonX + theme.scale(24),
                    buttonY + (buttonHeight - theme.textHeight()) / 2,
                    selected ? NoxaraStyle.TEXT : NoxaraStyle.MUTED, false);
            }
        }
    }

    /**
     * These icons use only GUI quads and circles. In particular, no ItemStack is
     * constructed while the client is starting and Minecraft's item components
     * may still be unbound.
     */
    private void drawTabIcon(GuiRenderer renderer, String name, double x, double y, double size, Color color) {
        double u = size / 16.0;
        switch (name) {
            case "Modules" -> {
                iconTile(renderer, x + u, y + u, 5 * u, 5 * u, u, color);
                iconTile(renderer, x + 10 * u, y + u, 5 * u, 5 * u, u, color);
                iconTile(renderer, x + u, y + 10 * u, 5 * u, 5 * u, u, color);
                iconTile(renderer, x + 10 * u, y + 10 * u, 5 * u, 5 * u, u, color);
            }
            case "Config" -> {
                iconRect(renderer, x + 2 * u, y + 3 * u, 12 * u, 1.4 * u, color);
                iconRect(renderer, x + 2 * u, y + 7.3 * u, 12 * u, 1.4 * u, color);
                iconRect(renderer, x + 2 * u, y + 11.6 * u, 12 * u, 1.4 * u, color);
                iconDot(renderer, x + 5 * u, y + 1.7 * u, 4 * u, color);
                iconDot(renderer, x + 9 * u, y + 6 * u, 4 * u, color);
                iconDot(renderer, x + 4 * u, y + 10.3 * u, 4 * u, color);
            }
            case "GUI" -> {
                iconRect(renderer, x + u, y + 2 * u, 14 * u, 1.6 * u, color);
                iconRect(renderer, x + u, y + 3.2 * u, 1.6 * u, 10.8 * u, color);
                iconRect(renderer, x + 13.4 * u, y + 3.2 * u, 1.6 * u, 10.8 * u, color);
                iconRect(renderer, x + 2 * u, y + 12.4 * u, 12 * u, 1.6 * u, color);
                iconDot(renderer, x + 3 * u, y + 2.1 * u, 1.3 * u, NoxaraStyle.VIOLET);
                iconDot(renderer, x + 5.2 * u, y + 2.1 * u, 1.3 * u, NoxaraStyle.VIOLET);
            }
            case "HUD" -> {
                iconRect(renderer, x + 7.2 * u, y + u, 1.6 * u, 4.2 * u, color);
                iconRect(renderer, x + 7.2 * u, y + 10.8 * u, 1.6 * u, 4.2 * u, color);
                iconRect(renderer, x + u, y + 7.2 * u, 4.2 * u, 1.6 * u, color);
                iconRect(renderer, x + 10.8 * u, y + 7.2 * u, 4.2 * u, 1.6 * u, color);
                iconDot(renderer, x + 6 * u, y + 6 * u, 4 * u, color);
            }
            case "Friends" -> {
                iconDot(renderer, x + 2 * u, y + 2 * u, 5 * u, color);
                iconDot(renderer, x + 9 * u, y + 3 * u, 4.5 * u, color);
                iconTile(renderer, x + 0.5 * u, y + 8 * u, 7 * u, 5.8 * u, 2 * u, color);
                iconTile(renderer, x + 8 * u, y + 9 * u, 7 * u, 4.8 * u, 2 * u, color);
            }
            case "Macros" -> {
                iconTile(renderer, x + 1.5 * u, y + 2 * u, 5.5 * u, 12 * u, 1.5 * u, color);
                iconTile(renderer, x + 9 * u, y + 2 * u, 5.5 * u, 12 * u, 1.5 * u, color);
                iconRect(renderer, x + 3 * u, y + 5 * u, 2.5 * u, 1.2 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 10.5 * u, y + 5 * u, 2.5 * u, 1.2 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 3 * u, y + 8 * u, 2.5 * u, 1.2 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 10.5 * u, y + 8 * u, 2.5 * u, 1.2 * u, NoxaraStyle.TOP_BAR);
            }
            case "Profiles" -> {
                iconTile(renderer, x + 2 * u, y + u, 12 * u, 14 * u, 1.5 * u, color);
                iconDot(renderer, x + 4 * u, y + 3 * u, 4 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 9 * u, y + 4 * u, 3 * u, 1.3 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 4 * u, y + 9 * u, 8 * u, 1.3 * u, NoxaraStyle.TOP_BAR);
                iconRect(renderer, x + 4 * u, y + 11.5 * u, 8 * u, 1.3 * u, NoxaraStyle.TOP_BAR);
            }
            case "Path Manager" -> {
                iconRect(renderer, x + 3 * u, y + 4 * u, 1.5 * u, 8 * u, color);
                iconRect(renderer, x + 3 * u, y + 10.5 * u, 8.5 * u, 1.5 * u, color);
                iconRect(renderer, x + 10 * u, y + 4 * u, 1.5 * u, 8 * u, color);
                iconDot(renderer, x + u, y + 2 * u, 5.5 * u, color);
                iconDot(renderer, x + 9 * u, y + 2 * u, 5.5 * u, color);
                iconDot(renderer, x + 9 * u, y + 9 * u, 5.5 * u, color);
            }
            default -> {
                iconDot(renderer, x + 1.5 * u, y + 6 * u, 3 * u, color);
                iconDot(renderer, x + 6.5 * u, y + 6 * u, 3 * u, color);
                iconDot(renderer, x + 11.5 * u, y + 6 * u, 3 * u, color);
            }
        }
    }

    private static void iconRect(GuiRenderer renderer, double x, double y, double width, double height, Color color) {
        renderer.quad(x, y, width, height, color);
    }

    private static void iconTile(GuiRenderer renderer, double x, double y, double width, double height,
                                 double radius, Color color) {
        NoxaraStyle.roundedRect(renderer, x, y, width, height, radius, color);
    }

    private static void iconDot(GuiRenderer renderer, double x, double y, double size, Color color) {
        renderer.quad(x, y, size, size, GuiRenderer.CIRCLE, color);
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
        // Leave a dedicated identity zone for the Noxara logo and name.
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
}
