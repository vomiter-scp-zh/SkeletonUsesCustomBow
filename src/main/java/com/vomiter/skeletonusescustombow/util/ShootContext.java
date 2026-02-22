package com.vomiter.skeletonusescustombow.util;

import net.minecraft.world.entity.monster.AbstractSkeleton;

public final class ShootContext {
    public static final ThreadLocal<AbstractSkeleton> OWNER = new ThreadLocal<>();
}
