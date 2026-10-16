/*
 * Noxara Client - 3x3 mining module.
 * Inspired by Meteor's native packet-mining utilities and Wurst7 feature behavior.
 *
 * This file is part of the Noxara Client distribution and is licensed under GPL-3.0.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ThreeByThree extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> reach = sgGeneral.add(new DoubleSetting.Builder()
        .name("reach")
        .description("Maximum distance to attempt mining blocks in the 3x3 plane. The server may enforce a shorter reach.")
        .defaultValue(4.5)
        .min(1.0)
        .max(5.5)
        .sliderMin(1.0)
        .sliderMax(5.5)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Swing the held item when a 3x3 mining pass is attempted.")
        .defaultValue(true)
        .build()
    );

    private BlockPos lastTarget;

    public ThreeByThree() {
        super(Categories.World, "3x3", "Attempts to break a 3x3 plane around the block you are looking at.");
    }

    @Override
    public void onActivate() {
        lastTarget = null;
    }

    @Override
    public void onDeactivate() {
        lastTarget = null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null || mc.gameMode == null || mc.getConnection() == null) return;

        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            lastTarget = null;
            return;
        }

        BlockPos center = hit.getBlockPos().immutable();
        if (center.equals(lastTarget)) return;

        BlockState centerState = mc.level.getBlockState(center);
        if (centerState.isAir() || !BlockUtils.canBreak(center, centerState)) {
            lastTarget = center;
            return;
        }

        // Only issue one mining pass per targeted center block. This avoids flooding the server
        // with repeated START/STOP packets while the crosshair stays on the same target.
        lastTarget = center;
        List<BlockPos> plane = getPlane(center, hit.getDirection());
        boolean attempted = false;

        for (BlockPos pos : plane) {
            BlockState state = mc.level.getBlockState(pos);
            if (state.isAir() || !BlockUtils.canBreak(pos, state)) continue;

            Vec3 blockCenter = Vec3.atCenterOf(pos);
            if (!PlayerUtils.isWithin(blockCenter, reach.get())) continue;

            Direction side = BlockUtils.getDirection(pos);
            mc.gameMode.startPrediction(mc.level, sequence ->
                new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, side, sequence
                )
            );
            mc.gameMode.startPrediction(mc.level, sequence ->
                new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, side, sequence
                )
            );
            attempted = true;
        }

        if (attempted && swing.get()) {
            mc.player.swing(InteractionHand.MAIN_HAND, mc.player.getMainHandItem().getAttackAnimation(), false);
        }
    }

    private List<BlockPos> getPlane(BlockPos center, Direction face) {
        List<BlockPos> result = new ArrayList<>(9);

        for (int u = -1; u <= 1; u++) {
            for (int v = -1; v <= 1; v++) {
                BlockPos pos = switch (face.getAxis()) {
                    case Y -> center.offset(u, 0, v);
                    case X -> center.offset(0, u, v);
                    case Z -> center.offset(u, v, 0);
                };
                result.add(pos);
            }
        }

        return result;
    }
}
