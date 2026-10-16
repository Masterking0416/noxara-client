/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.util.List;


public final class NoxaraMainPanel extends NoxaraPanel {
    private final NoxaraDashboard dashboard;
    private NoxaraContentHeading heading;
    private WTextBox searchBox;
    private WView moduleView;
    private double lastViewportHeight = -1;

    public NoxaraMainPanel(NoxaraDashboard dashboard) {
        super(NoxaraStyle.PANEL, 12, true);
        this.dashboard = dashboard;
        spacing = 6;
    }

    @Override
    public void init() {
        heading = add(new NoxaraContentHeading(dashboard)).expandX().padHorizontal(18).padTop(15).padBottom(4).widget();

        searchBox = add(theme.textBox("", "Search modules and settings…"))
            .expandX().padHorizontal(16).padVertical(3).widget();
        searchBox.action = dashboard::refreshModules;

        add(new HintLabel()).padHorizontal(18).padBottom(1);

        moduleView = theme.view();
        moduleView.spacing = 4;
        moduleView.hasScrollBar = true;
        moduleView.scrollOnlyWhenMouseOver = true;

        // WView.init() resets its default maxHeight, so attach/initialize it before
        // applying the dashboard's viewport. Otherwise its first layout uses nearly
        // the entire screen height and the module rows bleed outside this panel.
        add(moduleView).expandX().padHorizontal(13).padBottom(12);
        updateViewport();
    }

    public String searchText() {
        return searchBox == null ? "" : searchBox.get();
    }

    public void updateViewport() {
        if (moduleView == null) return;

        // Use the dashboard's known window geometry instead of this panel's height:
        // on first init the panel height is still zero while widgets are being created.
        // This keeps the initial list viewport correct before the first layout pass.
        double nextHeight = Math.max(theme.scale(110), dashboard.contentHeight() - theme.scale(156));
        if (Math.abs(nextHeight - lastViewportHeight) > 0.5) {
            lastViewportHeight = nextHeight;
            moduleView.maxHeight = nextHeight;
            moduleView.invalidate();
        }

        // Expanded descriptions use a max-width setting to calculate their line
        // layout, so update it before the next calculateSize() when the window resizes.
        for (var cell : moduleView.cells) {
            if (cell.widget() instanceof NoxaraModuleCard card) card.updateViewport();
        }
    }

    public void refreshModules() {
        if (moduleView == null) return;
        moduleView.clear();

        List<Module> modules = dashboard.visibleModules(searchText());
        if (modules.isEmpty()) {
            moduleView.add(new EmptyState()).pad(16);
        } else {
            for (Module module : modules) {
                moduleView.add(new NoxaraModuleCard(dashboard, module))
                    .expandX().padHorizontal(3).padVertical(1);
            }
        }

        invalidate();
    }

    public void tickSettings() {
        if (moduleView == null) return;
        for (var cell : moduleView.cells) {
            if (cell.widget() instanceof NoxaraModuleCard card) card.tickSettings();
        }
    }

    private static final class NoxaraContentHeading extends WWidget {
        private final NoxaraDashboard dashboard;
        private String cachedCounts = "";
        private int cachedModules = -1;
        private int cachedActive = -1;

        private NoxaraContentHeading(NoxaraDashboard dashboard) {
            this.dashboard = dashboard;
        }

        @Override
        protected void onCalculateSize() {
            width = theme.scale(480);
            height = theme.scale(62);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.text(dashboard.pageTitle(), x, y + theme.scale(4), NoxaraStyle.TEXT, true);
            renderer.text(dashboard.pageSubtitle(), x, y + theme.scale(28), NoxaraStyle.MUTED, false);

            int modules = meteordevelopment.meteorclient.systems.modules.Modules.get().getCount();
            int active = meteordevelopment.meteorclient.systems.modules.Modules.get().getActive().size();
            if (modules != cachedModules || active != cachedActive) {
                cachedModules = modules;
                cachedActive = active;
                cachedCounts = active + " active  /  " + modules + " modules";
            }

            double chipWidth = theme.textWidth(cachedCounts) + theme.scale(24);
            double chipHeight = theme.scale(28);
            double chipX = x + width - chipWidth;
            double chipY = y + theme.scale(13);
            NoxaraStyle.roundedRect(renderer, chipX, chipY, chipWidth, chipHeight, theme.scale(9), NoxaraStyle.CARD);
            NoxaraStyle.roundedRect(renderer, chipX + theme.scale(8), chipY + theme.scale(11), theme.scale(6), theme.scale(6), theme.scale(3), NoxaraStyle.LAVENDER);
            renderer.text(cachedCounts, chipX + theme.scale(19), chipY + (chipHeight - theme.textHeight()) / 2, NoxaraStyle.SUBTLE, false);
        }
    }

    private static final class HintLabel extends WWidget {
        private static final String TEXT = "Click a card to expand settings  •  Switch to toggle  •  Right-click for keybinds";

        @Override
        protected void onCalculateSize() {
            width = theme.textWidth(TEXT);
            height = theme.textHeight();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.text(TEXT, x, y, NoxaraStyle.MUTED, false);
        }
    }

    private static final class EmptyState extends WWidget {
        @Override
        protected void onCalculateSize() {
            width = theme.scale(240);
            height = theme.scale(70);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.text("No modules match this view.", x + theme.scale(12), y + theme.scale(10), NoxaraStyle.TEXT, false);
            renderer.text("Try another category or search term.", x + theme.scale(12), y + theme.scale(32), NoxaraStyle.MUTED, false);
        }
    }
}
