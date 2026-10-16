/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

/** Floating title bar for dragging the window and exposing a compact client identity. */
public final class NoxaraWindowBar extends WWidget {
    private final NoxaraDashboard dashboard;

    public NoxaraWindowBar(NoxaraDashboard dashboard) {
        this.dashboard = dashboard;
        tooltip = "Drag to move • Drag the bottom-right corner to resize • ESC closes";
    }

    @Override
    protected void onCalculateSize() {
        width = theme.scale(500);
        height = theme.scale(36);
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

        if (width > theme.scale(680)) {
            String hint = "DRAG TO MOVE  •  RESIZE FROM CORNER";
            renderer.text(hint, x + width * 0.49, y + (height - theme.textHeight()) / 2,
                NoxaraStyle.MUTED, false);
        }

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
}
