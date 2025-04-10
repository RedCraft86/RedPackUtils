package com.redcraft86.redpackutils.registries;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.redcraft86.redpackutils.ModClass;


@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistry {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("gamemode")
                .then(Commands.literal("1").executes(context -> setGameMode(context, GameType.CREATIVE)))
                .then(Commands.literal("0").executes(context -> setGameMode(context, GameType.SURVIVAL)))
                .then(Commands.literal("2").executes(context -> setGameMode(context, GameType.ADVENTURE)))
                .then(Commands.literal("3").executes(context -> setGameMode(context, GameType.SPECTATOR))));
    }

    private static int setGameMode(CommandContext<CommandSourceStack> context, GameType type) {
        ServerPlayer player = context.getSource().getPlayer();
        player.setGameMode(type);
        context.getSource().sendSuccess(() -> Component.literal("Set gamemode to " + type.getName()), true);
        return 1;
    }
}
