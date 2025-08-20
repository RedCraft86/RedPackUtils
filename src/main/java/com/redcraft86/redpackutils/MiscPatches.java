package com.redcraft86.redpackutils;

import com.redcraft86.redpackutils.mixin.accessor.ItemAccessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MiscPatches {
    public static void applyManualPatches() {
        applyMC151457();
    }

    private static void applyMC151457() {
        // Mojang literally forgetting to add .craftRemainder(BUCKET) to all of these
        // They have like 3 item classes for bucket related things extending Item. Why not
        // just have a common abstract class that applies the remaining item by default
        Item[] items = {
                Items.AXOLOTL_BUCKET,
                Items.COD_BUCKET,
                Items.POWDER_SNOW_BUCKET,
                Items.PUFFERFISH_BUCKET,
                Items.SALMON_BUCKET,
                Items.TADPOLE_BUCKET,
                Items.TROPICAL_FISH_BUCKET
        };

        for (Item item : items) {
            // I think there's another mod that does this as well, I've seen it in logs of a mod applying it in some mod packs
            // Just in case that mod is loaded together with this, we'll do a check
            //noinspection deprecation
            if (!item.hasCraftingRemainingItem()) {
                ((ItemAccessor)item).setCraftingRemainingItem(Items.BUCKET);
            }
        }
    }
}
