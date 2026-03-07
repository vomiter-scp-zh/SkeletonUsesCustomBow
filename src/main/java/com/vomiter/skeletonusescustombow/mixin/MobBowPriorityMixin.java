package com.vomiter.skeletonusescustombow.mixin;

import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeAdapters;
import com.vomiter.skeletonusescustombow.data.BowDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobBowPriorityMixin extends LivingEntity {
    protected MobBowPriorityMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "canReplaceCurrentItem", at = @At("HEAD"), cancellable = true)
    private void preventTakeSword(ItemStack picked, ItemStack holding, CallbackInfoReturnable<Boolean> cir){
        if(!((Object)this instanceof AbstractSkeleton)) return;
        if(!(picked.getItem() instanceof SwordItem)) return;
        if(BowLikeAdapters.isBowLike(holding)){
            ResourceLocation rl = ForgeRegistries.ITEMS.getKey(holding.getItem());
            if(BowDataManager.getPriority(rl) >= 3) cir.setReturnValue(false);
        }
    }

    @Inject(method = "canReplaceCurrentItem", at = @At("TAIL"), cancellable = true)
    private void replaceBowLike(ItemStack picked, ItemStack holding, CallbackInfoReturnable<Boolean> cir){
        if(!((Object)this instanceof AbstractSkeleton)) return;
        if(cir.getReturnValue()) return;
        Item item1 = holding.getItem();
        Item item2 = picked.getItem();
        boolean isVanillaBow1 = item1 instanceof BowItem;
        boolean isVanillaBow2 = item2 instanceof BowItem;
        if(isVanillaBow1 && isVanillaBow2) return;
        if(!isVanillaBow1 && !isVanillaBow2) return;
        boolean isBowLike1 = BowLikeAdapters.isBowLike(holding);
        boolean isBowLike2 = BowLikeAdapters.isBowLike(picked);
        if(!isBowLike1 && !isBowLike2) return;
        ResourceLocation rl1 = ForgeRegistries.ITEMS.getKey(item1);
        ResourceLocation rl2 = ForgeRegistries.ITEMS.getKey(item2);
        if(BowDataManager.getPriority(rl2) > BowDataManager.getPriority(rl1)) cir.setReturnValue(true);
    }

    @Inject(method = "canReplaceEqualItem", at = @At("HEAD"), cancellable = true)
    private void checkBowPriority(ItemStack picked, ItemStack holding, CallbackInfoReturnable<Boolean> cir){
        if(!((Object)this instanceof AbstractSkeleton)) return;
        Item item1 = holding.getItem();
        Item item2 = picked.getItem();
        ResourceLocation rl1 = ForgeRegistries.ITEMS.getKey(item1);
        ResourceLocation rl2 = ForgeRegistries.ITEMS.getKey(item2);
        //SkeletonUsesCustomBow.LOGGER.info("{}, {}", rl1, rl2);
        if(BowDataManager.getPriority(rl2) > BowDataManager.getPriority(rl1)) cir.setReturnValue(true);
    }
}
