package net.swzo.brassworksmissions.missions;

import net.swzo.brassworksmissions.missions.types.create.*;
import net.swzo.brassworksmissions.missions.types.vanilla.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MissionRegistry {
    private static final Map<String, IMissionType> missionTypes = new HashMap<>();

    public static void registerMissionTypes() {

        register(new BreakBlockMissionType());
        register(new ReachExperienceLevelMissionType());
        register(new UseTotemMissionType());
        register(new FishItemMissionType());
        register(new CraftItemMissionType());
        register(new KillEntityMissionType());
        register(new BreedAnimalsMissionType());
        register(new ConsumeItemMissionType());
        register(new WalkMissionType());
        register(new DriveBoatMissionType());
        register(new FlyElytraMissionType());
        register(new RideMobMissionType());
        register(new EatCakeSliceMissionType());

        register(new MixMissionType());
        register(new CompactMissionType());
        register(new PressMissionType());
        register(new SawMissionType());
        register(new CrushMissionType());
        register(new MillMissionType());
        register(new CutMissionType());
        register(new MechanicalCraftMissionType());
        register(new DrillMissionType());
        register(new MoveOnBeltMissionType());
        register(new TravelByChainConveyorMissionType());
        register(new HarvesterMissionType());
    }

    private static void register(IMissionType type) {
        if (missionTypes.containsKey(type.getId())) {
            throw new IllegalArgumentException("Duplicate mission type registered: " + type.getId());
        }
        missionTypes.put(type.getId(), type);
    }

    @Nullable
    public static IMissionType getMissionType(String id) {
        return missionTypes.get(id);
    }
}