/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;

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

    public void updateViewport() {
        if (settingsPanel != null) settingsPanel.updateViewport();
    }

    public void toggleExpanded() {
        expanded = !expanded;
        if (expanded) {
            settingsPanel = new NoxaraSettingsPanel(module, dashboard);
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
            add(theme.label("SETTINGS", true)).padHorizontal(10).padTop(8).padBottom(1);
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
}
