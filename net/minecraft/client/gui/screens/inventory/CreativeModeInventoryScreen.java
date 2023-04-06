/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class CreativeModeInventoryScreen
extends EffectRenderingInventoryScreen<ItemPickerMenu> {
    private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final SimpleContainer CONTAINER = new SimpleContainer(45);
    private static final Component TRASH_SLOT_TOOLTIP = new TranslatableComponent("inventory.binSlot");
    private static int selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
    private float scrollOffs;
    private boolean scrolling;
    private EditBox searchBox;
    @Nullable
    private List<Slot> originalSlots;
    @Nullable
    private Slot destroyItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTextInput;
    private boolean hasClickedOutside;
    private final Map<ResourceLocation, Tag<Item>> visibleTags = Maps.newTreeMap();

    public CreativeModeInventoryScreen(Player player) {
        super(new ItemPickerMenu(player), player.inventory, TextComponent.EMPTY);
        player.containerMenu = this.menu;
        this.passEvents = true;
        this.imageHeight = 136;
        this.imageWidth = 195;
    }

    @Override
    public void tick() {
        if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        } else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int n, int n2, ClickType clickType) {
        if (this.isCreativeSlot(slot)) {
            this.searchBox.moveCursorToEnd();
            this.searchBox.setHighlightPos(0);
        }
        boolean bl = clickType == ClickType.QUICK_MOVE;
        ClickType clickType2 = clickType = n == -999 && clickType == ClickType.PICKUP ? ClickType.THROW : clickType;
        if (slot != null || selectedTab == CreativeModeTab.TAB_INVENTORY.getId() || clickType == ClickType.QUICK_CRAFT) {
            if (slot != null && !slot.mayPickup(this.minecraft.player)) {
                return;
            }
            if (slot == this.destroyItemSlot && bl) {
                for (int i = 0; i < this.minecraft.player.inventoryMenu.getItems().size(); ++i) {
                    this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i);
                }
            } else if (selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
                if (slot == this.destroyItemSlot) {
                    this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
                } else if (clickType == ClickType.THROW && slot != null && slot.hasItem()) {
                    ItemStack itemStack = slot.remove(n2 == 0 ? 1 : slot.getItem().getMaxStackSize());
                    ItemStack itemStack2 = slot.getItem();
                    this.minecraft.player.drop(itemStack, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack);
                    this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack2, SlotWrapper.access$100((SlotWrapper)((SlotWrapper)slot)).index);
                } else if (clickType == ClickType.THROW && !this.minecraft.player.inventory.getCarried().isEmpty()) {
                    this.minecraft.player.drop(this.minecraft.player.inventory.getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(this.minecraft.player.inventory.getCarried());
                    this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
                } else {
                    this.minecraft.player.inventoryMenu.clicked(slot == null ? n : SlotWrapper.access$100((SlotWrapper)((SlotWrapper)slot)).index, n2, clickType, this.minecraft.player);
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            } else if (clickType != ClickType.QUICK_CRAFT && slot.container == CONTAINER) {
                Inventory inventory = this.minecraft.player.inventory;
                ItemStack itemStack = inventory.getCarried();
                ItemStack itemStack3 = slot.getItem();
                if (clickType == ClickType.SWAP) {
                    if (!itemStack3.isEmpty()) {
                        ItemStack itemStack4 = itemStack3.copy();
                        itemStack4.setCount(itemStack4.getMaxStackSize());
                        this.minecraft.player.inventory.setItem(n2, itemStack4);
                        this.minecraft.player.inventoryMenu.broadcastChanges();
                    }
                    return;
                }
                if (clickType == ClickType.CLONE) {
                    if (inventory.getCarried().isEmpty() && slot.hasItem()) {
                        ItemStack itemStack5 = slot.getItem().copy();
                        itemStack5.setCount(itemStack5.getMaxStackSize());
                        inventory.setCarried(itemStack5);
                    }
                    return;
                }
                if (clickType == ClickType.THROW) {
                    if (!itemStack3.isEmpty()) {
                        ItemStack itemStack6 = itemStack3.copy();
                        itemStack6.setCount(n2 == 0 ? 1 : itemStack6.getMaxStackSize());
                        this.minecraft.player.drop(itemStack6, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack6);
                    }
                    return;
                }
                if (!itemStack.isEmpty() && !itemStack3.isEmpty() && itemStack.sameItem(itemStack3) && ItemStack.tagMatches(itemStack, itemStack3)) {
                    if (n2 == 0) {
                        if (bl) {
                            itemStack.setCount(itemStack.getMaxStackSize());
                        } else if (itemStack.getCount() < itemStack.getMaxStackSize()) {
                            itemStack.grow(1);
                        }
                    } else {
                        itemStack.shrink(1);
                    }
                } else if (itemStack3.isEmpty() || !itemStack.isEmpty()) {
                    if (n2 == 0) {
                        inventory.setCarried(ItemStack.EMPTY);
                    } else {
                        inventory.getCarried().shrink(1);
                    }
                } else {
                    inventory.setCarried(itemStack3.copy());
                    itemStack = inventory.getCarried();
                    if (bl) {
                        itemStack.setCount(itemStack.getMaxStackSize());
                    }
                }
            } else if (this.menu != null) {
                ItemStack itemStack = slot == null ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
                ((ItemPickerMenu)this.menu).clicked(slot == null ? n : slot.index, n2, clickType, this.minecraft.player);
                if (AbstractContainerMenu.getQuickcraftHeader(n2) == 2) {
                    for (int i = 0; i < 9; ++i) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(((ItemPickerMenu)this.menu).getSlot(45 + i).getItem(), 36 + i);
                    }
                } else if (slot != null) {
                    ItemStack itemStack7 = ((ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
                    this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack7, slot.index - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    int n3 = 45 + n2;
                    if (clickType == ClickType.SWAP) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, n3 - ((ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                    } else if (clickType == ClickType.THROW && !itemStack.isEmpty()) {
                        ItemStack itemStack8 = itemStack.copy();
                        itemStack8.setCount(n2 == 0 ? 1 : itemStack8.getMaxStackSize());
                        this.minecraft.player.drop(itemStack8, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack8);
                    }
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            }
        } else {
            Inventory inventory = this.minecraft.player.inventory;
            if (!inventory.getCarried().isEmpty() && this.hasClickedOutside) {
                if (n2 == 0) {
                    this.minecraft.player.drop(inventory.getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(inventory.getCarried());
                    inventory.setCarried(ItemStack.EMPTY);
                }
                if (n2 == 1) {
                    ItemStack itemStack = inventory.getCarried().split(1);
                    this.minecraft.player.drop(itemStack, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(itemStack);
                }
            }
        }
    }

    private boolean isCreativeSlot(@Nullable Slot slot) {
        return slot != null && slot.container == CONTAINER;
    }

    @Override
    protected void checkEffectRendering() {
        int n = this.leftPos;
        super.checkEffectRendering();
        if (this.searchBox != null && this.leftPos != n) {
            this.searchBox.setX(this.leftPos + 82);
        }
    }

    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            super.init();
            this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
            this.font.getClass();
            this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, new TranslatableComponent("itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setBordered(false);
            this.searchBox.setVisible(false);
            this.searchBox.setTextColor(16777215);
            this.children.add(this.searchBox);
            int n = selectedTab;
            selectedTab = -1;
            this.selectTab(CreativeModeTab.TABS[n]);
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
            this.listener = new CreativeInventoryListener(this.minecraft);
            this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
        } else {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.searchBox.getValue();
        this.init(minecraft, n, n2);
        this.searchBox.setValue(string);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        }
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean charTyped(char c, int n) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
            return false;
        }
        String string = this.searchBox.getValue();
        if (this.searchBox.charTyped(c, n)) {
            if (!Objects.equals(string, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        this.ignoreTextInput = false;
        if (selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
            if (this.minecraft.options.keyChat.matches(n, n2)) {
                this.ignoreTextInput = true;
                this.selectTab(CreativeModeTab.TAB_SEARCH);
                return true;
            }
            return super.keyPressed(n, n2, n3);
        }
        boolean bl = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
        boolean bl2 = InputConstants.getKey(n, n2).getNumericKeyValue().isPresent();
        if (bl && bl2 && this.checkHotbarKeyPressed(n, n2)) {
            this.ignoreTextInput = true;
            return true;
        }
        String string = this.searchBox.getValue();
        if (this.searchBox.keyPressed(n, n2, n3)) {
            if (!Objects.equals(string, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && n != 256) {
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public boolean keyReleased(int n, int n2, int n3) {
        this.ignoreTextInput = false;
        return super.keyReleased(n, n2, n3);
    }

    private void refreshSearchResults() {
        ((ItemPickerMenu)this.menu).items.clear();
        this.visibleTags.clear();
        String string = this.searchBox.getValue();
        if (string.isEmpty()) {
            for (Item item : Registry.ITEM) {
                item.fillItemCategory(CreativeModeTab.TAB_SEARCH, ((ItemPickerMenu)this.menu).items);
            }
        } else {
            MutableSearchTree<ItemStack> mutableSearchTree;
            if (string.startsWith("#")) {
                string = string.substring(1);
                mutableSearchTree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
                this.updateVisibleTags(string);
            } else {
                mutableSearchTree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
            }
            ((ItemPickerMenu)this.menu).items.addAll(mutableSearchTree.search(string.toLowerCase(Locale.ROOT)));
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    private void updateVisibleTags(String string) {
        Object object;
        Predicate<ResourceLocation> predicate;
        int n = string.indexOf(58);
        if (n == -1) {
            predicate = resourceLocation -> resourceLocation.getPath().contains(string);
        } else {
            object = string.substring(0, n).trim();
            String string2 = string.substring(n + 1).trim();
            predicate = arg_0 -> CreativeModeInventoryScreen.lambda$updateVisibleTags$1((String)object, string2, arg_0);
        }
        object = ItemTags.getAllTags();
        object.getAvailableTags().stream().filter(predicate).forEach(arg_0 -> this.lambda$updateVisibleTags$2((TagCollection)object, arg_0));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        CreativeModeTab creativeModeTab = CreativeModeTab.TABS[selectedTab];
        if (creativeModeTab.showTitle()) {
            RenderSystem.disableBlend();
            this.font.draw(poseStack, creativeModeTab.getDisplayName(), 8.0f, 6.0f, 4210752);
        }
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (n == 0) {
            double d3 = d - (double)this.leftPos;
            double d4 = d2 - (double)this.topPos;
            for (CreativeModeTab creativeModeTab : CreativeModeTab.TABS) {
                if (!this.checkTabClicked(creativeModeTab, d3, d4)) continue;
                return true;
            }
            if (selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && this.insideScrollbar(d, d2)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        if (n == 0) {
            double d3 = d - (double)this.leftPos;
            double d4 = d2 - (double)this.topPos;
            this.scrolling = false;
            for (CreativeModeTab creativeModeTab : CreativeModeTab.TABS) {
                if (!this.checkTabClicked(creativeModeTab, d3, d4)) continue;
                this.selectTab(creativeModeTab);
                return true;
            }
        }
        return super.mouseReleased(d, d2, n);
    }

    private boolean canScroll() {
        return selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[selectedTab].canScroll() && ((ItemPickerMenu)this.menu).canScroll();
    }

    private void selectTab(CreativeModeTab creativeModeTab) {
        int n;
        Object object;
        int n2;
        Object object2;
        Object object3;
        Object object4;
        int n3 = selectedTab;
        selectedTab = creativeModeTab.getId();
        this.quickCraftSlots.clear();
        ((ItemPickerMenu)this.menu).items.clear();
        if (creativeModeTab == CreativeModeTab.TAB_HOTBAR) {
            object4 = this.minecraft.getHotbarManager();
            for (n2 = 0; n2 < 9; ++n2) {
                Hotbar hotbar = ((HotbarManager)object4).get(n2);
                if (hotbar.isEmpty()) {
                    for (n = 0; n < 9; ++n) {
                        if (n == n2) {
                            object2 = new ItemStack(Items.PAPER);
                            ((ItemStack)object2).getOrCreateTagElement("CustomCreativeLock");
                            object3 = this.minecraft.options.keyHotbarSlots[n2].getTranslatedKeyMessage();
                            object = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            ((ItemStack)object2).setHoverName(new TranslatableComponent("inventory.hotbarInfo", object, object3));
                            ((ItemPickerMenu)this.menu).items.add((ItemStack)object2);
                            continue;
                        }
                        ((ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((ItemPickerMenu)this.menu).items.addAll((Collection<ItemStack>)((Object)hotbar));
            }
        } else if (creativeModeTab != CreativeModeTab.TAB_SEARCH) {
            creativeModeTab.fillItemList(((ItemPickerMenu)this.menu).items);
        }
        if (creativeModeTab == CreativeModeTab.TAB_INVENTORY) {
            object4 = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf((Collection)((ItemPickerMenu)this.menu).slots);
            }
            ((ItemPickerMenu)this.menu).slots.clear();
            for (n2 = 0; n2 < ((AbstractContainerMenu)object4).slots.size(); ++n2) {
                int n4;
                if (n2 >= 5 && n2 < 9) {
                    int n5 = n2 - 5;
                    object3 = n5 / 2;
                    object = n5 % 2;
                    n4 = 54 + object3 * 54;
                    n = 6 + object * 27;
                } else if (n2 >= 0 && n2 < 5) {
                    n4 = -2000;
                    n = -2000;
                } else if (n2 == 45) {
                    n4 = 35;
                    n = 20;
                } else {
                    int n6 = n2 - 9;
                    object3 = n6 % 9;
                    object = n6 / 9;
                    n4 = 9 + object3 * 18;
                    n = n2 >= 36 ? 112 : 54 + object * 18;
                }
                object2 = new SlotWrapper(((AbstractContainerMenu)object4).slots.get(n2), n2, n4, n);
                ((ItemPickerMenu)this.menu).slots.add(object2);
            }
            this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
            ((ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
        } else if (n3 == CreativeModeTab.TAB_INVENTORY.getId()) {
            ((ItemPickerMenu)this.menu).slots.clear();
            ((ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
            this.originalSlots = null;
        }
        if (this.searchBox != null) {
            if (creativeModeTab == CreativeModeTab.TAB_SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.setCanLoseFocus(false);
                this.searchBox.setFocus(true);
                if (n3 != creativeModeTab.getId()) {
                    this.searchBox.setValue("");
                }
                this.refreshSearchResults();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.setCanLoseFocus(true);
                this.searchBox.setFocus(false);
                this.searchBox.setValue("");
            }
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        if (!this.canScroll()) {
            return false;
        }
        int n = (((ItemPickerMenu)this.menu).items.size() + 9 - 1) / 9 - 5;
        this.scrollOffs = (float)((double)this.scrollOffs - d3 / (double)n);
        this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double d, double d2, int n, int n2, int n3) {
        boolean bl = d < (double)n || d2 < (double)n2 || d >= (double)(n + this.imageWidth) || d2 >= (double)(n2 + this.imageHeight);
        this.hasClickedOutside = bl && !this.checkTabClicked(CreativeModeTab.TABS[selectedTab], d, d2);
        return this.hasClickedOutside;
    }

    protected boolean insideScrollbar(double d, double d2) {
        int n = this.leftPos;
        int n2 = this.topPos;
        int n3 = n + 175;
        int n4 = n2 + 18;
        int n5 = n3 + 14;
        int n6 = n4 + 112;
        return d >= (double)n3 && d2 >= (double)n4 && d < (double)n5 && d2 < (double)n6;
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.scrolling) {
            int n2 = this.topPos + 18;
            int n3 = n2 + 112;
            this.scrollOffs = ((float)d2 - (float)n2 - 7.5f) / ((float)(n3 - n2) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            return true;
        }
        return super.mouseDragged(d, d2, n, d3, d4);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        for (CreativeModeTab creativeModeTab : CreativeModeTab.TABS) {
            if (this.checkTabHovering(poseStack, creativeModeTab, n, n2)) break;
        }
        if (this.destroyItemSlot != null && selectedTab == CreativeModeTab.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, n, n2)) {
            this.renderTooltip(poseStack, TRASH_SLOT_TOOLTIP, n, n2);
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderTooltip(poseStack, n, n2);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, ItemStack itemStack, int n, int n2) {
        if (selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
            Map<Enchantment, Integer> map;
            List<Component> list = itemStack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
            ArrayList arrayList = Lists.newArrayList(list);
            Item item = itemStack.getItem();
            CreativeModeTab creativeModeTab = item.getItemCategory();
            if (creativeModeTab == null && item == Items.ENCHANTED_BOOK && (map = EnchantmentHelper.getEnchantments(itemStack)).size() == 1) {
                Enchantment enchantment = map.keySet().iterator().next();
                for (CreativeModeTab creativeModeTab2 : CreativeModeTab.TABS) {
                    if (!creativeModeTab2.hasEnchantmentCategory(enchantment.category)) continue;
                    creativeModeTab = creativeModeTab2;
                    break;
                }
            }
            this.visibleTags.forEach((resourceLocation, tag) -> {
                if (tag.contains(item)) {
                    arrayList.add(1, new TextComponent("#" + resourceLocation).withStyle(ChatFormatting.DARK_PURPLE));
                }
            });
            if (creativeModeTab != null) {
                arrayList.add(1, creativeModeTab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
            this.renderComponentTooltip(poseStack, arrayList, n, n2);
        } else {
            super.renderTooltip(poseStack, itemStack, n, n2);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        CreativeModeTab creativeModeTab = CreativeModeTab.TABS[selectedTab];
        for (CreativeModeTab creativeModeTab2 : CreativeModeTab.TABS) {
            this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
            if (creativeModeTab2.getId() == selectedTab) continue;
            this.renderTabButton(poseStack, creativeModeTab2);
        }
        this.minecraft.getTextureManager().bind(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativeModeTab.getBackgroundSuffix()));
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.searchBox.render(poseStack, n, n2, f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int n3 = this.leftPos + 175;
        int n4 = this.topPos + 18;
        int n5 = n4 + 112;
        this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
        if (creativeModeTab.canScroll()) {
            this.blit(poseStack, n3, n4 + (int)((float)(n5 - n4 - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        }
        this.renderTabButton(poseStack, creativeModeTab);
        if (creativeModeTab == CreativeModeTab.TAB_INVENTORY) {
            InventoryScreen.renderEntityInInventory(this.leftPos + 88, this.topPos + 45, 20, this.leftPos + 88 - n, this.topPos + 45 - 30 - n2, this.minecraft.player);
        }
    }

    protected boolean checkTabClicked(CreativeModeTab creativeModeTab, double d, double d2) {
        int n = creativeModeTab.getColumn();
        int n2 = 28 * n;
        int n3 = 0;
        if (creativeModeTab.isAlignedRight()) {
            n2 = this.imageWidth - 28 * (6 - n) + 2;
        } else if (n > 0) {
            n2 += n;
        }
        n3 = creativeModeTab.isTopRow() ? (n3 -= 32) : (n3 += this.imageHeight);
        return d >= (double)n2 && d <= (double)(n2 + 28) && d2 >= (double)n3 && d2 <= (double)(n3 + 32);
    }

    protected boolean checkTabHovering(PoseStack poseStack, CreativeModeTab creativeModeTab, int n, int n2) {
        int n3 = creativeModeTab.getColumn();
        int n4 = 28 * n3;
        int n5 = 0;
        if (creativeModeTab.isAlignedRight()) {
            n4 = this.imageWidth - 28 * (6 - n3) + 2;
        } else if (n3 > 0) {
            n4 += n3;
        }
        n5 = creativeModeTab.isTopRow() ? (n5 -= 32) : (n5 += this.imageHeight);
        if (this.isHovering(n4 + 3, n5 + 3, 23, 27, n, n2)) {
            this.renderTooltip(poseStack, creativeModeTab.getDisplayName(), n, n2);
            return true;
        }
        return false;
    }

    protected void renderTabButton(PoseStack poseStack, CreativeModeTab creativeModeTab) {
        boolean bl = creativeModeTab.getId() == selectedTab;
        boolean bl2 = creativeModeTab.isTopRow();
        int n = creativeModeTab.getColumn();
        int n2 = n * 28;
        int n3 = 0;
        int n4 = this.leftPos + 28 * n;
        int n5 = this.topPos;
        int n6 = 32;
        if (bl) {
            n3 += 32;
        }
        if (creativeModeTab.isAlignedRight()) {
            n4 = this.leftPos + this.imageWidth - 28 * (6 - n);
        } else if (n > 0) {
            n4 += n;
        }
        if (bl2) {
            n5 -= 28;
        } else {
            n3 += 64;
            n5 += this.imageHeight - 4;
        }
        this.blit(poseStack, n4, n5, n2, n3, 28, 32);
        this.itemRenderer.blitOffset = 100.0f;
        int n7 = bl2 ? 1 : -1;
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = creativeModeTab.getIconItem();
        this.itemRenderer.renderAndDecorateItem(itemStack, n4 += 6, n5 += 8 + n7);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, n4, n5);
        this.itemRenderer.blitOffset = 0.0f;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public static void handleHotbarLoadOrSave(Minecraft minecraft, int n, boolean bl, boolean bl2) {
        LocalPlayer localPlayer = minecraft.player;
        HotbarManager hotbarManager = minecraft.getHotbarManager();
        Hotbar hotbar = hotbarManager.get(n);
        if (bl) {
            for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
                ItemStack itemStack = ((ItemStack)hotbar.get(i)).copy();
                localPlayer.inventory.setItem(i, itemStack);
                minecraft.gameMode.handleCreativeModeItemAdd(itemStack, 36 + i);
            }
            localPlayer.inventoryMenu.broadcastChanges();
        } else if (bl2) {
            for (int i = 0; i < Inventory.getSelectionSize(); ++i) {
                hotbar.set(i, (Object)localPlayer.inventory.getItem(i).copy());
            }
            Component component = minecraft.options.keyHotbarSlots[n].getTranslatedKeyMessage();
            Component component2 = minecraft.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
            minecraft.gui.setOverlayMessage(new TranslatableComponent("inventory.hotbarSaved", component2, component), false);
            hotbarManager.save();
        }
    }

    private /* synthetic */ void lambda$updateVisibleTags$2(TagCollection tagCollection, ResourceLocation resourceLocation) {
        this.visibleTags.put(resourceLocation, tagCollection.getTag(resourceLocation));
    }

    private static /* synthetic */ boolean lambda$updateVisibleTags$1(String string, String string2, ResourceLocation resourceLocation) {
        return resourceLocation.getNamespace().contains(string) && resourceLocation.getPath().contains(string2);
    }

    static class CustomCreativeSlot
    extends Slot {
        public CustomCreativeSlot(Container container, int n, int n2, int n3) {
            super(container, n, n2, n3);
        }

        @Override
        public boolean mayPickup(Player player) {
            if (super.mayPickup(player) && this.hasItem()) {
                return this.getItem().getTagElement("CustomCreativeLock") == null;
            }
            return !this.hasItem();
        }
    }

    static class SlotWrapper
    extends Slot {
        private final Slot target;

        public SlotWrapper(Slot slot, int n, int n2, int n3) {
            super(slot.container, n, n2, n3);
            this.target = slot;
        }

        @Override
        public ItemStack onTake(Player player, ItemStack itemStack) {
            return this.target.onTake(player, itemStack);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return this.target.mayPlace(itemStack);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void set(ItemStack itemStack) {
            this.target.set(itemStack);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack itemStack) {
            return this.target.getMaxStackSize(itemStack);
        }

        @Nullable
        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        @Override
        public ItemStack remove(int n) {
            return this.target.remove(n);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        @Override
        public boolean mayPickup(Player player) {
            return this.target.mayPickup(player);
        }

        static /* synthetic */ Slot access$100(SlotWrapper slotWrapper) {
            return slotWrapper.target;
        }
    }

    public static class ItemPickerMenu
    extends AbstractContainerMenu {
        public final NonNullList<ItemStack> items = NonNullList.create();

        public ItemPickerMenu(Player player) {
            super(null, 0);
            int n;
            Inventory inventory = player.inventory;
            for (n = 0; n < 5; ++n) {
                for (int i = 0; i < 9; ++i) {
                    this.addSlot(new CustomCreativeSlot(CONTAINER, n * 9 + i, 9 + i * 18, 18 + n * 18));
                }
            }
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(inventory, n, 9 + n * 18, 112));
            }
            this.scrollTo(0.0f);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        public void scrollTo(float f) {
            int n = (this.items.size() + 9 - 1) / 9 - 5;
            int n2 = (int)((double)(f * (float)n) + 0.5);
            if (n2 < 0) {
                n2 = 0;
            }
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    int n3 = j + (i + n2) * 9;
                    if (n3 >= 0 && n3 < this.items.size()) {
                        CONTAINER.setItem(j + i * 9, this.items.get(n3));
                        continue;
                    }
                    CONTAINER.setItem(j + i * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean canScroll() {
            return this.items.size() > 45;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int n) {
            Slot slot;
            if (n >= this.slots.size() - 9 && n < this.slots.size() && (slot = (Slot)this.slots.get(n)) != null && slot.hasItem()) {
                slot.set(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
            return slot.container != CONTAINER;
        }

        @Override
        public boolean canDragTo(Slot slot) {
            return slot.container != CONTAINER;
        }
    }

}

