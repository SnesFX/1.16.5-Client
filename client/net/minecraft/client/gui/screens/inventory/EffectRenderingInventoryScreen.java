/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu>
extends AbstractContainerScreen<T> {
    protected boolean doRenderEffects;

    public EffectRenderingInventoryScreen(T t, Inventory inventory, Component component) {
        super(t, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        this.checkEffectRendering();
    }

    protected void checkEffectRendering() {
        if (this.minecraft.player.getActiveEffects().isEmpty()) {
            this.leftPos = (this.width - this.imageWidth) / 2;
            this.doRenderEffects = false;
        } else {
            this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
            this.doRenderEffects = true;
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        super.render(poseStack, n, n2, f);
        if (this.doRenderEffects) {
            this.renderEffects(poseStack);
        }
    }

    private void renderEffects(PoseStack poseStack) {
        int n = this.leftPos - 124;
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (collection.isEmpty()) {
            return;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int n2 = 33;
        if (collection.size() > 5) {
            n2 = 132 / (collection.size() - 1);
        }
        List list = Ordering.natural().sortedCopy(collection);
        this.renderBackgrounds(poseStack, n, n2, list);
        this.renderIcons(poseStack, n, n2, list);
        this.renderLabels(poseStack, n, n2, list);
    }

    private void renderBackgrounds(PoseStack poseStack, int n, int n2, Iterable<MobEffectInstance> iterable) {
        this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
        int n3 = this.topPos;
        for (MobEffectInstance mobEffectInstance : iterable) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit(poseStack, n, n3, 0, 166, 140, 32);
            n3 += n2;
        }
    }

    private void renderIcons(PoseStack poseStack, int n, int n2, Iterable<MobEffectInstance> iterable) {
        MobEffectTextureManager mobEffectTextureManager = this.minecraft.getMobEffectTextures();
        int n3 = this.topPos;
        for (MobEffectInstance mobEffectInstance : iterable) {
            MobEffect mobEffect = mobEffectInstance.getEffect();
            TextureAtlasSprite textureAtlasSprite = mobEffectTextureManager.get(mobEffect);
            this.minecraft.getTextureManager().bind(textureAtlasSprite.atlas().location());
            EffectRenderingInventoryScreen.blit(poseStack, n + 6, n3 + 7, this.getBlitOffset(), 18, 18, textureAtlasSprite);
            n3 += n2;
        }
    }

    private void renderLabels(PoseStack poseStack, int n, int n2, Iterable<MobEffectInstance> iterable) {
        int n3 = this.topPos;
        for (MobEffectInstance mobEffectInstance : iterable) {
            String string = I18n.get(mobEffectInstance.getEffect().getDescriptionId(), new Object[0]);
            if (mobEffectInstance.getAmplifier() >= 1 && mobEffectInstance.getAmplifier() <= 9) {
                string = string + ' ' + I18n.get("enchantment.level." + (mobEffectInstance.getAmplifier() + 1), new Object[0]);
            }
            this.font.drawShadow(poseStack, string, (float)(n + 10 + 18), (float)(n3 + 6), 16777215);
            String string2 = MobEffectUtil.formatDuration(mobEffectInstance, 1.0f);
            this.font.drawShadow(poseStack, string2, (float)(n + 10 + 18), (float)(n3 + 6 + 10), 8355711);
            n3 += n2;
        }
    }
}

