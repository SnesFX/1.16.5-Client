/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;

public class TreeNodePosition {
    private final Advancement advancement;
    private final TreeNodePosition parent;
    private final TreeNodePosition previousSibling;
    private final int childIndex;
    private final List<TreeNodePosition> children = Lists.newArrayList();
    private TreeNodePosition ancestor;
    private TreeNodePosition thread;
    private int x;
    private float y;
    private float mod;
    private float change;
    private float shift;

    public TreeNodePosition(Advancement advancement, @Nullable TreeNodePosition treeNodePosition, @Nullable TreeNodePosition treeNodePosition2, int n, int n2) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        }
        this.advancement = advancement;
        this.parent = treeNodePosition;
        this.previousSibling = treeNodePosition2;
        this.childIndex = n;
        this.ancestor = this;
        this.x = n2;
        this.y = -1.0f;
        TreeNodePosition treeNodePosition3 = null;
        for (Advancement advancement2 : advancement.getChildren()) {
            treeNodePosition3 = this.addChild(advancement2, treeNodePosition3);
        }
    }

    @Nullable
    private TreeNodePosition addChild(Advancement advancement, @Nullable TreeNodePosition treeNodePosition) {
        if (advancement.getDisplay() != null) {
            treeNodePosition = new TreeNodePosition(advancement, this, treeNodePosition, this.children.size() + 1, this.x + 1);
            this.children.add(treeNodePosition);
        } else {
            for (Advancement advancement2 : advancement.getChildren()) {
                treeNodePosition = this.addChild(advancement2, treeNodePosition);
            }
        }
        return treeNodePosition;
    }

    private void firstWalk() {
        if (this.children.isEmpty()) {
            this.y = this.previousSibling != null ? this.previousSibling.y + 1.0f : 0.0f;
            return;
        }
        TreeNodePosition treeNodePosition = null;
        for (TreeNodePosition treeNodePosition2 : this.children) {
            treeNodePosition2.firstWalk();
            treeNodePosition = treeNodePosition2.apportion(treeNodePosition == null ? treeNodePosition2 : treeNodePosition);
        }
        this.executeShifts();
        float f = (this.children.get((int)0).y + this.children.get((int)(this.children.size() - 1)).y) / 2.0f;
        if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0f;
            this.mod = this.y - f;
        } else {
            this.y = f;
        }
    }

    private float secondWalk(float f, int n, float f2) {
        this.y += f;
        this.x = n;
        if (this.y < f2) {
            f2 = this.y;
        }
        for (TreeNodePosition treeNodePosition : this.children) {
            f2 = treeNodePosition.secondWalk(f + this.mod, n + 1, f2);
        }
        return f2;
    }

    private void thirdWalk(float f) {
        this.y += f;
        for (TreeNodePosition treeNodePosition : this.children) {
            treeNodePosition.thirdWalk(f);
        }
    }

    private void executeShifts() {
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i = this.children.size() - 1; i >= 0; --i) {
            TreeNodePosition treeNodePosition = this.children.get(i);
            treeNodePosition.y += f;
            treeNodePosition.mod += f;
            f += treeNodePosition.shift + (f2 += treeNodePosition.change);
        }
    }

    @Nullable
    private TreeNodePosition previousOrThread() {
        if (this.thread != null) {
            return this.thread;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(0);
        }
        return null;
    }

    @Nullable
    private TreeNodePosition nextOrThread() {
        if (this.thread != null) {
            return this.thread;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(this.children.size() - 1);
        }
        return null;
    }

    private TreeNodePosition apportion(TreeNodePosition treeNodePosition) {
        if (this.previousSibling == null) {
            return treeNodePosition;
        }
        TreeNodePosition treeNodePosition2 = this;
        TreeNodePosition treeNodePosition3 = this;
        TreeNodePosition treeNodePosition4 = this.previousSibling;
        TreeNodePosition treeNodePosition5 = this.parent.children.get(0);
        float f = this.mod;
        float f2 = this.mod;
        float f3 = treeNodePosition4.mod;
        float f4 = treeNodePosition5.mod;
        while (treeNodePosition4.nextOrThread() != null && treeNodePosition2.previousOrThread() != null) {
            treeNodePosition4 = treeNodePosition4.nextOrThread();
            treeNodePosition2 = treeNodePosition2.previousOrThread();
            treeNodePosition5 = treeNodePosition5.previousOrThread();
            treeNodePosition3 = treeNodePosition3.nextOrThread();
            treeNodePosition3.ancestor = this;
            float f5 = treeNodePosition4.y + f3 - (treeNodePosition2.y + f) + 1.0f;
            if (f5 > 0.0f) {
                treeNodePosition4.getAncestor(this, treeNodePosition).moveSubtree(this, f5);
                f += f5;
                f2 += f5;
            }
            f3 += treeNodePosition4.mod;
            f += treeNodePosition2.mod;
            f4 += treeNodePosition5.mod;
            f2 += treeNodePosition3.mod;
        }
        if (treeNodePosition4.nextOrThread() != null && treeNodePosition3.nextOrThread() == null) {
            treeNodePosition3.thread = treeNodePosition4.nextOrThread();
            treeNodePosition3.mod += f3 - f2;
        } else {
            if (treeNodePosition2.previousOrThread() != null && treeNodePosition5.previousOrThread() == null) {
                treeNodePosition5.thread = treeNodePosition2.previousOrThread();
                treeNodePosition5.mod += f - f4;
            }
            treeNodePosition = this;
        }
        return treeNodePosition;
    }

    private void moveSubtree(TreeNodePosition treeNodePosition, float f) {
        float f2 = treeNodePosition.childIndex - this.childIndex;
        if (f2 != 0.0f) {
            treeNodePosition.change -= f / f2;
            this.change += f / f2;
        }
        treeNodePosition.shift += f;
        treeNodePosition.y += f;
        treeNodePosition.mod += f;
    }

    private TreeNodePosition getAncestor(TreeNodePosition treeNodePosition, TreeNodePosition treeNodePosition2) {
        if (this.ancestor != null && treeNodePosition.parent.children.contains(this.ancestor)) {
            return this.ancestor;
        }
        return treeNodePosition2;
    }

    private void finalizePosition() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setLocation(this.x, this.y);
        }
        if (!this.children.isEmpty()) {
            for (TreeNodePosition treeNodePosition : this.children) {
                treeNodePosition.finalizePosition();
            }
        }
    }

    public static void run(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        }
        TreeNodePosition treeNodePosition = new TreeNodePosition(advancement, null, null, 1, 0);
        treeNodePosition.firstWalk();
        float f = treeNodePosition.secondWalk(0.0f, 0, treeNodePosition.y);
        if (f < 0.0f) {
            treeNodePosition.thirdWalk(-f);
        }
        treeNodePosition.finalizePosition();
    }
}

