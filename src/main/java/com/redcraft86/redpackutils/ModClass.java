package com.redcraft86.redpackutils;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraft.world.item.CreativeModeTabs;

import com.redcraft86.redpackutils.config.ClientConfig;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.registries.items.ItemRegistry;
import com.redcraft86.redpackutils.registries.blocks.BlockRegistry;
import com.redcraft86.redpackutils.events.EntityInteraction;
import com.redcraft86.redpackutils.events.MobGriefing;
import com.redcraft86.redpackutils.events.VillageSpawn;

@Mod(ModClass.MOD_ID)
public class ModClass
{
    public static final String MOD_ID = "redpackutils";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModClass(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);

        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private void loadComplete(final FMLLoadCompleteEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EntityInteraction.class);
        MinecraftForge.EVENT_BUS.register(MobGriefing.class);
        MinecraftForge.EVENT_BUS.register(VillageSpawn.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BlockRegistry.RAINBOW_BEACON_BLOCK);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}