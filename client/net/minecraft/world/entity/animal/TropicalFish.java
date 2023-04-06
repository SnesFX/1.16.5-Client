/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TropicalFish
extends AbstractSchoolingFish {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
    private static final ResourceLocation[] BASE_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
    private static final ResourceLocation[] PATTERN_A_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final ResourceLocation[] PATTERN_B_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
    public static final int[] COMMON_VARIANTS = new int[]{TropicalFish.calculateVariant(Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), TropicalFish.calculateVariant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), TropicalFish.calculateVariant(Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), TropicalFish.calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), TropicalFish.calculateVariant(Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), TropicalFish.calculateVariant(Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), TropicalFish.calculateVariant(Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), TropicalFish.calculateVariant(Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), TropicalFish.calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), TropicalFish.calculateVariant(Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), TropicalFish.calculateVariant(Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), TropicalFish.calculateVariant(Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), TropicalFish.calculateVariant(Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), TropicalFish.calculateVariant(Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), TropicalFish.calculateVariant(Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), TropicalFish.calculateVariant(Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), TropicalFish.calculateVariant(Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), TropicalFish.calculateVariant(Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), TropicalFish.calculateVariant(Pattern.KOB, DyeColor.RED, DyeColor.WHITE), TropicalFish.calculateVariant(Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), TropicalFish.calculateVariant(Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), TropicalFish.calculateVariant(Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
    private boolean isSchool = true;

    private static int calculateVariant(Pattern pattern, DyeColor dyeColor, DyeColor dyeColor2) {
        return pattern.getBase() & 0xFF | (pattern.getIndex() & 0xFF) << 8 | (dyeColor.getId() & 0xFF) << 16 | (dyeColor2.getId() & 0xFF) << 24;
    }

    public TropicalFish(EntityType<? extends TropicalFish> entityType, Level level) {
        super(entityType, level);
    }

    public static String getPredefinedName(int n) {
        return "entity.minecraft.tropical_fish.predefined." + n;
    }

    public static DyeColor getBaseColor(int n) {
        return DyeColor.byId(TropicalFish.getBaseColorIdx(n));
    }

    public static DyeColor getPatternColor(int n) {
        return DyeColor.byId(TropicalFish.getPatternColorIdx(n));
    }

    public static String getFishTypeName(int n) {
        int n2 = TropicalFish.getBaseVariant(n);
        int n3 = TropicalFish.getPatternVariant(n);
        return "entity.minecraft.tropical_fish.type." + Pattern.getPatternName(n2, n3);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setVariant(compoundTag.getInt("Variant"));
    }

    public void setVariant(int n) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, n);
    }

    @Override
    public boolean isMaxGroupSizeReached(int n) {
        return !this.isSchool;
    }

    public int getVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    @Override
    protected void saveToBucketTag(ItemStack itemStack) {
        super.saveToBucketTag(itemStack);
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putInt("BucketVariantTag", this.getVariant());
    }

    @Override
    protected ItemStack getBucketItemStack() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    private static int getBaseColorIdx(int n) {
        return (n & 0xFF0000) >> 16;
    }

    public float[] getBaseColor() {
        return DyeColor.byId(TropicalFish.getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
    }

    private static int getPatternColorIdx(int n) {
        return (n & 0xFF000000) >> 24;
    }

    public float[] getPatternColor() {
        return DyeColor.byId(TropicalFish.getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
    }

    public static int getBaseVariant(int n) {
        return Math.min(n & 0xFF, 1);
    }

    public int getBaseVariant() {
        return TropicalFish.getBaseVariant(this.getVariant());
    }

    private static int getPatternVariant(int n) {
        return Math.min((n & 0xFF00) >> 8, 5);
    }

    public ResourceLocation getPatternTextureLocation() {
        if (TropicalFish.getBaseVariant(this.getVariant()) == 0) {
            return PATTERN_A_TEXTURE_LOCATIONS[TropicalFish.getPatternVariant(this.getVariant())];
        }
        return PATTERN_B_TEXTURE_LOCATIONS[TropicalFish.getPatternVariant(this.getVariant())];
    }

    public ResourceLocation getBaseTextureLocation() {
        return BASE_TEXTURE_LOCATIONS[TropicalFish.getBaseVariant(this.getVariant())];
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        int n;
        int n2;
        int n3;
        int n4;
        spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
            this.setVariant(compoundTag.getInt("BucketVariantTag"));
            return spawnGroupData;
        }
        if (spawnGroupData instanceof TropicalFishGroupData) {
            TropicalFishGroupData tropicalFishGroupData = (TropicalFishGroupData)spawnGroupData;
            n4 = tropicalFishGroupData.base;
            n2 = tropicalFishGroupData.pattern;
            n = tropicalFishGroupData.baseColor;
            n3 = tropicalFishGroupData.patternColor;
        } else if ((double)this.random.nextFloat() < 0.9) {
            int n5 = Util.getRandom(COMMON_VARIANTS, this.random);
            n4 = n5 & 0xFF;
            n2 = (n5 & 0xFF00) >> 8;
            n = (n5 & 0xFF0000) >> 16;
            n3 = (n5 & 0xFF000000) >> 24;
            spawnGroupData = new TropicalFishGroupData(this, n4, n2, n, n3);
        } else {
            this.isSchool = false;
            n4 = this.random.nextInt(2);
            n2 = this.random.nextInt(6);
            n = this.random.nextInt(15);
            n3 = this.random.nextInt(15);
        }
        this.setVariant(n4 | n2 << 8 | n << 16 | n3 << 24);
        return spawnGroupData;
    }

    static class TropicalFishGroupData
    extends AbstractSchoolingFish.SchoolSpawnGroupData {
        private final int base;
        private final int pattern;
        private final int baseColor;
        private final int patternColor;

        private TropicalFishGroupData(TropicalFish tropicalFish, int n, int n2, int n3, int n4) {
            super(tropicalFish);
            this.base = n;
            this.pattern = n2;
            this.baseColor = n3;
            this.patternColor = n4;
        }
    }

    static enum Pattern {
        KOB(0, 0),
        SUNSTREAK(0, 1),
        SNOOPER(0, 2),
        DASHER(0, 3),
        BRINELY(0, 4),
        SPOTTY(0, 5),
        FLOPPER(1, 0),
        STRIPEY(1, 1),
        GLITTER(1, 2),
        BLOCKFISH(1, 3),
        BETTY(1, 4),
        CLAYFISH(1, 5);
        
        private final int base;
        private final int index;
        private static final Pattern[] VALUES;

        private Pattern(int n2, int n3) {
            this.base = n2;
            this.index = n3;
        }

        public int getBase() {
            return this.base;
        }

        public int getIndex() {
            return this.index;
        }

        public static String getPatternName(int n, int n2) {
            return VALUES[n2 + 6 * n].getName();
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        static {
            VALUES = Pattern.values();
        }
    }

}

