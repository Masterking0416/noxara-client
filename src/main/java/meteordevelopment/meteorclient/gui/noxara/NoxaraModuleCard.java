/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** A compact module row with a clear, directly clickable settings dropdown. */
public final class NoxaraModuleCard extends NoxaraPanel {
    private final NoxaraDashboard dashboard;
    private final Module module;

    private NoxaraModuleButton header;
    private NoxaraSettingsPanel settingsPanel;
    private Cell<NoxaraSettingsPanel> settingsCell;
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

    public void updateViewport() {
        if (settingsPanel != null) settingsPanel.updateViewport();
    }

    /** Opens or fully removes the inline dropdown. No zero-height intermediate state. */
    public void toggleExpanded() {
        if (expanded) {
            expanded = false;
            if (settingsCell != null) remove(settingsCell);
            settingsCell = null;
            settingsPanel = null;
        } else {
            expanded = true;
            settingsPanel = new NoxaraSettingsPanel(module, dashboard);
            settingsCell = add(settingsPanel).expandX().padHorizontal(6);
        }

        invalidate();
        dashboard.invalidate();
    }

    public void tickSettings() {
        if (expanded && settingsPanel != null) {
            module.settings.tick(settingsPanel.settingsWidget(), theme);
        }
    }

    private static final class NoxaraSettingsPanel extends NoxaraPanel {
        private final Module module;
        private final NoxaraDashboard dashboard;
        private WMultiLabel descriptionLabel;
        private WContainer settingsWidget;

        private NoxaraSettingsPanel(Module module, NoxaraDashboard dashboard) {
            super(NoxaraStyle.PANEL_SOFT, 8, false);
            this.module = module;
            this.dashboard = dashboard;
            spacing = 6;
        }

        @Override
        public void init() {
            add(new SettingsPanelHeading()).padHorizontal(10).padTop(8).padBottom(1);

            var description = theme.label(module.description, dashboard.settingsDescriptionWidth());
            if (description instanceof WMultiLabel multiLabel) descriptionLabel = multiLabel;
            add(description).expandX().padHorizontal(10).padBottom(5);

            settingsWidget = (WContainer) theme.settings(module.settings);
            settingsWidget.minWidth = 0;
            add(settingsWidget).expandX().padHorizontal(8);

            WButton fullSettings = theme.button("More settings & keybinds");
            fullSettings.action = () -> mc.gui.setScreen(theme.moduleScreen(module));
            add(fullSettings).right().pad(9);
        }

        private void updateViewport() {
            if (descriptionLabel != null) {
                descriptionLabel.setMaxWidth(dashboard.settingsDescriptionWidth());
            }
        }

        private WContainer settingsWidget() {
            return settingsWidget;
        }
    }

    private static final class SettingsPanelHeading extends WWidget {
        @Override
        protected void onCalculateSize() {
            width = theme.scale(180);
            height = theme.scale(22);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            double unit = theme.scale(1);
            double cx = x + theme.scale(10);
            double cy = y + height / 2;
            double arm = theme.scale(5);
            double thick = Math.max(1, theme.scale(2));
            NoxaraStyle.roundedRect(renderer, cx - thick / 2, cy - arm, thick, arm * 2,
                thick / 2, NoxaraStyle.LAVENDER);
            NoxaraStyle.roundedRect(renderer, cx - arm, cy - thick / 2, arm * 2, thick,
                thick / 2, NoxaraStyle.LAVENDER);
            NoxaraStyle.roundedRect(renderer, cx - arm * 0.68, cy - arm * 0.68,
                arm * 1.36, arm * 1.36, theme.scale(2), NoxaraStyle.LAVENDER);
            renderer.quad(cx - unit, cy - unit, unit * 2, unit * 2, GuiRenderer.CIRCLE, NoxaraStyle.TOP_BAR);
            renderer.text("SETTINGS", x + theme.scale(23),
                y + (height - theme.textHeight()) / 2, NoxaraStyle.TEXT, true);
        }
    }
}
