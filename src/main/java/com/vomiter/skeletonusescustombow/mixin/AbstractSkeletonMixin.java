package com.vomiter.skeletonusescustombow.mixin;

import com.vomiter.skeletonusescustombow.compat.AExpWrapper;
import com.vomiter.skeletonusescustombow.core.IFakePlayerHolder;
import com.vomiter.skeletonusescustombow.core.SkeletonFakePlayer;
import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeAdapters;
import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeHelper;
import com.vomiter.skeletonusescustombow.data.BowDataManager;
import com.vomiter.skeletonusescustombow.util.ShootContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends Monster implements RangedAttackMob, IFakePlayerHolder {
    @Shadow
    @Final
    private MeleeAttackGoal meleeGoal;
    @Shadow
    @Final
    private RangedBowAttackGoal<AbstractSkeleton> bowGoal;
    @Unique
    private SkeletonFakePlayer sucb$player = null;

    /*
    Tech note: 有兩套計時器，一套是射擊後多久可以再次開始使用弓箭(由min interval決定)
    另一個則是使用時間累積到多少就可以射出弓箭，這個被寫死成20
    然後min interval根據難度不同，可能傳入20或40
    也就是整體來說骷髏的射速可能是 1/40tick 或 1/60tick 。
     */
    //TODO: 讓人可以用 datapack 決定 attack interval 和 using time

    @Unique
    @Override
    public SkeletonFakePlayer sucb$getPlayer(){
        return sucb$player;
    }

    protected AbstractSkeletonMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Inject(method = "performRangedAttack", at = @At("HEAD"), cancellable = true)
    private void fakeRelease(LivingEntity livingEntity, float p_32142_, CallbackInfo ci){
        if(!(livingEntity.level() instanceof ServerLevel serverLevel)) return;
        ItemStack weapon = BowLikeHelper.getBowLikeInHand((AbstractSkeleton)(Object)this);
        if (weapon.isEmpty()) return;
        if (weapon.is(Items.BOW)) return;
        if (AExpWrapper.isAExpBow(weapon)) return;

        if (sucb$player == null) sucb$player = SkeletonFakePlayer.createFakePlayer((AbstractSkeleton)(Object)this);

        try {
            ShootContext.OWNER.set((AbstractSkeleton)(Object)this);
            sucb$player.readyToShoot();
            var weaponId = ForgeRegistries.ITEMS.getKey(weapon.getItem());
            BowLikeAdapters.release(weapon, serverLevel, sucb$player, BowDataManager.getChargeTime(weaponId)); // useTicks
            AExpWrapper.applyEffects(weapon, (AbstractSkeleton)(Object)this);

        } finally {
            ShootContext.OWNER.remove();
        }
        ci.cancel();
    }

    @Inject(method = "reassessWeaponGoal", at = @At("RETURN"))
    private void useOtherBow(CallbackInfo ci){
        if (this.level() != null && !this.level().isClientSide) {
            boolean shouldUseBow = !BowLikeHelper.getBowLikeInHand((AbstractSkeleton) (Object)this).isEmpty();
            ItemStack weapon = BowLikeHelper.getBowLikeInHand((AbstractSkeleton)(Object)this);
            var weaponId = ForgeRegistries.ITEMS.getKey(weapon.getItem());
            if(shouldUseBow){
                this.goalSelector.removeGoal(meleeGoal);
                int i = this.level().getDifficulty() != Difficulty.HARD? BowDataManager.getPostShotDelay(weaponId): BowDataManager.getPostShotDelayHardMode(weaponId);
                bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.bowGoal);
            }
        }
    }

    @Inject(method = "canFireProjectileWeapon", at = @At("RETURN"), cancellable = true)
    private void canFireOtherBow(ProjectileWeaponItem projectileWeaponItem, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) return;
        cir.setReturnValue(projectileWeaponItem instanceof BowItem);
    }


}
