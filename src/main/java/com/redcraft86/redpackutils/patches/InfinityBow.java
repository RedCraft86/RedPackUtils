package com.redcraft86.redpackutils.patches;

import com.redcraft86.redpackutils.ModClass;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.InteractionResultHolder;

import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Lets you shoot bows with Infinity without needing a single arrow in inventory
@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfinityBow {
    @SubscribeEvent
    public static void InfinityArrows(ArrowNockEvent event) {
        ItemStack bow = event.getBow();
        if (bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0) {
            event.getEntity().startUsingItem(event.getHand());
            event.setAction(InteractionResultHolder.success(bow));
        }
    }
}
