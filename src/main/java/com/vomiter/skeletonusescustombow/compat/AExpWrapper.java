package com.vomiter.skeletonusescustombow.compat;

import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class AExpWrapper {
    public static void applyEffects(ItemStack stack, AbstractSkeleton skeleton){
        if(ModList.get().isLoaded("archeryexp")); //AExpCompat.applyEffects(stack, skeleton);
    }


    public static boolean isAExpBow(ItemStack stack){
        if(ModList.get().isLoaded("archeryexp")); //return AExpCompat.isAExpBow(stack);
        return false;
    }
}
