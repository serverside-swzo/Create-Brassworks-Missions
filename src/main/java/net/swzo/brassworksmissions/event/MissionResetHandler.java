package net.swzo.brassworksmissions.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.swzo.brassworksmissions.BrassworksmissionsMod;
import net.swzo.brassworksmissions.missions.MissionController;
import net.swzo.brassworksmissions.missions.PlayerMissionData;
import net.swzo.brassworksmissions.network.BrassworksmissionsModVariables;

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

@EventBusSubscriber(modid = BrassworksmissionsMod.MODID)
public class MissionResetHandler {

    public static long getMostRecentWeeklyResetTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime lastMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return lastMonday.toEpochSecond();
    }

    public static long getNextWeeklyResetTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextMonday = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return nextMonday.toEpochSecond();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            checkAndResetMissions(player);
        }
    }
    public static void checkAndResetMissions(ServerPlayer player) {
        BrassworksmissionsModVariables.PlayerVariables playerVariables = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        long currentResetTime = getMostRecentWeeklyResetTimestamp();
        long playerLastResetTime = playerVariables.lastWeeklyResetTime;

        if (playerVariables.missionData == null) {
            playerVariables.missionData = new PlayerMissionData();
            MissionController.reassignMissions(player);
            playerVariables.lastWeeklyResetTime = currentResetTime;
            playerVariables.syncPlayerVariables(player);
            return;
        }

        if (playerLastResetTime < currentResetTime) {
            BrassworksmissionsMod.LOGGER.info("Weekly missions reset for player {}.", player.getName().getString());
            MissionController.reassignMissions(player);
            playerVariables.lastWeeklyResetTime = currentResetTime;
            playerVariables.syncPlayerVariables(player);
        }
    }
}