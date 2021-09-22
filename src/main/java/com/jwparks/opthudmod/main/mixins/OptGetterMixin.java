package com.jwparks.opthudmod.main.mixins;
import com.jwparks.opthudmod.main.ModMain;

import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
//import net.minecraft.text.TextComponent;
import net.minecraft.client.gui.hud.ChatListenerHud;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@org.spongepowered.asm.mixin.Mixin({ChatListenerHud.class})
public class OptGetterMixin {
    @Inject(method = {"onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;)V"}, at = {@At("HEAD")}, cancellable = true)
    public void postSay(MessageType type, Text textComponent, CallbackInfo ci) {
        ModMain.say(textComponent);
    }
}
