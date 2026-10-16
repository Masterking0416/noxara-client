/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

/**
 * Shared palette and allocation-conscious primitives for the Noxara ClickGUI.
 *
 * Minecraft's GUI renderer batches quads, so rounded surfaces are composed from a
 * small number of batched quads and the client's antialiased circle texture.
 */
public final class NoxaraStyle {
    public static final Color BACKDROP_TOP_LEFT = new Color(12, 8, 23, 255);
    public static final Color BACKDROP_TOP_RIGHT = new Color(10, 13, 23, 255);
    public static final Color BACKDROP_BOTTOM_RIGHT = new Color(7, 8, 15, 255);
    public static final Color BACKDROP_BOTTOM_LEFT = new Color(21, 9, 35, 255);
    public static final Color NEBULA_LEFT = new Color(124, 58, 237, 34);
    public static final Color NEBULA_LOWER = new Color(124, 58, 237, 8);
    public static final Color NEBULA_CLEAR = new Color(124, 58, 237, 0);
    public static final Color NEBULA_RIGHT = new Color(89, 49, 180, 18);
    public static final Color NEBULA_RIGHT_CLEAR = new Color(89, 49, 180, 0);
    public static final Color SHELL = new Color(11, 11, 18, 232);
    public static final Color TOP_BAR = new Color(24, 21, 35, 242);
    public static final Color STAR_RAY = new Color(228, 214, 255, 125);

    public static final Color PANEL = new Color(18, 18, 24, 218);
    public static final Color PANEL_SOFT = new Color(24, 24, 31, 226);
    public static final Color CARD = new Color(27, 27, 36, 228);
    public static final Color CARD_HOVER = new Color(36, 31, 48, 238);
    public static final Color CARD_ACTIVE = new Color(44, 32, 66, 242);
    public static final Color NAV_SELECTED = new Color(124, 58, 237, 48);
    public static final Color BORDER = new Color(91, 76, 115, 122);
    public static final Color SHADOW = new Color(0, 0, 0, 64);
    public static final Color GLOW = new Color(124, 58, 237, 28);
    public static final Color GLOW_EDGE = new Color(124, 58, 237, 4);
    public static final Color GLOW_CLEAR = new Color(124, 58, 237, 0);

    public static final Color VIOLET = new Color(124, 58, 237, 255);
    public static final Color LAVENDER = new Color(192, 132, 252, 255);
    public static final Color TEXT = new Color(245, 243, 255, 255);
    public static final Color MUTED = new Color(156, 163, 175, 255);
    public static final Color SUBTLE = new Color(190, 184, 208, 255);
    public static final Color SWITCH_OFF = new Color(51, 51, 64, 255);
    public static final Color SWITCH_KNOB = new Color(248, 246, 255, 255);
    public static final Color GREEN = new Color(84, 220, 171, 255);

    private NoxaraStyle() {
    }

    public static void roundedRect(GuiRenderer renderer, double x, double y, double width, double height, double radius, Color color) {
        if (width <= 0 || height <= 0) return;

        double r = Math.min(radius, Math.min(width, height) / 2.0);
        if (r < 1 || GuiRenderer.CIRCLE == null) {
            renderer.quad(x, y, width, height, color);
            return;
        }

        // Straight body and side rails.
        renderer.quad(x + r, y, width - 2 * r, height, color);
        renderer.quad(x, y + r, r, height - 2 * r, color);
        renderer.quad(x + width - r, y + r, r, height - 2 * r, color);

        // Four antialiased circles form the corners.
        double diameter = r * 2;
        renderer.quad(x, y, diameter, diameter, GuiRenderer.CIRCLE, color);
        renderer.quad(x + width - diameter, y, diameter, diameter, GuiRenderer.CIRCLE, color);
        renderer.quad(x, y + height - diameter, diameter, diameter, GuiRenderer.CIRCLE, color);
        renderer.quad(x + width - diameter, y + height - diameter, diameter, diameter, GuiRenderer.CIRCLE, color);
    }

    public static void panel(GuiRenderer renderer, double x, double y, double width, double height, double radius, Color fill, boolean shadow) {
        if (shadow) roundedRect(renderer, x, y + 3, width, height, radius, SHADOW);
        roundedRect(renderer, x, y, width, height, radius, BORDER);
        roundedRect(renderer, x + 1, y + 1, width - 2, height - 2, Math.max(1, radius - 1), fill);
    }

    /** Writes an interpolated color into a caller-owned object to avoid render-loop allocations. */
    public static Color mix(Color output, Color start, Color end, double progress) {
        double t = Math.max(0, Math.min(1, progress));
        output.set(
            (int) (start.r + (end.r - start.r) * t),
            (int) (start.g + (end.g - start.g) * t),
            (int) (start.b + (end.b - start.b) * t),
            (int) (start.a + (end.a - start.a) * t)
        );
        return output;
    }

    public static String clipText(String text, double maxWidth, meteordevelopment.meteorclient.gui.GuiTheme theme) {
        if (text == null || text.isEmpty() || maxWidth <= 0) return "";
        if (theme.textWidth(text) <= maxWidth) return text;

        int end = text.length();
        while (end > 1 && theme.textWidth(text.substring(0, end) + "…") > maxWidth) end--;
        return text.substring(0, Math.max(1, end)) + "…";
    }
}
