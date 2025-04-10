package com.redcraft86.redpackutils.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.util.ModUtils;

public class EntityInteraction {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getLevel().isClientSide()) {
            return;
        }

        if (CommonConfig.unlimitedVillager && e.getTarget() instanceof AbstractVillager villager) {
            for (MerchantOffer offer : villager.getOffers()) {
                offer.resetUses();
                // We need to do some funky stuff to access them private content
                ModUtils.setPrivateField(offer, "demand", 0);
                ModUtils.setPrivateField(offer, "maxUses", Integer.MAX_VALUE);
            }
        }
    }
}
