package com.redcraft86.redpackutils.events;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.ModGameRules;
import com.redcraft86.redpackutils.config.CommonConfig;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getLevel().isClientSide() || e.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Entity entity = e.getTarget();
        if (entity instanceof AbstractVillager villager) {
            for (MerchantOffer offer : villager.getOffers()) {
                offer.resetUses();
                offer.maxUses = Integer.MAX_VALUE;
            }
        }
    }

    @SubscribeEvent
    static void onLivingDamage(LivingDamageEvent event) {
        Level level = event.getEntity().level();
        if (event.getEntity() == null || event.getSource() == null || level.isClientSide()) {
            return;
        }

        if (level.getGameRules().getBoolean(ModGameRules.NO_ATTACK_COOLDOWN)
                && event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            event.getEntity().invulnerableTime = 0;
        }
    }

    @SubscribeEvent
    static void onMobGrief(EntityMobGriefingEvent e) {
        Entity entity = e.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        ResourceLocation entityID = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (entityID != null && CommonConfig.griefBlacklist.contains(entityID)) {
            e.setResult(Event.Result.DENY);
        }
    }
}
