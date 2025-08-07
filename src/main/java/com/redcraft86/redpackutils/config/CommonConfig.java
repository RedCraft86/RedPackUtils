package com.redcraft86.redpackutils.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import com.redcraft86.redpackutils.ModClass;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ForgeConfigSpec.BooleanValue NO_POISON_REGEN;
    private static final ForgeConfigSpec.BooleanValue NO_ATK_COOLDOWN;
    private static final ForgeConfigSpec.BooleanValue NO_BOAT_FALL_DMG;
    private static final ForgeConfigSpec.BooleanValue UNLIMITED_VILLAGER;
    private static final ForgeConfigSpec.BooleanValue NO_TEMPT_COOLDOWN;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> GRIEF_BLACKLIST;

    private static final ForgeConfigSpec.ConfigValue<? extends String> STRUCTURE_SPAWNPOINT;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_POINT_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        NO_POISON_REGEN = BUILDER.comment("Makes poison and regeneration cancel each other out.")
            .define("noPoisonedRegen", true);

        NO_ATK_COOLDOWN = BUILDER.comment("Removes the attack cooldown introduced in the 1.9 Combat Update.")
            .define("noAtkCooldown", false);

        NO_BOAT_FALL_DMG = BUILDER.comment("Stop boats from taking fall damage and breaking.")
            .define("noBoatFallDamage", true);

        UNLIMITED_VILLAGER = BUILDER.comment("Prevents Villagers and Wandering Traders from locking their trades when they run 'out of stock.'")
            .define("unlimitedVillager", true);

        NO_TEMPT_COOLDOWN = BUILDER.comment("Disables the cooldown period that prevents animals from being immediately re-attracted to tempting items (like food) after losing interest.")
            .define("noTemptCooldown", true);

        GRIEF_BLACKLIST = BUILDER.comment("List of entity IDs that cannot grief the world.")
            .defineListAllowEmpty("mobGriefBlacklist", List.of("minecraft:creeper", "minecraft:enderman", "minecraft:fireball", "minecraft:wither_skull"),
                obj -> obj instanceof String);

        BUILDER.push("Spawn Structure");

        STRUCTURE_SPAWNPOINT = BUILDER.comment("Spawns the player in the nearest structure within a 128-chunk radius from [0, 0, 0]. (a single ID or a Tag, leave empty to disable)")
            .define("structureID", "#minecraft:village");

        SPAWN_POINT_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when searching for the nearest valid structure spawn point. (Only used when spawnStructure is a Tag)")
            .defineListAllowEmpty("structureBlacklist", List.of("minecraft:village_snowy"),
                obj -> obj instanceof String);

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean noPoisonRegen = true;
    public static boolean noAtkCooldown = false;
    public static boolean noBoatFallDmg = true;
    public static boolean unlimitedVillager = true;
    public static boolean noTemptCooldown = true;
    public static Set<ResourceLocation> griefBlacklist = new HashSet<>();

    public static String structureSpawnPoint = "#minecraft:village";
    public static Set<ResourceLocation> spawnPointBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        noPoisonRegen = NO_POISON_REGEN.get();
        noAtkCooldown = NO_ATK_COOLDOWN.get();
        noBoatFallDmg = NO_BOAT_FALL_DMG.get();
        unlimitedVillager = UNLIMITED_VILLAGER.get();
        noTemptCooldown = NO_TEMPT_COOLDOWN.get();
        processIdList(GRIEF_BLACKLIST.get(), griefBlacklist, "Grief Blacklist");

        structureSpawnPoint = STRUCTURE_SPAWNPOINT.get();
        processIdList(SPAWN_POINT_BLACKLIST.get(), spawnPointBlacklist, "Structure Spawn Point (Blacklist)");
    }

    private static void processIdList(List<? extends String> entries, Set<ResourceLocation> result, String logCategory) {
        result.clear();
        for (String entry : entries) {
            if (ResourceLocation.isValidResourceLocation(entry)){
                result.add(ResourceLocation.parse(entry));
            } else {
                LOGGER.error("[RedPackUtils: {}] Invalid ID: {}", logCategory, entry);
            }
        }
    }
}
