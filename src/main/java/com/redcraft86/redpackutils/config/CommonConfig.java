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
    private static final ForgeConfigSpec.BooleanValue ALWAYS_DRAGON_EGG;

    private static final ForgeConfigSpec.BooleanValue ZOMBIE_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue CREEPER_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue ENDERMAN_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue GHAST_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue DRAGON_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue WITHER_NO_GRIEF;

    private static final ForgeConfigSpec.BooleanValue SPAWN_IN_VILLAGE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_VILLAGE_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        BUILDER.push("Entity Tweaks");

        UNLIMITED_VILLAGER = BUILDER.comment("Makes Villager and Wandering Trader trades never lock up.")
                .define("unlimitedVillager", true);

        ALWAYS_DRAGON_EGG = BUILDER.comment("Makes the Ender Dragon always spawn a Dragon Egg on the podium no matter the fight count.")
                .define("alwaysDragonEgg", true);

        BUILDER.push("Mob Griefing");

        ZOMBIE_NO_GRIEF = BUILDER.comment("Makes Zombies unable to break doors.")
                .define("zombieNoGrief", true);

        CREEPER_NO_GRIEF = BUILDER.comment("Makes Creepers unable to break blocks upon explosions.")
                .define("creeperNoGrief", true);

        ENDERMAN_NO_GRIEF = BUILDER.comment("Makes Endermen unable to pick up blocks.")
                .define("endermanNoGrief", true);

        GHAST_NO_GRIEF = BUILDER.comment("Makes Ghast fireballs unable to break blocks via fireballs.")
                .define("ghastNoGrief", true);

        DRAGON_NO_GRIEF = BUILDER.comment("Makes the Ender Dragon unable to break blocks.")
                .define("dragonNoGrief", true);

        WITHER_NO_GRIEF = BUILDER.comment("Makes the Wither unable to destroy blocks.")
                .define("witherNoGrief", true);

        BUILDER.pop();
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
    public static boolean alwaysDragonEgg = true;

    public static boolean zombieNoGrief = true;
    public static boolean creeperNoGrief = true;
    public static boolean endermanNoGrief = true;
    public static boolean ghastNoGrief = true;
    public static boolean dragonNoGrief = true;
    public static boolean witherNoGrief = true;

    public static boolean spawnInVillage = true;
    public static Set<ResourceLocation> spawnVillageBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            unlimitedVillager = UNLIMITED_VILLAGER.get();
            alwaysDragonEgg = ALWAYS_DRAGON_EGG.get();

            zombieNoGrief = ZOMBIE_NO_GRIEF.get();
            creeperNoGrief = CREEPER_NO_GRIEF.get();
            endermanNoGrief = ENDERMAN_NO_GRIEF.get();
            ghastNoGrief = GHAST_NO_GRIEF.get();
            dragonNoGrief = DRAGON_NO_GRIEF.get();
            witherNoGrief = WITHER_NO_GRIEF.get();

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
