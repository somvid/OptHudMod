package com.jwparks.opthudmod;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;


@Environment(EnvType.CLIENT)
public class OptRenderer {
    MinecraftClient client;
    boolean haveRenderManager = false;
    public static OptRenderer instance;
    private ArrayList<String> newChatLines;
    boolean debug = true;
    private boolean initialized = false;
    private int OptLifetime = 200; // 200 ticks = 10 seconds
    public static String[] caseText;

    public OptRenderer(){
        this.client = MinecraftClient.getInstance();
        instance = this;
        this.newChatLines = new ArrayList<>();
        caseText = new String[]{"initialized", "text"};
    }


    public void onTickInGame(MinecraftClient client) {
        if (!this.haveRenderManager)
            loadRenderManager();
        if (!this.initialized)
            synchronized (this.newChatLines) {
                for (String line : this.newChatLines) { //TODO: make the branches to check if line has a "**INPUT**"
                    if (line.contains("**OPT**")) {
                        //TODO: need to replicate new parseLine method -> parseInput
                        caseText = parseOpt(line);
                        render();
                    } 
                }
                this.newChatLines.clear();
            }
    }

    private void loadRenderManager() {
        System.out.println("getting renderer");
        this.haveRenderManager = true;
    }
    

    public void clientString(String var1) {
        if (!this.initialized)
            synchronized (this.newChatLines) {
                this.newChatLines.add(var1);
            }
    }


    private  String[] parseOpt(String text){
        String[] caseText = {"", ""};
        Pattern PATTERN_BRACKET = Pattern.compile("\\([^\\(\\)]+\\)");

        text = text.replaceAll("(.)\\1{6,}+", "$1$1$1$1$1$1");
        text = text.replaceAll("(..)\\1{6,}+", "$1$1$1$1$1$1");

        Matcher matcher = PATTERN_BRACKET.matcher(text);

        String pureText = text;
        String findText = new String();

        int caseIdx = 0;
        while(matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            findText = pureText.substring(startIndex, endIndex);
            pureText = pureText.replace(findText, "");
            matcher = PATTERN_BRACKET.matcher(pureText);

            caseText[caseIdx] = findText;
            caseIdx += 1;
        }
        return caseText;
    }

    public void render() {
        TextRenderer renderer = client.textRenderer;
        GlStateManager.pushMatrix();
        int x1 = 20;
        int x2 = 200;
        int y1 = 150;
        int y2 = 100;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.3F);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex((double)x1, (double)y2, 0.0D).next();
        bufferBuilder.vertex((double)x2, (double)y2, 0.0D).next();
        bufferBuilder.vertex((double)x2, (double)y1, 0.0D).next();
        bufferBuilder.vertex((double)x1, (double)y1, 0.0D).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();


        renderer.draw(caseText[0], 30, 160, 0xFFFFFF);
        GlStateManager.popMatrix();
    }


}
