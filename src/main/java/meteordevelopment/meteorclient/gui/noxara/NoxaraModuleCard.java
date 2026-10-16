/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;
import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public final class NoxaraModuleCard extends NoxaraPanel {
    private final NoxaraDashboard dashboard;
    private final Module module;

    private NoxaraModuleButton header;
    private NoxaraSettingsPanel settingsPanel;
    private boolean expanded;

    public NoxaraModuleCard(NoxaraDashboard dashboard, Module module) {
        super(NoxaraStyle.CARD, 9, false);
        this.dashboard = dashboard;
        this.module = module;
        spacing = 2;
    }

    @Override
    public void init() {
        header = add(new NoxaraModuleButton(this, dashboard, module))
            .expandX().padHorizontal(5).padVertical(4).widget();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void toggleExpanded() {
        expanded = !expanded;
        if (expanded) {
            settingsPanel = new NoxaraSettingsPanel(module);
            add(settingsPanel).expandX().pad(8);
        } else if (settingsPanel != null) {
            for (var cell : cells) {
                if (cell.widget() == settingsPanel) {
                    remove(cell);
                    break;
                }
            }
            settingsPanel = null;
        }

        invalidate();
        dashboard.invalidate();
    }

    public void tickSettings() {
        if (settingsPanel != null) module.settings.tick(settingsPanel.settingsWidget(), theme);
    }

    private static final class NoxaraModuleButton extends WWidget {
        private final NoxaraModuleCard card;
        private final NoxaraDashboard dashboard;
        private final Module module;

        private double hoverProgress;
        private double toggleProgress;
        private String displayedTitle = "";
        private String displayedDescription = "";
        private final Color toggleColor = new Color(0, 0, 0, 255);

        private NoxaraModuleButton(NoxaraModuleCard card, NoxaraDashboard dashboard, Module module) {
            this.card = card;
            this.dashboard = dashboard;
            this.module = module;
            toggleProgress = module.isActive() ? 1 : 0;
            tooltip = module.description;
        }

        @Override
        protected void onCalculateSize() {
            width = theme.scale(330);
            height = theme.scale(58);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double reserved = theme.scale(98);
            displayedTitle = NoxaraStyle.clipText(module.title, width - theme.scale(26) - reserved, theme);
            displayedDescription = NoxaraStyle.clipText(module.description, width - theme.scale(30), theme);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean active = module.isActive();
            double targetHover = mouseOver ? 1 : 0;
            hoverProgress += (targetHover - hoverProgress) * Math.min(1, delta * 16);
            toggleProgress += ((active ? 1 : 0) - toggleProgress) * Math.min(1, delta * 18);

            if (hoverProgress > 0.01) {
                NoxaraStyle.mix(toggleColor, NoxaraStyle.CARD, NoxaraStyle.CARD_HOVER, hoverProgress);
                NoxaraStyle.roundedRect(renderer, x, y, width, height, theme.scale(8), toggleColor);
            }

            renderer.text(displayedTitle, x + theme.scale(12), y + theme.scale(9),
                active ? NoxaraStyle.LAVENDER : NoxaraStyle.TEXT, false);
            renderer.text(displayedDescription, x + theme.scale(12), y + theme.scale(32), NoxaraStyle.MUTED, false);

            // Expander is placed just left of the switch.
            double arrowSize = theme.scale(9);
            double arrowX = x + width - theme.scale(61);
            double arrowY = y + (height - arrowSize) / 2;
            renderer.rotatedQuad(arrowX, arrowY, arrowSize, arrowSize, card.isExpanded() ? 0 : -90, GuiRenderer.TRIANGLE, NoxaraStyle.SUBTLE);

            double switchWidth = theme.scale(34);
            double switchHeight = theme.scale(18);
            double switchX = x + width - switchWidth - theme.scale(10);
            double switchY = y + (height - switchHeight) / 2;

            if (toggleProgress > 0.05) {
                NoxaraStyle.roundedRect(renderer, switchX - theme.scale(2), switchY - theme.scale(2),
                    switchWidth + theme.scale(4), switchHeight + theme.scale(4), switchHeight / 2 + theme.scale(2), NoxaraStyle.GLOW);
            }

            NoxaraStyle.mix(toggleColor, NoxaraStyle.SWITCH_OFF, NoxaraStyle.VIOLET, toggleProgress);
            NoxaraStyle.roundedRect(renderer, switchX, switchY, switchWidth, switchHeight, switchHeight / 2, toggleColor);

            double knobSize = switchHeight - theme.scale(4);
            double knobX = switchX + theme.scale(2) + (switchWidth - knobSize - theme.scale(4)) * toggleProgress;
            renderer.quad(knobX, switchY + theme.scale(2), knobSize, knobSize, GuiRenderer.CIRCLE, NoxaraStyle.SWITCH_KNOB);
        }

        @Override
        public boolean onMouseClicked(MouseButtonEvent click, boolean doubled) {
            if (!mouseOver) return false;

            if (click.button() == MOUSE_BUTTON_RIGHT) {
                mc.gui.setScreen(theme.moduleScreen(module));
                return true;
            }

            if (click.button() != MOUSE_BUTTON_LEFT) return false;

            if (click.x() >= x + width - theme.scale(48)) {
                module.toggle();
                dashboard.invalidate();
            } else {
                card.toggleExpanded();
            }
            return true;
        }
    }

    private static final class NoxaraSettingsPanel extends NoxaraPanel {
        private final Module module;
        private WContainer settingsWidget;

        private NoxaraSettingsPanel(Module module) {
            super(NoxaraStyle.PANEL_SOFT, 8, false);
            this.module = module;
            spacing = 6;
        }

        @Override
        public void init() {
            add(theme.label("SETTINGS", true)).padHorizontal(10).padTop(8).padBottom(1);
            add(theme.label(module.description, Math.max(theme.scale(220), meteordevelopment.meteorclient.utils.Utils.getWindowWidth() * 0.42)))
                .expandX().padHorizontal(10).padBottom(5);

            settingsWidget = (WContainer) theme.settings(module.settings);
            settingsWidget.minWidth = 0;
            add(settingsWidget).expandX().padHorizontal(8);

            WButton fullSettings = theme.button("More settings & keybinds");
            fullSettings.action = () -> mc.gui.setScreen(theme.moduleScreen(module));
            add(fullSettings).right().pad(9);
        }

        private WContainer settingsWidget() {
            return settingsWidget;
        }
    }
}
