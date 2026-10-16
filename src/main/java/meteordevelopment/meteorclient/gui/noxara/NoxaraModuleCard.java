/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.input.MouseButtonEvent;

import static meteordevelopment.meteorclient.MeteorClient.mc;

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

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        super.onRender(renderer, mouseX, mouseY, delta);

        // Animate in render time, not only game ticks, so opening feels smooth at high FPS.
        if (settingsPanel != null && settingsPanel.animateReveal(expanded, delta)) invalidate();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void updateViewport() {
        if (settingsPanel != null) settingsPanel.updateViewport();
    }

    public void toggleExpanded() {
        expanded = !expanded;

        if (expanded && settingsPanel == null) {
            settingsPanel = new NoxaraSettingsPanel(module, dashboard);
            // No vertical cell padding: the panel can begin at zero height and slide down.
            settingsCell = add(settingsPanel).expandX().padHorizontal(6);
        }

        // When closing, keep the settings alive until the reveal reaches zero.
        invalidate();
        dashboard.invalidate();
    }

    public void tickSettings() {
        if (settingsPanel == null) return;

        if (!expanded && settingsPanel.isFullyCollapsed()) {
            if (settingsCell != null) remove(settingsCell);
            settingsCell = null;
            settingsPanel = null;
            invalidate();
            return;
        }

        module.settings.tick(settingsPanel.settingsWidget(), theme);
    }

    private static final class NoxaraSettingsPanel extends NoxaraPanel {
        private final Module module;
        private final NoxaraDashboard dashboard;
        private WMultiLabel descriptionLabel;
        private WContainer settingsWidget;

        private double revealProgress;
        private double naturalHeight;

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

        @Override
        protected void onCalculateSize() {
            // First calculate the full, natural layout. Only the panel's outer height
            // is animated; child controls must keep their normal vertical positions.
            super.onCalculateSize();
            naturalHeight = height;
            height = visibleHeight();
        }

        @Override
        public void calculateWidgetPositions() {
            // WVerticalList positions its children using the container height.
            // Temporarily restore the full height while placing them, then reinstate
            // the clipped visible height used by the parent list and renderer.
            double visibleHeight = height;
            if (naturalHeight > 0) height = naturalHeight;
            super.calculateWidgetPositions();
            height = visibleHeight;
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!visible) return true;

            double clipHeight = visibleHeight();
            if (clipHeight <= 0.5) return false;

            // Keep child positions stable and clip their drawing to the revealed part.
            boolean mouseInsideReveal = mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + clipHeight;
            renderer.scissorStart(x, y, width, clipHeight);
            try {
                return super.render(renderer,
                    mouseInsideReveal ? mouseX : -100000,
                    mouseInsideReveal ? mouseY : -100000, delta);
            } finally {
                renderer.scissorEnd();
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
            if (click.x() < x || click.x() > x + width
                || click.y() < y || click.y() > y + visibleHeight()) return false;
            return super.mouseClicked(click, doubled);
        }

        private double visibleHeight() {
            return Math.max(0, naturalHeight * Math.max(0, Math.min(1, revealProgress)));
        }

        private boolean animateReveal(boolean open, double delta) {
            double before = revealProgress;
            double target = open ? 1 : 0;
            double elapsed = Math.max(0, Math.min(delta, 0.1));
            double step = 1 - Math.exp(-elapsed * 14);
            revealProgress += (target - revealProgress) * step;

            if (Math.abs(target - revealProgress) < 0.006) revealProgress = target;
            return Math.abs(revealProgress - before) > 0.0001;
        }

        private boolean isFullyCollapsed() {
            return revealProgress <= 0.001;
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
