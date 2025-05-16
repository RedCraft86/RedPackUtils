package com.redcraft86.redpackutils;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.item.CreativeModeTabs;

import com.redcraft86.redpackutils.config.ClientConfig;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.registries.items.ItemRegistry;
import com.redcraft86.redpackutils.registries.blocks.BlockRegistry;
import com.redcraft86.redpackutils.events.MiscPatches;

@Mod(ModClass.MOD_ID)
public class ModClass {
    public static final String MOD_ID = "redpackutils";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModClass(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        MiscPatches.applyManualPatches();

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BlockRegistry.RAINBOW_BEACON_BLOCK);
        }
    }
}