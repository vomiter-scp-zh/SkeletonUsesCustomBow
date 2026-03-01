package com.vomiter.skeletonusescustombow.data;

import com.vomiter.skeletonusescustombow.Helpers;
import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Tags {
    private static TagKey<Item> create(String path){
        return TagKey.create(
                ForgeRegistries.ITEMS.getRegistryKey(),
                Helpers.id(SkeletonUsesCustomBow.MOD_ID, path)
        );
    }

    public static TagKey<Item> SKELETON_DO_NOT_USE = create("skeleton_do_not_use");
}
