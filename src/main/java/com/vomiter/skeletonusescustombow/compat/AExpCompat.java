package com.vomiter.skeletonusescustombow.compat;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.common.effects.ArcheryEffects;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;

public class AExpCompat {
    public static MobEffect getAExpQuickDrawEffect(){
        return ArcheryEffects.QUICKDRAW_EFFECT.get();
    }

    public static void applyEffects(ItemStack stack, AbstractSkeleton skeleton){
        if (stack.getItem() instanceof IBowProperties bow && bow.archeryexp$isSpecial()) {
            bow.archeryexp$getEffects().forEach(effect -> effect.apply(skeleton));
            bow.archeryexp$getParticles().forEach(particle -> particle.apply(skeleton));
        }
    }
}
