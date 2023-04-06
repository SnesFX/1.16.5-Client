/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu>
extends Screen
implements MenuAccess<T> {
    public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int inventoryLabelX;
    protected int inventoryLabelY;
    protected final T menu;
    protected final Inventory inventory;
    @Nullable
    protected Slot hoveredSlot;
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot snapbackEnd;
    @Nullable
    private Slot quickdropSlot;
    @Nullable
    private Slot lastClickSlot;
    protected int leftPos;
    protected int topPos;
    private boolean isSplittingStack;
    private ItemStack draggingItem = ItemStack.EMPTY;
    private int snapbackStartX;
    private int snapbackStartY;
    private long snapbackTime;
    private ItemStack snapbackItem = ItemStack.EMPTY;
    private long quickdropTime;
    protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
    protected boolean isQuickCrafting;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved = ItemStack.EMPTY;

    public AbstractContainerScreen(T t, Inventory inventory, Component component) {
        super(component);
        this.menu = t;
        this.inventory = inventory;
        this.skipNextRelease = true;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        int n3;
        Object object;
        int n4;
        int n5 = this.leftPos;
        int n6 = this.topPos;
        this.renderBg(poseStack, f, n, n2);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        super.render(poseStack, n, n2, f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(n5, n6, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        int n7 = 240;
        int n8 = 240;
        RenderSystem.glMultiTexCoord2f(33986, 240.0f, 240.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < ((AbstractContainerMenu)this.menu).slots.size(); ++i) {
            object = ((AbstractContainerMenu)this.menu).slots.get(i);
            if (((Slot)object).isActive()) {
                this.renderSlot(poseStack, (Slot)object);
            }
            if (!this.isHovering((Slot)object, n, n2) || !((Slot)object).isActive()) continue;
            this.hoveredSlot = object;
            RenderSystem.disableDepthTest();
            n3 = ((Slot)object).x;
            n4 = ((Slot)object).y;
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(poseStack, n3, n4, n3 + 16, n4 + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
        this.renderLabels(poseStack, n, n2);
        Inventory inventory = this.minecraft.player.inventory;
        Object object2 = object = this.draggingItem.isEmpty() ? inventory.getCarried() : this.draggingItem;
        if (!((ItemStack)object).isEmpty()) {
            n3 = 8;
            n4 = this.draggingItem.isEmpty() ? 8 : 16;
            String string = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                object = ((ItemStack)object).copy();
                ((ItemStack)object).setCount(Mth.ceil((float)((ItemStack)object).getCount() / 2.0f));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                object = ((ItemStack)object).copy();
                ((ItemStack)object).setCount(this.quickCraftingRemainder);
                if (((ItemStack)object).isEmpty()) {
                    string = "" + (Object)((Object)ChatFormatting.YELLOW) + "0";
                }
            }
            this.renderFloatingItem((ItemStack)object, n - n5 - 8, n2 - n6 - n4, string);
        }
        if (!this.snapbackItem.isEmpty()) {
            float f2 = (float)(Util.getMillis() - this.snapbackTime) / 100.0f;
            if (f2 >= 1.0f) {
                f2 = 1.0f;
                this.snapbackItem = ItemStack.EMPTY;
            }
            n4 = this.snapbackEnd.x - this.snapbackStartX;
            int n9 = this.snapbackEnd.y - this.snapbackStartY;
            int n10 = this.snapbackStartX + (int)((float)n4 * f2);
            int n11 = this.snapbackStartY + (int)((float)n9 * f2);
            this.renderFloatingItem(this.snapbackItem, n10, n11, null);
        }
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    protected void renderTooltip(PoseStack poseStack, int n, int n2) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            this.renderTooltip(poseStack, this.hoveredSlot.getItem(), n, n2);
        }
    }

    private void renderFloatingItem(ItemStack itemStack, int n, int n2, String string) {
        RenderSystem.translatef(0.0f, 0.0f, 32.0f);
        this.setBlitOffset(200);
        this.itemRenderer.blitOffset = 200.0f;
        this.itemRenderer.renderAndDecorateItem(itemStack, n, n2);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, n, n2 - (this.draggingItem.isEmpty() ? 0 : 8), string);
        this.setBlitOffset(0);
        this.itemRenderer.blitOffset = 0.0f;
    }

    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        this.font.draw(poseStack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }

    protected abstract void renderBg(PoseStack var1, float var2, int var3, int var4);

    private void renderSlot(PoseStack poseStack, Slot slot) {
        Pair<ResourceLocation, ResourceLocation> pair;
        int n = slot.x;
        int n2 = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemStack2 = this.minecraft.player.inventory.getCarried();
        String string = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }
            if (AbstractContainerMenu.canItemQuickReplace(slot, itemStack2, true) && ((AbstractContainerMenu)this.menu).canDragTo(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemStack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount());
                int n3 = Math.min(itemStack.getMaxStackSize(), slot.getMaxStackSize(itemStack));
                if (itemStack.getCount() > n3) {
                    string = ChatFormatting.YELLOW.toString() + n3;
                    itemStack.setCount(n3);
                }
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }
        this.setBlitOffset(100);
        this.itemRenderer.blitOffset = 100.0f;
        if (itemStack.isEmpty() && slot.isActive() && (pair = slot.getNoItemIcon()) != null) {
            TextureAtlasSprite textureAtlasSprite = this.minecraft.getTextureAtlas((ResourceLocation)pair.getFirst()).apply((ResourceLocation)pair.getSecond());
            this.minecraft.getTextureManager().bind(textureAtlasSprite.atlas().location());
            AbstractContainerScreen.blit(poseStack, n, n2, this.getBlitOffset(), 16, 16, textureAtlasSprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                AbstractContainerScreen.fill(poseStack, n, n2, n + 16, n2 + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemStack, n, n2);
            this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, n, n2, string);
        }
        this.itemRenderer.blitOffset = 0.0f;
        this.setBlitOffset(0);
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack itemStack = this.minecraft.player.inventory.getCarried();
        if (itemStack.isEmpty() || !this.isQuickCrafting) {
            return;
        }
        if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = itemStack.getMaxStackSize();
            return;
        }
        this.quickCraftingRemainder = itemStack.getCount();
        for (Slot slot : this.quickCraftSlots) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = slot.getItem();
            int n = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemStack2, n);
            int n2 = Math.min(itemStack2.getMaxStackSize(), slot.getMaxStackSize(itemStack2));
            if (itemStack2.getCount() > n2) {
                itemStack2.setCount(n2);
            }
            this.quickCraftingRemainder -= itemStack2.getCount() - n;
        }
    }

    @Nullable
    private Slot findSlot(double d, double d2) {
        for (int i = 0; i < ((AbstractContainerMenu)this.menu).slots.size(); ++i) {
            Slot slot = ((AbstractContainerMenu)this.menu).slots.get(i);
            if (!this.isHovering(slot, d, d2) || !slot.isActive()) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        boolean bl = this.minecraft.options.keyPickItem.matchesMouse(n);
        Slot slot = this.findSlot(d, d2);
        long l = Util.getMillis();
        this.doubleclick = this.lastClickSlot == slot && l - this.lastClickTime < 250L && this.lastClickButton == n;
        this.skipNextRelease = false;
        if (n == 0 || n == 1 || bl) {
            int n2 = this.leftPos;
            int n3 = this.topPos;
            boolean bl2 = this.hasClickedOutside(d, d2, n2, n3, n);
            int n4 = -1;
            if (slot != null) {
                n4 = slot.index;
            }
            if (bl2) {
                n4 = -999;
            }
            if (this.minecraft.options.touchscreen && bl2 && this.minecraft.player.inventory.getCarried().isEmpty()) {
                this.minecraft.setScreen(null);
                return true;
            }
            if (n4 != -1) {
                if (this.minecraft.options.touchscreen) {
                    if (slot != null && slot.hasItem()) {
                        this.clickedSlot = slot;
                        this.draggingItem = ItemStack.EMPTY;
                        this.isSplittingStack = n == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.isQuickCrafting) {
                    if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                        if (this.minecraft.options.keyPickItem.matchesMouse(n)) {
                            this.slotClicked(slot, n4, n, ClickType.CLONE);
                        } else {
                            boolean bl3 = n4 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                            ClickType clickType = ClickType.PICKUP;
                            if (bl3) {
                                this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                                clickType = ClickType.QUICK_MOVE;
                            } else if (n4 == -999) {
                                clickType = ClickType.THROW;
                            }
                            this.slotClicked(slot, n4, n, clickType);
                        }
                        this.skipNextRelease = true;
                    } else {
                        this.isQuickCrafting = true;
                        this.quickCraftingButton = n;
                        this.quickCraftSlots.clear();
                        if (n == 0) {
                            this.quickCraftingType = 0;
                        } else if (n == 1) {
                            this.quickCraftingType = 1;
                        } else if (this.minecraft.options.keyPickItem.matchesMouse(n)) {
                            this.quickCraftingType = 2;
                        }
                    }
                }
            }
        } else {
            this.checkHotbarMouseClicked(n);
        }
        this.lastClickSlot = slot;
        this.lastClickTime = l;
        this.lastClickButton = n;
        return true;
    }

    private void checkHotbarMouseClicked(int n) {
        if (this.hoveredSlot != null && this.minecraft.player.inventory.getCarried().isEmpty()) {
            if (this.minecraft.options.keySwapOffhand.matchesMouse(n)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.minecraft.options.keyHotbarSlots[i].matchesMouse(n)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
            }
        }
    }

    protected boolean hasClickedOutside(double d, double d2, int n, int n2, int n3) {
        return d < (double)n || d2 < (double)n2 || d >= (double)(n + this.imageWidth) || d2 >= (double)(n2 + this.imageHeight);
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        Slot slot = this.findSlot(d, d2);
        ItemStack itemStack = this.minecraft.player.inventory.getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
            if (n == 0 || n == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && slot != null && AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false)) {
                    long l = Util.getMillis();
                    if (this.quickdropSlot == slot) {
                        if (l - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = l + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = slot;
                        this.quickdropTime = l;
                    }
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack) && ((AbstractContainerMenu)this.menu).canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this.recalculateQuickCraftRemaining();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        Slot slot = this.findSlot(d, d2);
        int n2 = this.leftPos;
        int n3 = this.topPos;
        boolean bl = this.hasClickedOutside(d, d2, n2, n3, n);
        int n4 = -1;
        if (slot != null) {
            n4 = slot.index;
        }
        if (bl) {
            n4 = -999;
        }
        if (this.doubleclick && slot != null && n == 0 && ((AbstractContainerMenu)this.menu).canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            if (AbstractContainerScreen.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot slot2 : ((AbstractContainerMenu)this.menu).slots) {
                        if (slot2 == null || !slot2.mayPickup(this.minecraft.player) || !slot2.hasItem() || slot2.container != slot.container || !AbstractContainerMenu.canItemQuickReplace(slot2, this.lastQuickMoved, true)) continue;
                        this.slotClicked(slot2, slot2.index, n, ClickType.QUICK_MOVE);
                    }
                }
            } else {
                this.slotClicked(slot, n4, n, ClickType.PICKUP_ALL);
            }
            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != n) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }
            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }
            if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
                if (n == 0 || n == 1) {
                    if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }
                    boolean bl2 = AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false);
                    if (n4 != -1 && !this.draggingItem.isEmpty() && bl2) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, n, ClickType.PICKUP);
                        this.slotClicked(slot, n4, 0, ClickType.PICKUP);
                        if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, n, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor(d - (double)n2);
                            this.snapbackStartY = Mth.floor(d2 - (double)n3);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            this.snapbackTime = Util.getMillis();
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackStartX = Mth.floor(d - (double)n2);
                        this.snapbackStartY = Mth.floor(d2 - (double)n3);
                        this.snapbackEnd = this.clickedSlot;
                        this.snapbackItem = this.draggingItem;
                        this.snapbackTime = Util.getMillis();
                    }
                    this.draggingItem = ItemStack.EMPTY;
                    this.clickedSlot = null;
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
                for (Slot slot3 : this.quickCraftSlots) {
                    this.slotClicked(slot3, slot3.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!this.minecraft.player.inventory.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(n)) {
                    this.slotClicked(slot, n4, n, ClickType.CLONE);
                } else {
                    boolean bl3;
                    boolean bl4 = bl3 = n4 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if (bl3) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }
                    this.slotClicked(slot, n4, n, bl3 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }
        if (this.minecraft.player.inventory.getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }
        this.isQuickCrafting = false;
        return true;
    }

    private boolean isHovering(Slot slot, double d, double d2) {
        return this.isHovering(slot.x, slot.y, 16, 16, d, d2);
    }

    protected boolean isHovering(int n, int n2, int n3, int n4, double d, double d2) {
        int n5 = this.leftPos;
        int n6 = this.topPos;
        return (d -= (double)n5) >= (double)(n - 1) && d < (double)(n + n3 + 1) && (d2 -= (double)n6) >= (double)(n2 - 1) && d2 < (double)(n2 + n4 + 1);
    }

    protected void slotClicked(Slot slot, int n, int n2, ClickType clickType) {
        if (slot != null) {
            n = slot.index;
        }
        this.minecraft.gameMode.handleInventoryMouseClick(((AbstractContainerMenu)this.menu).containerId, n, n2, clickType, this.minecraft.player);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (this.minecraft.options.keyInventory.matches(n, n2)) {
            this.onClose();
            return true;
        }
        this.checkHotbarKeyPressed(n, n2);
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches(n, n2)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if (this.minecraft.options.keyDrop.matches(n, n2)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, AbstractContainerScreen.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
        }
        return true;
    }

    protected boolean checkHotbarKeyPressed(int n, int n2) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.options.keySwapOffhand.matches(n, n2)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return true;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.minecraft.options.keyHotbarSlots[i].matches(n, n2)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        ((AbstractContainerMenu)this.menu).removed(this.minecraft.player);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeContainer();
        }
    }

    @Override
    public T getMenu() {
        return this.menu;
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }
}

