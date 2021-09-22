package com.jwparks.opthudmod.main.mixins;
import com.jwparks.opthudmod.OptRenderer;
import com.jwparks.opthudmod.main.ModMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;

@Mixin({InGameHud.class})
public class OptRenderMixin {
    private OptRenderer optRenderer;

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay()V")})
    public void renderHUD(float tickDelta, CallbackInfo ci) {
        if (optRenderer==null){
            this.optRenderer = ModMain.getInstance().getOptRenderer();
        }
        this.optRenderer.render();
    }
}