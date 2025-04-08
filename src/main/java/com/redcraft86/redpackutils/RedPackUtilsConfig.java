package com.redcraft86.redpackutils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RedPackUtils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RedPackUtilsConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DISABLE_GOG_STARS = BUILDER
            .comment("Disable Botania Garden of Glass' skybox from changing the stars")
            .define("disableGardenOfGlassStars", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableGoGStars;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        disableGoGStars = DISABLE_GOG_STARS.get();
    }
}
