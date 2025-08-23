package com.redcraft86.redpackutils.mixin;

import com.redcraft86.redpackutils.config.CommonConfig;
import com.redcraft86.redpackutils.systems.StructureSpawn;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.gui.screens.LevelLoadingScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadScreenMixin {
    @Inject(method = "getFormattedProgress", at = @At("RETURN"), cancellable = true)
    private void getLoadString(CallbackInfoReturnable<String> cir) {
        if (!CommonConfig.structureSpawnPoint.isBlank() && StructureSpawn.isSearching()) {
            String structureName = CommonConfig.structureSpawnPoint;
            if (structureName.startsWith("#")) {
                structureName = structureName.substring(1);
            }

            cir.setReturnValue(String.format("%s%s%s",
                    I18n.get("text.redpackutils.structure_search_1"), structureName,
                    I18n.get("text.redpackutils.structure_search_2")));
        } else {
            cir.setReturnValue(String.format("Loading %s", cir.getReturnValue()));
        }
    }
}
