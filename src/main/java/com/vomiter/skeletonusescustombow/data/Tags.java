package com.vomiter.skeletonusescustombow.data;

import com.vomiter.skeletonusescustombow.Helpers;
import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

public class Tags {
    private static TagKey<Item> create(String path){
        return TagKey.create(
                BuiltInRegistries.ITEM.key(),
                Helpers.id(SkeletonUsesCustomBow.MOD_ID, path)
        );
    }

    public static TagKey<Item> SKELETON_DO_NOT_USE = create("skeleton_do_not_use");
}
