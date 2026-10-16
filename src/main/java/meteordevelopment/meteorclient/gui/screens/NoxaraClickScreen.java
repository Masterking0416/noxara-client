/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.noxara.NoxaraDashboard;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import net.minecraft.nbt.CompoundTag;

public final class NoxaraClickScreen extends TabScreen {
    private NoxaraDashboard dashboard;

    public NoxaraClickScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);
    }

    @Override
    public void initWidgets() {
        dashboard = addDirect(new NoxaraDashboard()).widget();
    }

    @Override
    public void tick() {
        super.tick();
        if (dashboard != null) dashboard.tickSettings();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (dashboard != null) dashboard.updateViewport();
    }

    @Override
    public boolean toClipboard() {
        return NbtUtils.toClipboard(Modules.get());
    }

    @Override
    public boolean fromClipboard() {
        CompoundTag tag = NbtUtils.fromClipboard();
        if (tag == null) return false;

        Modules.get().fromTag(tag);
        invalidate();
        return true;
    }
}
