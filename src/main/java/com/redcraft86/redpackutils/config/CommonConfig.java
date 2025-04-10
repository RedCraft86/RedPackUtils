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
    private static final ForgeConfigSpec.BooleanValue UNLIMITED_VILLAGER;
    private static final ForgeConfigSpec.BooleanValue SPAWN_IN_VILLAGE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_VILLAGE_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        BUILDER.push("Gameplay Tweaks");

        UNLIMITED_VILLAGER = BUILDER.comment("Make villager and wandering trader trades never lock up. (also have the side effect of unlocking every trade)")
                .define("unlimitedVillager", true);

        BUILDER.pop();
        BUILDER.push("Spawn Tweaks");

        SPAWN_IN_VILLAGE = BUILDER.comment("Have the player spawn at the nearest village within a 128 chunk radius.")
                .define("spawnInVillage", true);

        SPAWN_VILLAGE_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when locating the closest village to spawn in.")
                .defineListAllowEmpty("spawnVillageBlacklist", List.of("minecraft:village_snowy"),
                        obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name));

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean unlimitedVillager = true;
    public static boolean spawnInVillage = true;
    public static Set<ResourceLocation> spawnVillageBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            unlimitedVillager = UNLIMITED_VILLAGER.get();
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
