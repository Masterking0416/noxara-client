/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

/** Visual corner affordance; the dashboard owns resize hit-testing and pointer capture. */
public final class NoxaraResizeGrip extends WWidget {
    private final NoxaraDashboard dashboard;

    public NoxaraResizeGrip(NoxaraDashboard dashboard) {
        this.dashboard = dashboard;
        tooltip = "Drag to resize the ClickGUI";
    }

    @Override
    protected void onCalculateSize() {
        width = theme.scale(30);
        height = theme.scale(30);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (mouseOver) {
            NoxaraStyle.roundedRect(renderer, x + theme.scale(5), y + theme.scale(5),
                width - theme.scale(5), height - theme.scale(5), theme.scale(8), NoxaraStyle.GLOW);
        }

        double baseX = x + width - theme.scale(6);
        double baseY = y + height - theme.scale(6);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col <= row; col++) {
                double dotX = baseX - col * theme.scale(5);
                double dotY = baseY - row * theme.scale(5);
                renderer.quad(dotX, dotY, theme.scale(2), theme.scale(2), GuiRenderer.CIRCLE,
                    mouseOver ? NoxaraStyle.LAVENDER : NoxaraStyle.SUBTLE);
            }
        }
    }
}
