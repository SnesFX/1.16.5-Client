/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.sensing;

import java.lang.invoke.LambdaMetafactory;
import java.util.function.Supplier;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.AdultSensor;
import net.minecraft.world.entity.ai.sensing.DummySensor;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.HoglinSpecificSensor;
import net.minecraft.world.entity.ai.sensing.HurtBySensor;
import net.minecraft.world.entity.ai.sensing.NearestBedSensor;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.PiglinBruteSpecificSensor;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.SecondaryPoiSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;

public class SensorType<U extends Sensor<?>> {
    public static final SensorType<DummySensor> DUMMY = SensorType.register("dummy", (Supplier<DummySensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/DummySensor;)());
    public static final SensorType<NearestItemSensor> NEAREST_ITEMS = SensorType.register("nearest_items", (Supplier<NearestItemSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/NearestItemSensor;)());
    public static final SensorType<NearestLivingEntitySensor> NEAREST_LIVING_ENTITIES = SensorType.register("nearest_living_entities", (Supplier<NearestLivingEntitySensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/NearestLivingEntitySensor;)());
    public static final SensorType<PlayerSensor> NEAREST_PLAYERS = SensorType.register("nearest_players", (Supplier<PlayerSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/PlayerSensor;)());
    public static final SensorType<NearestBedSensor> NEAREST_BED = SensorType.register("nearest_bed", NearestBedSensor::new);
    public static final SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", (Supplier<HurtBySensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/HurtBySensor;)());
    public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = SensorType.register("villager_hostiles", VillagerHostilesSensor::new);
    public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = SensorType.register("villager_babies", VillagerBabiesSensor::new);
    public static final SensorType<SecondaryPoiSensor> SECONDARY_POIS = SensorType.register("secondary_pois", SecondaryPoiSensor::new);
    public static final SensorType<GolemSensor> GOLEM_DETECTED = SensorType.register("golem_detected", GolemSensor::new);
    public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = SensorType.register("piglin_specific_sensor", (Supplier<PiglinSpecificSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/PiglinSpecificSensor;)());
    public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = SensorType.register("piglin_brute_specific_sensor", (Supplier<PiglinBruteSpecificSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/PiglinBruteSpecificSensor;)());
    public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = SensorType.register("hoglin_specific_sensor", (Supplier<HoglinSpecificSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/HoglinSpecificSensor;)());
    public static final SensorType<AdultSensor> NEAREST_ADULT = SensorType.register("nearest_adult", (Supplier<AdultSensor>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Lnet/minecraft/world/entity/ai/sensing/AdultSensor;)());
    private final Supplier<U> factory;

    private SensorType(Supplier<U> supplier) {
        this.factory = supplier;
    }

    public U create() {
        return (U)((Sensor)this.factory.get());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String string, Supplier<U> supplier) {
        return Registry.register(Registry.SENSOR_TYPE, new ResourceLocation(string), new SensorType<U>(supplier));
    }
}

