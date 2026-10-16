/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class NoxaraDashboard extends WContainer {
    public static final String PAGE_ALL = "@all";
    public static final String PAGE_ACTIVE = "@active";
    public static final String PAGE_FAVORITES = "@favorites";

    private NoxaraSidebar sidebar;
    private NoxaraMainPanel mainPanel;
    private String page = PAGE_ALL;

    @Override
    public void init() {
        sidebar = new NoxaraSidebar(this);
        mainPanel = new NoxaraMainPanel(this);

        add(sidebar);
        add(mainPanel);

        mainPanel.refreshModules();
        updateViewport();
    }

    public String page() {
        return page;
    }

    public String pageTitle() {
        if (PAGE_ALL.equals(page)) return "All modules";
        if (PAGE_ACTIVE.equals(page)) return "Active modules";
        if (PAGE_FAVORITES.equals(page)) return "Favorites";
        return page;
    }

    public String pageSubtitle() {
        if (PAGE_ACTIVE.equals(page)) return "Everything currently enabled in your session";
        if (PAGE_FAVORITES.equals(page)) return "Your pinned tools, ready when you need them";
        if (PAGE_ALL.equals(page)) return "Your client, your modules, one control surface";
        return "Configure " + page.toLowerCase(Locale.ROOT) + " modules";
    }

    public void selectPage(String nextPage) {
        if (nextPage == null || nextPage.equals(page)) return;
        page = nextPage;
        mainPanel.refreshModules();
        invalidate();
    }

    public String searchText() {
        return mainPanel == null ? "" : mainPanel.searchText();
    }

    public void refreshModules() {
        if (mainPanel != null) mainPanel.refreshModules();
        invalidate();
    }

    public void updateViewport() {
        if (mainPanel != null) mainPanel.updateViewport();
        invalidate();
    }

    public void tickSettings() {
        if (mainPanel != null) mainPanel.tickSettings();
    }

    public List<Module> visibleModules(String query) {
        String filter = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Module> result = new ArrayList<>();

        Category category = null;
        if (!PAGE_ALL.equals(page) && !PAGE_ACTIVE.equals(page) && !PAGE_FAVORITES.equals(page)) {
            for (Category candidate : Modules.loopCategories()) {
                if (candidate.name.equals(page)) {
                    category = candidate;
                    break;
                }
            }
        }

        for (Module module : Modules.get().getAll()) {
            if (Config.get().hiddenModules.get().contains(module)) continue;
            if (category != null && module.category != category) continue;
            if (PAGE_ACTIVE.equals(page) && !module.isActive()) continue;
            if (PAGE_FAVORITES.equals(page) && !module.favorite) continue;

            if (!filter.isEmpty()
                && !module.title.toLowerCase(Locale.ROOT).contains(filter)
                && !module.name.toLowerCase(Locale.ROOT).contains(filter)
                && !module.description.toLowerCase(Locale.ROOT).contains(filter)) continue;

            result.add(module);
        }

        result.sort(Comparator.comparing(module -> module.title, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    @Override
    protected void onCalculateSize() {
        width = Utils.getWindowWidth();
        height = Utils.getWindowHeight();
    }

    @Override
    protected void onCalculateWidgetPositions() {
        double margin = theme.scale(18);
        double gap = theme.scale(14);
        double availableWidth = Math.max(theme.scale(360), width - margin * 2 - gap);
        double sidebarWidth = Math.min(theme.scale(190), Math.max(theme.scale(152), availableWidth * 0.24));
        sidebarWidth = Math.min(sidebarWidth, Math.max(theme.scale(132), width * 0.31));

        sidebar.x = x + margin;
        sidebar.y = y + margin;
        sidebar.width = sidebarWidth;
        sidebar.height = Math.max(theme.scale(220), height - margin * 2);

        mainPanel.x = sidebar.x + sidebarWidth + gap;
        mainPanel.y = sidebar.y;
        mainPanel.width = Math.max(theme.scale(280), x + width - margin - mainPanel.x);
        mainPanel.height = sidebar.height;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Translucent slate veil lets the current world/panorama show through like smoked glass.
        renderer.quad(0, 0, width, height,
            NoxaraStyle.BACKDROP_TOP_LEFT,
            NoxaraStyle.BACKDROP_TOP_RIGHT,
            NoxaraStyle.BACKDROP_BOTTOM_RIGHT,
            NoxaraStyle.BACKDROP_BOTTOM_LEFT);

        double glowWidth = width * 0.62;
        double glowHeight = height * 0.44;
        renderer.quad(0, 0, glowWidth, glowHeight,
            NoxaraStyle.GLOW,
            new meteordevelopment.meteorclient.utils.render.color.Color(124, 58, 237, 4),
            new meteordevelopment.meteorclient.utils.render.color.Color(124, 58, 237, 0),
            new meteordevelopment.meteorclient.utils.render.color.Color(124, 58, 237, 0));
    }
}
