package com.vomiter.skeletonusescustombow.data;

import com.google.gson.*;
import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BowDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "skeleton_bow";

    private static final int DEFAULT_PRIORITY = 1;
    private static final int DEFAULT_CHARGE_TIME = 20;
    private static final int DEFAULT_POST_SHOT_DELAY = 40;
    private static final int DEFAULT_POST_SHOT_DELAY_HARD_MODE = 20;
    private static final List<EntityType<?>> DEFAULT_ENTITY_TYPES = List.of(EntityType.SKELETON);
    private static final List<TagKey<EntityType<?>>> DEFAULT_ENTITY_TYPE_TAGS = List.of();


    /*
     * Work as intended:
     * If no datapack entry exists, chance = 0f.
     * This means custom bow selection is disabled unless explicitly configured.
     */
    private static final float DEFAULT_CHANCE = 0f;
    private static final float DEFAULT_DROP_CHANCE = 0.05f;
    private static final float DEFAULT_MIN_DIFFICULTY = 0f;
    private static final float DEFAULT_MAX_DIFFICULTY = Float.MAX_VALUE;

    private static final BowData DEFAULT = buildData(
            DEFAULT_PRIORITY,
            DEFAULT_CHARGE_TIME,
            DEFAULT_POST_SHOT_DELAY,
            DEFAULT_POST_SHOT_DELAY_HARD_MODE
    );

    private static Map<ResourceLocation, BowData> ENTRIES = Map.of();
    public static Map<ResourceLocation, BowData> getEntries() {
        return ENTRIES;
    }

    public BowDataManager() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(
            @NotNull Map<ResourceLocation, JsonElement> jsons,
            @NotNull ResourceManager manager,
            @NotNull ProfilerFiller profiler
    ) {
        Map<ResourceLocation, BowData> temp = new HashMap<>();

        jsons.forEach((rl, json) -> {
            try {
                JsonObject obj = json.getAsJsonObject();

                int priority = getInt(obj, "priority", DEFAULT_PRIORITY);
                int chargeTime = getInt(obj, "charge_time", DEFAULT_CHARGE_TIME);
                int postShotDelay = getInt(obj, "post_shot_delay", DEFAULT_POST_SHOT_DELAY);
                int postShotDelayHardMode = getInt(obj, "post_shot_delay_hard_mode", DEFAULT_POST_SHOT_DELAY_HARD_MODE);

                float chance = clamp01(getFloat(obj, "chance", DEFAULT_CHANCE));
                float dropChance = clamp01(getFloat(obj, "drop_chance", DEFAULT_DROP_CHANCE));
                float minDifficulty = sanitizeFloat(getFloat(obj, "min_difficulty", DEFAULT_MIN_DIFFICULTY), DEFAULT_MIN_DIFFICULTY);
                float maxDifficulty = sanitizeFloat(getFloat(obj, "max_difficulty", DEFAULT_MAX_DIFFICULTY), DEFAULT_MAX_DIFFICULTY);

                if (maxDifficulty < minDifficulty) {
                    SkeletonUsesCustomBow.LOGGER.warn(
                            "Skeleton Bow Data at {} has max_difficulty < min_difficulty. Swapping values. min = {}, max = {}",
                            rl,
                            minDifficulty,
                            maxDifficulty
                    );

                    float oldMin = minDifficulty;
                    minDifficulty = maxDifficulty;
                    maxDifficulty = oldMin;
                }

                List<EntityType<?>> entityTypes = new ArrayList<>();
                List<TagKey<EntityType<?>>> entityTypeTags = new ArrayList<>();
                JsonArray entityArray = obj.getAsJsonArray("entity_types");

                if (entityArray == null) {
                    entityTypes.addAll(DEFAULT_ENTITY_TYPES);
                } else {
                    for (JsonElement element : entityArray) {
                        if (!element.isJsonPrimitive()
                                || !element.getAsJsonPrimitive().isString()) {
                            SkeletonUsesCustomBow.LOGGER.warn(
                                    "Invalid entity_types entry {} in {}: expected string",
                                    element, rl
                            );
                            continue;
                        }

                        String value = element.getAsString();

                        if (value.startsWith("#")) {
                            ResourceLocation id = ResourceLocation.tryParse(value.substring(1));

                            if (id == null) {
                                SkeletonUsesCustomBow.LOGGER.warn(
                                        "Invalid entity type tag '{}' in {}",
                                        value, rl
                                );
                                continue;
                            }

                            entityTypeTags.add(
                                    TagKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id)
                            );
                        } else {
                            ResourceLocation id = ResourceLocation.tryParse(value);

                            if (id == null) {
                                SkeletonUsesCustomBow.LOGGER.warn(
                                        "Invalid entity type id '{}' in {}",
                                        value, rl
                                );
                                continue;
                            }

                            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                                SkeletonUsesCustomBow.LOGGER.warn(
                                        "Unknown entity type '{}' in {}",
                                        id, rl
                                );
                                continue;
                            }

                            entityTypes.add(BuiltInRegistries.ENTITY_TYPE.get(id));
                        }
                    }
                }

                BowData bowData = buildData(
                        priority,
                        chargeTime,
                        postShotDelay,
                        postShotDelayHardMode,
                        chance,
                        minDifficulty,
                        maxDifficulty,
                        dropChance,
                        entityTypes,
                        entityTypeTags
                );

                SkeletonUsesCustomBow.LOGGER.debug(
                        "Loaded Bow data of {}. priority = {}; charge_time = {}; post_shot_delay = {}; post_shot_delay_hard_mode = {}; chance = {}; difficulty = [{} ~ {}]; drop_chance = {}",
                        rl,
                        priority,
                        chargeTime,
                        postShotDelay,
                        postShotDelayHardMode,
                        chance,
                        minDifficulty,
                        maxDifficulty,
                        dropChance
                );

                temp.put(rl, bowData);
            } catch (Exception ex) {
                SkeletonUsesCustomBow.LOGGER.error("Skeleton Bow Data Parsing Error at {}", rl, ex);
            }
        });

        ENTRIES = Map.copyOf(temp);
    }

    public static int getPriority(ResourceLocation rl) {
        return getData(rl).priority();
    }

    public static int getChargeTime(ResourceLocation rl) {
        return getData(rl).chargeTime();
    }

    public static int getPostShotDelay(ResourceLocation rl) {
        return getData(rl).postShotDelay();
    }

    public static int getPostShotDelayHardMode(ResourceLocation rl) {
        return getData(rl).postShotDelayHardMode();
    }

    public static float getChance(ResourceLocation rl) {
        return getData(rl).chance();
    }

    public static float getMinDifficulty(ResourceLocation rl) {
        return getData(rl).minDifficulty();
    }

    public static float getMaxDifficulty(ResourceLocation rl) {
        return getData(rl).maxDifficulty();
    }

    public static BowData getData(ResourceLocation rl) {
        return ENTRIES.getOrDefault(rl, DEFAULT);
    }

    public static boolean hasData(ResourceLocation rl) {
        return ENTRIES.containsKey(rl);
    }

    public static boolean matchesDifficulty(ResourceLocation rl, float difficulty) {
        BowData data = getData(rl);
        return difficulty >= data.minDifficulty() && difficulty <= data.maxDifficulty();
    }

    public static boolean rollChance(ResourceLocation rl, Random random) {
        return random.nextFloat() < getChance(rl);
    }

    public static boolean canUse(ResourceLocation rl, float difficulty, Random random) {
        BowData data = getData(rl);

        if (difficulty < data.minDifficulty()) return false;
        if (difficulty > data.maxDifficulty()) return false;

        return random.nextFloat() < data.chance();
    }

    private static BowData buildData(
            int priority,
            int chargeTime,
            int postShotDelay,
            int postShotDelayHardMode){
        return new BowData(
                priority,
                chargeTime,
                postShotDelay,
                postShotDelayHardMode,
                DEFAULT_CHANCE,
                DEFAULT_MIN_DIFFICULTY,
                DEFAULT_MAX_DIFFICULTY,
                DEFAULT_DROP_CHANCE,
                DEFAULT_ENTITY_TYPES,
                DEFAULT_ENTITY_TYPE_TAGS
        );
    }

    private static BowData buildData(
            int priority,
            int chargeTime,
            int postShotDelay,
            int postShotDelayHardMode,
            float chance,
            float minDifficulty,
            float maxDifficulty,
            float dropChance,
            List<EntityType<?>> entityTypes, List<TagKey<EntityType<?>>> entityTypeTags) {
        return new BowData(
                priority,
                chargeTime,
                postShotDelay,
                postShotDelayHardMode,
                chance,
                minDifficulty,
                maxDifficulty,
                dropChance,
                entityTypes,
                entityTypeTags
        );
    }

    private static int getInt(JsonObject obj, String key, int fallback) {
        return obj.has(key) ? obj.get(key).getAsInt() : fallback;
    }

    private static float getFloat(JsonObject obj, String key, float fallback) {
        return obj.has(key) ? obj.get(key).getAsFloat() : fallback;
    }

    private static float clamp01(float value) {
        if (Float.isNaN(value)) return DEFAULT_CHANCE;
        if (value < 0f) return 0f;
        if (value > 1f) return 1f;
        return value;
    }

    private static float sanitizeFloat(float value, float fallback) {
        if (Float.isNaN(value)) return fallback;
        return value;
    }

    public record BowData(
            int priority,
            int chargeTime,
            int postShotDelay,
            int postShotDelayHardMode,
            float chance,
            float minDifficulty,
            float maxDifficulty,
            float dropChance,
            List<EntityType<?>> entityTypes,
            List<TagKey<EntityType<?>>> entityTypeTags
    ) {
    }
}