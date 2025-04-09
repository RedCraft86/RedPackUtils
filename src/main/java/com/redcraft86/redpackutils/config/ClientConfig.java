package com.redcraft86.redpackutils.config;

import com.redcraft86.redpackutils.ModClass;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DISABLE_CREATIVE_TAB_TIPS = BUILDER
            .comment("Disable the Creative Tab tooltips on the creative inventory like \"Functional Blocks,\" \"Natural Blocks,\" etc")
            .define("disableCreativeTabTips", true);

    private static final ForgeConfigSpec.BooleanValue DISABLE_GOG_STARS = BUILDER
            .comment("Disable Botania Garden of Glass' skybox from changing the stars")
            .define("disableGardenOfGlassStars", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableCreativeTabTips;
    public static boolean disableGoGStars;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        disableCreativeTabTips = DISABLE_CREATIVE_TAB_TIPS.get();
        disableGoGStars = DISABLE_GOG_STARS.get();
    }
}
