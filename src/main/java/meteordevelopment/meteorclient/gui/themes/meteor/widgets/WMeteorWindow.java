/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;

public class WMeteorWindow extends WWindow implements MeteorWidget {
    public WMeteorWindow(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    protected WHeader header(WWidget icon) {
        return new WMeteorHeader(icon);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0) {
            double stroke = theme().scale(1);
            renderer.quad(x, y + header.height, width, height - header.height, theme().backgroundColor.get());

            // Subtle graphite outline makes floating category panels easier to distinguish.
            renderer.quad(x, y + header.height, stroke, height - header.height, theme().separatorEdges.get());
            renderer.quad(x + width - stroke, y + header.height, stroke, height - header.height, theme().separatorEdges.get());
            renderer.quad(x, y + height - stroke, width, stroke, theme().separatorEdges.get());
        }
    }

    private class WMeteorHeader extends WHeader {
        public WMeteorHeader(WWidget icon) {
            super(icon);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quad(this, theme().backgroundColor.get());
            renderer.quad(x, y, theme().scale(3), height, theme().accentColor.get());
            renderer.quad(x, y, width, theme().scale(1), theme().separatorEdges.get());
            renderer.quad(x, y + height - theme().scale(1), width, theme().scale(1), theme().separatorEdges.get());
        }
    }
}