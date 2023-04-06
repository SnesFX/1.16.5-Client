/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$FastEntrySet
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFurnaceBlockEntity
extends BaseContainerBlockEntity
implements WorldlyContainer,
RecipeHolder,
StackedContentsCompatible,
TickableBlockEntity {
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int litTime;
    private int litDuration;
    private int cookingProgress;
    private int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int n) {
            switch (n) {
                case 0: {
                    return AbstractFurnaceBlockEntity.this.litTime;
                }
                case 1: {
                    return AbstractFurnaceBlockEntity.this.litDuration;
                }
                case 2: {
                    return AbstractFurnaceBlockEntity.this.cookingProgress;
                }
                case 3: {
                    return AbstractFurnaceBlockEntity.this.cookingTotalTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int n, int n2) {
            switch (n) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.litTime = n2;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.litDuration = n2;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookingProgress = n2;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookingTotalTime = n2;
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType);
        this.recipeType = recipeType;
    }

    public static Map<Item, Integer> getFuel() {
        LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.LAVA_BUCKET, 20000);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.COAL_BLOCK, 16000);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.BLAZE_ROD, 2400);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.COAL, 1600);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.CHARCOAL, 1600);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.LOGS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.PLANKS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_STAIRS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_SLABS, 150);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_TRAPDOORS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.BIRCH_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.SPRUCE_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.JUNGLE_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.DARK_OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.ACACIA_FENCE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.BIRCH_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.SPRUCE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.JUNGLE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.DARK_OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.ACACIA_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.NOTE_BLOCK, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.BOOKSHELF, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.LECTERN, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.JUKEBOX, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.CHEST, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.TRAPPED_CHEST, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.CRAFTING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.DAYLIGHT_DETECTOR, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.BANNERS, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.BOW, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.FISHING_ROD, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.LADDER, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.SIGNS, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.WOODEN_SHOVEL, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.WOODEN_SWORD, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.WOODEN_HOE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.WOODEN_AXE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.WOODEN_PICKAXE, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_DOORS, 200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.BOATS, 1200);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOOL, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.WOODEN_BUTTONS, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.STICK, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.SAPLINGS, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.BOWL, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, ItemTags.CARPETS, 67);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.DRIED_KELP_BLOCK, 4001);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Items.CROSSBOW, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.BAMBOO, 50);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.DEAD_BUSH, 100);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.SCAFFOLDING, 400);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.LOOM, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.BARREL, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceBlockEntity.add((Map<Item, Integer>)linkedHashMap, Blocks.COMPOSTER, 300);
        return linkedHashMap;
    }

    private static boolean isNeverAFurnaceFuel(Item item) {
        return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void add(Map<Item, Integer> map, Tag<Item> tag, int n) {
        for (Item item : tag.getValues()) {
            if (AbstractFurnaceBlockEntity.isNeverAFurnaceFuel(item)) continue;
            map.put(item, n);
        }
    }

    private static void add(Map<Item, Integer> map, ItemLike itemLike, int n) {
        Item item = itemLike.asItem();
        if (AbstractFurnaceBlockEntity.isNeverAFurnaceFuel(item)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw Util.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        map.put(item, n);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.items);
        this.litTime = compoundTag.getShort("BurnTime");
        this.cookingProgress = compoundTag.getShort("CookTime");
        this.cookingTotalTime = compoundTag.getShort("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag compoundTag2 = compoundTag.getCompound("RecipesUsed");
        for (String string : compoundTag2.getAllKeys()) {
            this.recipesUsed.put((Object)new ResourceLocation(string), compoundTag2.getInt(string));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putShort("BurnTime", (short)this.litTime);
        compoundTag.putShort("CookTime", (short)this.cookingProgress);
        compoundTag.putShort("CookTimeTotal", (short)this.cookingTotalTime);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        CompoundTag compoundTag2 = new CompoundTag();
        this.recipesUsed.forEach((resourceLocation, n) -> compoundTag2.putInt(resourceLocation.toString(), (int)n));
        compoundTag.put("RecipesUsed", compoundTag2);
        return compoundTag;
    }

    @Override
    public void tick() {
        boolean bl = this.isLit();
        boolean bl2 = false;
        if (this.isLit()) {
            --this.litTime;
        }
        if (!this.level.isClientSide) {
            ItemStack itemStack = this.items.get(1);
            if (this.isLit() || !itemStack.isEmpty() && !this.items.get(0).isEmpty()) {
                Recipe recipe = this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).orElse(null);
                if (!this.isLit() && this.canBurn(recipe)) {
                    this.litDuration = this.litTime = this.getBurnDuration(itemStack);
                    if (this.isLit()) {
                        bl2 = true;
                        if (!itemStack.isEmpty()) {
                            Item item = itemStack.getItem();
                            itemStack.shrink(1);
                            if (itemStack.isEmpty()) {
                                Item item2 = item.getCraftingRemainingItem();
                                this.items.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                            }
                        }
                    }
                }
                if (this.isLit() && this.canBurn(recipe)) {
                    ++this.cookingProgress;
                    if (this.cookingProgress == this.cookingTotalTime) {
                        this.cookingProgress = 0;
                        this.cookingTotalTime = this.getTotalCookTime();
                        this.burn(recipe);
                        bl2 = true;
                    }
                } else {
                    this.cookingProgress = 0;
                }
            } else if (!this.isLit() && this.cookingProgress > 0) {
                this.cookingProgress = Mth.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
            }
            if (bl != this.isLit()) {
                bl2 = true;
                this.level.setBlock(this.worldPosition, (BlockState)this.level.getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT, this.isLit()), 3);
            }
        }
        if (bl2) {
            this.setChanged();
        }
    }

    protected boolean canBurn(@Nullable Recipe<?> recipe) {
        if (this.items.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getResultItem();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = this.items.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.sameItem(itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < this.getMaxStackSize() && itemStack2.getCount() < itemStack2.getMaxStackSize()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxStackSize();
    }

    private void burn(@Nullable Recipe<?> recipe) {
        if (recipe == null || !this.canBurn(recipe)) {
            return;
        }
        ItemStack itemStack = this.items.get(0);
        ItemStack itemStack2 = recipe.getResultItem();
        ItemStack itemStack3 = this.items.get(2);
        if (itemStack3.isEmpty()) {
            this.items.set(2, itemStack2.copy());
        } else if (itemStack3.getItem() == itemStack2.getItem()) {
            itemStack3.grow(1);
        }
        if (!this.level.isClientSide) {
            this.setRecipeUsed(recipe);
        }
        if (itemStack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        itemStack.shrink(1);
    }

    protected int getBurnDuration(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        Item item = itemStack.getItem();
        return AbstractFurnaceBlockEntity.getFuel().getOrDefault(item, 0);
    }

    protected int getTotalCookTime() {
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    public static boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.getFuel().containsKey(itemStack.getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        }
        if (direction == Direction.UP) {
            return SLOTS_FOR_UP;
        }
        return SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int n, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(n, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int n, ItemStack itemStack, Direction direction) {
        Item item;
        return direction != Direction.DOWN || n != 1 || (item = itemStack.getItem()) == Items.WATER_BUCKET || item == Items.BUCKET;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int n) {
        return this.items.get(n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        return ContainerHelper.removeItem(this.items, n, n2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        return ContainerHelper.takeItem(this.items, n);
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        ItemStack itemStack2 = this.items.get(n);
        boolean bl = !itemStack.isEmpty() && itemStack.sameItem(itemStack2) && ItemStack.tagMatches(itemStack, itemStack2);
        this.items.set(n, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        if (n == 0 && !bl) {
            this.cookingTotalTime = this.getTotalCookTime();
            this.cookingProgress = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int n, ItemStack itemStack) {
        if (n == 2) {
            return false;
        }
        if (n == 1) {
            ItemStack itemStack2 = this.items.get(1);
            return AbstractFurnaceBlockEntity.isFuel(itemStack) || itemStack.getItem() == Items.BUCKET && itemStack2.getItem() != Items.BUCKET;
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourceLocation = recipe.getId();
            this.recipesUsed.addTo((Object)resourceLocation, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player) {
    }

    public void awardUsedRecipesAndPopExperience(Player player) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(player.level, player.position());
        player.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(Level level, Vec3 vec3) {
        ArrayList arrayList = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent(recipe -> {
                arrayList.add(recipe);
                AbstractFurnaceBlockEntity.createExperience(level, vec3, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }
        return arrayList;
    }

    private static void createExperience(Level level, Vec3 vec3, int n, float f) {
        int n2 = Mth.floor((float)n * f);
        float f2 = Mth.frac((float)n * f);
        if (f2 != 0.0f && Math.random() < (double)f2) {
            ++n2;
        }
        while (n2 > 0) {
            int n3 = ExperienceOrb.getExperienceValue(n2);
            n2 -= n3;
            level.addFreshEntity(new ExperienceOrb(level, vec3.x, vec3.y, vec3.z, n3));
        }
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack itemStack : this.items) {
            stackedContents.accountStack(itemStack);
        }
    }

}

