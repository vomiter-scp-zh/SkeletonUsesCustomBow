package com.vomiter.skeletonusescustombow.core.bowlike;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public final class AlexCavesDreadbowAdapter implements BowLikeAdapter {
    private static final ResourceLocation DREADBOW = ResourceLocation.fromNamespaceAndPath("alexscaves", "dreadbow");

    @Override
    public boolean matches(ItemStack stack) {
        return DREADBOW.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    @Override
    public void release(ItemStack weapon, ServerLevel level, LivingEntity shooter, int useTicks) {
        weapon.getItem().releaseUsing(weapon, level, shooter, weapon.getUseDuration(shooter) - useTicks);
    }
}
