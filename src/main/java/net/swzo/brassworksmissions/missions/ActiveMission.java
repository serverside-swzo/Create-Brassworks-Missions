package net.swzo.brassworksmissions.missions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class ActiveMission {
    private String missionType;
    private String title;
    private ItemStack requirementItemStack;
    private int requiredAmount;
    private String requirementType;
    private ItemStack rewardItemStack;
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

    private ActiveMission() {}

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
        ActiveMission mission = new ActiveMission();
        mission.missionType = nbt.getString("missionType");
        mission.title = nbt.getString("title");
        if (nbt.contains("requirementItem")) {
            mission.requirementItemStack = ItemStack.parse(provider, nbt.getCompound("requirementItem")).orElse(ItemStack.EMPTY);
        } else {
            mission.requirementItemStack = ItemStack.EMPTY;
        }
        mission.requiredAmount = nbt.getInt("requiredAmount");

        if (nbt.contains("requirementType", Tag.TAG_STRING)) {
            mission.requirementType = nbt.getString("requirementType");
        } else if (nbt.contains("isItemRequirement")) {
            mission.requirementType = nbt.getBoolean("isItemRequirement") ? "item" : "block";
        }

        mission.rewardItemStack = ItemStack.parse(provider, nbt.getCompound("rewardItem")).orElse(ItemStack.EMPTY);
        mission.progress = nbt.getInt("progress");
        mission.claimed = nbt.getBoolean("claimed");
        return mission;
    }
}
