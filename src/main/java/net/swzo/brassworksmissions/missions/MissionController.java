package net.swzo.brassworksmissions.missions;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.swzo.brassworksmissions.BrassworksmissionsMod;
import net.swzo.brassworksmissions.init.CustomStats;
import net.swzo.brassworksmissions.network.BrassworksmissionsModVariables;

import javax.annotation.Nullable;
import java.util.Map;

public class MissionController {

    private static final Map<Integer, ResourceLocation> ADVANCEMENT_THRESHOLDS = Map.of(
            10, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_10"),
            50, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_50"),
            100, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_100"),
            250, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_250"),
            500, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_500"),
            1000, ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "missions_1000")
    );


    @Nullable
    public static PlayerMissionData getMissionData(Player player) {
        return player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).missionData;
    }

    @Nullable
    public static ActiveMission getMissionInSlot(Player player, int slot) {
        PlayerMissionData data = getMissionData(player);
        if (data == null || slot < 0 || slot >= PlayerMissionData.MISSION_SLOTS) {
            return null;
        }

        return data.getMission(slot);
    }

    public static void reassignMissions(ServerPlayer player) {
        PlayerMissionData data = getMissionData(player);
        if (data == null) return;

        data.clear();
        RandomSource random = player.getRandom();
        BrassworksmissionsModVariables.PlayerVariables playerVariables = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        playerVariables.reRollAmount = 1;
        playerVariables.syncPlayerVariables(player);

        for (int i = 0; i < PlayerMissionData.MISSION_SLOTS; i++) {
            Mission missionTemplate = BrassworksmissionsMod.getMissionManager().getWeightedRandomMission(random);
            if (missionTemplate != null) {
                data.setMission(i, missionTemplate.createInstance(random));
            }
        }
        playerVariables.trackedMissions.clear();
        playerVariables.syncPlayerVariables(player);
    }

    public static void rerollMission(ServerPlayer player, int slot) {
        if (isMissionCompleted(player, slot)) {
            player.sendSystemMessage(Component.translatable("gui.brassworksmissions.error.completed_mission")
                    .withStyle(ChatFormatting.RED));
            return;
        }
        BrassworksmissionsModVariables.PlayerVariables playerVariables = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        int rerollCost = playerVariables.reRollAmount * 2;
        int cappedCost = Math.min(rerollCost, 32);
        Item rerollitem = BrassworksmissionsMod.getRewardManager().getRewardItem().getItem();
        ItemStack rerollstack = new ItemStack(rerollitem);
        if (playerVariables.reRollAmount * 2 > 32) {
            player.sendSystemMessage(Component.translatable(
                    "gui.brassworksmissions.error.cost_cap_reached",
                    rerollstack.getHoverName().copy().append(Component.translatable("gui.brassworksmissions.ui.plural_format"))
            ).withStyle(ChatFormatting.RED));
            return;
        }
        int playerOwned = player.getInventory().countItem(rerollitem);
        if (playerOwned < cappedCost) {
            player.sendSystemMessage(Component.translatable(
                    "gui.brassworksmissions.error.not_enough",
                    cappedCost,
                    rerollstack.getHoverName().copy().append(Component.translatable("gui.brassworksmissions.ui.plural_format"))
            ).withStyle(ChatFormatting.RED));
            return;
        }
        player.getInventory().clearOrCountMatchingItems(
                stack -> stack.is(rerollitem), cappedCost, player.inventoryMenu.getCraftSlots()
        );
        player.inventoryMenu.broadcastChanges();
        player.sendSystemMessage(Component.translatable(
                "gui.brassworksmissions.success.rerolled",
                cappedCost,
                rerollstack.getHoverName().copy().append(Component.translatable("gui.brassworksmissions.ui.plural_format"))
        ).withStyle(ChatFormatting.GREEN));

        playerVariables.reRollAmount++;
        playerVariables.syncPlayerVariables(player);
        PlayerMissionData data = getMissionData(player);
        if (data == null || slot < 0 || slot >= PlayerMissionData.MISSION_SLOTS) {
            return;
        }
        RandomSource random = player.getRandom();
        Mission missionTemplate = BrassworksmissionsMod.getMissionManager().getWeightedRandomMission(random);
        if (missionTemplate != null) {
            data.setMission(slot, missionTemplate.createInstance(random));
            playerVariables.syncPlayerVariables(player);
        }
    }

    public static void forceRerollMission(ServerPlayer player, int slot) {
        PlayerMissionData data = getMissionData(player);
        if (data == null || slot < 0 || slot >= PlayerMissionData.MISSION_SLOTS) {
            return;
        }

        RandomSource random = player.getRandom();
        Mission missionTemplate = BrassworksmissionsMod.getMissionManager().getWeightedRandomMission(random);
        if (missionTemplate != null) {
            data.setMission(slot, missionTemplate.createInstance(random));

            player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(player);
        }
    }

    public static boolean isMissionCompleted(Player player, int slot) {
        ActiveMission mission = getMissionInSlot(player, slot);
        return mission != null && mission.isComplete();
    }

    public static void resetRerollCost(ServerPlayer player) {
        BrassworksmissionsModVariables.PlayerVariables playerVariables = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
        if (playerVariables.reRollAmount > 1) {
            playerVariables.reRollAmount = 1;
            playerVariables.syncPlayerVariables(player);
        }
    }

    public static boolean forceCompleteMission(ServerPlayer player, int slot) {
        ActiveMission mission = getMissionInSlot(player, slot);
        if (mission == null || mission.isComplete()) {
            return false;
        }

        int targetAmount = mission.getRequiredAmount();

        mission.setProgress(targetAmount);

        player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(player);
        return true;
    }

    private static boolean claimRewardInternal(ServerPlayer player, int slot) {
        ActiveMission mission = getMissionInSlot(player, slot);
        if (mission == null || !mission.isComplete() || mission.isClaimed()) {
            return false;
        }
        mission.setClaimed(true);
        ItemStack rewardStack = mission.getRewardItemStack().copy();
        boolean inserted = player.getInventory().add(rewardStack);
        if (!rewardStack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(
                    player.level(),
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    rewardStack
            );
            itemEntity.setNoPickUpDelay();
            player.level().addFreshEntity(itemEntity);
        }
        player.awardStat(CustomStats.MISSIONS_COMPLETED.get());
        checkAndGrantAdvancements(player);
        return true;
    }

    public static boolean claimReward(ServerPlayer player, int slot) {
        boolean success = claimRewardInternal(player, slot);
        if (success) {
            player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(player);
        }
        return success;
    }

    public static boolean claimAllRewards(ServerPlayer player) {
        boolean anyClaimed = false;
        for (int i = 0; i < PlayerMissionData.MISSION_SLOTS; i++) {
            if (claimRewardInternal(player, i)) {
                anyClaimed = true;
            }
        }

        if (anyClaimed) {
            player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES).syncPlayerVariables(player);
        }

        return anyClaimed;
    }

    public static int getTotalClaimableItemCount(Player player) {
        PlayerMissionData data = getMissionData(player);
        if (data == null) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < PlayerMissionData.MISSION_SLOTS; i++) {
            ActiveMission mission = data.getMission(i);
            if (mission != null && mission.isComplete() && !mission.isClaimed()) {
                count += mission.getRewardItemStack().getCount();
            }
        }
        return count;
    }

    private static void checkAndGrantAdvancements(ServerPlayer player) {
        int completedMissions = player.getStats().getValue(Stats.CUSTOM.get(CustomStats.MISSIONS_COMPLETED.get()));

        for (Map.Entry<Integer, ResourceLocation> entry : ADVANCEMENT_THRESHOLDS.entrySet()) {
            int requiredCount = entry.getKey();
            ResourceLocation advancementId = entry.getValue();

            if (completedMissions >= requiredCount) {
                AdvancementHolder advancement = player.server.getAdvancements().get(advancementId);
                if (advancement != null) {
                    // Award all criteria for the advancement to grant it.
                    // This is safe to call multiple times; it won't re-grant if already completed.
                    for (String criterion : advancement.value().criteria().keySet()) {
                        player.getAdvancements().award(advancement, criterion);
                    }
                }
            }
        }
    }
}