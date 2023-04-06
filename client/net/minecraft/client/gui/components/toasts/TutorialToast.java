/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TutorialToast
implements Toast {
    private final Icons icon;
    private final Component title;
    private final Component message;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastProgressTime;
    private float lastProgress;
    private float progress;
    private final boolean progressable;

    public TutorialToast(Icons icons, Component component, @Nullable Component component2, boolean bl) {
        this.icon = icons;
        this.title = component;
        this.message = component2;
        this.progressable = bl;
    }

    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long l) {
        toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        toastComponent.blit(poseStack, 0, 0, 0, 96, this.width(), this.height());
        this.icon.render(poseStack, toastComponent, 6, 6);
        if (this.message == null) {
            toastComponent.getMinecraft().font.draw(poseStack, this.title, 30.0f, 12.0f, -11534256);
        } else {
            toastComponent.getMinecraft().font.draw(poseStack, this.title, 30.0f, 7.0f, -11534256);
            toastComponent.getMinecraft().font.draw(poseStack, this.message, 30.0f, 18.0f, -16777216);
        }
        if (this.progressable) {
            GuiComponent.fill(poseStack, 3, 28, 157, 29, -1);
            float f = (float)Mth.clampedLerp(this.lastProgress, this.progress, (float)(l - this.lastProgressTime) / 100.0f);
            int n = this.progress >= this.lastProgress ? -16755456 : -11206656;
            GuiComponent.fill(poseStack, 3, 28, (int)(3.0f + 154.0f * f), 29, n);
            this.lastProgress = f;
            this.lastProgressTime = l;
        }
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void updateProgress(float f) {
        this.progress = f;
    }

    public static enum Icons {
        MOVEMENT_KEYS(0, 0),
        MOUSE(1, 0),
        TREE(2, 0),
        RECIPE_BOOK(0, 1),
        WOODEN_PLANKS(1, 1),
        SOCIAL_INTERACTIONS(2, 1);
        
        private final int x;
        private final int y;

        private Icons(int n2, int n3) {
            this.x = n2;
            this.y = n3;
        }

        public void render(PoseStack poseStack, GuiComponent guiComponent, int n, int n2) {
            RenderSystem.enableBlend();
            guiComponent.blit(poseStack, n, n2, 176 + this.x * 20, this.y * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }

}

