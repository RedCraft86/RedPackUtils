package com.redcraft86.redpackutils.config;

import com.redcraft86.redpackutils.ModClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig
{
    private static final ForgeConfigSpec.BooleanValue SHULKER_DOUBLE_DROP;
    private static final ForgeConfigSpec.BooleanValue DRAGON_ELYTRA_DROP;
    private static final ForgeConfigSpec.BooleanValue SPAWN_IN_VILLAGE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_VILLAGE_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        BUILDER.push("Loot Tweaks");

        SHULKER_DOUBLE_DROP = BUILDER.comment("Should Shulkers drop two Shulker Shells instead of one?")
                .define("shulkerDoubleDrop", true);

        DRAGON_ELYTRA_DROP = BUILDER.comment("Should the Ender Dragon drop an elytra?")
                .define("dragonElytraDrop", true);

        BUILDER.pop();
        BUILDER.push("Spawn Tweaks");

        SPAWN_IN_VILLAGE = BUILDER.comment("Whether the player should spawn at the nearest village within a 128 chunk radius.")
                .define("spawnInVillage", true);

        SPAWN_VILLAGE_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when locating the closest valid village to spawn in.")
                .defineListAllowEmpty("spawnVillageBlacklist", List.of("minecraft:village_snowy"),
                        obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name));

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean shulkerDoubleDrop = true;
    public static boolean dragonElytraDrop = true;
    public static boolean spawnInVillage = true;
    public static Set<ResourceLocation> spawnVillageBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            shulkerDoubleDrop = SHULKER_DOUBLE_DROP.get();
            dragonElytraDrop = DRAGON_ELYTRA_DROP.get();
            spawnInVillage = SPAWN_IN_VILLAGE.get();
            if (spawnInVillage) {
                spawnVillageBlacklist = SPAWN_VILLAGE_BLACKLIST.get().stream()
                        .map(Object::toString)
                        .map(ResourceLocation::new)
                        .collect(Collectors.toSet());
            }
        }
    }
}
