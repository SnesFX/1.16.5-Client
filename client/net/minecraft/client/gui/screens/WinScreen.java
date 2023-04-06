/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation EDITION_LOCATION = new ResourceLocation("textures/gui/title/edition.png");
    private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
    private static final String OBFUSCATE_TOKEN = "" + (Object)((Object)ChatFormatting.WHITE) + (Object)((Object)ChatFormatting.OBFUSCATED) + (Object)((Object)ChatFormatting.GREEN) + (Object)((Object)ChatFormatting.AQUA);
    private final boolean poem;
    private final Runnable onFinished;
    private float time;
    private List<FormattedCharSequence> lines;
    private IntSet centeredLines;
    private int totalScrollLength;
    private float scrollSpeed = 0.5f;

    public WinScreen(boolean bl, Runnable runnable) {
        super(NarratorChatListener.NO_TITLE);
        this.poem = bl;
        this.onFinished = runnable;
        if (!bl) {
            this.scrollSpeed = 0.75f;
        }
    }

    @Override
    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        float f = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        if (this.time > f) {
            this.respawn();
        }
    }

    @Override
    public void onClose() {
        this.respawn();
    }

    private void respawn() {
        this.onFinished.run();
        this.minecraft.setScreen(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        if (this.lines != null) {
            return;
        }
        this.lines = Lists.newArrayList();
        this.centeredLines = new IntOpenHashSet();
        Resource resource = null;
        try {
            InputStream inputStream;
            Object object;
            BufferedReader bufferedReader;
            int n = 274;
            if (this.poem) {
                String string;
                int n2;
                resource = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
                inputStream = resource.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                object = new Random(8124371L);
                while ((string = bufferedReader.readLine()) != null) {
                    string = string.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
                    while ((n2 = string.indexOf(OBFUSCATE_TOKEN)) != -1) {
                        String string2 = string.substring(0, n2);
                        String object2 = string.substring(n2 + OBFUSCATE_TOKEN.length());
                        string = (String)string2 + (Object)((Object)ChatFormatting.WHITE) + (Object)((Object)ChatFormatting.OBFUSCATED) + "XXXXXXXX".substring(0, ((Random)object).nextInt(4) + 3) + object2;
                    }
                    this.lines.addAll(this.minecraft.font.split(new TextComponent(string), 274));
                    this.lines.add(FormattedCharSequence.EMPTY);
                }
                inputStream.close();
                for (n2 = 0; n2 < 8; ++n2) {
                    this.lines.add(FormattedCharSequence.EMPTY);
                }
            }
            inputStream = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((object = bufferedReader.readLine()) != null) {
                boolean bl;
                object = ((String)object).replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
                if (((String)(object = ((String)object).replaceAll("\t", "    "))).startsWith("[C]")) {
                    object = ((String)object).substring(3);
                    bl = true;
                } else {
                    bl = false;
                }
                List<FormattedCharSequence> list = this.minecraft.font.split(new TextComponent((String)object), 274);
                for (FormattedCharSequence formattedCharSequence : list) {
                    if (bl) {
                        this.centeredLines.add(this.lines.size());
                    }
                    this.lines.add(formattedCharSequence);
                }
                this.lines.add(FormattedCharSequence.EMPTY);
            }
            inputStream.close();
            this.totalScrollLength = this.lines.size() * 12;
            IOUtils.closeQuietly((Closeable)resource);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load credits", (Throwable)exception);
        }
        finally {
            IOUtils.closeQuietly(resource);
        }
    }

    private void renderBg(int n, int n2, float f) {
        this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
        int n3 = this.width;
        float f2 = -this.time * 0.5f * this.scrollSpeed;
        float f3 = (float)this.height - this.time * 0.5f * this.scrollSpeed;
        float f4 = 0.015625f;
        float f5 = this.time * 0.02f;
        float f6 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
        float f7 = (f6 - 20.0f - this.time) * 0.005f;
        if (f7 < f5) {
            f5 = f7;
        }
        if (f5 > 1.0f) {
            f5 = 1.0f;
        }
        f5 *= f5;
        f5 = f5 * 96.0f / 255.0f;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.getBlitOffset()).uv(0.0f, f2 * 0.015625f).color(f5, f5, f5, 1.0f).endVertex();
        bufferBuilder.vertex(n3, this.height, this.getBlitOffset()).uv((float)n3 * 0.015625f, f2 * 0.015625f).color(f5, f5, f5, 1.0f).endVertex();
        bufferBuilder.vertex(n3, 0.0, this.getBlitOffset()).uv((float)n3 * 0.015625f, f3 * 0.015625f).color(f5, f5, f5, 1.0f).endVertex();
        bufferBuilder.vertex(0.0, 0.0, this.getBlitOffset()).uv(0.0f, f3 * 0.015625f).color(f5, f5, f5, 1.0f).endVertex();
        tesselator.end();
    }

    @Override
    public void render(PoseStack poseStack, int n3, int n4, float f) {
        int n5;
        this.renderBg(n3, n4, f);
        int n6 = 274;
        int n7 = this.width / 2 - 137;
        int n8 = this.height + 50;
        this.time += f;
        float f2 = -this.time * this.scrollSpeed;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, f2, 0.0f);
        this.minecraft.getTextureManager().bind(LOGO_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        this.blitOutlineBlack(n7, n8, (n, n2) -> {
            this.blit(poseStack, n + 0, (int)n2, 0, 0, 155, 44);
            this.blit(poseStack, n + 155, (int)n2, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        this.minecraft.getTextureManager().bind(EDITION_LOCATION);
        WinScreen.blit(poseStack, n7 + 88, n8 + 37, 0.0f, 0.0f, 98, 14, 128, 16);
        RenderSystem.disableAlphaTest();
        int n9 = n8 + 100;
        for (n5 = 0; n5 < this.lines.size(); ++n5) {
            float f3;
            if (n5 == this.lines.size() - 1 && (f3 = (float)n9 + f2 - (float)(this.height / 2 - 6)) < 0.0f) {
                RenderSystem.translatef(0.0f, -f3, 0.0f);
            }
            if ((float)n9 + f2 + 12.0f + 8.0f > 0.0f && (float)n9 + f2 < (float)this.height) {
                FormattedCharSequence formattedCharSequence = this.lines.get(n5);
                if (this.centeredLines.contains(n5)) {
                    this.font.drawShadow(poseStack, formattedCharSequence, (float)(n7 + (274 - this.font.width(formattedCharSequence)) / 2), (float)n9, 16777215);
                } else {
                    this.font.random.setSeed((long)((float)((long)n5 * 4238972211L) + this.time / 4.0f));
                    this.font.drawShadow(poseStack, formattedCharSequence, (float)n7, (float)n9, 16777215);
                }
            }
            n9 += 12;
        }
        RenderSystem.popMatrix();
        this.minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        n5 = this.width;
        int n10 = this.height;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, n10, this.getBlitOffset()).uv(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(n5, n10, this.getBlitOffset()).uv(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(n5, 0.0, this.getBlitOffset()).uv(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(0.0, 0.0, this.getBlitOffset()).uv(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tesselator.end();
        RenderSystem.disableBlend();
        super.render(poseStack, n3, n4, f);
    }
}

