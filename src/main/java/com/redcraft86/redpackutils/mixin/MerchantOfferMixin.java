package com.redcraft86.redpackutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.trading.MerchantOffer;
import com.redcraft86.redpackutils.config.CommonConfig;

// Needed for unlimitedVillagers. It basically makes them never run out of stock.
@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {
    @Shadow
    private int demand = 0;

    @Inject(method = "resetUses()V", at = @At(value = "TAIL"), require = 0)
    public void resetDemand(CallbackInfo ci) {
        if (CommonConfig.unlimitedVillager) {
            demand = 0;
        }
    }

    @Inject(method = "getMaxUses", at = @At("HEAD"), cancellable = true, require = 0)
    private void injectGetMaxUses(CallbackInfoReturnable<Integer> cir) {
        if (CommonConfig.unlimitedVillager) {
            cir.setReturnValue(Integer.MAX_VALUE);
        }
    }

    @Inject(method = "increaseUses", at = @At("HEAD"), cancellable = true, require = 0)
    private void cancelIncreaseUses(CallbackInfo ci) {
        if (CommonConfig.unlimitedVillager) {
            ci.cancel();
        }
    }
}
