package com.vomiter.skeletonusescustombow;

import com.mojang.logging.LogUtils;
import com.vomiter.skeletonusescustombow.core.bowlike.AlexCavesDreadbowAdapter;
import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeAdapters;
import com.vomiter.skeletonusescustombow.core.bowlike.CataclysmBowAdapter;
import com.vomiter.skeletonusescustombow.core.bowlike.VanillaBowAdapter;
import com.vomiter.skeletonusescustombow.data.BowDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SkeletonUsesCustomBow.MOD_ID)
public class SkeletonUsesCustomBow
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "skeletonusescustombow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SkeletonUsesCustomBow(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::commonSetup);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onAddReloadListeners);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BowLikeAdapters.register(new VanillaBowAdapter());
            if(ModList.get().isLoaded("alexscaves")) BowLikeAdapters.register(new AlexCavesDreadbowAdapter());
            if(ModList.get().isLoaded("cataclysm")) BowLikeAdapters.register(new CataclysmBowAdapter());
        });
    }

    private void onAddReloadListeners(AddReloadListenerEvent e){
        e.addListener(new BowDataManager());
    }

    /*
/summon skeleton ~ ~ ~ {HandItems:[{id:"archeryexp:diamond_bow",Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}]}
/summon skeleton ~ ~ ~ {HandItems:[{id:"cataclysm:cursed_bow",Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}]}
/summon skeleton ~ ~ ~ {HandItems:[{id:"cataclysm:wrath_of_the_desert",Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}]}
/summon skeleton ~ ~ ~ {HandItems:[{id:"kubejs:bow_golden",Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}]}
/summon skeleton ~ ~ ~ {HandItems:[{id:"alexscaves:dreadbow",Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}]}
/summon skeleton ~ ~ ~ {HandItems:[{id:bow,Count:1},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}], CanPickUpLoot:1b}
/summon skeleton ~ ~ ~ {HandItems:[{},{}],ArmorItems:[{},{},{},{id:iron_helmet,Count:1}], CanPickUpLoot:1b}
     */

}
