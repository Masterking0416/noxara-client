/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets.input;

import meteordevelopment.meteorclient.gui.noxara.NoxaraStyle;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;

public class WMeteorSlider extends WSlider implements MeteorWidget {
    private final meteordevelopment.meteorclient.utils.render.color.Color handleColor = new meteordevelopment.meteorclient.utils.render.color.Color(0, 0, 0, 255);

    public WMeteorSlider(double value, double min, double max) {
        super(value, min, max);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double valueWidth = valueWidth();
        MeteorGuiTheme theme = theme();
        double handle = handleSize();
        double trackX = x + handle / 2;
        double trackWidth = Math.max(0, width - handle);
        double trackHeight = theme.scale(5);
        double trackY = y + height / 2 - trackHeight / 2;

        NoxaraStyle.roundedRect(renderer, trackX, trackY, trackWidth, trackHeight, trackHeight / 2, NoxaraStyle.SWITCH_OFF);
        NoxaraStyle.roundedRect(renderer, trackX, trackY, Math.min(trackWidth, Math.max(0, valueWidth)), trackHeight, trackHeight / 2, NoxaraStyle.VIOLET);

        double knobX = x + valueWidth;
        if (dragging || handleMouseOver) {
            NoxaraStyle.roundedRect(renderer, knobX - theme.scale(2), y + height / 2 - handle / 2 - theme.scale(2),
                handle + theme.scale(4), handle + theme.scale(4), handle / 2 + theme.scale(2), NoxaraStyle.GLOW);
        }
        NoxaraStyle.mix(handleColor, NoxaraStyle.VIOLET, NoxaraStyle.LAVENDER, dragging || handleMouseOver ? 1 : 0);
        renderer.quad(knobX, y + height / 2 - handle / 2, handle, handle, GuiRenderer.CIRCLE, handleColor);

        if (dragging) {
            String valueText = Math.round(value * 100.0) / 100.0 + "";
            double labelWidth = theme.textWidth(valueText) + theme.scale(12);
            double labelHeight = theme.textHeight() + theme.scale(8);
            double labelX = Math.max(0, Math.min(knobX + handle / 2 - labelWidth / 2, meteordevelopment.meteorclient.utils.Utils.getWindowWidth() - labelWidth));
            double labelY = Math.max(0, y - labelHeight - theme.scale(3));
            NoxaraStyle.panel(renderer, labelX, labelY, labelWidth, labelHeight, theme.scale(6), NoxaraStyle.CARD_ACTIVE, false);
            renderer.text(valueText, labelX + theme.scale(6), labelY + theme.scale(4), NoxaraStyle.TEXT, false);
        }
    }
}
