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

    private static Map<ResourceLocation, Integer> ENTRIES = Map.of();
    public BowDataManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, Integer> temp = new HashMap<>();
        jsons.forEach((rl, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();
                Integer priority = obj.get("priority").getAsInt();
                //SkeletonUsesCustomBow.LOGGER.info("{}:{}", rl, priority);
                temp.put(rl, priority);
            } catch (Exception ex) {
                SkeletonUsesCustomBow.LOGGER.error("Skeleton Bow Data Parsing Error at {}", rl);
            }
        });
        ENTRIES = Map.copyOf(temp);
    }

    public static int getPriority(ResourceLocation rl){
        return ENTRIES.getOrDefault(rl, 1);
    }
}
