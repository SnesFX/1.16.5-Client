/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
    public static int getItemEnchantmentLevel(Enchantment enchantment, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        ResourceLocation resourceLocation = Registry.ENCHANTMENT.getKey(enchantment);
        ListTag listTag = itemStack.getEnchantmentTags();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            ResourceLocation resourceLocation2 = ResourceLocation.tryParse(compoundTag.getString("id"));
            if (resourceLocation2 == null || !resourceLocation2.equals(resourceLocation)) continue;
            return Mth.clamp(compoundTag.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
        ListTag listTag = itemStack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(itemStack) : itemStack.getEnchantmentTags();
        return EnchantmentHelper.deserializeEnchantments(listTag);
    }

    public static Map<Enchantment, Integer> deserializeEnchantments(ListTag listTag) {
        LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundTag.getString("id"))).ifPresent(enchantment -> linkedHashMap.put(enchantment, compoundTag.getInt("lvl")));
        }
        return linkedHashMap;
    }

    public static void setEnchantments(Map<Enchantment, Integer> map, ItemStack itemStack) {
        ListTag listTag = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == null) continue;
            int n = entry.getValue();
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
            compoundTag.putShort("lvl", (short)n);
            listTag.add(compoundTag);
            if (itemStack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, n));
        }
        if (listTag.isEmpty()) {
            itemStack.removeTagKey("Enchantments");
        } else if (itemStack.getItem() != Items.ENCHANTED_BOOK) {
            itemStack.addTagElement("Enchantments", listTag);
        }
    }

    private static void runIterationOnItem(EnchantmentVisitor enchantmentVisitor, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        ListTag listTag = itemStack.getEnchantmentTags();
        for (int i = 0; i < listTag.size(); ++i) {
            String string = listTag.getCompound(i).getString("id");
            int n = listTag.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(string)).ifPresent(enchantment -> enchantmentVisitor.accept((Enchantment)enchantment, n));
        }
    }

    private static void runIterationOnInventory(EnchantmentVisitor enchantmentVisitor, Iterable<ItemStack> iterable) {
        for (ItemStack itemStack : iterable) {
            EnchantmentHelper.runIterationOnItem(enchantmentVisitor, itemStack);
        }
    }

    public static int getDamageProtection(Iterable<ItemStack> iterable, DamageSource damageSource) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.runIterationOnInventory((enchantment, n) -> mutableInt.add(enchantment.getDamageProtection(n, damageSource)), iterable);
        return mutableInt.intValue();
    }

    public static float getDamageBonus(ItemStack itemStack, MobType mobType) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.runIterationOnItem((enchantment, n) -> mutableFloat.add(enchantment.getDamageBonus(n, mobType)), itemStack);
        return mutableFloat.floatValue();
    }

    public static float getSweepingDamageRatio(LivingEntity livingEntity) {
        int n = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, livingEntity);
        if (n > 0) {
            return SweepingEdgeEnchantment.getSweepingDamageRatio(n);
        }
        return 0.0f;
    }

    public static void doPostHurtEffects(LivingEntity livingEntity, Entity entity) {
        EnchantmentVisitor enchantmentVisitor = (enchantment, n) -> enchantment.doPostHurt(livingEntity, entity, n);
        if (livingEntity != null) {
            EnchantmentHelper.runIterationOnInventory(enchantmentVisitor, livingEntity.getAllSlots());
        }
        if (entity instanceof Player) {
            EnchantmentHelper.runIterationOnItem(enchantmentVisitor, livingEntity.getMainHandItem());
        }
    }

    public static void doPostDamageEffects(LivingEntity livingEntity, Entity entity) {
        EnchantmentVisitor enchantmentVisitor = (enchantment, n) -> enchantment.doPostAttack(livingEntity, entity, n);
        if (livingEntity != null) {
            EnchantmentHelper.runIterationOnInventory(enchantmentVisitor, livingEntity.getAllSlots());
        }
        if (livingEntity instanceof Player) {
            EnchantmentHelper.runIterationOnItem(enchantmentVisitor, livingEntity.getMainHandItem());
        }
    }

    public static int getEnchantmentLevel(Enchantment enchantment, LivingEntity livingEntity) {
        Collection<ItemStack> collection = enchantment.getSlotItems(livingEntity).values();
        if (collection == null) {
            return 0;
        }
        int n = 0;
        for (ItemStack itemStack : collection) {
            int n2 = EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
            if (n2 <= n) continue;
            n = n2;
        }
        return n;
    }

    public static int getKnockbackBonus(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, livingEntity);
    }

    public static int getFireAspect(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, livingEntity);
    }

    public static int getRespiration(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.RESPIRATION, livingEntity);
    }

    public static int getDepthStrider(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, livingEntity);
    }

    public static int getBlockEfficiency(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, livingEntity);
    }

    public static int getFishingLuckBonus(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_LUCK, itemStack);
    }

    public static int getFishingSpeedBonus(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_SPEED, itemStack);
    }

    public static int getMobLooting(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.MOB_LOOTING, livingEntity);
    }

    public static boolean hasAquaAffinity(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, livingEntity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, livingEntity) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingEntity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, itemStack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, itemStack) > 0;
    }

    public static int getLoyalty(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, itemStack);
    }

    public static int getRiptide(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RIPTIDE, itemStack);
    }

    public static boolean hasChanneling(ItemStack itemStack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, itemStack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment enchantment, LivingEntity livingEntity) {
        return EnchantmentHelper.getRandomItemWith(enchantment, livingEntity, itemStack -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getSlotItems(livingEntity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList arrayList = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack itemStack = entry.getValue();
            if (itemStack.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack) <= 0 || !predicate.test(itemStack)) continue;
            arrayList.add(entry);
        }
        return arrayList.isEmpty() ? null : (Map.Entry)arrayList.get(livingEntity.getRandom().nextInt(arrayList.size()));
    }

    public static int getEnchantmentCost(Random random, int n, int n2, ItemStack itemStack) {
        Item item = itemStack.getItem();
        int n3 = item.getEnchantmentValue();
        if (n3 <= 0) {
            return 0;
        }
        if (n2 > 15) {
            n2 = 15;
        }
        int n4 = random.nextInt(8) + 1 + (n2 >> 1) + random.nextInt(n2 + 1);
        if (n == 0) {
            return Math.max(n4 / 3, 1);
        }
        if (n == 1) {
            return n4 * 2 / 3 + 1;
        }
        return Math.max(n4, n2 * 2);
    }

    public static ItemStack enchantItem(Random random, ItemStack itemStack, int n, boolean bl) {
        boolean bl2;
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(random, itemStack, n, bl);
        boolean bl3 = bl2 = itemStack.getItem() == Items.BOOK;
        if (bl2) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentInstance enchantmentInstance : list) {
            if (bl2) {
                EnchantedBookItem.addEnchantment(itemStack, enchantmentInstance);
                continue;
            }
            itemStack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
        }
        return itemStack;
    }

    public static List<EnchantmentInstance> selectEnchantment(Random random, ItemStack itemStack, int n, boolean bl) {
        ArrayList arrayList = Lists.newArrayList();
        Item item = itemStack.getItem();
        int n2 = item.getEnchantmentValue();
        if (n2 <= 0) {
            return arrayList;
        }
        n += 1 + random.nextInt(n2 / 4 + 1) + random.nextInt(n2 / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentInstance> list = EnchantmentHelper.getAvailableEnchantmentResults(n = Mth.clamp(Math.round((float)n + (float)n * f), 1, Integer.MAX_VALUE), itemStack, bl);
        if (!list.isEmpty()) {
            arrayList.add(WeighedRandom.getRandomItem(random, list));
            while (random.nextInt(50) <= n) {
                EnchantmentHelper.filterCompatibleEnchantments(list, (EnchantmentInstance)Util.lastOf(arrayList));
                if (list.isEmpty()) break;
                arrayList.add(WeighedRandom.getRandomItem(random, list));
                n /= 2;
            }
        }
        return arrayList;
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> list, EnchantmentInstance enchantmentInstance) {
        Iterator<EnchantmentInstance> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (enchantmentInstance.enchantment.isCompatibleWith(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean isEnchantmentCompatible(Collection<Enchantment> collection, Enchantment enchantment) {
        for (Enchantment enchantment2 : collection) {
            if (enchantment2.isCompatibleWith(enchantment)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int n, ItemStack itemStack, boolean bl) {
        ArrayList arrayList = Lists.newArrayList();
        Item item = itemStack.getItem();
        boolean bl2 = itemStack.getItem() == Items.BOOK;
        block0 : for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.isTreasureOnly() && !bl || !enchantment.isDiscoverable() || !enchantment.category.canEnchant(item) && !bl2) continue;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (n < enchantment.getMinCost(i) || n > enchantment.getMaxCost(i)) continue;
                arrayList.add(new EnchantmentInstance(enchantment, i));
                continue block0;
            }
        }
        return arrayList;
    }

    @FunctionalInterface
    static interface EnchantmentVisitor {
        public void accept(Enchantment var1, int var2);
    }

}

