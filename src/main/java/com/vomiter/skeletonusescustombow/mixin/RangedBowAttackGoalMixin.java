package com.vomiter.skeletonusescustombow.mixin;

import com.vomiter.skeletonusescustombow.SkeletonUsesCustomBow;
import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeHelper;
import com.vomiter.skeletonusescustombow.data.BowDataManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RangedBowAttackGoal.class)
public class RangedBowAttackGoalMixin {
    @Shadow
    @Final
    private Mob mob;

    @Shadow
    private int attackTime;

    @Shadow
    private int attackIntervalMin;

    @Inject(method = "isHoldingBow", at = @At("HEAD"), cancellable = true)
    private void isHoldingOtherProjectileWeapon(CallbackInfoReturnable<Boolean> cir){
        if(mob instanceof AbstractSkeleton skeleton){
            if(!BowLikeHelper.getBowLikeInHand(skeleton).isEmpty()) cir.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;startUsingItem(Lnet/minecraft/world/InteractionHand;)V", shift = At.Shift.AFTER))
    private void startUseOtherWeapon(CallbackInfo ci){
        if(mob.isUsingItem()) return;
        if(mob instanceof AbstractSkeleton skeleton) {
            var hand = BowLikeHelper.getHand(skeleton);
            if(hand != null) skeleton.startUsingItem(hand);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getTicksUsingItem()I"), cancellable = true)
    private void overtakingShoot(CallbackInfo ci){
        if(!(mob instanceof AbstractSkeleton skeleton)) return; //only for abstract skeletons
        var weapon = BowLikeHelper.getBowLikeInHand(skeleton);
        if(weapon.isEmpty()) return; //only for our bow like
        var weaponId = ForgeRegistries.ITEMS.getKey(weapon.getItem());
        int chargeTime = BowDataManager.getChargeTime(weaponId);
        int actualUseTicks = mob.getTicksUsingItem();
        if(actualUseTicks >= chargeTime && chargeTime >= 20) return; //shoot naturally
        if(actualUseTicks >= chargeTime){
            mob.stopUsingItem();
            assert mob.getTarget() != null;
            skeleton.performRangedAttack(mob.getTarget(), BowDataManager.getChargeTime(weaponId));
            attackTime = attackIntervalMin;
        }
        ci.cancel();
    }
}
