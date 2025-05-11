package com.redcraft86.redpackutils.mixin;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private boolean isBotaniaLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        isBotaniaLoaded = ModList.get().isLoaded("botania");
    }

    @Override
    public boolean shouldApplyMixin(String targetClass, String mixinClass) {
        if (mixinClass.contains("BotaniaSkyboxRendererMixin")) {
            return isBotaniaLoaded;
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
