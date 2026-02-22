package com.vomiter.skeletonusescustombow.core.bowlike;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;

public final class BowLikeHelper {
    public static ItemStack getBowLikeInHand(AbstractSkeleton skel) {
        ItemStack main = skel.getMainHandItem();
        if (BowLikeAdapters.isBowLike(main)) return main;
        ItemStack off = skel.getOffhandItem();
        if (BowLikeAdapters.isBowLike(off)) return off;
        return ItemStack.EMPTY;
    }

    public static InteractionHand getHand(AbstractSkeleton skeleton){
        ItemStack main = skeleton.getMainHandItem();
        if (BowLikeAdapters.isBowLike(main)) return InteractionHand.MAIN_HAND;
        ItemStack off = skeleton.getOffhandItem();
        if (BowLikeAdapters.isBowLike(off)) return InteractionHand.OFF_HAND;
        return null;
    }
}
