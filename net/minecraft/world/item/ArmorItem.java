/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class ArmorItem
extends Item
implements Wearable {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior(){

        @Override
        protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            return ArmorItem.dispenseArmor(blockSource, itemStack) ? itemStack : super.execute(blockSource, itemStack);
        }
    };
    protected final EquipmentSlot slot;
    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public static boolean dispenseArmor(BlockSource blockSource, ItemStack itemStack) {
        BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
        List<Entity> list = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(itemStack)));
        if (list.isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = (LivingEntity)list.get(0);
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        ItemStack itemStack2 = itemStack.split(1);
        livingEntity.setItemSlot(equipmentSlot, itemStack2);
        if (livingEntity instanceof Mob) {
            ((Mob)livingEntity).setDropChance(equipmentSlot, 2.0f);
            ((Mob)livingEntity).setPersistenceRequired();
        }
        return true;
    }

    public ArmorItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Item.Properties properties) {
        super(properties.defaultDurability(armorMaterial.getDurabilityForSlot(equipmentSlot)));
        this.material = armorMaterial;
        this.slot = equipmentSlot;
        this.defense = armorMaterial.getDefenseForSlot(equipmentSlot);
        this.toughness = armorMaterial.getToughness();
        this.knockbackResistance = armorMaterial.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()];
        builder.put((Object)Attributes.ARMOR, (Object)new AttributeModifier(uUID, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
        builder.put((Object)Attributes.ARMOR_TOUGHNESS, (Object)new AttributeModifier(uUID, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if (armorMaterial == ArmorMaterials.NETHERITE) {
            builder.put((Object)Attributes.KNOCKBACK_RESISTANCE, (Object)new AttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        this.defaultModifiers = builder.build();
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return this.material.getRepairIngredient().test(itemStack2) || super.isValidRepairItem(itemStack, itemStack2);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        ItemStack itemStack2 = player.getItemBySlot(equipmentSlot);
        if (itemStack2.isEmpty()) {
            player.setItemSlot(equipmentSlot, itemStack.copy());
            itemStack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == this.slot) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

}

