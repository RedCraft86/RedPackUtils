package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.mixin.accessor.ItemAccessor;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiscPatches {
    public static void applyManualPatches() {
        if (CommonConfig.fixBucketCrafting) applyMC151457();
    }

    private static void applyMC151457() {
        // Mojang literally forgetting to add .craftRemainder(BUCKET) to all of these
        // They have like 3 item classes for bucket related things, why not just have them apply it by default
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
            // Just in case that mod is loaded together with this, we'll do a sanity check
            if (!item.hasCraftingRemainingItem()) {
                ((ItemAccessor)item).setCraftingRemainingItem(Items.BUCKET);
            }
        }
    }

    @SubscribeEvent
    public static void InfinityArrows(ArrowNockEvent event) {
        if (CommonConfig.infiniteArrows) {
            ItemStack bow = event.getBow();
            if (bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0) {
                event.getEntity().startUsingItem(event.getHand());
                event.setAction(InteractionResultHolder.success(bow));
            }
        }
    }
}
