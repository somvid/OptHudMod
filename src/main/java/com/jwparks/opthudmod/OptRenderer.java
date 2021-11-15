package com.jwparks.opthudmod;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class OptRenderer {
    MinecraftClient client;
    public static OptRenderer instance;
    private ArrayList<String> newChatLines;
    private boolean initialized = false;
    private boolean OptEnabled = false;
    private int OptLifetime = 9999; // 200 ticks = 10 seconds
    public static String[] caseText;
//    public int windowedWidth;
//    public int windowedHeight;
    public int color;

//    class class01 {
//        public void main(String[] args) {
//            class key implements KeyListener{
//                public void keyPressed(KeyEvent e) {
//                    OptEnabled = e.getKeyCode() != 71 && e.getKeyCode() != 74 && e.getKeyCode() != 75;
//                }
//                public void keyReleased(KeyEvent e){ }
//                public void keyTyped(KeyEvent e) { }
//            }
//        }
//    }

    public OptRenderer(){
        this.client = MinecraftClient.getInstance();
        instance = this;
        this.newChatLines = new ArrayList<>();
        caseText = new String[]{"initialized", "text"};
    }

    public void onTickInGame(MinecraftClient client) {
        if (!this.initialized)
            synchronized (this.newChatLines) {
                for (String line : this.newChatLines) { //TODO: make the branches to check if line has a "**INPUT**"
                    if (line.contains("**OPT**")) {
                        this.OptEnabled = true;
                        //TODO: need to replicate new parseLine method -> parseInput
                        caseText = parseOpt(line);
                        render();
                    }
                    else {
                        this.OptEnabled = false;
                        render();
                        caseText = new String[]{"", ""};
                    }
                }
                this.newChatLines.clear();
            }

        if (this.OptEnabled){
            this.OptLifetime -= 1;
            System.out.println(OptLifetime);
            if (this.OptLifetime == 0){
                this.OptEnabled = false;
                render();
                caseText = new String[]{"", ""};
            }
        }

    }
    public void clientString(String var1) {
        if (!this.initialized)
            synchronized (this.newChatLines) {
                this.newChatLines.add(var1);
                //this.OptEnabled = true;
                this.OptLifetime = 9999;
            }
    }

    private  String[] parseOpt(String text){
        String[] caseText = {"", ""};
        Pattern PATTERN_BRACKET = Pattern.compile("\\([^\\(\\)]+\\)");

        text = text.replaceAll("(.)\\1{6,}+", "$1$1$1$1$1$1");
        text = text.replaceAll("(..)\\1{6,}+", "$1$1$1$1$1$1");

        Matcher matcher = PATTERN_BRACKET.matcher(text);

        String pureText = text;
        String findText;

        int caseIdx = 0;
        while(matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            findText = pureText.substring(startIndex, endIndex);
            pureText = pureText.replace(findText, "");
            matcher = PATTERN_BRACKET.matcher(pureText);
            findText = findText.replace("(","");
            findText = findText.replace(")","");

            caseText[caseIdx] = findText;
            caseIdx += 1;
        }
        return caseText;
    }
    public static long countChar(String str, char ch) {
        return str.chars()
                .filter(c -> c == ch)
                .count();
    }

    public void render() {
//        int windowedWidth = this.client.currentScreen.width;
//        int windowedHeight = this.client.currentScreen.height;
        MinecraftClient client = MinecraftClient.getInstance();
        int windowedWidth = 5*client.window.getScaledWidth()/6;
        int windowedHeight = 5*client.window.getScaledHeight()/6;
        int offset = 4*windowedWidth/8;
        int x01 = windowedWidth/4;
        int x02 = 3*windowedWidth/4;
        int x00 = (x01 + x02)/2;
        int y01 = 6*windowedHeight/10;
        int y02 = 9*windowedHeight/10;
        int x1 = windowedWidth/8;
        int x2 = 3*windowedWidth/8;
        int x0 = (x1 + x2)/2;
        int y1 = 3*windowedHeight/10;
        int y2 = 8*windowedHeight/10;
        if (this.OptEnabled) {
            TextRenderer renderer = client.textRenderer;
            GlStateManager.pushMatrix();

            GL11.glScalef(1.2f, 1.2f, 1.2f);
            String[] left = caseText[0].split("//");
            String[] right = caseText[1].split("//");
            float alpha = 0.3F;
            int right_length = caseText[1].length();
            if (right_length == 0){
                drawRect(x01, x02, y01, y02, alpha);
                int y_middle = y01 + 10;
                for (String s : left) {
                    long sp_len = countChar(s, ' ');
                    long sk_len = countChar(s, '.') + countChar(s, ',');
                    long sj_len = countChar(s, '?') + countChar(s, '!');
                    long sb_len = countChar(s, '[') + countChar(s, ']');
                    long s_len = s.length() - sk_len - sj_len - sp_len - sb_len;
                    long sp = Math.round(sp_len*2.08);
                    long sk = Math.round(sk_len);
                    long sj = Math.round(sj_len*1.67);
                    long sb = Math.round(sb_len*1.4);
                    long sl = Math.round(s_len*2.1);
                    if (s.contains("[")) {
                        color = 0xAAAAAA;
                    }
                    else {
                        color = 0xFFFFFF;
                    }
                    renderer.draw(s, Math.round(x00 - (sp+sk+sj+sl+sb)), y_middle, color);
                    y_middle += 10;
                }
            }
            else {
                drawRect(x1, x2, y1, y2, alpha);
                drawRect(x1 + offset, x2 + offset, y1, y2, alpha);
                int y_left = y1 + 10;
                for (String s : left) {
                    long sp_len = countChar(s, ' ');
                    long sk_len = countChar(s, '.') + countChar(s, ',');
                    long sj_len = countChar(s, '?') + countChar(s, '!');
                    long sb_len = countChar(s, '[') + countChar(s, ']');
                    long s_len = s.length() - sk_len - sj_len - sp_len - sb_len;
                    long sp = Math.round(sp_len*2.08);
                    long sk = Math.round(sk_len);
                    long sj = Math.round(sj_len*1.67);
                    long sb = Math.round(sb_len*1.4);
                    long sl = Math.round(s_len*2.1);
                    if (s.contains("[")) {
                        color = 0xAAAAAA;
                    }
                    else {
                        color = 0xFFFFFF;
                    }
                    renderer.draw(s, Math.round(x0 - (sp+sk+sj+sl+sb)), y_left, color);
                    y_left += 10;
                }
                int y_right = y1 + 10;
                for (String s : right) {
                    long sp_len = countChar(s, ' ');
                    long sk_len = countChar(s, '.') + countChar(s, ',');
                    long sj_len = countChar(s, '?') + countChar(s, '!');
                    long sb_len = countChar(s, '[') + countChar(s, ']');
                    long s_len = s.length() - sk_len - sj_len - sp_len - sb_len;
                    long sp = Math.round(sp_len*2.08);
                    long sk = Math.round(sk_len);
                    long sj = Math.round(sj_len*1.67);
                    long sb = Math.round(sb_len*1.4);
                    long sl = Math.round(s_len*2.1);
                    if (s.contains("[")) {
                        color = 0xAAAAAA;
                    }
                    else {
                        color = 0xFFFFFF;
                    }
                    renderer.draw(s, Math.round(x0 - (sp+sk+sj+sl+sb) + offset), y_right, color);
                    y_right += 10;
                }
            }
        }
        else{
            float alpha = 0.0F;
            GlStateManager.pushMatrix();
            drawRect(x1, x2, y1, y2, alpha);
            drawRect(x1 + offset, x2 + offset, y1, y2, alpha);
        }
        GlStateManager.popMatrix();
    }

    public void drawRect(int x1, int x2, int y1, int y2, float alpha){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.1F, 0.1F, 0.1F, alpha);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex((double)x1, (double)y2, 0.0D).next();
        bufferBuilder.vertex((double)x2, (double)y2, 0.0D).next();
        bufferBuilder.vertex((double)x2, (double)y1, 0.0D).next();
        bufferBuilder.vertex((double)x1, (double)y1, 0.0D).next();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}
