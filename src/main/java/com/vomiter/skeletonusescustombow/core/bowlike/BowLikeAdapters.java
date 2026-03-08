package com.vomiter.skeletonusescustombow.core.bowlike;

import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import com.vomiter.skeletonusescustombow.compat.AExpWrapper;
import com.vomiter.skeletonusescustombow.data.Tags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BowLikeAdapters {
    private static final List<BowLikeAdapter> ADAPTERS = new ArrayList<>();
    private static final Map<ResourceLocation, BowLikeAdapter> CACHE = new ConcurrentHashMap<>();
    private static final BowLikeAdapter MISS = new BowLikeAdapter() {
        @Override public boolean matches(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isRangedWeapon(ItemStack stack) { return false; }

        @Override
        public void release(ItemStack weapon, ServerLevel level, LivingEntity shooter, int useTicks) {
            var itemId = ForgeRegistries.ITEMS.getKey(weapon.getItem());
            SkeletonUsesCustomBow.LOGGER.error("BowLikeAdapters MISS: item={}, stack={}, shooter={}",
                    itemId, weapon, shooter.getName().getString());

            throw new IllegalStateException("MISS adapter should never be used");
        }
    };


    public static void register(BowLikeAdapter adapter) {
        ADAPTERS.add(adapter);
    }

    public static @Nullable BowLikeAdapter find(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.is(Tags.SKELETON_DO_NOT_USE)) return null;
        if (AExpWrapper.isAExpBow(stack)) return  null;
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
        BowLikeAdapter cached = CACHE.get(rl);
        if(cached == MISS) return null;
        if (cached != null) return cached;

        for (BowLikeAdapter a : ADAPTERS) {
            if (a.matches(stack)) {
                CACHE.put(rl, a);
                return a;
            }
        }
        CACHE.put(rl, MISS);
        return null;
    }

    public static boolean isBowLike(ItemStack stack) {
        BowLikeAdapter a = find(stack);
        return a != null && a.isRangedWeapon(stack);
    }

    public static void release(ItemStack weapon, ServerLevel level, LivingEntity shooter, int useTicks) {
        BowLikeAdapter a = find(weapon);
        if (a != null) a.release(weapon, level, shooter, useTicks);
    }
}

