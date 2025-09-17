package net.swzo.brassworksmissions.missions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class PlayerMissionData {
    public static final int MISSION_SLOTS = 6;
    private final ActiveMission[] missions = new ActiveMission[MISSION_SLOTS];

    @Nullable
    public ActiveMission getMission(int slot) {
        if (slot < 0 || slot >= MISSION_SLOTS) {
            return null;
        }
        return missions[slot];
    }

    public void setMission(int slot, ActiveMission mission) {
        if (slot >= 0 && slot < MISSION_SLOTS) {
            missions[slot] = mission;
        }
    }

    public ActiveMission[] getMissions() {
        return missions;
    }

    public void clear() {
        for (int i = 0; i < MISSION_SLOTS; i++) {
            missions[i] = null;
        }
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag missionList = new ListTag();
        for (int i = 0; i < MISSION_SLOTS; i++) {
            CompoundTag missionTag = new CompoundTag();
            if (missions[i] != null) {
                missionTag.put("MissionData", missions[i].serializeNBT(provider));
            }
            missionList.add(missionTag);
        }
        nbt.put("Missions", missionList);
        return nbt;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ListTag missionList = nbt.getList("Missions", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(missionList.size(), MISSION_SLOTS); i++) {
            CompoundTag missionTag = missionList.getCompound(i);
            if (missionTag.contains("MissionData", Tag.TAG_COMPOUND)) {
                missions[i] = ActiveMission.deserializeNBT(provider, missionTag.getCompound("MissionData"));
            } else {
                missions[i] = null;
            }
        }
    }
}