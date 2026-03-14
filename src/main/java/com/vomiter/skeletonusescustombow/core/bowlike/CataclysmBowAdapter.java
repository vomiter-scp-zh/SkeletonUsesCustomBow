package com.vomiter.skeletonusescustombow.core.bowlike;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class CataclysmBowAdapter  implements BowLikeAdapter {
    static final String CATACLYSM = "cataclysm";
    static final String CURSED_BOW = "cursed_bow";
    static final String WRATH_OF_THE_DESERT = "wrath_of_the_desert";


    @Override
    public boolean matches(ItemStack stack) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(rl == null) return false;
        if(!CATACLYSM.equals(rl.getNamespace())) return false;

        if(CURSED_BOW.equals(rl.getPath())) return true;
        else return WRATH_OF_THE_DESERT.equals(rl.getPath());
    }
}
