/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class FastUse extends Module {
    public enum Mode {
        All,
        Some
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which items to fast use.")
        .defaultValue(Mode.All)
        .build()
    );

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Which items should fast place work on in \"Some\" mode.")
        .visible(() -> mode.get() == Mode.Some)
        .build()
    );

    private final Setting<Boolean> blocks = sgGeneral.add(new BoolSetting.Builder()
        .name("blocks")
        .description("Fast-places blocks if the mode is \"Some\" mode.")
        .visible(() -> mode.get() == Mode.Some)
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> cooldown = sgGeneral.add(new IntSetting.Builder()
        .name("cooldown")
        .description("Fast-use cooldown in ticks.")
        .defaultValue(0)
        .min(0)
        .sliderMax(4)
        .build()
    );

    // Wurst-inspired FastPlace behavior, merged into FastUse rather than creating a duplicate module.
    private final Setting<Boolean> fastPlace = sgGeneral.add(new BoolSetting.Builder()
        .name("fast-place")
        .description("Also clears Minecraft's right-click delay every tick to speed up block placement.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> fastPlaceBlocksOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("fast-place-blocks-only")
        .description("Only clear the right-click delay while holding a placeable block.")
        .defaultValue(true)
        .visible(fastPlace::get)
        .build()
    );

    public FastUse() {
        super(Categories.Player, "fast-use", "Allows you to use items at very high speeds.");
    }

    public int getItemUseCooldown(ItemStack itemStack) {
        if (mode.get() == Mode.All || shouldWorkSome(itemStack)) {
            return cooldown.get();
        }
        return 4; //default cooldown
    }

    public boolean shouldResetRightClickDelay() {
        if (!fastPlace.get()) return false;
        if (!fastPlaceBlocksOnly.get()) return true;
        return mc.player != null && mc.player.getMainHandItem().getItem() instanceof BlockItem;
    }

    private boolean shouldWorkSome(ItemStack itemStack) {
        return (blocks.get() && itemStack.getItem() instanceof BlockItem) || items.get().contains(itemStack.getItem());
    }
}
