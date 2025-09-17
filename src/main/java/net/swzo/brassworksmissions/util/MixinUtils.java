package net.swzo.brassworksmissions.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.swzo.brassworksmissions.missions.ActiveMission;
import net.swzo.brassworksmissions.missions.IMissionType;
import net.swzo.brassworksmissions.missions.MissionRegistry;
import net.swzo.brassworksmissions.missions.types.create.CreateMissionType;
import net.swzo.brassworksmissions.network.BrassworksmissionsModVariables;

public class MixinUtils {

    public static <T extends CreateMissionType<?>> void handleMixinMissionItem(BlockEntity be, Class<T> missionClass, ItemStack output) {
        Level level = be.getLevel();
        if (level == null || level.isClientSide() || output.isEmpty()) {
            return;
        }

        BlockPos worldPosition = be.getBlockPos();
        Player player = getClosestPlayer(level, worldPosition);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var missionsCapability = serverPlayer.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        var missions = missionsCapability.missionData.getMissions();
        boolean needsSync = false;

        for (ActiveMission mission : missions) {
            IMissionType type = MissionRegistry.getMissionType(mission.getMissionType());

            if (!mission.isComplete() && missionClass.isInstance(type)) {
                T missionType = missionClass.cast(type);
                if (missionType.check(output, mission)) {
                    needsSync = true;
                }
            }
        }

        if (needsSync) {
            missionsCapability.syncPlayerVariables(serverPlayer);
        }
    }

    public static <T extends CreateMissionType<?>> void handlePlayerMissionIncrement(Player player, Class<T> missionClass, ItemStack itemStack) {
        if (player == null || player.level().isClientSide() || itemStack.isEmpty()) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var missionsCapability = serverPlayer.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        var missions = missionsCapability.missionData.getMissions();
        boolean needsSync = false;

        for (ActiveMission mission : missions) {
            IMissionType type = MissionRegistry.getMissionType(mission.getMissionType());

            if (!mission.isComplete() && missionClass.isInstance(type)) {
                T missionType = missionClass.cast(type);
                if (missionType.check(itemStack, mission)) {
                    needsSync = true;
                }
            }
        }

        if (needsSync) {
            missionsCapability.syncPlayerVariables(serverPlayer);
        }
    }

    public static Player getClosestPlayer(Level level, BlockPos worldPosition) {
        if (level == null) return null;
        return level.getNearestPlayer(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                128,
                false);
    }
}