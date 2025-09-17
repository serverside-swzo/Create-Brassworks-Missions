package net.swzo.brassworksmissions.missions.types.vanilla;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.swzo.brassworksmissions.missions.ActiveMission;
import net.swzo.brassworksmissions.missions.IMissionType;
import net.swzo.brassworksmissions.network.BrassworksmissionsModVariables;

public class EatCakeSliceMissionType implements IMissionType {
    public static final String ID = "brassworksmissions:eat_cake_slice";

    @Override
    public String getId() {
        return ID;
    }

    public static void handleSliceEaten(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        boolean needsSync = false;
        var missions = serverPlayer.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).missionData.getMissions();

        for (ActiveMission mission : missions) {
            if (mission.getMissionType().equals(ID) && !mission.isComplete()) {

                if (mission.getRequirementItemStack().is(Items.CAKE)) {
                    mission.incrementProgress(1);
                    needsSync = true;
                }
            }
        }

        if (needsSync) {
            serverPlayer.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(serverPlayer);
        }
    }
}