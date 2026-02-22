package com.vomiter.skeletonusescustombow.mixin.compat;

import com.github.L_Ender.cataclysm.items.Wrath_of_the_desert;
import com.vomiter.skeletonusescustombow.core.SkeletonFakePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Wrath_of_the_desert.class, remap = false)
public class WrathOfTheDesertMixin {
    @Inject(method = "getPlayerLookTarget", at = @At("HEAD"), cancellable = true)
    private void redirectSandStorm(Level level, LivingEntity living, CallbackInfoReturnable<Entity> cir){
        if(living instanceof SkeletonFakePlayer fakePlayer){
            cir.setReturnValue(fakePlayer.getSkeleton().getTarget());
        }
    }
}
