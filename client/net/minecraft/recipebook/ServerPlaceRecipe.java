/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlaceRecipe<C extends Container>
implements PlaceRecipe<Integer> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final StackedContents stackedContents = new StackedContents();
    protected Inventory inventory;
    protected RecipeBookMenu<C> menu;

    public ServerPlaceRecipe(RecipeBookMenu<C> recipeBookMenu) {
        this.menu = recipeBookMenu;
    }

    public void recipeClicked(ServerPlayer serverPlayer, @Nullable Recipe<C> recipe, boolean bl) {
        if (recipe == null || !serverPlayer.getRecipeBook().contains(recipe)) {
            return;
        }
        this.inventory = serverPlayer.inventory;
        if (!this.testClearGrid() && !serverPlayer.isCreative()) {
            return;
        }
        this.stackedContents.clear();
        serverPlayer.inventory.fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        if (this.stackedContents.canCraft(recipe, null)) {
            this.handleRecipeClicked(recipe, bl);
        } else {
            this.clearGrid();
            serverPlayer.connection.send(new ClientboundPlaceGhostRecipePacket(serverPlayer.containerMenu.containerId, recipe));
        }
        serverPlayer.inventory.setChanged();
    }

    protected void clearGrid() {
        for (int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
            if (i == this.menu.getResultSlotIndex() && (this.menu instanceof CraftingMenu || this.menu instanceof InventoryMenu)) continue;
            this.moveItemToInventory(i);
        }
        this.menu.clearCraftingContent();
    }

    protected void moveItemToInventory(int n) {
        ItemStack itemStack = this.menu.getSlot(n).getItem();
        if (itemStack.isEmpty()) {
            return;
        }
        while (itemStack.getCount() > 0) {
            int n2 = this.inventory.getSlotWithRemainingSpace(itemStack);
            if (n2 == -1) {
                n2 = this.inventory.getFreeSlot();
            }
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            if (!this.inventory.add(n2, itemStack2)) {
                LOGGER.error("Can't find any space for item in the inventory");
            }
            this.menu.getSlot(n).remove(1);
        }
    }

    protected void handleRecipeClicked(Recipe<C> recipe, boolean bl) {
        int n;
        Object object;
        boolean bl2 = this.menu.recipeMatches(recipe);
        int n2 = this.stackedContents.getBiggestCraftableStack(recipe, null);
        if (bl2) {
            for (n = 0; n < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++n) {
                if (n == this.menu.getResultSlotIndex() || ((ItemStack)(object = this.menu.getSlot(n).getItem())).isEmpty() || Math.min(n2, ((ItemStack)object).getMaxStackSize()) >= ((ItemStack)object).getCount() + 1) continue;
                return;
            }
        }
        if (this.stackedContents.canCraft(recipe, (IntList)(object = new IntArrayList()), n = this.getStackSize(bl, n2, bl2))) {
            int n3 = n;
            IntListIterator intListIterator = object.iterator();
            while (intListIterator.hasNext()) {
                int n4 = (Integer)intListIterator.next();
                int n5 = StackedContents.fromStackingIndex(n4).getMaxStackSize();
                if (n5 >= n3) continue;
                n3 = n5;
            }
            n = n3;
            if (this.stackedContents.canCraft(recipe, (IntList)object, n)) {
                this.clearGrid();
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, object.iterator(), n);
            }
        }
    }

    @Override
    public void addItemToSlot(Iterator<Integer> iterator, int n, int n2, int n3, int n4) {
        Slot slot = this.menu.getSlot(n);
        ItemStack itemStack = StackedContents.fromStackingIndex(iterator.next());
        if (!itemStack.isEmpty()) {
            for (int i = 0; i < n2; ++i) {
                this.moveItemToGrid(slot, itemStack);
            }
        }
    }

    protected int getStackSize(boolean bl, int n, boolean bl2) {
        int n2 = 1;
        if (bl) {
            n2 = n;
        } else if (bl2) {
            n2 = 64;
            for (int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
                ItemStack itemStack;
                if (i == this.menu.getResultSlotIndex() || (itemStack = this.menu.getSlot(i).getItem()).isEmpty() || n2 <= itemStack.getCount()) continue;
                n2 = itemStack.getCount();
            }
            if (n2 < 64) {
                ++n2;
            }
        }
        return n2;
    }

    protected void moveItemToGrid(Slot slot, ItemStack itemStack) {
        int n = this.inventory.findSlotMatchingUnusedItem(itemStack);
        if (n == -1) {
            return;
        }
        ItemStack itemStack2 = this.inventory.getItem(n).copy();
        if (itemStack2.isEmpty()) {
            return;
        }
        if (itemStack2.getCount() > 1) {
            this.inventory.removeItem(n, 1);
        } else {
            this.inventory.removeItemNoUpdate(n);
        }
        itemStack2.setCount(1);
        if (slot.getItem().isEmpty()) {
            slot.set(itemStack2);
        } else {
            slot.getItem().grow(1);
        }
    }

    private boolean testClearGrid() {
        ArrayList arrayList = Lists.newArrayList();
        int n = this.getAmountOfFreeSlotsInInventory();
        for (int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
            ItemStack itemStack;
            if (i == this.menu.getResultSlotIndex() || (itemStack = this.menu.getSlot(i).getItem().copy()).isEmpty()) continue;
            int n2 = this.inventory.getSlotWithRemainingSpace(itemStack);
            if (n2 == -1 && arrayList.size() <= n) {
                for (ItemStack itemStack2 : arrayList) {
                    if (!itemStack2.sameItem(itemStack) || itemStack2.getCount() == itemStack2.getMaxStackSize() || itemStack2.getCount() + itemStack.getCount() > itemStack2.getMaxStackSize()) continue;
                    itemStack2.grow(itemStack.getCount());
                    itemStack.setCount(0);
                    break;
                }
                if (itemStack.isEmpty()) continue;
                if (arrayList.size() < n) {
                    arrayList.add(itemStack);
                    continue;
                }
                return false;
            }
            if (n2 != -1) continue;
            return false;
        }
        return true;
    }

    private int getAmountOfFreeSlotsInInventory() {
        int n = 0;
        for (ItemStack itemStack : this.inventory.items) {
            if (!itemStack.isEmpty()) continue;
            ++n;
        }
        return n;
    }
}

