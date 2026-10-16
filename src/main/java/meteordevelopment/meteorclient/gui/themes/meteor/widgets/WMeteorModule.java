/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.Mth;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WMeteorModule extends WPressable implements MeteorWidget {
    private final Module module;
    private final String title;

    private double titleWidth;
    private double hoverProgress;
    private double activeProgress;

    public WMeteorModule(Module module, String title) {
        this.module = module;
        this.title = title;
        this.tooltip = module.description;

        if (module.isActive()) {
            hoverProgress = 1;
            activeProgress = 1;
        }
    }

    @Override
    public double pad() {
        return theme.scale(5);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();
        if (titleWidth == 0) titleWidth = theme.textWidth(title);

        // Reserve a little space for the active-state marker on the right.
        width = pad + titleWidth + pad + theme.scale(5);
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == MOUSE_BUTTON_LEFT) module.toggle();
        else if (button == MOUSE_BUTTON_RIGHT) mc.gui.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        MeteorGuiTheme theme = theme();

        hoverProgress += delta * 5 * ((module.isActive() || mouseOver) ? 1 : -1);
        hoverProgress = Mth.clamp(hoverProgress, 0, 1);

        activeProgress += delta * 7 * (module.isActive() ? 1 : -1);
        activeProgress = Mth.clamp(activeProgress, 0, 1);

        if (hoverProgress > 0) {
            var fill = module.isActive()
                ? theme.moduleBackground.get()
                : theme.backgroundColor.get(false, true, false);
            renderer.quad(x, y, width * hoverProgress, height, fill);
        }

        if (activeProgress > 0) {
            renderer.quad(x, y + height * (1 - activeProgress), theme.scale(2), height * activeProgress, theme.accentColor.get());
        }

        // A fine divider separates rows without adding heavy panel borders.
        renderer.quad(x, y + height - theme.scale(0.5), width, theme.scale(0.5), theme.separatorEdges.get());

        double textX = this.x + pad();
        double availableWidth = width - pad() * 2 - theme.scale(4);

        if (theme.moduleAlignment.get() == AlignmentX.Center) {
            textX += availableWidth / 2 - titleWidth / 2;
        } else if (theme.moduleAlignment.get() == AlignmentX.Right) {
            textX += availableWidth - titleWidth;
        }

        renderer.text(title, textX, y + pad(), module.isActive() ? theme.accentColor.get() : theme.textColor.get(), false);

        if (module.isActive()) {
            double dot = theme.scale(3);
            renderer.quad(this.x + width - pad() - dot, this.y + (height - dot) / 2, dot, dot, theme.accentColor.get());
        }
    }
}