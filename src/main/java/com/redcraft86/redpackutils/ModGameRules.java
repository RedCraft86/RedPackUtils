package com.redcraft86.redpackutils;

import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> NO_ATTACK_COOLDOWN;

    public static void registerRules() {
        NO_ATTACK_COOLDOWN = GameRules.register("noAttackCooldown",
                GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    }
}
