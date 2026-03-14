package com.vomiter.skeletonusescustombow;

import com.mojang.logging.LogUtils;
import com.vomiter.skeletonusescustombow.core.bowlike.AlexCavesDreadbowAdapter;
import com.vomiter.skeletonusescustombow.core.bowlike.BowLikeAdapters;
import com.vomiter.skeletonusescustombow.core.bowlike.CataclysmBowAdapter;
import com.vomiter.skeletonusescustombow.core.bowlike.VanillaBowAdapter;
import com.vomiter.skeletonusescustombow.data.BowDataManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(SkeletonUsesCustomBow.MOD_ID)
public class SkeletonUsesCustomBow
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "skeletonusescustombow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SkeletonUsesCustomBow(ModContainer mod, IEventBus modBus) {
        modBus.addListener(this::commonSetup);
        mod.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus forgeBus = NeoForge.EVENT_BUS;
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
