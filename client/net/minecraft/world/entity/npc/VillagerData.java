/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
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
 */
package net.minecraft.world.entity.npc;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerData {
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.VILLAGER_TYPE.fieldOf("type").orElseGet(() -> VillagerType.PLAINS).forGetter(villagerData -> villagerData.type), (App)Registry.VILLAGER_PROFESSION.fieldOf("profession").orElseGet(() -> VillagerProfession.NONE).forGetter(villagerData -> villagerData.profession), (App)Codec.INT.fieldOf("level").orElse((Object)1).forGetter(villagerData -> villagerData.level)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> VillagerData.new(arg_0, arg_1, arg_2)));
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType villagerType, VillagerProfession villagerProfession, int n) {
        this.type = villagerType;
        this.profession = villagerProfession;
        this.level = Math.max(1, n);
    }

    public VillagerType getType() {
        return this.type;
    }

    public VillagerProfession getProfession() {
        return this.profession;
    }

    public int getLevel() {
        return this.level;
    }

    public VillagerData setType(VillagerType villagerType) {
        return new VillagerData(villagerType, this.profession, this.level);
    }

    public VillagerData setProfession(VillagerProfession villagerProfession) {
        return new VillagerData(this.type, villagerProfession, this.level);
    }

    public VillagerData setLevel(int n) {
        return new VillagerData(this.type, this.profession, n);
    }

    public static int getMinXpPerLevel(int n) {
        return VillagerData.canLevelUp(n) ? NEXT_LEVEL_XP_THRESHOLDS[n - 1] : 0;
    }

    public static int getMaxXpPerLevel(int n) {
        return VillagerData.canLevelUp(n) ? NEXT_LEVEL_XP_THRESHOLDS[n] : 0;
    }

    public static boolean canLevelUp(int n) {
        return n >= 1 && n < 5;
    }
}

