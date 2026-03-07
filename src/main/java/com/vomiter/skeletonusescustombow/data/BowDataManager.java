package com.vomiter.skeletonusescustombow.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BowDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "skeleton_bow";
    private static final int DEFAULT_PRIORITY = 1;
    private static final int DEFAULT_CHARGE_TIME = 20;
    private static final int DEFAULT_POST_SHOT_DELAY = 40;
    private static final int DEFAULT_POST_SHOT_DELAY_HARD_MODE = 20;
    private static final BowData DEFAULT = new BowData(DEFAULT_PRIORITY, DEFAULT_CHARGE_TIME, DEFAULT_POST_SHOT_DELAY, DEFAULT_POST_SHOT_DELAY_HARD_MODE);

    private static Map<ResourceLocation, BowData> ENTRIES = Map.of();
    public BowDataManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, BowData> temp = new HashMap<>();
        jsons.forEach((rl, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                Integer priority = obj.has("priority") ? obj.get("priority").getAsInt() : DEFAULT_PRIORITY;
                Integer chargeTime = obj.has("charge_time") ? obj.get("charge_time").getAsInt() : DEFAULT_CHARGE_TIME;
                Integer postShotDelay = obj.has("post_shot_delay") ? obj.get("post_shot_delay").getAsInt() : DEFAULT_POST_SHOT_DELAY;
                Integer postShotDelayHardMode = obj.has("post_shot_delay_hard_mode") ? obj.get("post_shot_delay_hard_mode").getAsInt() : DEFAULT_POST_SHOT_DELAY_HARD_MODE;
                var bowData = new BowData(priority, chargeTime, postShotDelay, postShotDelayHardMode);

                SkeletonUsesCustomBow.LOGGER.debug("Loaded Bow data of {}. priority = {}; charge time = {}", rl, priority, chargeTime);
                temp.put(rl, bowData);
            } catch (Exception ex) {
                SkeletonUsesCustomBow.LOGGER.error("Skeleton Bow Data Parsing Error at {}", rl);
            }
        });
        ENTRIES = Map.copyOf(temp);
    }

    public static int getPriority(ResourceLocation rl){
        return ENTRIES.getOrDefault(rl, DEFAULT).priority();
    }

    public static int getChargeTime(ResourceLocation rl){
        return ENTRIES.getOrDefault(rl, DEFAULT).chargeTime();
    }

    public static int getPostShotDelay(ResourceLocation rl){
        return ENTRIES.getOrDefault(rl, DEFAULT).postShotDelay();
    }

    public static int getPostShotDelayHardMode(ResourceLocation rl){
        return ENTRIES.getOrDefault(rl, DEFAULT).postShotDelayHardMode();
    }


    public record BowData(Integer priority, Integer chargeTime, Integer postShotDelay, Integer postShotDelayHardMode) {
    }
}
