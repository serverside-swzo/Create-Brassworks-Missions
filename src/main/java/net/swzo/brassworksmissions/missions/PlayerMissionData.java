package net.swzo.brassworksmissions.missions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void setMission(int slot, @Nullable ActiveMission mission) {
        if (slot >= 0 && slot < MISSION_SLOTS) {
            missions[slot] = mission;
        }
    }

    public List<ActiveMission> getActiveMissions() {
        List<ActiveMission> activeMissions = new ArrayList<>();
        for (ActiveMission mission : missions) {
            if (mission != null) {
                activeMissions.add(mission);
            }
        }
        return activeMissions;
    }

    public ActiveMission[] getMissions() {
        return Arrays.copyOf(missions, missions.length);
    }

    public void clear() {
        Arrays.fill(missions, null);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag missionList = new ListTag();
        for (ActiveMission mission : missions) {
            CompoundTag missionTag = new CompoundTag();
            if (mission != null) {
                missionTag.put("MissionData", mission.serializeNBT(provider));
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