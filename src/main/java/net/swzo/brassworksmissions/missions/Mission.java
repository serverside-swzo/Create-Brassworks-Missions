package net.swzo.brassworksmissions.missions;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.swzo.brassworksmissions.BrassworksmissionsMod;

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

    public ActiveMission createInstance(RandomSource random) {

        String title = titles.get(random.nextInt(titles.size()));

        int reqAmount = requirement.minAmount + random.nextInt(requirement.maxAmount - requirement.minAmount + 1);

        String selectedItemName = null;
        if (requirement.item instanceof String) {
            selectedItemName = (String) requirement.item;
        } else if (requirement.item instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> items = (List<String>) requirement.item;
            if (!items.isEmpty()) {
                selectedItemName = items.get(random.nextInt(items.size()));
            }
        }

        ItemStack reqStack = Optional.ofNullable(selectedItemName)
                .map(ResourceLocation::parse)
                .map(BuiltInRegistries.ITEM::get)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
        if(!reqStack.isEmpty()) reqStack.setCount(1);

        int rewardAmount = reward.minAmount + random.nextInt(reward.maxAmount - reward.minAmount + 1);
        ItemStack rewardStack = BrassworksmissionsMod.getRewardManager().getRewardItem();
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