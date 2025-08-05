package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.mixin.MerchantOfferMixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getLevel().isClientSide() && e.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        MerchantOfferMixin.resetTrades(e.getTarget());
    }

    @SubscribeEvent
    public static void onMobGrief(EntityMobGriefingEvent e) {
        ResourceLocation entityID = ForgeRegistries.ENTITY_TYPES.getKey(e.getEntity().getType());
        if (entityID != null && CommonConfig.griefBlacklist.contains(entityID)) {
            e.setResult(Event.Result.DENY);
        }
    }
}
