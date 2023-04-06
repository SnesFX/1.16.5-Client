/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public class ItemProperties {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = Maps.newHashMap();
    private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
    private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
    private static final ItemPropertyFunction PROPERTY_DAMAGED = (itemStack, clientLevel, livingEntity) -> itemStack.isDamaged() ? 1.0f : 0.0f;
    private static final ItemPropertyFunction PROPERTY_DAMAGE = (itemStack, clientLevel, livingEntity) -> Mth.clamp((float)itemStack.getDamageValue() / (float)itemStack.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<ResourceLocation, ItemPropertyFunction>> PROPERTIES = Maps.newHashMap();

    private static ItemPropertyFunction registerGeneric(ResourceLocation resourceLocation, ItemPropertyFunction itemPropertyFunction) {
        GENERIC_PROPERTIES.put(resourceLocation, itemPropertyFunction);
        return itemPropertyFunction;
    }

    private static void register(Item item2, ResourceLocation resourceLocation, ItemPropertyFunction itemPropertyFunction) {
        PROPERTIES.computeIfAbsent(item2, item -> Maps.newHashMap()).put(resourceLocation, itemPropertyFunction);
    }

    @Nullable
    public static ItemPropertyFunction getProperty(Item item, ResourceLocation resourceLocation) {
        ItemPropertyFunction itemPropertyFunction;
        if (item.getMaxDamage() > 0) {
            if (DAMAGE.equals(resourceLocation)) {
                return PROPERTY_DAMAGE;
            }
            if (DAMAGED.equals(resourceLocation)) {
                return PROPERTY_DAMAGED;
            }
        }
        if ((itemPropertyFunction = GENERIC_PROPERTIES.get(resourceLocation)) != null) {
            return itemPropertyFunction;
        }
        Map<ResourceLocation, ItemPropertyFunction> map = PROPERTIES.get(item);
        if (map == null) {
            return null;
        }
        return map.get(resourceLocation);
    }

    static {
        ItemProperties.registerGeneric(new ResourceLocation("lefthanded"), (itemStack, clientLevel, livingEntity) -> livingEntity == null || livingEntity.getMainArm() == HumanoidArm.RIGHT ? 0.0f : 1.0f);
        ItemProperties.registerGeneric(new ResourceLocation("cooldown"), (itemStack, clientLevel, livingEntity) -> livingEntity instanceof Player ? ((Player)livingEntity).getCooldowns().getCooldownPercent(itemStack.getItem(), 0.0f) : 0.0f);
        ItemProperties.registerGeneric(new ResourceLocation("custom_model_data"), (itemStack, clientLevel, livingEntity) -> itemStack.hasTag() ? (float)itemStack.getTag().getInt("CustomModelData") : 0.0f);
        ItemProperties.register(Items.BOW, new ResourceLocation("pull"), (itemStack, clientLevel, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            if (livingEntity.getUseItem() != itemStack) {
                return 0.0f;
            }
            return (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0f;
        });
        ItemProperties.register(Items.BOW, new ResourceLocation("pulling"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f);
        ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), new ItemPropertyFunction(){
            private double rotation;
            private double rota;
            private long lastUpdateTick;

            @Override
            public float call(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
                Entity entity;
                Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
                if (entity == null) {
                    return 0.0f;
                }
                if (clientLevel == null && entity.level instanceof ClientLevel) {
                    clientLevel = (ClientLevel)entity.level;
                }
                if (clientLevel == null) {
                    return 0.0f;
                }
                double d = clientLevel.dimensionType().natural() ? (double)clientLevel.getTimeOfDay(1.0f) : Math.random();
                d = this.wobble(clientLevel, d);
                return (float)d;
            }

            private double wobble(Level level, double d) {
                if (level.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = level.getGameTime();
                    double d2 = d - this.rotation;
                    d2 = Mth.positiveModulo(d2 + 0.5, 1.0) - 0.5;
                    this.rota += d2 * 0.1;
                    this.rota *= 0.9;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0);
                }
                return this.rotation;
            }
        });
        ItemProperties.register(Items.COMPASS, new ResourceLocation("angle"), new ItemPropertyFunction(){
            private final CompassWobble wobble = new CompassWobble();
            private final CompassWobble wobbleRandom = new CompassWobble();

            @Override
            public float call(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
                double d;
                Entity entity;
                Entity entity2 = entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
                if (entity == null) {
                    return 0.0f;
                }
                if (clientLevel == null && entity.level instanceof ClientLevel) {
                    clientLevel = (ClientLevel)entity.level;
                }
                BlockPos blockPos = CompassItem.isLodestoneCompass(itemStack) ? this.getLodestonePosition(clientLevel, itemStack.getOrCreateTag()) : this.getSpawnPosition(clientLevel);
                long l = clientLevel.getGameTime();
                if (blockPos == null || entity.position().distanceToSqr((double)blockPos.getX() + 0.5, entity.position().y(), (double)blockPos.getZ() + 0.5) < 9.999999747378752E-6) {
                    if (this.wobbleRandom.shouldUpdate(l)) {
                        this.wobbleRandom.update(l, Math.random());
                    }
                    double d2 = this.wobbleRandom.rotation + (double)((float)itemStack.hashCode() / 2.14748365E9f);
                    return Mth.positiveModulo((float)d2, 1.0f);
                }
                boolean bl = livingEntity instanceof Player && ((Player)livingEntity).isLocalPlayer();
                double d3 = 0.0;
                if (bl) {
                    d3 = livingEntity.yRot;
                } else if (entity instanceof ItemFrame) {
                    d3 = this.getFrameRotation((ItemFrame)entity);
                } else if (entity instanceof ItemEntity) {
                    d3 = 180.0f - ((ItemEntity)entity).getSpin(0.5f) / 6.2831855f * 360.0f;
                } else if (livingEntity != null) {
                    d3 = livingEntity.yBodyRot;
                }
                d3 = Mth.positiveModulo(d3 / 360.0, 1.0);
                double d4 = this.getAngleTo(Vec3.atCenterOf(blockPos), entity) / 6.2831854820251465;
                if (bl) {
                    if (this.wobble.shouldUpdate(l)) {
                        this.wobble.update(l, 0.5 - (d3 - 0.25));
                    }
                    d = d4 + this.wobble.rotation;
                } else {
                    d = 0.5 - (d3 - 0.25 - d4);
                }
                return Mth.positiveModulo((float)d, 1.0f);
            }

            @Nullable
            private BlockPos getSpawnPosition(ClientLevel clientLevel) {
                return clientLevel.dimensionType().natural() ? clientLevel.getSharedSpawnPos() : null;
            }

            @Nullable
            private BlockPos getLodestonePosition(Level level, CompoundTag compoundTag) {
                Optional<ResourceKey<Level>> optional;
                boolean bl = compoundTag.contains("LodestonePos");
                boolean bl2 = compoundTag.contains("LodestoneDimension");
                if (bl && bl2 && (optional = CompassItem.getLodestoneDimension(compoundTag)).isPresent() && level.dimension() == optional.get()) {
                    return NbtUtils.readBlockPos(compoundTag.getCompound("LodestonePos"));
                }
                return null;
            }

            private double getFrameRotation(ItemFrame itemFrame) {
                Direction direction = itemFrame.getDirection();
                int n = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
                return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + n);
            }

            private double getAngleTo(Vec3 vec3, Entity entity) {
                return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX());
            }
        });
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("pull"), (itemStack, clientLevel, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(itemStack)) {
                return 0.0f;
            }
            return (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(itemStack);
        });
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("pulling"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("charged"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        ItemProperties.register(Items.CROSSBOW, new ResourceLocation("firework"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ItemProperties.register(Items.ELYTRA, new ResourceLocation("broken"), (itemStack, clientLevel, livingEntity) -> ElytraItem.isFlyEnabled(itemStack) ? 0.0f : 1.0f);
        ItemProperties.register(Items.FISHING_ROD, new ResourceLocation("cast"), (itemStack, clientLevel, livingEntity) -> {
            boolean bl;
            if (livingEntity == null) {
                return 0.0f;
            }
            boolean bl2 = livingEntity.getMainHandItem() == itemStack;
            boolean bl3 = bl = livingEntity.getOffhandItem() == itemStack;
            if (livingEntity.getMainHandItem().getItem() instanceof FishingRodItem) {
                bl = false;
            }
            return (bl2 || bl) && livingEntity instanceof Player && ((Player)livingEntity).fishing != null ? 1.0f : 0.0f;
        });
        ItemProperties.register(Items.SHIELD, new ResourceLocation("blocking"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f);
        ItemProperties.register(Items.TRIDENT, new ResourceLocation("throwing"), (itemStack, clientLevel, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f);
    }

    static class CompassWobble {
        private double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        private CompassWobble() {
        }

        private boolean shouldUpdate(long l) {
            return this.lastUpdateTick != l;
        }

        private void update(long l, double d) {
            this.lastUpdateTick = l;
            double d2 = d - this.rotation;
            d2 = Mth.positiveModulo(d2 + 0.5, 1.0) - 0.5;
            this.deltaRotation += d2 * 0.1;
            this.deltaRotation *= 0.8;
            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0);
        }
    }

}

