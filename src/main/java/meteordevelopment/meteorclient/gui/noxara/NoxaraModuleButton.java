/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT;

/** Module row with category icon, obvious settings control, and a separate enable switch. */
public final class NoxaraModuleButton extends WWidget {
    private final NoxaraModuleCard card;
    private final NoxaraDashboard dashboard;
    private final Module module;
    private final ItemStack categoryIcon;

    private double hoverProgress;
    private double toggleProgress;
    private String displayedTitle = "";
    private final Color surfaceColor = new Color(0, 0, 0, 255);
    private final Color toggleColor = new Color(0, 0, 0, 255);
    private final Color settingsFill = new Color(0, 0, 0, 255);

    public NoxaraModuleButton(NoxaraModuleCard card, NoxaraDashboard dashboard, Module module) {
        this.card = card;
        this.dashboard = dashboard;
        this.module = module;
        this.categoryIcon = module.category.icon.get();
        toggleProgress = module.isActive() ? 1 : 0;
        tooltip = module.description;
    }

    @Override
    protected void onCalculateSize() {
        width = theme.scale(300);
        height = theme.scale(42);
    }

    @Override
    protected void onCalculateWidgetPositions() {
        // Reserve room for the category icon, labeled settings dropdown, and toggle.
        displayedTitle = NoxaraStyle.clipText(module.title, width - theme.scale(190), theme);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        boolean active = module.isActive();
        hoverProgress += ((mouseOver ? 1 : 0) - hoverProgress) * Math.min(1, delta * 16);
        toggleProgress += ((active ? 1 : 0) - toggleProgress) * Math.min(1, delta * 18);
        hoverProgress = Mth.clamp(hoverProgress, 0, 1);
        toggleProgress = Mth.clamp(toggleProgress, 0, 1);

        if (hoverProgress > 0.01 || active) {
            NoxaraStyle.mix(surfaceColor, active ? NoxaraStyle.CARD_ACTIVE : NoxaraStyle.CARD,
                NoxaraStyle.CARD_HOVER, hoverProgress);
            NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(6), surfaceColor);
        }

        if (categoryIcon != null && !categoryIcon.isEmpty()) {
            double iconSize = theme.scale(18);
            renderer.item(categoryIcon, (int) (x + theme.scale(10)),
                (int) (y + (height - iconSize) / 2), 1, false);
        }

        renderer.text(displayedTitle, x + theme.scale(34),
            y + (height - theme.textHeight()) / 2,
            active ? NoxaraStyle.LAVENDER : NoxaraStyle.TEXT, false);

        double switchWidth = theme.scale(34);
        double switchHeight = theme.scale(18);
        double switchX = x + width - switchWidth - theme.scale(10);
        double switchY = y + (height - switchHeight) / 2;

        double settingsWidth = theme.scale(84);
        double settingsHeight = theme.scale(25);
        double settingsX = switchX - theme.scale(8) - settingsWidth;
        double settingsY = y + (height - settingsHeight) / 2;
        boolean settingsHovered = mouseX >= settingsX && mouseX <= settingsX + settingsWidth
            && mouseY >= settingsY && mouseY <= settingsY + settingsHeight;
        NoxaraStyle.mix(settingsFill, NoxaraStyle.CARD, NoxaraStyle.CARD_ACTIVE,
            card.isExpanded() ? 1 : settingsHovered ? 0.9 : 0.55);
        NoxaraStyle.roundedRect(renderer, settingsX, settingsY, settingsWidth, settingsHeight,
            theme.scale(6), settingsFill);

        drawSettingsIcon(renderer, settingsX + theme.scale(8), settingsY + theme.scale(7),
            theme.scale(10), card.isExpanded() || settingsHovered ? NoxaraStyle.LAVENDER : NoxaraStyle.MUTED);
        renderer.text("Settings", settingsX + theme.scale(23),
            settingsY + (settingsHeight - theme.textHeight()) / 2,
            card.isExpanded() || settingsHovered ? NoxaraStyle.TEXT : NoxaraStyle.MUTED, false);

        if (toggleProgress > 0.05) {
            NoxaraStyle.roundedRect(renderer, switchX - theme.scale(2), switchY - theme.scale(2),
                switchWidth + theme.scale(4), switchHeight + theme.scale(4),
                switchHeight / 2 + theme.scale(2), NoxaraStyle.GLOW);
        }

        NoxaraStyle.mix(toggleColor, NoxaraStyle.SWITCH_OFF, NoxaraStyle.VIOLET, toggleProgress);
        NoxaraStyle.roundedRect(renderer, switchX, switchY, switchWidth, switchHeight, switchHeight / 2, toggleColor);

        double knobSize = switchHeight - theme.scale(4);
        double knobX = switchX + theme.scale(2) + (switchWidth - knobSize - theme.scale(4)) * toggleProgress;
        renderer.quad(knobX, switchY + theme.scale(2), knobSize, knobSize, GuiRenderer.CIRCLE, NoxaraStyle.SWITCH_KNOB);
    }

    private void drawSettingsIcon(GuiRenderer renderer, double x, double y, double size, Color color) {
        double cx = x + size / 2;
        double cy = y + size / 2;
        double thick = Math.max(1, theme.scale(1.5));
        NoxaraStyle.roundedRect(renderer, cx - thick / 2, y, thick, size, thick / 2, color);
        NoxaraStyle.roundedRect(renderer, x, cy - thick / 2, size, thick, thick / 2, color);
        NoxaraStyle.roundedRect(renderer, x + size * 0.18, y + size * 0.18,
            size * 0.64, size * 0.64, size * 0.2, color);
        renderer.quad(cx - size * 0.13, cy - size * 0.13, size * 0.26, size * 0.26,
            GuiRenderer.CIRCLE, NoxaraStyle.CARD);
    }

    @Override
    public boolean onMouseClicked(MouseButtonEvent click, boolean doubled) {
        if (!mouseOver) return false;

        if (click.button() == MOUSE_BUTTON_RIGHT) {
            // Keep module settings inline: right-click opens the same slide-down as the row action.
            card.toggleExpanded();
            return true;
        }

        if (click.button() != MOUSE_BUTTON_LEFT) return false;

        double switchStart = x + width - theme.scale(48);

        if (click.x() >= switchStart) {
            module.toggle();
            dashboard.invalidate();
        } else {
            // Clicking the labeled Settings pill or the rest of the row toggles the dropdown.
            card.toggleExpanded();
        }
        return true;
    }
}
