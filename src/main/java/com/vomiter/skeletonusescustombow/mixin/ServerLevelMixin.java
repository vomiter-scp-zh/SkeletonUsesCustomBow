package com.vomiter.skeletonusescustombow.mixin;

import com.vomiter.skeletonusescustombow.core.SkeletonFakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void sucb$rewriteOwner(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Projectile projectile) {
            // 把 owner 從 FakePlayer 改回 skeleton
            if(projectile.getOwner() instanceof SkeletonFakePlayer fakePlayer){
                projectile.setOwner(fakePlayer.getSkeleton());
            }
        }
    }
}
