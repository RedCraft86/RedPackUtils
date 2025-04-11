package com.redcraft86.redpackutils.events;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.util.ModUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
        if (e.getLevel().isClientSide()) {
            return;
        }

        if (CommonConfig.unlimitedVillager && e.getTarget() instanceof AbstractVillager villager) {
            for (MerchantOffer offer : villager.getOffers()) {
                offer.resetUses();
            }
        }
    }

    @SubscribeEvent
    public static void onMobGrief(EntityMobGriefingEvent e) {
        Entity entity = e.getEntity();
        if (entity == null) {
            return;
        }

        if (entity instanceof Zombie && CommonConfig.zombieNoGrief) {
            e.setResult(Event.Result.DENY);
        } else if (entity instanceof Creeper && CommonConfig.creeperNoGrief) {
            e.setResult(Event.Result.DENY);
        } else if (isGhast(entity) && CommonConfig.ghastNoGrief) {
            e.setResult(Event.Result.DENY);
        } else if (entity instanceof EnderMan && CommonConfig.endermanNoGrief) {
            e.setResult(Event.Result.DENY);
        } else if (isWither(entity) && CommonConfig.witherNoGrief) {
            e.setResult(Event.Result.DENY);
        } else if (entity instanceof EnderDragon && CommonConfig.dragonNoGrief) {
            e.setResult(Event.Result.DENY);
        }
    }

    private static boolean isGhast(Entity entity) {
        return entity instanceof Ghast || (entity instanceof Fireball ball && ball.getOwner() instanceof Ghast);
    }

    private static boolean isWither(Entity entity) {
        return entity instanceof WitherBoss || (entity instanceof WitherSkull skull && skull.getOwner() instanceof WitherBoss);
    }
}
