package net.swzo.brassworksmissions.missions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.swzo.brassworksmissions.BrassworksmissionsMod;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissionManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type MISSION_LIST_TYPE = new TypeToken<List<Mission>>() {}.getType();
    private static final String DIRECTORY = "missions";
    private static final ResourceLocation REWARD_FILE_ID = ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "mission_reward");

    private List<Mission> availableMissions = new ArrayList<>();
    private double totalWeight = 0.0;

    public MissionManager() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<Mission> loadedMissions = new ArrayList<>();
        totalWeight = 0.0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            if (entry.getKey().equals(REWARD_FILE_ID)) {
                continue;
            }

            if (!entry.getKey().getNamespace().equals(BrassworksmissionsMod.MODID)) {
                continue;
            }
            try {

                List<Mission> missionsFromFile = GSON.fromJson(entry.getValue(), MISSION_LIST_TYPE);
                if (missionsFromFile != null) {
                    loadedMissions.addAll(missionsFromFile);
                }
            } catch (Exception e) {
                BrassworksmissionsMod.LOGGER.error("Could not parse mission file: " + entry.getKey(), e);
            }
        }

        availableMissions = loadedMissions;
        for (Mission mission : availableMissions) {
            totalWeight += mission.getWeight();
        }

        BrassworksmissionsMod.LOGGER.info("Loaded {} missions with a total weight of {}", availableMissions.size(), totalWeight);
    }

    @Nullable
    public Mission getWeightedRandomMission(RandomSource random) {
        if (totalWeight == 0 || availableMissions.isEmpty()) {
            return null;
        }

        double randomValue = random.nextDouble() * totalWeight;
        for (Mission mission : availableMissions) {
            randomValue -= mission.getWeight();
            if (randomValue <= 0) {
                return mission;
            }
        }

        return availableMissions.get(availableMissions.size() - 1);
    }
}