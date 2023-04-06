/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core.dispenser;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public interface DispenseItemBehavior {
    public static final DispenseItemBehavior NOOP = (blockSource, itemStack) -> itemStack;

    public ItemStack dispense(BlockSource var1, ItemStack var2);

    public static void bootStrap() {
        DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Arrow arrow = new Arrow(level, position.x(), position.y(), position.z());
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                Arrow arrow = new Arrow(level, position.x(), position.y(), position.z());
                arrow.setEffectsFromItem(itemStack);
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return arrow;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                SpectralArrow spectralArrow = new SpectralArrow(level, position.x(), position.y(), position.z());
                spectralArrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return spectralArrow;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return Util.make(new ThrownEgg(level, position.x(), position.y(), position.z()), thrownEgg -> thrownEgg.setItem(itemStack));
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return Util.make(new Snowball(level, position.x(), position.y(), position.z()), snowball -> snowball.setItem(itemStack));
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return Util.make(new ThrownExperienceBottle(level, position.x(), position.y(), position.z()), thrownExperienceBottle -> thrownExperienceBottle.setItem(itemStack));
            }

            @Override
            protected float getUncertainty() {
                return super.getUncertainty() * 0.5f;
            }

            @Override
            protected float getPower() {
                return super.getPower() * 1.25f;
            }
        });
        DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior(){

            @Override
            public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
                return new AbstractProjectileDispenseBehavior(){

                    @Override
                    protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                        return Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), thrownPotion -> thrownPotion.setItem(itemStack));
                    }

                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense(blockSource, itemStack);
            }

        });
        DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior(){

            @Override
            public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
                return new AbstractProjectileDispenseBehavior(){

                    @Override
                    protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                        return Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), thrownPotion -> thrownPotion.setItem(itemStack));
                    }

                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense(blockSource, itemStack);
            }

        });
        DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem)itemStack.getItem()).getType(itemStack.getTag());
                entityType.spawn(blockSource.getLevel(), itemStack, null, blockSource.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                itemStack.shrink(1);
                return itemStack;
            }
        };
        for (SpawnEggItem object2 : SpawnEggItem.eggs()) {
            DispenserBlock.registerBehavior(object2, defaultDispenseItemBehavior);
        }
        DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.getPos().relative(direction);
                ServerLevel serverLevel = blockSource.getLevel();
                ArmorStand armorStand = new ArmorStand(serverLevel, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
                EntityType.updateCustomEntityTag(serverLevel, null, armorStand, itemStack.getTag());
                armorStand.yRot = direction.toYRot();
                serverLevel.addFreshEntity(armorStand);
                itemStack.shrink(1);
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                List<LivingEntity> list = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), livingEntity -> {
                    if (livingEntity instanceof Saddleable) {
                        Saddleable saddleable = (Saddleable)((Object)livingEntity);
                        return !saddleable.isSaddled() && saddleable.isSaddleable();
                    }
                    return false;
                });
                if (!list.isEmpty()) {
                    ((Saddleable)((Object)list.get(0))).equipSaddle(SoundSource.BLOCKS);
                    itemStack.shrink(1);
                    this.setSuccess(true);
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        });
        OptionalDispenseItemBehavior optionalDispenseItemBehavior = new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                List<AbstractHorse> list = blockSource.getLevel().getEntitiesOfClass(AbstractHorse.class, new AABB(blockPos), abstractHorse -> abstractHorse.isAlive() && abstractHorse.canWearArmor());
                for (AbstractHorse abstractHorse2 : list) {
                    if (!abstractHorse2.isArmor(itemStack) || abstractHorse2.isWearingArmor() || !abstractHorse2.isTamed()) continue;
                    abstractHorse2.setSlot(401, itemStack.split(1));
                    this.setSuccess(true);
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        };
        DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.WHITE_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.ORANGE_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.CYAN_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.BLUE_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.BROWN_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.BLACK_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.GRAY_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.GREEN_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.LIME_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.PINK_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.PURPLE_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.RED_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.YELLOW_CARPET, optionalDispenseItemBehavior);
        DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                List<AbstractChestedHorse> list = blockSource.getLevel().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(blockPos), abstractChestedHorse -> abstractChestedHorse.isAlive() && !abstractChestedHorse.hasChest());
                for (AbstractChestedHorse abstractChestedHorse2 : list) {
                    if (!abstractChestedHorse2.isTamed() || !abstractChestedHorse2.setSlot(499, itemStack)) continue;
                    itemStack.shrink(1);
                    this.setSuccess(true);
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        });
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity((Level)blockSource.getLevel(), itemStack, blockSource.x(), blockSource.y(), blockSource.x(), true);
                DispenseItemBehavior.setEntityPokingOutOfBlock(blockSource, fireworkRocketEntity, direction);
                fireworkRocketEntity.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 0.5f, 1.0f);
                blockSource.getLevel().addFreshEntity(fireworkRocketEntity);
                itemStack.shrink(1);
                return itemStack;
            }

            @Override
            protected void playSound(BlockSource blockSource) {
                blockSource.getLevel().levelEvent(1004, blockSource.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                Position position = DispenserBlock.getDispensePosition(blockSource);
                double d = position.x() + (double)((float)direction.getStepX() * 0.3f);
                double d2 = position.y() + (double)((float)direction.getStepY() * 0.3f);
                double d3 = position.z() + (double)((float)direction.getStepZ() * 0.3f);
                ServerLevel serverLevel = blockSource.getLevel();
                Random random = serverLevel.random;
                double d4 = random.nextGaussian() * 0.05 + (double)direction.getStepX();
                double d5 = random.nextGaussian() * 0.05 + (double)direction.getStepY();
                double d6 = random.nextGaussian() * 0.05 + (double)direction.getStepZ();
                serverLevel.addFreshEntity(Util.make(new SmallFireball(serverLevel, d, d2, d3, d4, d5, d6), smallFireball -> smallFireball.setItem(itemStack)));
                itemStack.shrink(1);
                return itemStack;
            }

            @Override
            protected void playSound(BlockSource blockSource) {
                blockSource.getLevel().levelEvent(1018, blockSource.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
        DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
        DefaultDispenseItemBehavior defaultDispenseItemBehavior2 = new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BucketItem bucketItem = (BucketItem)itemStack.getItem();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                ServerLevel serverLevel = blockSource.getLevel();
                if (bucketItem.emptyBucket(null, serverLevel, blockPos, null)) {
                    bucketItem.checkExtraContent(serverLevel, itemStack, blockPos);
                    return new ItemStack(Items.BUCKET);
                }
                return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
            }
        };
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.SALMON_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.COD_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, defaultDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                BlockPos blockPos;
                Fluid fluid;
                ServerLevel serverLevel = blockSource.getLevel();
                BlockState blockState = serverLevel.getBlockState(blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING)));
                Block block = blockState.getBlock();
                if (block instanceof BucketPickup) {
                    fluid = ((BucketPickup)((Object)block)).takeLiquid(serverLevel, blockPos, blockState);
                    if (!(fluid instanceof FlowingFluid)) {
                        return super.execute(blockSource, itemStack);
                    }
                } else {
                    return super.execute(blockSource, itemStack);
                }
                Item item = fluid.getBucket();
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    return new ItemStack(item);
                }
                if (((DispenserBlockEntity)blockSource.getEntity()).addItem(new ItemStack(item)) < 0) {
                    this.defaultDispenseItemBehavior.dispense(blockSource, new ItemStack(item));
                }
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                ServerLevel serverLevel = blockSource.getLevel();
                this.setSuccess(true);
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.getPos().relative(direction);
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (BaseFireBlock.canBePlacedAt(serverLevel, blockPos, direction)) {
                    serverLevel.setBlockAndUpdate(blockPos, BaseFireBlock.getState(serverLevel, blockPos));
                } else if (CampfireBlock.canLight(blockState)) {
                    serverLevel.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(BlockStateProperties.LIT, true));
                } else if (blockState.getBlock() instanceof TntBlock) {
                    TntBlock.explode(serverLevel, blockPos);
                    serverLevel.removeBlock(blockPos, false);
                } else {
                    this.setSuccess(false);
                }
                if (this.isSuccess() && itemStack.hurt(1, serverLevel.random, null)) {
                    itemStack.setCount(0);
                }
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(true);
                ServerLevel serverLevel = blockSource.getLevel();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                if (BoneMealItem.growCrop(itemStack, serverLevel, blockPos) || BoneMealItem.growWaterPlant(itemStack, serverLevel, blockPos, null)) {
                    if (!serverLevel.isClientSide) {
                        serverLevel.levelEvent(2005, blockPos, 0);
                    }
                } else {
                    this.setSuccess(false);
                }
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                ServerLevel serverLevel = blockSource.getLevel();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                PrimedTnt primedTnt = new PrimedTnt(serverLevel, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
                serverLevel.addFreshEntity(primedTnt);
                ((Level)serverLevel).playSound(null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                itemStack.shrink(1);
                return itemStack;
            }
        });
        OptionalDispenseItemBehavior optionalDispenseItemBehavior2 = new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                return itemStack;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, optionalDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, optionalDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, optionalDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, optionalDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, optionalDispenseItemBehavior2);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                ServerLevel serverLevel = blockSource.getLevel();
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.getPos().relative(direction);
                if (serverLevel.isEmptyBlock(blockPos) && WitherSkullBlock.canSpawnMob(serverLevel, blockPos, itemStack)) {
                    serverLevel.setBlock(blockPos, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().get2DDataValue() * 4), 3);
                    BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                    if (blockEntity instanceof SkullBlockEntity) {
                        WitherSkullBlock.checkSpawn(serverLevel, blockPos, (SkullBlockEntity)blockEntity);
                    }
                    itemStack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                }
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                ServerLevel serverLevel = blockSource.getLevel();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                if (serverLevel.isEmptyBlock(blockPos) && carvedPumpkinBlock.canSpawnGolem(serverLevel, blockPos)) {
                    if (!serverLevel.isClientSide) {
                        serverLevel.setBlock(blockPos, carvedPumpkinBlock.defaultBlockState(), 3);
                    }
                    itemStack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(blockSource, itemStack));
                }
                return itemStack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());
        for (DyeColor dyeColor : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(dyeColor).asItem(), new ShulkerBoxDispenseBehavior());
        }
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            private ItemStack takeLiquid(BlockSource blockSource, ItemStack itemStack, ItemStack itemStack2) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    return itemStack2.copy();
                }
                if (((DispenserBlockEntity)blockSource.getEntity()).addItem(itemStack2.copy()) < 0) {
                    this.defaultDispenseItemBehavior.dispense(blockSource, itemStack2.copy());
                }
                return itemStack;
            }

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                this.setSuccess(false);
                ServerLevel serverLevel = blockSource.getLevel();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                BlockState blockState = serverLevel.getBlockState(blockPos);
                if (blockState.is(BlockTags.BEEHIVES, blockStateBase -> blockStateBase.hasProperty(BeehiveBlock.HONEY_LEVEL)) && blockState.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)blockState.getBlock()).releaseBeesAndResetHoneyLevel(serverLevel, blockState, blockPos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.takeLiquid(blockSource, itemStack, new ItemStack(Items.HONEY_BOTTLE));
                }
                if (serverLevel.getFluidState(blockPos).is(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid(blockSource, itemStack, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                }
                return super.execute(blockSource, itemStack);
            }
        });
        DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.getPos().relative(direction);
                ServerLevel serverLevel = blockSource.getLevel();
                BlockState blockState = serverLevel.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                    if (blockState.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                        RespawnAnchorBlock.charge(serverLevel, blockPos, blockState);
                        itemStack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        });
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
    }

    public static void setEntityPokingOutOfBlock(BlockSource blockSource, Entity entity, Direction direction) {
        entity.setPos(blockSource.x() + (double)direction.getStepX() * (0.5000099999997474 - (double)entity.getBbWidth() / 2.0), blockSource.y() + (double)direction.getStepY() * (0.5000099999997474 - (double)entity.getBbHeight() / 2.0) - (double)entity.getBbHeight() / 2.0, blockSource.z() + (double)direction.getStepZ() * (0.5000099999997474 - (double)entity.getBbWidth() / 2.0));
    }

}

