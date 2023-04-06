/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.advancements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);
    
    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final int max;

    private AdvancementTabType(int n2, int n3, int n4, int n5, int n6) {
        this.textureX = n2;
        this.textureY = n3;
        this.width = n4;
        this.height = n5;
        this.max = n6;
    }

    public int getMax() {
        return this.max;
    }

    public void draw(PoseStack poseStack, GuiComponent guiComponent, int n, int n2, boolean bl, int n3) {
        int n4 = this.textureX;
        if (n3 > 0) {
            n4 += this.width;
        }
        if (n3 == this.max - 1) {
            n4 += this.width;
        }
        int n5 = bl ? this.textureY + this.height : this.textureY;
        guiComponent.blit(poseStack, n + this.getX(n3), n2 + this.getY(n3), n4, n5, this.width, this.height);
    }

    public void drawIcon(int n, int n2, int n3, ItemRenderer itemRenderer, ItemStack itemStack) {
        int n4 = n + this.getX(n3);
        int n5 = n2 + this.getY(n3);
        switch (this) {
            case ABOVE: {
                n4 += 6;
                n5 += 9;
                break;
            }
            case BELOW: {
                n4 += 6;
                n5 += 6;
                break;
            }
            case LEFT: {
                n4 += 10;
                n5 += 5;
                break;
            }
            case RIGHT: {
                n4 += 6;
                n5 += 5;
            }
        }
        itemRenderer.renderAndDecorateFakeItem(itemStack, n4, n5);
    }

    public int getX(int n) {
        switch (this) {
            case ABOVE: {
                return (this.width + 4) * n;
            }
            case BELOW: {
                return (this.width + 4) * n;
            }
            case LEFT: {
                return -this.width + 4;
            }
            case RIGHT: {
                return 248;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + (Object)((Object)this));
    }

    public int getY(int n) {
        switch (this) {
            case ABOVE: {
                return -this.height + 4;
            }
            case BELOW: {
                return 136;
            }
            case LEFT: {
                return this.height * n;
            }
            case RIGHT: {
                return this.height * n;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + (Object)((Object)this));
    }

    public boolean isMouseOver(int n, int n2, int n3, double d, double d2) {
        int n4 = n + this.getX(n3);
        int n5 = n2 + this.getY(n3);
        return d > (double)n4 && d < (double)(n4 + this.width) && d2 > (double)n5 && d2 < (double)(n5 + this.height);
    }

}

