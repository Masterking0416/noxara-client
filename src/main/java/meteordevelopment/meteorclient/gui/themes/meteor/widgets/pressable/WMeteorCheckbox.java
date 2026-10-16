/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.noxara.NoxaraStyle;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Mth;

public class WMeteorCheckbox extends WCheckbox implements MeteorWidget {
    private double animProgress;
    private final Color trackColor = new Color(0, 0, 0, 255);

    public WMeteorCheckbox(boolean checked) {
        super(checked);
        animProgress = checked ? 1 : 0;
    }

    @Override
    protected void onCalculateSize() {
        MeteorGuiTheme theme = theme();
        width = theme.scale(32);
        height = theme.scale(18);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MeteorGuiTheme theme = theme();
        animProgress += ((checked ? 1 : 0) - animProgress) * Math.min(1, delta * 18);
        animProgress = Mth.clamp(animProgress, 0, 1);

        double radius = height / 2;
        if (animProgress > 0.02) {
            NoxaraStyle.roundedRect(renderer, x - theme.scale(2), y - theme.scale(2),
                width + theme.scale(4), height + theme.scale(4), radius + theme.scale(2), NoxaraStyle.GLOW);
        }
        NoxaraStyle.mix(trackColor, NoxaraStyle.SWITCH_OFF, NoxaraStyle.VIOLET, animProgress);
        NoxaraStyle.roundedRect(renderer, x, y, width, height, radius, trackColor);

        double knobSize = height - theme.scale(4);
        double knobX = x + theme.scale(2) + (width - knobSize - theme.scale(4)) * animProgress;
        renderer.quad(knobX, y + theme.scale(2), knobSize, knobSize, GuiRenderer.CIRCLE, NoxaraStyle.SWITCH_KNOB);
    }
}
