/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonParseException
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ITEM.fieldOf("id").forGetter(itemStack -> itemStack.item), (App)Codec.INT.fieldOf("Count").forGetter(itemStack -> itemStack.count), (App)CompoundTag.CODEC.optionalFieldOf("tag").forGetter(itemStack -> Optional.ofNullable(itemStack.tag))).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> ItemStack.new(arg_0, arg_1, arg_2)));
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemLike)null);
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
    private int count;
    private int popTime;
    @Deprecated
    private final Item item;
    private CompoundTag tag;
    private boolean emptyCacheFlag;
    private Entity entityRepresentation;
    private BlockInWorld cachedBreakBlock;
    private boolean cachedBreakBlockResult;
    private BlockInWorld cachedPlaceBlock;
    private boolean cachedPlaceBlockResult;

    public ItemStack(ItemLike itemLike) {
        this(itemLike, 1);
    }

    private ItemStack(ItemLike itemLike, int n, Optional<CompoundTag> optional) {
        this(itemLike, n);
        optional.ifPresent(this::setTag);
    }

    public ItemStack(ItemLike itemLike, int n) {
        this.item = itemLike == null ? null : itemLike.asItem();
        this.count = n;
        if (this.item != null && this.item.canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        this.updateEmptyCacheFlag();
    }

    private void updateEmptyCacheFlag() {
        this.emptyCacheFlag = false;
        this.emptyCacheFlag = this.isEmpty();
    }

    private ItemStack(CompoundTag compoundTag) {
        this.item = Registry.ITEM.get(new ResourceLocation(compoundTag.getString("id")));
        this.count = compoundTag.getByte("Count");
        if (compoundTag.contains("tag", 10)) {
            this.tag = compoundTag.getCompound("tag");
            this.getItem().verifyTagAfterLoad(compoundTag);
        }
        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
        this.updateEmptyCacheFlag();
    }

    public static ItemStack of(CompoundTag compoundTag) {
        try {
            return new ItemStack(compoundTag);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)compoundTag, (Object)runtimeException);
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (this.getItem() == null || this.getItem() == Items.AIR) {
            return true;
        }
        return this.count <= 0;
    }

    public ItemStack split(int n) {
        int n2 = Math.min(n, this.count);
        ItemStack itemStack = this.copy();
        itemStack.setCount(n2);
        this.shrink(n2);
        return itemStack;
    }

    public Item getItem() {
        return this.emptyCacheFlag ? Items.AIR : this.item;
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockInWorld blockInWorld = new BlockInWorld(useOnContext.getLevel(), blockPos, false);
        if (player != null && !player.abilities.mayBuild && !this.hasAdventureModePlaceTagForBlock(useOnContext.getLevel().getTagManager(), blockInWorld)) {
            return InteractionResult.PASS;
        }
        Item item = this.getItem();
        InteractionResult interactionResult = item.useOn(useOnContext);
        if (player != null && interactionResult.consumesAction()) {
            player.awardStat(Stats.ITEM_USED.get(item));
        }
        return interactionResult;
    }

    public float getDestroySpeed(BlockState blockState) {
        return this.getItem().getDestroySpeed(this, blockState);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return this.getItem().use(level, player, interactionHand);
    }

    public ItemStack finishUsingItem(Level level, LivingEntity livingEntity) {
        return this.getItem().finishUsingItem(this, level, livingEntity);
    }

    public CompoundTag save(CompoundTag compoundTag) {
        ResourceLocation resourceLocation = Registry.ITEM.getKey(this.getItem());
        compoundTag.putString("id", resourceLocation == null ? "minecraft:air" : resourceLocation.toString());
        compoundTag.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            compoundTag.put("tag", this.tag.copy());
        }
        return compoundTag;
    }

    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
    }

    public boolean isDamageableItem() {
        if (this.emptyCacheFlag || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        CompoundTag compoundTag = this.getTag();
        return compoundTag == null || !compoundTag.getBoolean("Unbreakable");
    }

    public boolean isDamaged() {
        return this.isDamageableItem() && this.getDamageValue() > 0;
    }

    public int getDamageValue() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamageValue(int n) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, n));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean hurt(int n, Random random, @Nullable ServerPlayer serverPlayer) {
        int n2;
        if (!this.isDamageableItem()) {
            return false;
        }
        if (n > 0) {
            n2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int n3 = 0;
            for (int i = 0; n2 > 0 && i < n; ++i) {
                if (!DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, n2, random)) continue;
                ++n3;
            }
            if ((n -= n3) <= 0) {
                return false;
            }
        }
        if (serverPlayer != null && n != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, this, this.getDamageValue() + n);
        }
        n2 = this.getDamageValue() + n;
        this.setDamageValue(n2);
        return n2 >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void hurtAndBreak(int n, T t, Consumer<T> consumer) {
        if (t.level.isClientSide || t instanceof Player && ((Player)t).abilities.instabuild) {
            return;
        }
        if (!this.isDamageableItem()) {
            return;
        }
        if (this.hurt(n, ((LivingEntity)t).getRandom(), t instanceof ServerPlayer ? (ServerPlayer)t : null)) {
            consumer.accept(t);
            Item item = this.getItem();
            this.shrink(1);
            if (t instanceof Player) {
                ((Player)t).awardStat(Stats.ITEM_BROKEN.get(item));
            }
            this.setDamageValue(0);
        }
    }

    public void hurtEnemy(LivingEntity livingEntity, Player player) {
        Item item = this.getItem();
        if (item.hurtEnemy(this, livingEntity, player)) {
            player.awardStat(Stats.ITEM_USED.get(item));
        }
    }

    public void mineBlock(Level level, BlockState blockState, BlockPos blockPos, Player player) {
        Item item = this.getItem();
        if (item.mineBlock(this, level, blockState, blockPos, player)) {
            player.awardStat(Stats.ITEM_USED.get(item));
        }
    }

    public boolean isCorrectToolForDrops(BlockState blockState) {
        return this.getItem().isCorrectToolForDrops(blockState);
    }

    public InteractionResult interactLivingEntity(Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        return this.getItem().interactLivingEntity(this, player, livingEntity, interactionHand);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemStack = new ItemStack(this.getItem(), this.count);
        itemStack.setPopTime(this.getPopTime());
        if (this.tag != null) {
            itemStack.tag = this.tag.copy();
        }
        return itemStack;
    }

    public static boolean tagMatches(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return true;
        }
        if (itemStack.isEmpty() || itemStack2.isEmpty()) {
            return false;
        }
        if (itemStack.tag == null && itemStack2.tag != null) {
            return false;
        }
        return itemStack.tag == null || itemStack.tag.equals(itemStack2.tag);
    }

    public static boolean matches(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return true;
        }
        if (itemStack.isEmpty() || itemStack2.isEmpty()) {
            return false;
        }
        return itemStack.matches(itemStack2);
    }

    private boolean matches(ItemStack itemStack) {
        if (this.count != itemStack.count) {
            return false;
        }
        if (this.getItem() != itemStack.getItem()) {
            return false;
        }
        if (this.tag == null && itemStack.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(itemStack.tag);
    }

    public static boolean isSame(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == itemStack2) {
            return true;
        }
        if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            return itemStack.sameItem(itemStack2);
        }
        return false;
    }

    public static boolean isSameIgnoreDurability(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == itemStack2) {
            return true;
        }
        if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            return itemStack.sameItemStackIgnoreDurability(itemStack2);
        }
        return false;
    }

    public boolean sameItem(ItemStack itemStack) {
        return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
    }

    public boolean sameItemStackIgnoreDurability(ItemStack itemStack) {
        if (this.isDamageableItem()) {
            return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
        }
        return this.sameItem(itemStack);
    }

    public String getDescriptionId() {
        return this.getItem().getDescriptionId(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(Level level, Entity entity, int n, boolean bl) {
        if (this.popTime > 0) {
            --this.popTime;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, level, entity, n, bl);
        }
    }

    public void onCraftedBy(Level level, Player player, int n) {
        player.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), n);
        this.getItem().onCraftedBy(this, level, player);
    }

    public int getUseDuration() {
        return this.getItem().getUseDuration(this);
    }

    public UseAnim getUseAnimation() {
        return this.getItem().getUseAnimation(this);
    }

    public void releaseUsing(Level level, LivingEntity livingEntity, int n) {
        this.getItem().releaseUsing(this, level, livingEntity, n);
    }

    public boolean useOnRelease() {
        return this.getItem().useOnRelease(this);
    }

    public boolean hasTag() {
        return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }

    public CompoundTag getOrCreateTagElement(String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            CompoundTag compoundTag = new CompoundTag();
            this.addTagElement(string, compoundTag);
            return compoundTag;
        }
        return this.tag.getCompound(string);
    }

    @Nullable
    public CompoundTag getTagElement(String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            return null;
        }
        return this.tag.getCompound(string);
    }

    public void removeTagKey(String string) {
        if (this.tag != null && this.tag.contains(string)) {
            this.tag.remove(string);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }

    public ListTag getEnchantmentTags() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new ListTag();
    }

    public void setTag(@Nullable CompoundTag compoundTag) {
        this.tag = compoundTag;
        if (this.getItem().canBeDepleted()) {
            this.setDamageValue(this.getDamageValue());
        }
    }

    public Component getHoverName() {
        CompoundTag compoundTag = this.getTagElement("display");
        if (compoundTag != null && compoundTag.contains("Name", 8)) {
            try {
                MutableComponent mutableComponent = Component.Serializer.fromJson(compoundTag.getString("Name"));
                if (mutableComponent != null) {
                    return mutableComponent;
                }
                compoundTag.remove("Name");
            }
            catch (JsonParseException jsonParseException) {
                compoundTag.remove("Name");
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setHoverName(@Nullable Component component) {
        CompoundTag compoundTag = this.getOrCreateTagElement("display");
        if (component != null) {
            compoundTag.putString("Name", Component.Serializer.toJson(component));
        } else {
            compoundTag.remove("Name");
        }
        return this;
    }

    public void resetHoverName() {
        CompoundTag compoundTag = this.getTagElement("display");
        if (compoundTag != null) {
            compoundTag.remove("Name");
            if (compoundTag.isEmpty()) {
                this.removeTagKey("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }

    public boolean hasCustomHoverName() {
        CompoundTag compoundTag = this.getTagElement("display");
        return compoundTag != null && compoundTag.contains("Name", 8);
    }

    public List<Component> getTooltipLines(@Nullable Player player, TooltipFlag tooltipFlag) {
        Object object;
        Object object2;
        int n;
        Object object3;
        int n2;
        ArrayList arrayList = Lists.newArrayList();
        MutableComponent mutableComponent = new TextComponent("").append(this.getHoverName()).withStyle(this.getRarity().color);
        if (this.hasCustomHoverName()) {
            mutableComponent.withStyle(ChatFormatting.ITALIC);
        }
        arrayList.add(mutableComponent);
        if (!tooltipFlag.isAdvanced() && !this.hasCustomHoverName() && this.getItem() == Items.FILLED_MAP) {
            arrayList.add(new TextComponent("#" + MapItem.getMapId(this)).withStyle(ChatFormatting.GRAY));
        }
        if (ItemStack.shouldShowInTooltip(n = this.getHideFlags(), TooltipPart.ADDITIONAL)) {
            this.getItem().appendHoverText(this, player == null ? null : player.level, arrayList, tooltipFlag);
        }
        if (this.hasTag()) {
            if (ItemStack.shouldShowInTooltip(n, TooltipPart.ENCHANTMENTS)) {
                ItemStack.appendEnchantmentNames(arrayList, this.getEnchantmentTags());
            }
            if (this.tag.contains("display", 10)) {
                object = this.tag.getCompound("display");
                if (ItemStack.shouldShowInTooltip(n, TooltipPart.DYE) && ((CompoundTag)object).contains("color", 99)) {
                    if (tooltipFlag.isAdvanced()) {
                        arrayList.add(new TranslatableComponent("item.color", String.format("#%06X", ((CompoundTag)object).getInt("color"))).withStyle(ChatFormatting.GRAY));
                    } else {
                        arrayList.add(new TranslatableComponent("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    }
                }
                if (((CompoundTag)object).getTagType("Lore") == 9) {
                    ListTag listTag = ((CompoundTag)object).getList("Lore", 8);
                    for (n2 = 0; n2 < listTag.size(); ++n2) {
                        object3 = listTag.getString(n2);
                        try {
                            object2 = Component.Serializer.fromJson((String)object3);
                            if (object2 == null) continue;
                            arrayList.add(ComponentUtils.mergeStyles(object2, LORE_STYLE));
                            continue;
                        }
                        catch (JsonParseException jsonParseException) {
                            ((CompoundTag)object).remove("Lore");
                        }
                    }
                }
            }
        }
        if (ItemStack.shouldShowInTooltip(n, TooltipPart.MODIFIERS)) {
            object = EquipmentSlot.values();
            int n3 = ((EquipmentSlot[])object).length;
            for (n2 = 0; n2 < n3; ++n2) {
                object3 = object[n2];
                object2 = this.getAttributeModifiers((EquipmentSlot)((Object)object3));
                if (object2.isEmpty()) continue;
                arrayList.add(TextComponent.EMPTY);
                arrayList.add(new TranslatableComponent("item.modifiers." + object3.getName()).withStyle(ChatFormatting.GRAY));
                for (Map.Entry entry : object2.entries()) {
                    AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
                    double d = attributeModifier.getAmount();
                    boolean bl = false;
                    if (player != null) {
                        if (attributeModifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                            d += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                            d += (double)EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                            bl = true;
                        } else if (attributeModifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                            d += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                            bl = true;
                        }
                    }
                    double d2 = attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL ? d * 100.0 : (((Attribute)entry.getKey()).equals(Attributes.KNOCKBACK_RESISTANCE) ? d * 10.0 : d);
                    if (bl) {
                        arrayList.add(new TextComponent(" ").append(new TranslatableComponent("attribute.modifier.equals." + attributeModifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d2), new TranslatableComponent(((Attribute)entry.getKey()).getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                        continue;
                    }
                    if (d > 0.0) {
                        arrayList.add(new TranslatableComponent("attribute.modifier.plus." + attributeModifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d2), new TranslatableComponent(((Attribute)entry.getKey()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                        continue;
                    }
                    if (!(d < 0.0)) continue;
                    arrayList.add(new TranslatableComponent("attribute.modifier.take." + attributeModifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d2 *= -1.0), new TranslatableComponent(((Attribute)entry.getKey()).getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
        if (this.hasTag()) {
            if (ItemStack.shouldShowInTooltip(n, TooltipPart.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                arrayList.add(new TranslatableComponent("item.unbreakable").withStyle(ChatFormatting.BLUE));
            }
            if (ItemStack.shouldShowInTooltip(n, TooltipPart.CAN_DESTROY) && this.tag.contains("CanDestroy", 9) && !((ListTag)(object = this.tag.getList("CanDestroy", 8))).isEmpty()) {
                arrayList.add(TextComponent.EMPTY);
                arrayList.add(new TranslatableComponent("item.canBreak").withStyle(ChatFormatting.GRAY));
                for (int i = 0; i < ((ListTag)object).size(); ++i) {
                    arrayList.addAll(ItemStack.expandBlockState(((ListTag)object).getString(i)));
                }
            }
            if (ItemStack.shouldShowInTooltip(n, TooltipPart.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9) && !((ListTag)(object = this.tag.getList("CanPlaceOn", 8))).isEmpty()) {
                arrayList.add(TextComponent.EMPTY);
                arrayList.add(new TranslatableComponent("item.canPlace").withStyle(ChatFormatting.GRAY));
                for (int i = 0; i < ((ListTag)object).size(); ++i) {
                    arrayList.addAll(ItemStack.expandBlockState(((ListTag)object).getString(i)));
                }
            }
        }
        if (tooltipFlag.isAdvanced()) {
            if (this.isDamaged()) {
                arrayList.add(new TranslatableComponent("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
            }
            arrayList.add(new TextComponent(Registry.ITEM.getKey(this.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (this.hasTag()) {
                arrayList.add(new TranslatableComponent("item.nbt_tags", this.tag.getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return arrayList;
    }

    private static boolean shouldShowInTooltip(int n, TooltipPart tooltipPart) {
        return (n & tooltipPart.getMask()) == 0;
    }

    private int getHideFlags() {
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            return this.tag.getInt("HideFlags");
        }
        return 0;
    }

    public void hideTooltipPart(TooltipPart tooltipPart) {
        CompoundTag compoundTag = this.getOrCreateTag();
        compoundTag.putInt("HideFlags", compoundTag.getInt("HideFlags") | tooltipPart.getMask());
    }

    public static void appendEnchantmentNames(List<Component> list, ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> list.add(enchantment.getFullname(compoundTag.getInt("lvl"))));
        }
    }

    private static Collection<Component> expandBlockState(String string) {
        try {
            boolean bl;
            BlockStateParser blockStateParser = new BlockStateParser(new StringReader(string), true).parse(true);
            BlockState blockState = blockStateParser.getState();
            ResourceLocation resourceLocation = blockStateParser.getTag();
            boolean bl2 = blockState != null;
            boolean bl3 = bl = resourceLocation != null;
            if (bl2 || bl) {
                List<Block> list;
                if (bl2) {
                    return Lists.newArrayList((Object[])new Component[]{blockState.getBlock().getName().withStyle(ChatFormatting.DARK_GRAY)});
                }
                Tag<Block> tag = BlockTags.getAllTags().getTag(resourceLocation);
                if (tag != null && !(list = tag.getValues()).isEmpty()) {
                    return list.stream().map(Block::getName).map(mutableComponent -> mutableComponent.withStyle(ChatFormatting.DARK_GRAY)).collect(Collectors.toList());
                }
            }
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return Lists.newArrayList((Object[])new Component[]{new TextComponent("missingno").withStyle(ChatFormatting.DARK_GRAY)});
    }

    public boolean hasFoil() {
        return this.getItem().isFoil(this);
    }

    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        }
        return !this.isEnchanted();
    }

    public void enchant(Enchantment enchantment, int n) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        ListTag listTag = this.tag.getList("Enchantments", 10);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
        compoundTag.putShort("lvl", (byte)n);
        listTag.add(compoundTag);
    }

    public boolean isEnchanted() {
        if (this.tag != null && this.tag.contains("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void addTagElement(String string, net.minecraft.nbt.Tag tag) {
        this.getOrCreateTag().put(string, tag);
    }

    public boolean isFramed() {
        return this.entityRepresentation instanceof ItemFrame;
    }

    public void setEntityRepresentation(@Nullable Entity entity) {
        this.entityRepresentation = entity;
    }

    @Nullable
    public ItemFrame getFrame() {
        return this.entityRepresentation instanceof ItemFrame ? (ItemFrame)this.getEntityRepresentation() : null;
    }

    @Nullable
    public Entity getEntityRepresentation() {
        return !this.emptyCacheFlag ? this.entityRepresentation : null;
    }

    public int getBaseRepairCost() {
        if (this.hasTag() && this.tag.contains("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }

    public void setRepairCost(int n) {
        this.getOrCreateTag().putInt("RepairCost", n);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        HashMultimap hashMultimap;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            hashMultimap = HashMultimap.create();
            ListTag listTag = this.tag.getList("AttributeModifiers", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                Optional<Attribute> optional;
                AttributeModifier attributeModifier;
                CompoundTag compoundTag = listTag.getCompound(i);
                if (compoundTag.contains("Slot", 8) && !compoundTag.getString("Slot").equals(equipmentSlot.getName()) || !(optional = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundTag.getString("AttributeName")))).isPresent() || (attributeModifier = AttributeModifier.load(compoundTag)) == null || attributeModifier.getId().getLeastSignificantBits() == 0L || attributeModifier.getId().getMostSignificantBits() == 0L) continue;
                hashMultimap.put((Object)optional.get(), (Object)attributeModifier);
            }
        } else {
            hashMultimap = this.getItem().getDefaultAttributeModifiers(equipmentSlot);
        }
        return hashMultimap;
    }

    public void addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier, @Nullable EquipmentSlot equipmentSlot) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag listTag = this.tag.getList("AttributeModifiers", 10);
        CompoundTag compoundTag = attributeModifier.save();
        compoundTag.putString("AttributeName", Registry.ATTRIBUTE.getKey(attribute).toString());
        if (equipmentSlot != null) {
            compoundTag.putString("Slot", equipmentSlot.getName());
        }
        listTag.add(compoundTag);
    }

    public Component getDisplayName() {
        MutableComponent mutableComponent = new TextComponent("").append(this.getHoverName());
        if (this.hasCustomHoverName()) {
            mutableComponent.withStyle(ChatFormatting.ITALIC);
        }
        MutableComponent mutableComponent2 = ComponentUtils.wrapInSquareBrackets(mutableComponent);
        if (!this.emptyCacheFlag) {
            mutableComponent2.withStyle(this.getRarity().color).withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(this))));
        }
        return mutableComponent2;
    }

    private static boolean areSameBlocks(BlockInWorld blockInWorld, @Nullable BlockInWorld blockInWorld2) {
        if (blockInWorld2 == null || blockInWorld.getState() != blockInWorld2.getState()) {
            return false;
        }
        if (blockInWorld.getEntity() == null && blockInWorld2.getEntity() == null) {
            return true;
        }
        if (blockInWorld.getEntity() == null || blockInWorld2.getEntity() == null) {
            return false;
        }
        return Objects.equals(blockInWorld.getEntity().save(new CompoundTag()), blockInWorld2.getEntity().save(new CompoundTag()));
    }

    public boolean hasAdventureModeBreakTagForBlock(TagContainer tagContainer, BlockInWorld blockInWorld) {
        if (ItemStack.areSameBlocks(blockInWorld, this.cachedBreakBlock)) {
            return this.cachedBreakBlockResult;
        }
        this.cachedBreakBlock = blockInWorld;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag listTag = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<BlockInWorld> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(string)).create(tagContainer);
                    if (predicate.test(blockInWorld)) {
                        this.cachedBreakBlockResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.cachedBreakBlockResult = false;
        return false;
    }

    public boolean hasAdventureModePlaceTagForBlock(TagContainer tagContainer, BlockInWorld blockInWorld) {
        if (ItemStack.areSameBlocks(blockInWorld, this.cachedPlaceBlock)) {
            return this.cachedPlaceBlockResult;
        }
        this.cachedPlaceBlock = blockInWorld;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag listTag = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getString(i);
                try {
                    Predicate<BlockInWorld> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(string)).create(tagContainer);
                    if (predicate.test(blockInWorld)) {
                        this.cachedPlaceBlockResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.cachedPlaceBlockResult = false;
        return false;
    }

    public int getPopTime() {
        return this.popTime;
    }

    public void setPopTime(int n) {
        this.popTime = n;
    }

    public int getCount() {
        return this.emptyCacheFlag ? 0 : this.count;
    }

    public void setCount(int n) {
        this.count = n;
        this.updateEmptyCacheFlag();
    }

    public void grow(int n) {
        this.setCount(this.count + n);
    }

    public void shrink(int n) {
        this.grow(-n);
    }

    public void onUseTick(Level level, LivingEntity livingEntity, int n) {
        this.getItem().onUseTick(level, livingEntity, this, n);
    }

    public boolean isEdible() {
        return this.getItem().isEdible();
    }

    public SoundEvent getDrinkingSound() {
        return this.getItem().getDrinkingSound();
    }

    public SoundEvent getEatingSound() {
        return this.getItem().getEatingSound();
    }

    public static enum TooltipPart {
        ENCHANTMENTS,
        MODIFIERS,
        UNBREAKABLE,
        CAN_DESTROY,
        CAN_PLACE,
        ADDITIONAL,
        DYE;
        
        private int mask = 1 << this.ordinal();

        public int getMask() {
            return this.mask;
        }
    }

}

