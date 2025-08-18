package com.redcraft86.redpackutils.systems;

import com.redcraft86.redpackutils.ModClass;
import com.redcraft86.redpackutils.config.ClientConfig;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModClass.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class WindowTitle {
    private static final int UPDATE_INTERVAL = 10;
    private static final Minecraft MC = Minecraft.getInstance();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++; // Slow tick
            if (tickCounter >= UPDATE_INTERVAL) {
                MC.getWindow().setTitle(createWindowTitle());
                tickCounter = 0;
            }
        }
    }

    // Cause immediate update on screen changes since MC tries to change it back
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        tickCounter = UPDATE_INTERVAL;
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        tickCounter = UPDATE_INTERVAL;
    }

    // This is a copy of the Minecraft class' createTitle method since I could not inject into that method...
    private static String createWindowTitle() {
        StringBuilder result = new StringBuilder();

        // === Modified Original Code ===
        if (ClientConfig.titleName == null) {
            result.append("Minecraft");
            if (Minecraft.checkModStatus().shouldReportAsModified()) {
                result.append("* ").append("Forge");
            }
            result.append(" ");
            result.append(SharedConstants.getCurrentVersion().getName());
        } else {
            result.append(ClientConfig.titleName);
        }

        // === Unmodified Original Code ===
        ClientPacketListener clientpacketlistener = MC.getConnection();
        if (clientpacketlistener != null && clientpacketlistener.getConnection().isConnected()) {
            result.append(" - ");
            if (MC.getSingleplayerServer() != null && !MC.getSingleplayerServer().isPublished()) {
                result.append(I18n.get("title.singleplayer"));
            } else if (MC.isConnectedToRealms()) {
                result.append(I18n.get("title.multiplayer.realms"));
            } else if (MC.getSingleplayerServer() != null || MC.getCurrentServer() != null && MC.getCurrentServer().isLan()) {
                result.append(I18n.get("title.multiplayer.lan"));
            } else {
                result.append(I18n.get("title.multiplayer.other"));
            }
        }

        // === Memory Usage ===
        if (ClientConfig.memUsage) {
            Runtime runtime = Runtime.getRuntime();
            long totalMem = runtime.totalMemory();
            long usedMem = (totalMem - runtime.freeMemory()) / (1024 * 1024);
            result.append(String.format(" | Memory: %,d MB / %,d MB", usedMem, totalMem / (1024 * 1024)));
        }

        return result.toString();
    }
}
