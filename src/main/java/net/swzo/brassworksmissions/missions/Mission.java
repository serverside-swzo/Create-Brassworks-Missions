package net.swzo.brassworksmissions.missions;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.swzo.brassworksmissions.BrassworksmissionsMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Mission {
    @SerializedName("id")
    private String id;
    @SerializedName("weight")
    private double weight;
    @SerializedName("titles")
    private List<String> titles;
    @SerializedName("requirement")
    private Requirement requirement;
    @SerializedName("reward")
    private Reward reward;

    public String getId() { return id; }
    public double getWeight() { return weight; }

    @Nullable
    public ActiveMission createInstance(RandomSource random) {

        if (id == null || id.isBlank()) {
            BrassworksmissionsMod.LOGGER.error("A mission in the JSON files is missing its 'id'. Skipping instance creation.");
            return null;
        }
        if (titles == null || titles.isEmpty()) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' has no titles defined. Skipping instance creation.", id);
            return null;
        }
        if (requirement == null) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' is missing the entire 'requirement' block. Skipping instance creation.", id);
            return null;
        }
        if (requirement.item == null) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' is missing the 'item' field in its requirement. Skipping instance creation.", id);
            return null;
        }
        if (reward == null) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' is missing the entire 'reward' block. Skipping instance creation.", id);
            return null;
        }

        String title = titles.get(random.nextInt(titles.size()));

        int reqBound = requirement.maxAmount - requirement.minAmount + 1;
        if (reqBound <= 0) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' has invalid requirement amounts (min > max). Skipping instance creation.", id);
            return null;
        }
        int reqAmount = requirement.minAmount + random.nextInt(reqBound);

        String selectedItemName = null;
        if (requirement.item instanceof String) {
            selectedItemName = (String) requirement.item;
        } else if (requirement.item instanceof List<?> list && !list.isEmpty()) {
            Object randomElement = list.get(random.nextInt(list.size()));
            if (randomElement instanceof String) {
                selectedItemName = (String) randomElement;
            }
        }

        ItemStack reqStack = Optional.ofNullable(selectedItemName)
                .map(ResourceLocation::tryParse)
                .map(BuiltInRegistries.ITEM::get)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
        if(!reqStack.isEmpty()) reqStack.setCount(1);

        if (reqStack.isEmpty() && selectedItemName != null) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' requires an invalid or unknown item: {}. Skipping instance creation.", id, selectedItemName);
            return null;
        }

        int rewardBound = reward.maxAmount - reward.minAmount + 1;
        if (rewardBound <= 0) {
            BrassworksmissionsMod.LOGGER.error("Mission '{}' has invalid reward amounts (min > max). Skipping instance creation.", id);
            return null;
        }
        int rewardAmount = reward.minAmount + random.nextInt(rewardBound);
        ItemStack rewardStack = BrassworksmissionsMod.getRewardManager().getRewardItem();
        if (rewardStack == null || rewardStack.isEmpty()) {
            BrassworksmissionsMod.LOGGER.error("The global Reward Manager provided a null or empty reward item. Aborting mission creation for '{}'.", id);
            return null;
        }
        rewardStack.setCount(rewardAmount);

        return new ActiveMission(id, title, reqStack, reqAmount, requirement.requirementType, rewardStack);
    }

    public static class Requirement {
        @SerializedName("requirementType")
        private String requirementType;
        @SerializedName("item")
        private Object item;
        @SerializedName("minAmount")
        private int minAmount;
        @SerializedName("maxAmount")
        private int maxAmount;
    }

    public static class Reward {
        @SerializedName("minAmount")
        private int minAmount;
        @SerializedName("maxAmount")
        private int maxAmount;
    }
}