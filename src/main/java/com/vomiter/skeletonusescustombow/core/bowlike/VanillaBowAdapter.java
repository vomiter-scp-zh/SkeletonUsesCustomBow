package com.vomiter.skeletonusescustombow.core.bowlike;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class VanillaBowAdapter implements BowLikeAdapter {
    @Override
    public boolean matches(ItemStack stack) {
        return stack.getItem() instanceof BowItem && !stack.is(Items.BOW); // 排除 vanilla bow
    }

    @Override
    public void release(ItemStack weapon, ServerLevel level, LivingEntity shooter, int useTicks) {
        BowItem bow = (BowItem) weapon.getItem();
        bow.releaseUsing(weapon, level, shooter, bow.getUseDuration(weapon) - useTicks);
    }
}
