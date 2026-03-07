package com.vomiter.skeletonusescustombow.mixin.compat;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.infernalstudios.archeryexp.client.renderer.MaterialArrowRenderer;
import org.infernalstudios.archeryexp.common.entities.ArcheryEntityTypes;
import org.infernalstudios.archeryexp.common.items.ArcheryItems;
import org.infernalstudios.archeryexp.util.BowUtil;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.infernalstudios.archeryexp.ArcheryExpansionForgeClient$ClientEvents", remap = false)
public abstract class ArcheryExpansionClientEventsMixin {

    @Inject(method = "onClientSetup", at = @At("HEAD"), cancellable = true)
    private static void sucb$enqueueClientSetup(FMLClientSetupEvent event, CallbackInfo ci) {
        ci.cancel();

        event.enqueueWork(() -> {
            EntityRenderers.register(ArcheryEntityTypes.IRON_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.GOLD_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.DIAMOND_ARROW.get(), MaterialArrowRenderer::new);
            EntityRenderers.register(ArcheryEntityTypes.NETHERITE_ARROW.get(), MaterialArrowRenderer::new);

            ArcheryItems.BOWS.forEach(supplier -> {
                Item item = supplier.get();

                ItemProperties.register(item, new ResourceLocation("drawing"),
                        (stack, world, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
                );

                ItemProperties.register(item, new ResourceLocation("draw"),
                        (stack, world, entity, seed) -> {
                            if (entity != null && entity.getUseItem() == stack) {
                                IBowProperties properties = (IBowProperties) stack.getItem();
                                return BowUtil.getPowerForDrawTime(
                                        stack.getUseDuration() - entity.getUseItemRemainingTicks(),
                                        properties
                                );
                            }
                            return 0.0F;
                        }
                );
            });
        });
    }
}