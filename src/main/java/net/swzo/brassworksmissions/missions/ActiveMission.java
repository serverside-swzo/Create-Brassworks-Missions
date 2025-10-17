package net.swzo.brassworksmissions.missions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ActiveMission {
    private final String missionType;
    private final String title;
    private final ItemStack requirementItemStack;
    private final int requiredAmount;
    private final String requirementType;
    private final ItemStack rewardItemStack;
    private int progress;
    private boolean claimed;

    public ActiveMission(String missionType, String title, ItemStack requirementItemStack, int requiredAmount, String requirementType, ItemStack rewardItemStack) {
        this.missionType = missionType;
        this.title = title;
        this.requirementItemStack = requirementItemStack;
        this.requiredAmount = requiredAmount;
        this.requirementType = requirementType;
        this.rewardItemStack = rewardItemStack;
        this.progress = 0;
        this.claimed = false;
    }

    private ActiveMission() {
        this.missionType = "";
        this.title = "";
        this.requirementItemStack = ItemStack.EMPTY;
        this.requiredAmount = 0;
        this.requirementType = "";
        this.rewardItemStack = ItemStack.EMPTY;
    }

    public String getTitle() { return title; }
    public int getProgress() { return progress; }
    public ItemStack getRequirementItemStack() { return requirementItemStack.copy(); }
    public ItemStack getRewardItemStack() { return rewardItemStack.copy(); }
    public int getRequiredAmount() { return requiredAmount; }
    public String getMissionType() { return missionType; }
    public boolean isComplete() { return progress >= requiredAmount; }
    public boolean isClaimed() { return claimed; }
    public String getRequirementType() { return requirementType; }

    public boolean isRequirementType(String type) {
        return this.requirementType != null && this.requirementType.equalsIgnoreCase(type);
    }

    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    public void incrementProgress(int amount) {
        if (!isComplete()) {
            this.progress = Math.min(this.progress + amount, requiredAmount);
        }
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, requiredAmount);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("missionType", missionType);
        nbt.putString("title", title);
        if (requirementItemStack != null && !requirementItemStack.isEmpty()) {
            nbt.put("requirementItem", requirementItemStack.save(provider));
        }
        nbt.putInt("requiredAmount", requiredAmount);
        if (requirementType != null) {
            nbt.putString("requirementType", requirementType);
        }
        nbt.put("rewardItem", rewardItemStack.save(provider));
        nbt.putInt("progress", progress);
        nbt.putBoolean("claimed", claimed);
        return nbt;
    }

    public static ActiveMission deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ActiveMission mission = new ActiveMission(
            nbt.getString("missionType"),
            nbt.getString("title"),
            ItemStack.parse(provider, nbt.getCompound("requirementItem")).orElse(ItemStack.EMPTY),
            nbt.getInt("requiredAmount"),
            getRequirementTypeFromNBT(nbt),
            ItemStack.parse(provider, nbt.getCompound("rewardItem")).orElse(ItemStack.EMPTY)
        );

        mission.progress = nbt.getInt("progress");
        mission.claimed = nbt.getBoolean("claimed");
        return mission;
    }

    private static String getRequirementTypeFromNBT(CompoundTag nbt) {
        if (nbt.contains("requirementType", Tag.TAG_STRING)) {
            return nbt.getString("requirementType");
        }
        if (nbt.contains("isItemRequirement")) {
            return nbt.getBoolean("isItemRequirement") ? "item" : "block";
        }
        return "";
    }
}
