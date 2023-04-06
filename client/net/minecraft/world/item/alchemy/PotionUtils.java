/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class PotionUtils {
    private static final MutableComponent NO_EFFECT = new TranslatableComponent("effect.none").withStyle(ChatFormatting.GRAY);

    public static List<MobEffectInstance> getMobEffects(ItemStack itemStack) {
        return PotionUtils.getAllEffects(itemStack.getTag());
    }

    public static List<MobEffectInstance> getAllEffects(Potion potion, Collection<MobEffectInstance> collection) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.addAll(potion.getEffects());
        arrayList.addAll(collection);
        return arrayList;
    }

    public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag compoundTag) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.addAll(PotionUtils.getPotion(compoundTag).getEffects());
        PotionUtils.getCustomEffects(compoundTag, arrayList);
        return arrayList;
    }

    public static List<MobEffectInstance> getCustomEffects(ItemStack itemStack) {
        return PotionUtils.getCustomEffects(itemStack.getTag());
    }

    public static List<MobEffectInstance> getCustomEffects(@Nullable CompoundTag compoundTag) {
        ArrayList arrayList = Lists.newArrayList();
        PotionUtils.getCustomEffects(compoundTag, arrayList);
        return arrayList;
    }

    public static void getCustomEffects(@Nullable CompoundTag compoundTag, List<MobEffectInstance> list) {
        if (compoundTag != null && compoundTag.contains("CustomPotionEffects", 9)) {
            ListTag listTag = compoundTag.getList("CustomPotionEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                MobEffectInstance mobEffectInstance = MobEffectInstance.load(compoundTag2);
                if (mobEffectInstance == null) continue;
                list.add(mobEffectInstance);
            }
        }
    }

    public static int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
            return compoundTag.getInt("CustomPotionColor");
        }
        return PotionUtils.getPotion(itemStack) == Potions.EMPTY ? 16253176 : PotionUtils.getColor(PotionUtils.getMobEffects(itemStack));
    }

    public static int getColor(Potion potion) {
        return potion == Potions.EMPTY ? 16253176 : PotionUtils.getColor(potion.getEffects());
    }

    public static int getColor(Collection<MobEffectInstance> collection) {
        int n = 3694022;
        if (collection.isEmpty()) {
            return 3694022;
        }
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        int n2 = 0;
        for (MobEffectInstance mobEffectInstance : collection) {
            if (!mobEffectInstance.isVisible()) continue;
            int n3 = mobEffectInstance.getEffect().getColor();
            int n4 = mobEffectInstance.getAmplifier() + 1;
            f += (float)(n4 * (n3 >> 16 & 0xFF)) / 255.0f;
            f2 += (float)(n4 * (n3 >> 8 & 0xFF)) / 255.0f;
            f3 += (float)(n4 * (n3 >> 0 & 0xFF)) / 255.0f;
            n2 += n4;
        }
        if (n2 == 0) {
            return 0;
        }
        f = f / (float)n2 * 255.0f;
        f2 = f2 / (float)n2 * 255.0f;
        f3 = f3 / (float)n2 * 255.0f;
        return (int)f << 16 | (int)f2 << 8 | (int)f3;
    }

    public static Potion getPotion(ItemStack itemStack) {
        return PotionUtils.getPotion(itemStack.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            return Potions.EMPTY;
        }
        return Potion.byName(compoundTag.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack itemStack, Potion potion) {
        ResourceLocation resourceLocation = Registry.POTION.getKey(potion);
        if (potion == Potions.EMPTY) {
            itemStack.removeTagKey("Potion");
        } else {
            itemStack.getOrCreateTag().putString("Potion", resourceLocation.toString());
        }
        return itemStack;
    }

    public static ItemStack setCustomEffects(ItemStack itemStack, Collection<MobEffectInstance> collection) {
        if (collection.isEmpty()) {
            return itemStack;
        }
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = compoundTag.getList("CustomPotionEffects", 9);
        for (MobEffectInstance mobEffectInstance : collection) {
            listTag.add(mobEffectInstance.save(new CompoundTag()));
        }
        compoundTag.put("CustomPotionEffects", listTag);
        return itemStack;
    }

    public static void addPotionTooltip(ItemStack itemStack, List<Component> list, float f) {
        Object object;
        List<MobEffectInstance> list2 = PotionUtils.getMobEffects(itemStack);
        ArrayList arrayList = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(NO_EFFECT);
        } else {
            for (MobEffectInstance mobEffectInstance : list2) {
                object = new TranslatableComponent(mobEffectInstance.getDescriptionId());
                MobEffect mobEffect = mobEffectInstance.getEffect();
                Map<Attribute, AttributeModifier> map = mobEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributeModifier = entry.getValue();
                        AttributeModifier attributeModifier2 = new AttributeModifier(attributeModifier.getName(), mobEffect.getAttributeModifierValue(mobEffectInstance.getAmplifier(), attributeModifier), attributeModifier.getOperation());
                        arrayList.add(new Pair((Object)entry.getKey(), (Object)attributeModifier2));
                    }
                }
                if (mobEffectInstance.getAmplifier() > 0) {
                    object = new TranslatableComponent("potion.withAmplifier", object, new TranslatableComponent("potion.potency." + mobEffectInstance.getAmplifier()));
                }
                if (mobEffectInstance.getDuration() > 20) {
                    object = new TranslatableComponent("potion.withDuration", object, MobEffectUtil.formatDuration(mobEffectInstance, f));
                }
                list.add(object.withStyle(mobEffect.getCategory().getTooltipFormatting()));
            }
        }
        if (!arrayList.isEmpty()) {
            list.add(TextComponent.EMPTY);
            list.add(new TranslatableComponent("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            for (MobEffectInstance mobEffectInstance : arrayList) {
                object = (AttributeModifier)mobEffectInstance.getSecond();
                double d = ((AttributeModifier)object).getAmount();
                double d2 = ((AttributeModifier)object).getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || ((AttributeModifier)object).getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL ? ((AttributeModifier)object).getAmount() * 100.0 : ((AttributeModifier)object).getAmount();
                if (d > 0.0) {
                    list.add(new TranslatableComponent("attribute.modifier.plus." + ((AttributeModifier)object).getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d2), new TranslatableComponent(((Attribute)mobEffectInstance.getFirst()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableComponent("attribute.modifier.take." + ((AttributeModifier)object).getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d2 *= -1.0), new TranslatableComponent(((Attribute)mobEffectInstance.getFirst()).getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}

