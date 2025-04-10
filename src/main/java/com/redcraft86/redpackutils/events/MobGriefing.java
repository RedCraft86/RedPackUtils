package com.redcraft86.redpackutils.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import com.redcraft86.redpackutils.config.CommonConfig;

public class MobGriefing {
    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent e) {
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
