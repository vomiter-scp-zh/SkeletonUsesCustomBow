package com.vomiter.skeletonusescustombow;

import net.minecraft.resources.ResourceLocation;

public class Helpers {
    public static ResourceLocation id(String namespace, String path){
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation id(String path){
        return id(SkeletonUsesCustomBow.MOD_ID, path);
    }
    public static ResourceLocation minecraftId(String path){
        return id("minecraft", path);
    }

}
