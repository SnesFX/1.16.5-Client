/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.realms.RealmsObjectSelectionList;

public abstract class RowButton {
    public final int width;
    public final int height;
    public final int xOffset;
    public final int yOffset;

    public RowButton(int n, int n2, int n3, int n4) {
        this.width = n;
        this.height = n2;
        this.xOffset = n3;
        this.yOffset = n4;
    }

    public void drawForRowAt(PoseStack poseStack, int n, int n2, int n3, int n4) {
        int n5 = n + this.xOffset;
        int n6 = n2 + this.yOffset;
        boolean bl = false;
        if (n3 >= n5 && n3 <= n5 + this.width && n4 >= n6 && n4 <= n6 + this.height) {
            bl = true;
        }
        this.draw(poseStack, n5, n6, bl);
    }

    protected abstract void draw(PoseStack var1, int var2, int var3, boolean var4);

    public int getRight() {
        return this.xOffset + this.width;
    }

    public int getBottom() {
        return this.yOffset + this.height;
    }

    public abstract void onClick(int var1);

    public static void drawButtonsInRow(PoseStack poseStack, List<RowButton> list, RealmsObjectSelectionList<?> realmsObjectSelectionList, int n, int n2, int n3, int n4) {
        for (RowButton rowButton : list) {
            if (realmsObjectSelectionList.getRowWidth() <= rowButton.getRight()) continue;
            rowButton.drawForRowAt(poseStack, n, n2, n3, n4);
        }
    }

    public static void rowButtonMouseClicked(RealmsObjectSelectionList<?> realmsObjectSelectionList, ObjectSelectionList.Entry<?> entry, List<RowButton> list, int n, double d, double d2) {
        int n2;
        if (n == 0 && (n2 = realmsObjectSelectionList.children().indexOf(entry)) > -1) {
            realmsObjectSelectionList.selectItem(n2);
            int n3 = realmsObjectSelectionList.getRowLeft();
            int n4 = realmsObjectSelectionList.getRowTop(n2);
            int n5 = (int)(d - (double)n3);
            int n6 = (int)(d2 - (double)n4);
            for (RowButton rowButton : list) {
                if (n5 < rowButton.xOffset || n5 > rowButton.getRight() || n6 < rowButton.yOffset || n6 > rowButton.getBottom()) continue;
                rowButton.onClick(n2);
            }
        }
    }
}

