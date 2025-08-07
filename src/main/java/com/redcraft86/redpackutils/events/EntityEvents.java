package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        LivingEntity player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            checkPoisonRegen(player);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getLevel().isClientSide() && e.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Entity entity = e.getTarget();

        if (CommonConfig.unlimitedVillager && entity instanceof AbstractVillager villager) {
            for (MerchantOffer offer : villager.getOffers()) {
                offer.resetUses();
                offer.maxUses = Integer.MAX_VALUE;
            }
        }
    }

    @SubscribeEvent
    public static void onMobGrief(EntityMobGriefingEvent e) {
        Entity entity = e.getEntity();

        ResourceLocation entityID = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (entityID != null && CommonConfig.griefBlacklist.contains(entityID)) {
            e.setResult(Event.Result.DENY);
        }
    }

    private static void checkPoisonRegen(LivingEntity player) {
        if (!CommonConfig.noPoisonRegen) {
            return;
        }

        MobEffectInstance poison = player.getEffect(MobEffects.POISON);
        MobEffectInstance regen = player.getEffect(MobEffects.REGENERATION);

        if (poison != null && regen != null){
            int durationDiff = poison.getDuration() - regen.getDuration();
            int amplifierDiff = Math.max(Math.abs(poison.getAmplifier() - regen.getAmplifier()), 255);

            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.REGENERATION);

            if (durationDiff != 0) {
                MobEffect effectToKeep = durationDiff > 0 ? MobEffects.POISON : MobEffects.REGENERATION;
                player.addEffect(new MobEffectInstance(effectToKeep, Math.abs(durationDiff), amplifierDiff));
            }
        }
    }
}
