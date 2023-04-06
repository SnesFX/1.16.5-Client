/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;

public class LoomMenu
extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot selectedBannerPatternIndex = DataSlot.standalone();
    private Runnable slotUpdateListener = () -> {};
    private final Slot bannerSlot;
    private final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot resultSlot;
    private long lastSoundTime;
    private final Container inputContainer = new SimpleContainer(3){

        @Override
        public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotsChanged(this);
            LoomMenu.this.slotUpdateListener.run();
        }
    };
    private final Container outputContainer = new SimpleContainer(1){

        @Override
        public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotUpdateListener.run();
        }
    };

    public LoomMenu(int n, Inventory inventory) {
        this(n, inventory, ContainerLevelAccess.NULL);
    }

    public LoomMenu(int n, Inventory inventory, final ContainerLevelAccess containerLevelAccess) {
        super(MenuType.LOOM, n);
        int n2;
        this.access = containerLevelAccess;
        this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof BannerPatternItem;
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public ItemStack onTake(Player player, ItemStack itemStack) {
                LoomMenu.this.bannerSlot.remove(1);
                LoomMenu.this.dyeSlot.remove(1);
                if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
                    LoomMenu.this.selectedBannerPatternIndex.set(0);
                }
                containerLevelAccess.execute((level, blockPos) -> {
                    long l = level.getGameTime();
                    if (LoomMenu.this.lastSoundTime != l) {
                        level.playSound(null, (BlockPos)blockPos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        LoomMenu.this.lastSoundTime = l;
                    }
                });
                return super.onTake(player, itemStack);
            }
        });
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 8 + i * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 8 + n2 * 18, 142));
        }
        this.addDataSlot(this.selectedBannerPatternIndex);
    }

    public int getSelectedBannerPatternIndex() {
        return this.selectedBannerPatternIndex.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return LoomMenu.stillValid(this.access, player, Blocks.LOOM);
    }

    @Override
    public boolean clickMenuButton(Player player, int n) {
        if (n > 0 && n <= BannerPattern.AVAILABLE_PATTERNS) {
            this.selectedBannerPatternIndex.set(n);
            this.setupResultSlot();
            return true;
        }
        return false;
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack itemStack = this.bannerSlot.getItem();
        ItemStack itemStack2 = this.dyeSlot.getItem();
        ItemStack itemStack3 = this.patternSlot.getItem();
        ItemStack itemStack4 = this.resultSlot.getItem();
        if (!itemStack4.isEmpty() && (itemStack.isEmpty() || itemStack2.isEmpty() || this.selectedBannerPatternIndex.get() <= 0 || this.selectedBannerPatternIndex.get() >= BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT && itemStack3.isEmpty())) {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectedBannerPatternIndex.set(0);
        } else if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
            boolean bl;
            CompoundTag compoundTag = itemStack.getOrCreateTagElement("BlockEntityTag");
            boolean bl2 = bl = compoundTag.contains("Patterns", 9) && !itemStack.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
            if (bl) {
                this.selectedBannerPatternIndex.set(0);
            } else {
                this.selectedBannerPatternIndex.set(((BannerPatternItem)itemStack3.getItem()).getBannerPattern().ordinal());
            }
        }
        this.setupResultSlot();
        this.broadcastChanges();
    }

    public void registerUpdateListener(Runnable runnable) {
        this.slotUpdateListener = runnable;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == this.resultSlot.index) {
                if (!this.moveItemStackTo(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n == this.dyeSlot.index || n == this.bannerSlot.index || n == this.patternSlot.index ? !this.moveItemStackTo(itemStack2, 4, 40, false) : (itemStack2.getItem() instanceof BannerItem ? !this.moveItemStackTo(itemStack2, this.bannerSlot.index, this.bannerSlot.index + 1, false) : (itemStack2.getItem() instanceof DyeItem ? !this.moveItemStackTo(itemStack2, this.dyeSlot.index, this.dyeSlot.index + 1, false) : (itemStack2.getItem() instanceof BannerPatternItem ? !this.moveItemStackTo(itemStack2, this.patternSlot.index, this.patternSlot.index + 1, false) : (n >= 4 && n < 31 ? !this.moveItemStackTo(itemStack2, 31, 40, false) : n >= 31 && n < 40 && !this.moveItemStackTo(itemStack2, 4, 31, false)))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, player.level, this.inputContainer));
    }

    private void setupResultSlot() {
        if (this.selectedBannerPatternIndex.get() > 0) {
            ItemStack itemStack = this.bannerSlot.getItem();
            ItemStack itemStack2 = this.dyeSlot.getItem();
            ItemStack itemStack3 = ItemStack.EMPTY;
            if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
                ListTag listTag;
                itemStack3 = itemStack.copy();
                itemStack3.setCount(1);
                BannerPattern bannerPattern = BannerPattern.values()[this.selectedBannerPatternIndex.get()];
                DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getDyeColor();
                CompoundTag compoundTag = itemStack3.getOrCreateTagElement("BlockEntityTag");
                if (compoundTag.contains("Patterns", 9)) {
                    listTag = compoundTag.getList("Patterns", 10);
                } else {
                    listTag = new ListTag();
                    compoundTag.put("Patterns", listTag);
                }
                CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.putString("Pattern", bannerPattern.getHashname());
                compoundTag2.putInt("Color", dyeColor.getId());
                listTag.add(compoundTag2);
            }
            if (!ItemStack.matches(itemStack3, this.resultSlot.getItem())) {
                this.resultSlot.set(itemStack3);
            }
        }
    }

    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    public Slot getResultSlot() {
        return this.resultSlot;
    }

}

