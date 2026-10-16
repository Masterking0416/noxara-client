/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class Sprint extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum Mode {
        Strict,
        Rage
    }

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("sprint-mode")
        .description("What mode of sprinting.")
        .defaultValue(Mode.Strict)
        .build()
    );

    private final Setting<Boolean> keepSprint = sgGeneral.add(new BoolSetting.Builder()
        .name("keep-sprint")
        .description("Whether to keep sprinting after attacking.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> unsprintOnHit = sgGeneral.add(new BoolSetting.Builder()
        .name("unsprint-on-hit")
        .description("Whether to stop sprinting before attacking, to ensure you get crits and sweep attacks.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> unsprintInWater = sgGeneral.add(new BoolSetting.Builder()
        .name("unsprint-in-water")
        .description("Whether to stop sprinting when in water.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Rage)
        .build()
    );

    private final Setting<Boolean> permaSprint = sgGeneral.add(new BoolSetting.Builder()
        .name("sprint-while-stationary")
        .description("Sprint even when not moving.")
        .defaultValue(false)
        .visible(() -> mode.get() == Mode.Rage)
        .build()
    );

    // Wurst AutoSprint controls merged into this existing Sprint module.
    private final Setting<Boolean> allDirections = sgGeneral.add(new BoolSetting.Builder()
        .name("omnidirectional-sprint")
        .description("Allows sprinting while moving sideways or backwards, independently of the selected sprint mode.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> hungrySprint = sgGeneral.add(new BoolSetting.Builder()
        .name("hungry-sprint")
        .description("Allows sprinting when hunger is too low for vanilla sprinting.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> pauseWhileSneaking = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-while-sneaking")
        .description("Do not force sprinting while the sneak key is held.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> pauseWhileSwimming = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-while-swimming")
        .description("Do not force sprinting while swimming.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> pauseUnderwater = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-underwater")
        .description("Do not force sprinting underwater unless flying.")
        .defaultValue(true)
        .build()
    );

    // Wurst AutoSwim behavior integrated into the existing Meteor Sprint module.
    private final Setting<Boolean> autoSwim = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-swim")
        .description("Automatically sprint forward while swimming or underwater.")
        .defaultValue(false)
        .build()
    );

    public Sprint() {
        super(Categories.Movement, "sprint", "Automatically sprints.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onTickMovement(TickEvent.Post event) {
        if (shouldAutoSwimSprint()) {
            mc.player.setSprinting(true);
            return;
        }

        if (unsprintInWater.get() && mc.player.isInWater()) return;
        mc.player.setSprinting(shouldSprint());
    }

    /**
     * Wurst AutoSwim-inspired behavior: keep forward sprinting while swimming,
     * without forcing it during collision, sneaking, or flight.
     */
    private boolean shouldAutoSwimSprint() {
        if (!isActive() || !autoSwim.get()) return false;
        if (mc.player.horizontalCollision || mc.player.isShiftKeyDown()) return false;
        if (mc.player.getAbilities().flying || !mc.player.isInWater()) return false;
        if (!mc.player.isSwimming() && !mc.player.isUnderWater()) return false;
        return mc.player.zza > 0;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPacketSend(PacketEvent.Send event) {
        if (!unsprintOnHit.get()) return;
        if (!(event.packet instanceof ServerboundAttackPacket)) return;

        mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
        mc.player.setSprinting(false);
    }

    @EventHandler
    private void onPacketSent(PacketEvent.Sent event) {
        if (!unsprintOnHit.get() || !keepSprint.get()) return;
        if (!(event.packet instanceof ServerboundAttackPacket)) return;
        if (!shouldSprint() || mc.player.isSprinting()) return;

        mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, ServerboundPlayerCommandPacket.Action.START_SPRINTING));
        mc.player.setSprinting(true);
    }

    public boolean shouldSprint() {
        if (mc.gui.screen() != null && !Modules.get().get(GUIMove.class).sprint.get()) return false;
        if (pauseWhileSneaking.get() && mc.options.keyShift.isDown()) return false;
        if (pauseWhileSwimming.get() && mc.player.isSwimming()) return false;
        if (pauseUnderwater.get() && mc.player.isUnderWater() && !mc.player.getAbilities().flying) return false;

        float movement = (mode.get() == Mode.Rage || allDirections.get())
            ? (Math.abs(mc.player.zza) + Math.abs(mc.player.xxa))
            : mc.player.zza;

        if (movement <= (mc.player.isUnderWater() ? 1.0E-5F : 0.8)) {
            if (mode.get() == Mode.Strict || !permaSprint.get()) return false;
        }

        boolean strictSprint = !(mc.player.isInShallowWater())
            && !mc.player.isMobilityRestricted()
            && mc.player.isPassenger() ? (mc.player.getVehicle().canSprint() && mc.player.getVehicle().isLocalInstanceAuthoritative()) : (hungrySprint.get() || mc.player.getFoodData().hasEnoughFood())
                                                                                                                                         && (!mc.player.horizontalCollision || mc.player.minorHorizontalCollision);

        return isActive() && (mode.get() == Mode.Rage || strictSprint);
    }

    public boolean rageSprint() {
        return isActive() && mode.get() == Mode.Rage;
    }

    public boolean unsprintInWater() {
        return isActive() && unsprintInWater.get();
    }

    public boolean stopSprinting() {
        return !isActive() || !keepSprint.get();
    }
}
