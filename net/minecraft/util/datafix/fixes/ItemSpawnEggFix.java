/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemSpawnEggFix
extends DataFix {
    private static final String[] ID_TO_ENTITY = (String[])DataFixUtils.make((Object)new String[256], arrstring -> {
        arrstring[1] = "Item";
        arrstring[2] = "XPOrb";
        arrstring[7] = "ThrownEgg";
        arrstring[8] = "LeashKnot";
        arrstring[9] = "Painting";
        arrstring[10] = "Arrow";
        arrstring[11] = "Snowball";
        arrstring[12] = "Fireball";
        arrstring[13] = "SmallFireball";
        arrstring[14] = "ThrownEnderpearl";
        arrstring[15] = "EyeOfEnderSignal";
        arrstring[16] = "ThrownPotion";
        arrstring[17] = "ThrownExpBottle";
        arrstring[18] = "ItemFrame";
        arrstring[19] = "WitherSkull";
        arrstring[20] = "PrimedTnt";
        arrstring[21] = "FallingSand";
        arrstring[22] = "FireworksRocketEntity";
        arrstring[23] = "TippedArrow";
        arrstring[24] = "SpectralArrow";
        arrstring[25] = "ShulkerBullet";
        arrstring[26] = "DragonFireball";
        arrstring[30] = "ArmorStand";
        arrstring[41] = "Boat";
        arrstring[42] = "MinecartRideable";
        arrstring[43] = "MinecartChest";
        arrstring[44] = "MinecartFurnace";
        arrstring[45] = "MinecartTNT";
        arrstring[46] = "MinecartHopper";
        arrstring[47] = "MinecartSpawner";
        arrstring[40] = "MinecartCommandBlock";
        arrstring[48] = "Mob";
        arrstring[49] = "Monster";
        arrstring[50] = "Creeper";
        arrstring[51] = "Skeleton";
        arrstring[52] = "Spider";
        arrstring[53] = "Giant";
        arrstring[54] = "Zombie";
        arrstring[55] = "Slime";
        arrstring[56] = "Ghast";
        arrstring[57] = "PigZombie";
        arrstring[58] = "Enderman";
        arrstring[59] = "CaveSpider";
        arrstring[60] = "Silverfish";
        arrstring[61] = "Blaze";
        arrstring[62] = "LavaSlime";
        arrstring[63] = "EnderDragon";
        arrstring[64] = "WitherBoss";
        arrstring[65] = "Bat";
        arrstring[66] = "Witch";
        arrstring[67] = "Endermite";
        arrstring[68] = "Guardian";
        arrstring[69] = "Shulker";
        arrstring[90] = "Pig";
        arrstring[91] = "Sheep";
        arrstring[92] = "Cow";
        arrstring[93] = "Chicken";
        arrstring[94] = "Squid";
        arrstring[95] = "Wolf";
        arrstring[96] = "MushroomCow";
        arrstring[97] = "SnowMan";
        arrstring[98] = "Ozelot";
        arrstring[99] = "VillagerGolem";
        arrstring[100] = "EntityHorse";
        arrstring[101] = "Rabbit";
        arrstring[120] = "Villager";
        arrstring[200] = "EnderCrystal";
    });

    public ItemSpawnEggFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        Type type = schema.getType(References.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"id", (Type)DSL.string());
        OpticFinder opticFinder3 = type.findField("tag");
        OpticFinder opticFinder4 = opticFinder3.type().findField("EntityTag");
        OpticFinder opticFinder5 = DSL.typeFinder((Type)schema.getTypeRaw(References.ENTITY));
        Type type2 = this.getOutputSchema().getTypeRaw(References.ENTITY);
        return this.fixTypeEverywhereTyped("ItemSpawnEggFix", type, typed2 -> {
            Optional optional = typed2.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:spawn_egg")) {
                Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
                short s = dynamic.get("Damage").asShort((short)0);
                Optional optional2 = typed2.getOptionalTyped(opticFinder3);
                Optional optional3 = optional2.flatMap(typed -> typed.getOptionalTyped(opticFinder4));
                Optional optional4 = optional3.flatMap(typed -> typed.getOptionalTyped(opticFinder5));
                Optional optional5 = optional4.flatMap(typed -> typed.getOptional(opticFinder2));
                Typed typed3 = typed2;
                String string = ID_TO_ENTITY[s & 0xFF];
                if (!(string == null || optional5.isPresent() && Objects.equals(optional5.get(), string))) {
                    Typed typed4 = typed2.getOrCreateTyped(opticFinder3);
                    Typed typed5 = typed4.getOrCreateTyped(opticFinder4);
                    Typed typed6 = typed5.getOrCreateTyped(opticFinder5);
                    Dynamic dynamic3 = dynamic;
                    Typed typed7 = (Typed)((Pair)typed6.write().flatMap(dynamic2 -> type2.readTyped(dynamic2.set("id", dynamic3.createString(string)))).result().orElseThrow(() -> new IllegalStateException("Could not parse new entity"))).getFirst();
                    typed3 = typed3.set(opticFinder3, typed4.set(opticFinder4, typed5.set(opticFinder5, typed7)));
                }
                if (s != 0) {
                    dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
                    typed3 = typed3.set(DSL.remainderFinder(), (Object)dynamic);
                }
                return typed3;
            }
            return typed2;
        });
    }
}

