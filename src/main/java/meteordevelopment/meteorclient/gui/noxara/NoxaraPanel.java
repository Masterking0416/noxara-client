/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Rounded, translucent panel with a quiet outline and optional ambient shadow. */
public class NoxaraPanel extends WVerticalList {
    protected Color fill = NoxaraStyle.PANEL;
    protected double cornerRadius = 12;
    protected boolean shadow;

    public NoxaraPanel() {
        this(NoxaraStyle.PANEL, 12, true);
    }

    public NoxaraPanel(Color fill, double cornerRadius, boolean shadow) {
        this.fill = fill;
        this.cornerRadius = cornerRadius;
        this.shadow = shadow;
        spacing = 5;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        NoxaraStyle.panel(renderer, x, y, width, height, theme.scale(cornerRadius), fill, shadow);
    }
}
