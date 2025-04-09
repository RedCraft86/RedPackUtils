package com.redcraft86.redpackutils.config;

import com.redcraft86.redpackutils.ModClass;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig
{
    private static final ForgeConfigSpec.BooleanValue DISABLE_CREATIVE_TAB_TIPS;
    private static final ForgeConfigSpec.BooleanValue DISABLE_GOG_STARS;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        //BUILDER.push("Tweaks");

        DISABLE_CREATIVE_TAB_TIPS = BUILDER.comment("Disable the Creative Tab tooltips on the creative inventory like \"Functional Blocks,\" \"Natural Blocks,\" etc")
                .define("disableCreativeTabTips", true);

        DISABLE_GOG_STARS = BUILDER.comment("Disable Botania Garden of Glass' skybox from changing the stars")
                .define("disableGardenOfGlassStars", true);

        //BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean disableCreativeTabTips = true;
    public static boolean disableGoGStars = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            disableCreativeTabTips = DISABLE_CREATIVE_TAB_TIPS.get();
            disableGoGStars = DISABLE_GOG_STARS.get();
        }
    }
}
