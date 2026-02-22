package com.vomiter.skeletonusescustombow.mixin;

import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
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
}
