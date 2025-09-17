package net.swzo.brassworksmissions.missions.types.vanilla;

import net.minecraft.server.level.ServerPlayer;
import net.swzo.brassworksmissions.missions.ActiveMission;
import net.swzo.brassworksmissions.missions.IMissionType;
import net.swzo.brassworksmissions.network.BrassworksmissionsModVariables;

public abstract class AbstractDistanceMissionType implements IMissionType {

    public static void handleDistanceChange(ServerPlayer player, String missionId, int distance) {
        if (player == null || distance <= 0) {
            return;
        }

        boolean needsSync = false;
        var missions = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).missionData.getMissions();

        if (missions == null) {
            return;
        }

        for (ActiveMission mission : missions) {
            if (mission.getMissionType().equals(missionId) && !mission.isComplete()) {
                mission.incrementProgress(distance);
                needsSync = true;
            }
        }

        if (needsSync) {
            player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(player);
        }
    }
}