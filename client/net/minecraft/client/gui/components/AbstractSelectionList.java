/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends Entry<E>>
extends AbstractContainerEventHandler
implements Widget {
    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children = new TrackedList();
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    private E selected;
    private boolean renderBackground = true;
    private boolean renderTopAndBottom = true;

    public AbstractSelectionList(Minecraft minecraft, int n, int n2, int n3, int n4, int n5) {
        this.minecraft = minecraft;
        this.width = n;
        this.height = n2;
        this.y0 = n3;
        this.y1 = n4;
        this.itemHeight = n5;
        this.x0 = 0;
        this.x1 = n;
    }

    public void setRenderSelection(boolean bl) {
        this.renderSelection = bl;
    }

    protected void setRenderHeader(boolean bl, int n) {
        this.renderHeader = bl;
        this.headerHeight = n;
        if (!bl) {
            this.headerHeight = 0;
        }
    }

    public int getRowWidth() {
        return 220;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E e) {
        this.selected = e;
    }

    public void setRenderBackground(boolean bl) {
        this.renderBackground = bl;
    }

    public void setRenderTopAndBottom(boolean bl) {
        this.renderTopAndBottom = bl;
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
    }

    protected void replaceEntries(Collection<E> collection) {
        this.children.clear();
        this.children.addAll(collection);
    }

    protected E getEntry(int n) {
        return (E)((Entry)this.children().get(n));
    }

    protected int addEntry(E e) {
        this.children.add(e);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int n) {
        return Objects.equals(this.getSelected(), this.children().get(n));
    }

    @Nullable
    protected final E getEntryAtPosition(double d, double d2) {
        int n = this.getRowWidth() / 2;
        int n2 = this.x0 + this.width / 2;
        int n3 = n2 - n;
        int n4 = n2 + n;
        int n5 = Mth.floor(d2 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n6 = n5 / this.itemHeight;
        if (d < (double)this.getScrollbarPosition() && d >= (double)n3 && d <= (double)n4 && n6 >= 0 && n5 >= 0 && n6 < this.getItemCount()) {
            return (E)((Entry)this.children().get(n6));
        }
        return null;
    }

    public void updateSize(int n, int n2, int n3, int n4) {
        this.width = n;
        this.height = n2;
        this.y0 = n3;
        this.y1 = n4;
        this.x0 = 0;
        this.x1 = n;
    }

    public void setLeftPos(int n) {
        this.x0 = n;
        this.x1 = n + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int n, int n2) {
    }

    protected void renderHeader(PoseStack poseStack, int n, int n2, Tesselator tesselator) {
    }

    protected void renderBackground(PoseStack poseStack) {
    }

    protected void renderDecorations(PoseStack poseStack, int n, int n2) {
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        int n3;
        int n4;
        int n5;
        this.renderBackground(poseStack);
        int n6 = this.getScrollbarPosition();
        int n7 = n6 + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        if (this.renderBackground) {
            this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f2 = 32.0f;
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(this.x0, this.y1, 0.0).uv((float)this.x0 / 32.0f, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
            bufferBuilder.vertex(this.x1, this.y1, 0.0).uv((float)this.x1 / 32.0f, (float)(this.y1 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
            bufferBuilder.vertex(this.x1, this.y0, 0.0).uv((float)this.x1 / 32.0f, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
            bufferBuilder.vertex(this.x0, this.y0, 0.0).uv((float)this.x0 / 32.0f, (float)(this.y0 + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).endVertex();
            tesselator.end();
        }
        int n8 = this.getRowLeft();
        int n9 = this.y0 + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(poseStack, n8, n9, tesselator);
        }
        this.renderList(poseStack, n8, n9, n, n2, f);
        if (this.renderTopAndBottom) {
            this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
            float f3 = 32.0f;
            n5 = -100;
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(this.x0, this.y0, -100.0).uv(0.0f, (float)this.y0 / 32.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0 + this.width, this.y0, -100.0).uv((float)this.width / 32.0f, (float)this.y0 / 32.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0 + this.width, 0.0, -100.0).uv((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0, 0.0, -100.0).uv(0.0f, 0.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0, this.height, -100.0).uv(0.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0 + this.width, this.height, -100.0).uv((float)this.width / 32.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0 + this.width, this.y1, -100.0).uv((float)this.width / 32.0f, (float)this.y1 / 32.0f).color(64, 64, 64, 255).endVertex();
            bufferBuilder.vertex(this.x0, this.y1, -100.0).uv(0.0f, (float)this.y1 / 32.0f).color(64, 64, 64, 255).endVertex();
            tesselator.end();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableTexture();
            n3 = 4;
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(this.x0, this.y0 + 4, 0.0).uv(0.0f, 1.0f).color(0, 0, 0, 0).endVertex();
            bufferBuilder.vertex(this.x1, this.y0 + 4, 0.0).uv(1.0f, 1.0f).color(0, 0, 0, 0).endVertex();
            bufferBuilder.vertex(this.x1, this.y0, 0.0).uv(1.0f, 0.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(this.x0, this.y0, 0.0).uv(0.0f, 0.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(this.x0, this.y1, 0.0).uv(0.0f, 1.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(this.x1, this.y1, 0.0).uv(1.0f, 1.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(this.x1, this.y1 - 4, 0.0).uv(1.0f, 0.0f).color(0, 0, 0, 0).endVertex();
            bufferBuilder.vertex(this.x0, this.y1 - 4, 0.0).uv(0.0f, 0.0f).color(0, 0, 0, 0).endVertex();
            tesselator.end();
        }
        if ((n4 = this.getMaxScroll()) > 0) {
            RenderSystem.disableTexture();
            n5 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            n5 = Mth.clamp(n5, 32, this.y1 - this.y0 - 8);
            n3 = (int)this.getScrollAmount() * (this.y1 - this.y0 - n5) / n4 + this.y0;
            if (n3 < this.y0) {
                n3 = this.y0;
            }
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(n6, this.y1, 0.0).uv(0.0f, 1.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n7, this.y1, 0.0).uv(1.0f, 1.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n7, this.y0, 0.0).uv(1.0f, 0.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n6, this.y0, 0.0).uv(0.0f, 0.0f).color(0, 0, 0, 255).endVertex();
            bufferBuilder.vertex(n6, n3 + n5, 0.0).uv(0.0f, 1.0f).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(n7, n3 + n5, 0.0).uv(1.0f, 1.0f).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(n7, n3, 0.0).uv(1.0f, 0.0f).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(n6, n3, 0.0).uv(0.0f, 0.0f).color(128, 128, 128, 255).endVertex();
            bufferBuilder.vertex(n6, n3 + n5 - 1, 0.0).uv(0.0f, 1.0f).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(n7 - 1, n3 + n5 - 1, 0.0).uv(1.0f, 1.0f).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(n7 - 1, n3, 0.0).uv(1.0f, 0.0f).color(192, 192, 192, 255).endVertex();
            bufferBuilder.vertex(n6, n3, 0.0).uv(0.0f, 0.0f).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }
        this.renderDecorations(poseStack, n, n2);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E e) {
        this.setScrollAmount(this.children().indexOf(e) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2);
    }

    protected void ensureVisible(E e) {
        int n;
        int n2 = this.getRowTop(this.children().indexOf(e));
        int n3 = n2 - this.y0 - 4 - this.itemHeight;
        if (n3 < 0) {
            this.scroll(n3);
        }
        if ((n = this.y1 - n2 - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-n);
        }
    }

    private void scroll(int n) {
        this.setScrollAmount(this.getScrollAmount() + (double)n);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double d) {
        this.scrollAmount = Mth.clamp(d, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    protected void updateScrollingState(double d, double d2, int n) {
        this.scrolling = n == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        this.updateScrollingState(d, d2, n);
        if (!this.isMouseOver(d, d2)) {
            return false;
        }
        E e = this.getEntryAtPosition(d, d2);
        if (e != null) {
            if (e.mouseClicked(d, d2, n)) {
                this.setFocused((GuiEventListener)e);
                this.setDragging(true);
                return true;
            }
        } else if (n == 0) {
            this.clickedHeader((int)(d - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(d2 - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(d, d2, n);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (super.mouseDragged(d, d2, n, d3, d4)) {
            return true;
        }
        if (n != 0 || !this.scrolling) {
            return false;
        }
        if (d2 < (double)this.y0) {
            this.setScrollAmount(0.0);
        } else if (d2 > (double)this.y1) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double d5 = Math.max(1, this.getMaxScroll());
            int n2 = this.y1 - this.y0;
            int n3 = Mth.clamp((int)((float)(n2 * n2) / (float)this.getMaxPosition()), 32, n2 - 8);
            double d6 = Math.max(1.0, d5 / (double)(n2 - n3));
            this.setScrollAmount(this.getScrollAmount() + d4 * d6);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        this.setScrollAmount(this.getScrollAmount() - d3 * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (n == 264) {
            this.moveSelection(SelectionDirection.DOWN);
            return true;
        }
        if (n == 265) {
            this.moveSelection(SelectionDirection.UP);
            return true;
        }
        return false;
    }

    protected void moveSelection(SelectionDirection selectionDirection) {
        this.moveSelection(selectionDirection, entry -> true);
    }

    protected void refreshSelection() {
        E e = this.getSelected();
        if (e != null) {
            this.setSelected(e);
            this.ensureVisible(e);
        }
    }

    protected void moveSelection(SelectionDirection selectionDirection, Predicate<E> predicate) {
        int n;
        int n2 = n = selectionDirection == SelectionDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int n3;
            int n4 = this.children().indexOf(this.getSelected());
            while (n4 != (n3 = Mth.clamp(n4 + n, 0, this.getItemCount() - 1))) {
                Entry entry = (Entry)this.children().get(n3);
                if (predicate.test(entry)) {
                    this.setSelected(entry);
                    this.ensureVisible(entry);
                    break;
                }
                n4 = n3;
            }
        }
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return d2 >= (double)this.y0 && d2 <= (double)this.y1 && d >= (double)this.x0 && d <= (double)this.x1;
    }

    protected void renderList(PoseStack poseStack, int n, int n2, int n3, int n4, float f) {
        int n5 = this.getItemCount();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        for (int i = 0; i < n5; ++i) {
            int n6;
            int n7 = this.getRowTop(i);
            int n8 = this.getRowBottom(i);
            if (n8 < this.y0 || n7 > this.y1) continue;
            int n9 = n2 + i * this.itemHeight + this.headerHeight;
            int n10 = this.itemHeight - 4;
            E e = this.getEntry(i);
            int n11 = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(i)) {
                n6 = this.x0 + this.width / 2 - n11 / 2;
                int n12 = this.x0 + this.width / 2 + n11 / 2;
                RenderSystem.disableTexture();
                float f2 = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(f2, f2, f2, 1.0f);
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
                bufferBuilder.vertex(n6, n9 + n10 + 2, 0.0).endVertex();
                bufferBuilder.vertex(n12, n9 + n10 + 2, 0.0).endVertex();
                bufferBuilder.vertex(n12, n9 - 2, 0.0).endVertex();
                bufferBuilder.vertex(n6, n9 - 2, 0.0).endVertex();
                tesselator.end();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
                bufferBuilder.vertex(n6 + 1, n9 + n10 + 1, 0.0).endVertex();
                bufferBuilder.vertex(n12 - 1, n9 + n10 + 1, 0.0).endVertex();
                bufferBuilder.vertex(n12 - 1, n9 - 1, 0.0).endVertex();
                bufferBuilder.vertex(n6 + 1, n9 - 1, 0.0).endVertex();
                tesselator.end();
                RenderSystem.enableTexture();
            }
            n6 = this.getRowLeft();
            ((Entry)e).render(poseStack, i, n7, n6, n11, n10, n3, n4, this.isMouseOver(n3, n4) && Objects.equals(this.getEntryAtPosition(n3, n4), e), f);
        }
    }

    public int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int n) {
        return this.y0 + 4 - (int)this.getScrollAmount() + n * this.itemHeight + this.headerHeight;
    }

    private int getRowBottom(int n) {
        return this.getRowTop(n) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    protected E remove(int n) {
        Entry entry = (Entry)this.children.get(n);
        if (this.removeEntry((Entry)this.children.get(n))) {
            return (E)entry;
        }
        return null;
    }

    protected boolean removeEntry(E e) {
        boolean bl = this.children.remove(e);
        if (bl && e == this.getSelected()) {
            this.setSelected(null);
        }
        return bl;
    }

    private void bindEntryToSelf(Entry<E> entry) {
        entry.list = this;
    }

    @Nullable
    @Override
    public /* synthetic */ GuiEventListener getFocused() {
        return this.getFocused();
    }

    class TrackedList
    extends AbstractList<E> {
        private final List<E> delegate = Lists.newArrayList();

        private TrackedList() {
        }

        @Override
        public E get(int n) {
            return (E)((Entry)this.delegate.get(n));
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public E set(int n, E e) {
            Entry entry = (Entry)this.delegate.set(n, e);
            AbstractSelectionList.this.bindEntryToSelf(e);
            return (E)entry;
        }

        @Override
        public void add(int n, E e) {
            this.delegate.add(n, e);
            AbstractSelectionList.this.bindEntryToSelf(e);
        }

        @Override
        public E remove(int n) {
            return (E)((Entry)this.delegate.remove(n));
        }

        @Override
        public /* synthetic */ Object remove(int n) {
            return this.remove(n);
        }

        @Override
        public /* synthetic */ void add(int n, Object object) {
            this.add(n, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object set(int n, Object object) {
            return this.set(n, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object get(int n) {
            return this.get(n);
        }
    }

    public static abstract class Entry<E extends Entry<E>>
    implements GuiEventListener {
        @Deprecated
        private AbstractSelectionList<E> list;

        public abstract void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double d, double d2) {
            return Objects.equals(this.list.getEntryAtPosition(d, d2), this);
        }
    }

    public static enum SelectionDirection {
        UP,
        DOWN;
        
    }

}

