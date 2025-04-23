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
    private static final ForgeConfigSpec.BooleanValue NO_TEMPT_COOLDOWN;

    private static final ForgeConfigSpec.BooleanValue ZOMBIE_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue CREEPER_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue ENDERMAN_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue GHAST_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue DRAGON_NO_GRIEF;
    private static final ForgeConfigSpec.BooleanValue WITHER_NO_GRIEF;

    private static final ForgeConfigSpec.BooleanValue CAMPFIRE_SPAWN_POINT;
    private static final ForgeConfigSpec.ConfigValue<? extends String> SPAWN_STRUCTURE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_STRUCTURE_BLACKLIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    static {
        BUILDER.push("Mob Tweaks");

        UNLIMITED_VILLAGER = BUILDER.comment("Prevents Villagers and Wandering Traders from locking their trades when they run 'out of stock.'")
                .define("unlimitedVillager", true);

        ALWAYS_DRAGON_EGG = BUILDER.comment("Ensures the Ender Dragon always drops a Dragon Egg on the exit portal, regardless of how many times it's been defeated.")
                .define("alwaysDragonEgg", true);

        NO_TEMPT_COOLDOWN = BUILDER.comment("Disables the cooldown period that prevents animals from being immediately re-attracted to tempting items (like food) after losing interest.")
                .define("noTemptCooldown", true);

        BUILDER.push("Mob Griefing");

        ZOMBIE_NO_GRIEF = BUILDER.comment("Prevents Zombies from breaking down doors.")
                .define("zombieNoGrief", true);

        CREEPER_NO_GRIEF = BUILDER.comment("Stops Creepers from destroying blocks when they explode.")
                .define("creeperNoGrief", true);

        ENDERMAN_NO_GRIEF = BUILDER.comment("Disables Endermen's ability to pick up blocks.")
                .define("endermanNoGrief", true);

        GHAST_NO_GRIEF = BUILDER.comment("Prevents Ghast fireballs from destroying blocks. (As long as they remain alive before impact)")
                .define("ghastNoGrief", true);

        DRAGON_NO_GRIEF = BUILDER.comment("Stops the Ender Dragon from breaking blocks during its flight.")
                .define("dragonNoGrief", true);

        WITHER_NO_GRIEF = BUILDER.comment("Disables the Wither's attacks from breaking blocks. (Flying skulls are affected only if the Wither is alive)")
                .define("witherNoGrief", true);

        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Spawn Tweaks");

        CAMPFIRE_SPAWN_POINT = BUILDER.comment("Lets the player use lit campfires as a temporary spawnpoint.")
                .define("campfireSpawnPoint", true);

        SPAWN_STRUCTURE = BUILDER.comment("Spawns the player in the nearest structure within a 128-chunk radius from [0, 0, 0]. (a single ID or a Tag, leave empty to disable)")
                .define("spawnStructure", "#minecraft:village");

        SPAWN_STRUCTURE_BLACKLIST = BUILDER.comment("List of structure IDs to ignore when searching for the nearest valid structure spawn point. (Only used when spawnStructure is a Tag)")
                .defineListAllowEmpty("spawnStructureBlacklist", List.of("minecraft:village_snowy"),
                        obj -> obj instanceof final String name && ResourceLocation.isValidResourceLocation(name));

        BUILDER.pop();
    }
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean unlimitedVillager = true;
    public static boolean alwaysDragonEgg = true;
    public static boolean noTemptCooldown = true;

    public static boolean zombieNoGrief = true;
    public static boolean creeperNoGrief = true;
    public static boolean endermanNoGrief = true;
    public static boolean ghastNoGrief = true;
    public static boolean dragonNoGrief = true;
    public static boolean witherNoGrief = true;

    public static boolean campfireSpawnPoint = true;
    public static String spawnStructure = "#minecraft:village";
    public static Set<ResourceLocation> spawnStructureBlacklist = new HashSet<>();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            unlimitedVillager = UNLIMITED_VILLAGER.get();
            alwaysDragonEgg = ALWAYS_DRAGON_EGG.get();
            noTemptCooldown = NO_TEMPT_COOLDOWN.get();

            zombieNoGrief = ZOMBIE_NO_GRIEF.get();
            creeperNoGrief = CREEPER_NO_GRIEF.get();
            endermanNoGrief = ENDERMAN_NO_GRIEF.get();
            ghastNoGrief = GHAST_NO_GRIEF.get();
            dragonNoGrief = DRAGON_NO_GRIEF.get();
            witherNoGrief = WITHER_NO_GRIEF.get();

            campfireSpawnPoint = CAMPFIRE_SPAWN_POINT.get();
            spawnStructure = SPAWN_STRUCTURE.get();
            if (spawnStructure.isEmpty()) {
                spawnStructureBlacklist = SPAWN_STRUCTURE_BLACKLIST.get().stream()
                        .map(Object::toString)
                        .map(ResourceLocation::new)
                        .collect(Collectors.toSet());
            }
        }
    }
}
