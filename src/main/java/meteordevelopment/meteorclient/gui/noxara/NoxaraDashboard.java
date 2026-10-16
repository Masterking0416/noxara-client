/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.noxara;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT;

public final class NoxaraDashboard extends WContainer {
    public static final String PAGE_ALL = "@all";
    public static final String PAGE_ACTIVE = "@active";
    public static final String PAGE_FAVORITES = "@favorites";
    public static final String PAGE_EXPLOITS = "@exploits";

    private static final int DRAG_NONE = 0;
    private static final int DRAG_WINDOW = 1;
    private static final int DRAG_RESIZE = 2;
    private static final double SCREEN_GUTTER = 14;
    private static final double WINDOW_RADIUS = 16;
    private static final double TITLE_BAR_HEIGHT = 36;
    private static final int PARTICLE_COUNT = 58;

    // Kept for the current client session so closing/reopening the GUI does not reset its geometry.
    private static double rememberedX = Double.NaN;
    private static double rememberedY = Double.NaN;
    private static double rememberedWidth = Double.NaN;
    private static double rememberedHeight = Double.NaN;

    private final Random particleRandom = new Random(0x4E4F58415241L);
    private final double[] particleX = new double[PARTICLE_COUNT];
    private final double[] particleY = new double[PARTICLE_COUNT];
    private final double[] particleSpeed = new double[PARTICLE_COUNT];
    private final double[] particleDrift = new double[PARTICLE_COUNT];
    private final double[] particleSize = new double[PARTICLE_COUNT];
    private final double[] particlePhase = new double[PARTICLE_COUNT];
    private final double[] particleTwinkleSpeed = new double[PARTICLE_COUNT];
    private final Color[] particleCore = new Color[PARTICLE_COUNT];
    private final Color[] particleGlow = new Color[PARTICLE_COUNT];

    private NoxaraSidebar sidebar;
    private NoxaraMainPanel mainPanel;
    private NoxaraWindowBar windowBar;
    private NoxaraResizeGrip resizeGrip;

    private String page = PAGE_ALL;
    private boolean windowInitialized;
    private boolean particlesInitialized;
    private int particleScreenWidth;
    private int particleScreenHeight;

    private double windowX;
    private double windowY;
    private double windowWidth;
    private double windowHeight;

    private int dragMode = DRAG_NONE;
    private double dragOffsetX;
    private double dragOffsetY;
    private double resizeStartMouseX;
    private double resizeStartMouseY;
    private double resizeStartWidth;
    private double resizeStartHeight;

    public NoxaraDashboard() {
    }

    @Override
    public void init() {
        initializeWindow(Utils.getWindowWidth(), Utils.getWindowHeight());

        sidebar = new NoxaraSidebar(this);
        mainPanel = new NoxaraMainPanel(this);
        windowBar = new NoxaraWindowBar(this);
        resizeGrip = new NoxaraResizeGrip(this);

        add(sidebar);
        add(mainPanel);
        add(windowBar);
        add(resizeGrip);

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
        if (PAGE_EXPLOITS.equals(page)) return "Exploits";
        return page;
    }

    public String pageSubtitle() {
        if (PAGE_ACTIVE.equals(page)) return "Everything currently enabled in your session";
        if (PAGE_FAVORITES.equals(page)) return "Your pinned tools, ready when you need them";
        if (PAGE_ALL.equals(page)) return "Your client, your modules, one control surface";
        if (PAGE_EXPLOITS.equals(page)) return "Packet and interaction tools; server checks may limit them";
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

    public double windowWidth() {
        return windowWidth;
    }

    public double windowHeight() {
        return windowHeight;
    }

    public double contentHeight() {
        return Math.max(theme.scale(150), windowHeight - theme.scale(60));
    }

    /**
     * Maximum width for wrapped module descriptions. The width is derived from the
     * ClickGUI window, not the monitor, so expanded settings cannot inherit a
     * full-screen width and wrap incorrectly inside the smaller dashboard.
     */
    public double settingsDescriptionWidth() {
        double outerInset = theme.scale(11);
        double windowContentWidth = Math.max(theme.scale(240), windowWidth - outerInset * 2);
        double sidebarWidth = Math.min(theme.scale(174), Math.max(theme.scale(136), windowWidth * 0.235));
        double mainWidth = Math.max(theme.scale(230), windowContentWidth - sidebarWidth - theme.scale(10));
        double widthInPixels = Math.max(theme.scale(180), mainWidth - theme.scale(94));
        // WMultiLabel applies theme.scale(maxWidth) internally, so store this width
        // in theme units rather than passing it a physical-pixel measurement.
        double scaleFactor = theme.scale(1);
        return widthInPixels / (scaleFactor > 0.001 ? scaleFactor : 1);
    }

    public void updateViewport() {
        if (windowInitialized) {
            clampWindowToScreen();
            if (sidebar != null) sidebar.invalidate();
            if (mainPanel != null) mainPanel.updateViewport();
        }
        invalidate();
    }

    public void tickSettings() {
        if (mainPanel != null) mainPanel.tickSettings();
    }

    public List<Module> visibleModules(String query) {
        String filter = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Module> result = new ArrayList<>();

        Category category = null;
        if (!PAGE_ALL.equals(page) && !PAGE_ACTIVE.equals(page) && !PAGE_FAVORITES.equals(page)
            && !PAGE_EXPLOITS.equals(page)) {
            for (Category candidate : Modules.loopCategories()) {
                if (candidate.name.equals(page)) {
                    category = candidate;
                    break;
                }
            }
        }

        for (Module module : Modules.get().getAll()) {
            if (Config.get().hiddenModules.get().contains(module)) continue;
            if (PAGE_EXPLOITS.equals(page) && !isExploitModule(module.name)) continue;
            if (category != null && !module.category.equals(category)) continue;
            if (PAGE_ACTIVE.equals(page) && !module.isActive()) continue;
            if (PAGE_FAVORITES.equals(page) && !module.favorite) continue;
            if (!filter.isEmpty() && !matchesQuery(module, filter)) continue;
            result.add(module);
        }

        result.sort(Comparator.comparing(module -> module.title, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    /**
     * Existing Meteor modules that use packets or relaxed client-side interaction
     * checks. This is a curated shortcut page, not a promise that a remote server
     * will accept every action.
     */
    private static boolean isExploitModule(String name) {
        return switch (name) {
            case "anti-hunger", "fast-use", "ghost-hand", "reach", "liquid-interact",
                "no-mining-trace", "instant-rebreak", "no-fall", "no-slow",
                "packet-mine", "blink", "packet-canceller", "server-spoof",
                "anti-packet-kick" -> true;
            default -> false;
        };
    }

    private boolean matchesQuery(Module module, String filter) {
        if (module.title.toLowerCase(Locale.ROOT).contains(filter)
            || module.name.toLowerCase(Locale.ROOT).contains(filter)
            || module.description.toLowerCase(Locale.ROOT).contains(filter)) return true;

        for (var group : module.settings) {
            for (var setting : group) {
                if (setting.title.toLowerCase(Locale.ROOT).contains(filter)) return true;
            }
        }
        return false;
    }

    private void initializeWindow(double screenWidth, double screenHeight) {
        if (windowInitialized) return;

        double gutter = theme.scale(SCREEN_GUTTER);
        double maxWidth = Math.max(theme.scale(200), screenWidth - gutter * 2);
        double maxHeight = Math.max(theme.scale(180), screenHeight - gutter * 2);
        double minWidth = Math.min(theme.scale(620), maxWidth);
        double minHeight = Math.min(theme.scale(420), maxHeight);

        windowWidth = Double.isFinite(rememberedWidth)
            ? rememberedWidth : Math.min(theme.scale(1000), screenWidth * 0.86);
        windowHeight = Double.isFinite(rememberedHeight)
            ? rememberedHeight : Math.min(theme.scale(680), screenHeight * 0.82);

        windowWidth = clamp(windowWidth, minWidth, maxWidth);
        windowHeight = clamp(windowHeight, minHeight, maxHeight);

        windowX = Double.isFinite(rememberedX) ? rememberedX : (screenWidth - windowWidth) / 2;
        windowY = Double.isFinite(rememberedY) ? rememberedY : (screenHeight - windowHeight) / 2;

        windowInitialized = true;
        clampWindowToScreen();
        initializeParticles((int) screenWidth, (int) screenHeight);
    }

    private void clampWindowToScreen() {
        double screenWidth = Utils.getWindowWidth();
        double screenHeight = Utils.getWindowHeight();
        double gutter = Math.min(theme.scale(SCREEN_GUTTER), Math.min(screenWidth / 3, screenHeight / 3));
        double maxWidth = Math.max(theme.scale(200), screenWidth - gutter * 2);
        double maxHeight = Math.max(theme.scale(180), screenHeight - gutter * 2);
        double minWidth = Math.min(theme.scale(620), maxWidth);
        double minHeight = Math.min(theme.scale(420), maxHeight);

        windowWidth = clamp(windowWidth, minWidth, maxWidth);
        windowHeight = clamp(windowHeight, minHeight, maxHeight);
        windowX = clamp(windowX, gutter, Math.max(gutter, screenWidth - gutter - windowWidth));
        windowY = clamp(windowY, gutter, Math.max(gutter, screenHeight - gutter - windowHeight));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void rememberWindow() {
        rememberedX = windowX;
        rememberedY = windowY;
        rememberedWidth = windowWidth;
        rememberedHeight = windowHeight;
    }

    private boolean inResizeCorner(double mouseX, double mouseY) {
        double grip = theme.scale(30);
        return mouseX >= windowX + windowWidth - grip
            && mouseY >= windowY + windowHeight - grip
            && mouseX <= windowX + windowWidth
            && mouseY <= windowY + windowHeight;
    }

    private boolean inTitleBar(double mouseX, double mouseY) {
        return mouseX >= windowX + theme.scale(4)
            && mouseX <= windowX + windowWidth - theme.scale(4)
            && mouseY >= windowY + theme.scale(4)
            && mouseY <= windowY + theme.scale(TITLE_BAR_HEIGHT + 8);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (windowBar != null && windowBar.handleTabClick(click)) return true;

        if (click.button() == MOUSE_BUTTON_LEFT && inResizeCorner(click.x(), click.y())) {
            dragMode = DRAG_RESIZE;
            resizeStartMouseX = click.x();
            resizeStartMouseY = click.y();
            resizeStartWidth = windowWidth;
            resizeStartHeight = windowHeight;
            return true;
        }

        if (click.button() == MOUSE_BUTTON_LEFT && inTitleBar(click.x(), click.y())) {
            dragMode = DRAG_WINDOW;
            dragOffsetX = click.x() - windowX;
            dragOffsetY = click.y() - windowY;
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (dragMode != DRAG_NONE) {
            dragMode = DRAG_NONE;
            rememberWindow();
            invalidate();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
        if (dragMode == DRAG_WINDOW) {
            windowX = mouseX - dragOffsetX;
            windowY = mouseY - dragOffsetY;
            clampWindowToScreen();
            invalidate();
        } else if (dragMode == DRAG_RESIZE) {
            windowWidth = resizeStartWidth + mouseX - resizeStartMouseX;
            windowHeight = resizeStartHeight + mouseY - resizeStartMouseY;
            clampWindowToScreen();
            // Recompute text wrapping and the list viewport before the next layout pass.
            if (mainPanel != null) mainPanel.updateViewport();
            invalidate();
        }
    }

    @Override
    protected void onCalculateSize() {
        width = Utils.getWindowWidth();
        height = Utils.getWindowHeight();
        if (windowInitialized) clampWindowToScreen();
    }

    @Override
    protected void onCalculateWidgetPositions() {
        if (!windowInitialized) initializeWindow(Utils.getWindowWidth(), Utils.getWindowHeight());

        double outerInset = theme.scale(11);
        double gap = theme.scale(10);
        double titleBarHeight = theme.scale(TITLE_BAR_HEIGHT);
        double sidebarWidth = Math.min(theme.scale(174), Math.max(theme.scale(136), windowWidth * 0.235));
        double contentY = windowY + titleBarHeight + theme.scale(13);
        double contentHeight = Math.max(theme.scale(150), windowHeight - titleBarHeight - theme.scale(24));
        double contentX = windowX + outerInset;
        double contentWidth = Math.max(theme.scale(240), windowWidth - outerInset * 2);

        sidebar.x = contentX;
        sidebar.y = contentY;
        sidebar.width = sidebarWidth;
        sidebar.height = contentHeight;

        mainPanel.x = contentX + sidebarWidth + gap;
        mainPanel.y = contentY;
        mainPanel.width = Math.max(theme.scale(230), contentWidth - sidebarWidth - gap);
        mainPanel.height = contentHeight;

        windowBar.x = windowX + theme.scale(7);
        windowBar.y = windowY + theme.scale(6);
        windowBar.width = windowWidth - theme.scale(14);
        windowBar.height = titleBarHeight;

        double gripSize = theme.scale(30);
        resizeGrip.x = windowX + windowWidth - gripSize;
        resizeGrip.y = windowY + windowHeight - gripSize;
        resizeGrip.width = gripSize;
        resizeGrip.height = gripSize;

        mainPanel.updateViewport();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        initializeParticles((int) Math.max(1, width), (int) Math.max(1, height));

        // Deep midnight veil leaves the existing scene visible beneath the optional multi-pass blur.
        renderer.quad(0, 0, width, height,
            NoxaraStyle.BACKDROP_TOP_LEFT,
            NoxaraStyle.BACKDROP_TOP_RIGHT,
            NoxaraStyle.BACKDROP_BOTTOM_RIGHT,
            NoxaraStyle.BACKDROP_BOTTOM_LEFT);

        // A slow, broad violet nebula creates depth without competing with the content.
        renderer.quad(0, height * 0.06, width * 0.76, height * 0.70,
            NoxaraStyle.NEBULA_LEFT, NoxaraStyle.NEBULA_CLEAR,
            NoxaraStyle.NEBULA_CLEAR, NoxaraStyle.NEBULA_LOWER);
        renderer.quad(width * 0.43, height * 0.31, width * 0.57, height * 0.69,
            NoxaraStyle.NEBULA_CLEAR, NoxaraStyle.NEBULA_RIGHT,
            NoxaraStyle.NEBULA_RIGHT_CLEAR, NoxaraStyle.NEBULA_CLEAR);

        // Keep the top edge clean: no drifting particles are drawn over the GUI.

        NoxaraStyle.panel(renderer, windowX, windowY, windowWidth, windowHeight,
            theme.scale(WINDOW_RADIUS), NoxaraStyle.SHELL, true);
    }

    private void initializeParticles(int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return;

        if (particlesInitialized) {
            if (screenWidth != particleScreenWidth || screenHeight != particleScreenHeight) {
                double scaleX = (double) screenWidth / Math.max(1, particleScreenWidth);
                double scaleY = (double) screenHeight / Math.max(1, particleScreenHeight);
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    particleX[i] *= scaleX;
                    particleY[i] *= scaleY;
                }
            }
            particleScreenWidth = screenWidth;
            particleScreenHeight = screenHeight;
            return;
        }

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particleX[i] = particleRandom.nextDouble() * screenWidth;
            particleY[i] = particleRandom.nextDouble() * screenHeight;
            particleSpeed[i] = 0.35 + particleRandom.nextDouble() * 1.25;
            particleDrift[i] = (particleRandom.nextDouble() - 0.5) * 0.72;
            particleSize[i] = 1.0 + particleRandom.nextDouble() * 2.0;
            particlePhase[i] = particleRandom.nextDouble() * Math.PI * 2;
            particleTwinkleSpeed[i] = 0.012 + particleRandom.nextDouble() * 0.055;

            int coreAlpha = 64 + particleRandom.nextInt(110);
            particleCore[i] = (i % 5 == 0)
                ? new Color(206, 184, 255, coreAlpha)
                : new Color(231, 223, 255, coreAlpha);
            particleGlow[i] = new Color(140, 83, 255, 18 + particleRandom.nextInt(16));
        }

        particleScreenWidth = screenWidth;
        particleScreenHeight = screenHeight;
        particlesInitialized = true;
    }

    private void renderSnow(GuiRenderer renderer, double delta) {
        double frame = clamp(delta * 20, 0, 2.5);
        double w = Math.max(1, width);
        double h = Math.max(1, height);

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particleX[i] += particleDrift[i] * frame;
            particleY[i] += particleSpeed[i] * frame;
            particlePhase[i] += particleTwinkleSpeed[i] * frame;

            if (particleY[i] > h + theme.scale(8)) {
                particleY[i] = -theme.scale(8);
                particleX[i] = particleRandom.nextDouble() * w;
            }
            if (particleX[i] < -theme.scale(8)) particleX[i] = w + theme.scale(8);
            else if (particleX[i] > w + theme.scale(8)) particleX[i] = -theme.scale(8);

            double twinkle = (Math.sin(particlePhase[i]) + 1) * 0.5;
            Color glow = particleGlow[i];
            Color core = particleCore[i];
            glow.a = 7 + (int) (twinkle * 15);
            core.a = 44 + (int) (twinkle * 100);

            double size = theme.scale(particleSize[i]);
            double glowSize = size * 3.7;
            renderer.quad(particleX[i] - glowSize / 2, particleY[i] - glowSize / 2,
                glowSize, glowSize, GuiRenderer.CIRCLE, glow);
            renderer.quad(particleX[i] - size / 2, particleY[i] - size / 2,
                size, size, GuiRenderer.CIRCLE, core);

            if (i % 13 == 0 && twinkle > 0.72) {
                double ray = size * 2.8;
                renderer.quad(particleX[i] - ray / 2, particleY[i] - theme.scale(0.35),
                    ray, theme.scale(0.7), NoxaraStyle.STAR_RAY);
                renderer.quad(particleX[i] - theme.scale(0.35), particleY[i] - ray / 2,
                    theme.scale(0.7), ray, NoxaraStyle.STAR_RAY);
            }
        }
    }
}
