/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class InventoryMenu
extends RecipeBookMenu<CraftingContainer> {
    public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
    private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingContainer craftSlots = new CraftingContainer(this, 2, 2);
    private final ResultContainer resultSlots = new ResultContainer();
    public final boolean active;
    private final Player owner;

    public InventoryMenu(Inventory inventory, boolean bl, Player player) {
        super(null, 0);
        int n;
        this.active = bl;
        this.owner = player;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));
        for (n = 0; n < 2; ++n) {
            for (int i = 0; i < 2; ++i) {
                this.addSlot(new Slot(this.craftSlots, i + n * 2, 98 + i * 18, 18 + n * 18));
            }
        }
        for (n = 0; n < 4; ++n) {
            final EquipmentSlot equipmentSlot = SLOT_IDS[n];
            this.addSlot(new Slot(inventory, 39 - n, 8, 8 + n * 18){

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack itemStack) {
                    return equipmentSlot == Mob.getEquipmentSlotForItem(itemStack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack itemStack = this.getItem();
                    if (!itemStack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.mayPickup(player);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of((Object)BLOCK_ATLAS, (Object)TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
                }
            });
        }
        for (n = 0; n < 3; ++n) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + (n + 1) * 9, 8 + i * 18, 84 + n * 18));
            }
        }
        for (n = 0; n < 9; ++n) {
            this.addSlot(new Slot(inventory, n, 8 + n * 18, 142));
        }
        this.addSlot(new Slot(inventory, 40, 77, 62){

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of((Object)BLOCK_ATLAS, (Object)EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
        this.craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        this.resultSlots.clearContent();
        this.craftSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
        return recipe.matches(this.craftSlots, this.owner.level);
    }

    @Override
    public void slotsChanged(Container container) {
        CraftingMenu.slotChangedCraftingGrid(this.containerId, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultSlots.clearContent();
        if (player.level.isClientSide) {
            return;
        }
        this.clearContainer(player, player.level, this.craftSlots);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            int n2;
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
            if (n == 0) {
                if (!this.moveItemStackTo(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (n >= 1 && n < 5 ? !this.moveItemStackTo(itemStack2, 9, 45, false) : (n >= 5 && n < 9 ? !this.moveItemStackTo(itemStack2, 9, 45, false) : (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - equipmentSlot.getIndex())).hasItem() ? !this.moveItemStackTo(itemStack2, n2 = 8 - equipmentSlot.getIndex(), n2 + 1, false) : (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasItem() ? !this.moveItemStackTo(itemStack2, 45, 46, false) : (n >= 9 && n < 36 ? !this.moveItemStackTo(itemStack2, 36, 45, false) : (n >= 36 && n < 45 ? !this.moveItemStackTo(itemStack2, 9, 36, false) : !this.moveItemStackTo(itemStack2, 9, 45, false))))))) {
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
            ItemStack itemStack3 = slot.onTake(player, itemStack2);
            if (n == 0) {
                player.drop(itemStack3, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 5;
    }

    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

}

