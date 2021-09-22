package com.jwparks.opthudmod.main;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import com.jwparks.opthudmod.OptRenderer;
import org.jetbrains.annotations.NotNull;

public class ModMain implements ClientModInitializer, ClientTickCallback {
    static ModMain instance;
    MinecraftClient client;
    OptRenderer optRenderer;

    private boolean initialized = false;

    @Override
    public void onInitializeClient() {
        ClientTickCallback.EVENT.register(this);
        instance = this;

    }

    public void Init() {
        this.client = MinecraftClient.getInstance();
        this.optRenderer = new OptRenderer();
    }

    public static ModMain getInstance(){
        return instance;
    }

    public OptRenderer getOptRenderer(){
        Init();
        return this.optRenderer;
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!this.initialized) {
            if (client != null && client.getOverlay() == null) {
                Init();
                this.initialized = true;
            }
        } else {
            Entity renderViewEntity = this.client.getCameraEntity();
            boolean inGame = (renderViewEntity != null && renderViewEntity.world != null);
            if (inGame)
                this.optRenderer.onTickInGame(this.client);
        }
    }

    public static void say(@NotNull Text textComponent) {
        instance.optRenderer.clientString(textComponent.getString());
    }

}
