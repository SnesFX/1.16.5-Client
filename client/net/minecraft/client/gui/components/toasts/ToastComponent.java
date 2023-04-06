/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;

public class ToastComponent
extends GuiComponent {
    private final Minecraft minecraft;
    private final ToastInstance<?>[] visible = new ToastInstance[5];
    private final Deque<Toast> queued = Queues.newArrayDeque();

    public ToastComponent(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void render(PoseStack poseStack) {
        if (this.minecraft.options.hideGui) {
            return;
        }
        for (int i = 0; i < this.visible.length; ++i) {
            ToastInstance<?> toastInstance = this.visible[i];
            if (toastInstance != null && toastInstance.render(this.minecraft.getWindow().getGuiScaledWidth(), i, poseStack)) {
                this.visible[i] = null;
            }
            if (this.visible[i] != null || this.queued.isEmpty()) continue;
            this.visible[i] = new ToastInstance(this.queued.removeFirst());
        }
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> class_, Object object) {
        for (ToastInstance<?> toastInstance : this.visible) {
            if (toastInstance == null || !class_.isAssignableFrom(toastInstance.getToast().getClass()) || !toastInstance.getToast().getToken().equals(object)) continue;
            return (T)toastInstance.getToast();
        }
        for (Toast toast : this.queued) {
            if (!class_.isAssignableFrom(toast.getClass()) || !toast.getToken().equals(object)) continue;
            return (T)toast;
        }
        return null;
    }

    public void clear() {
        Arrays.fill(this.visible, null);
        this.queued.clear();
    }

    public void addToast(Toast toast) {
        this.queued.add(toast);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    class ToastInstance<T extends Toast> {
        private final T toast;
        private long animationTime = -1L;
        private long visibleTime = -1L;
        private Toast.Visibility visibility = Toast.Visibility.SHOW;

        private ToastInstance(T t) {
            this.toast = t;
        }

        public T getToast() {
            return this.toast;
        }

        private float getVisibility(long l) {
            float f = Mth.clamp((float)(l - this.animationTime) / 600.0f, 0.0f, 1.0f);
            f *= f;
            if (this.visibility == Toast.Visibility.HIDE) {
                return 1.0f - f;
            }
            return f;
        }

        public boolean render(int n, int n2, PoseStack poseStack) {
            long l = Util.getMillis();
            if (this.animationTime == -1L) {
                this.animationTime = l;
                this.visibility.playSound(this$0.minecraft.getSoundManager());
            }
            if (this.visibility == Toast.Visibility.SHOW && l - this.animationTime <= 600L) {
                this.visibleTime = l;
            }
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)n - (float)this.toast.width() * this.getVisibility(l), n2 * this.toast.height(), 800 + n2);
            Toast.Visibility visibility = this.toast.render(poseStack, this$0, l - this.visibleTime);
            RenderSystem.popMatrix();
            if (visibility != this.visibility) {
                this.animationTime = l - (long)((int)((1.0f - this.getVisibility(l)) * 600.0f));
                this.visibility = visibility;
                this.visibility.playSound(this$0.minecraft.getSoundManager());
            }
            return this.visibility == Toast.Visibility.HIDE && l - this.animationTime > 600L;
        }
    }

}

