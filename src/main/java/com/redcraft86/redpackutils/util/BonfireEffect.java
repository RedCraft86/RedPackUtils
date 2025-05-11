package com.redcraft86.redpackutils.util;

import com.mojang.logging.LogUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public class BonfireEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    public MobEffect effect = null;
    public int amplifier = 0;
    public int duration = 20;

    public BonfireEffect(String entry) {
        String[] parts = entry.split(" ", 3);
        if (parts.length < 3) {
            LOGGER.error("[RedPackUtils: Bonfire Blessing] Failed to parse entry '{}'", entry);
            return;
        }

        effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(parts[0]));
        if (effect == null) {
            LOGGER.error("[RedPackUtils: Bonfire Blessing] Effect {} does not exist in registry", parts[0]);
            return;
        }

        try {
            amplifier = Mth.clamp(Integer.parseInt(parts[1]) - 1, 0, 255);
        } catch (NumberFormatException e) {
            LOGGER.error("[RedPackUtils: Bonfire Blessing] Failed to parse power in '{}' because {}", entry, e);
        }

        try {
            duration = Math.max(20, Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            LOGGER.error("[RedPackUtils: Bonfire Blessing] Failed to parse duration in '{}' because {}", entry, e);
        }
    }
}
