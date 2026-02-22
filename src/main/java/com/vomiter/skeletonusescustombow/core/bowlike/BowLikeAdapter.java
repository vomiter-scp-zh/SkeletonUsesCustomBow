package com.vomiter.skeletonusescustombow.core.bowlike;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface BowLikeAdapter {
    /** 這個 adapter 是否處理該 stack（可用 item id / class / tag / 其他特徵判定） */
    boolean matches(ItemStack stack);

    /** 骷髏能不能用它當 ranged weapon（供 reassessWeaponGoal / canFireProjectileWeapon 使用） */
    default boolean isRangedWeapon(ItemStack stack) { return true; }

    /** 供 performRangedAttack 注入：實際觸發射擊 */
    default void release(ItemStack weapon, ServerLevel level, LivingEntity shooter, int useTicks){
        weapon.getItem().releaseUsing(weapon, level, shooter, weapon.getUseDuration() - useTicks);
    };
}

