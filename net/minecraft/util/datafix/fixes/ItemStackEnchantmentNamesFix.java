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
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class ItemStackEnchantmentNamesFix
extends DataFix {
    private static final Int2ObjectMap<String> MAP = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(0, (Object)"minecraft:protection");
        int2ObjectOpenHashMap.put(1, (Object)"minecraft:fire_protection");
        int2ObjectOpenHashMap.put(2, (Object)"minecraft:feather_falling");
        int2ObjectOpenHashMap.put(3, (Object)"minecraft:blast_protection");
        int2ObjectOpenHashMap.put(4, (Object)"minecraft:projectile_protection");
        int2ObjectOpenHashMap.put(5, (Object)"minecraft:respiration");
        int2ObjectOpenHashMap.put(6, (Object)"minecraft:aqua_affinity");
        int2ObjectOpenHashMap.put(7, (Object)"minecraft:thorns");
        int2ObjectOpenHashMap.put(8, (Object)"minecraft:depth_strider");
        int2ObjectOpenHashMap.put(9, (Object)"minecraft:frost_walker");
        int2ObjectOpenHashMap.put(10, (Object)"minecraft:binding_curse");
        int2ObjectOpenHashMap.put(16, (Object)"minecraft:sharpness");
        int2ObjectOpenHashMap.put(17, (Object)"minecraft:smite");
        int2ObjectOpenHashMap.put(18, (Object)"minecraft:bane_of_arthropods");
        int2ObjectOpenHashMap.put(19, (Object)"minecraft:knockback");
        int2ObjectOpenHashMap.put(20, (Object)"minecraft:fire_aspect");
        int2ObjectOpenHashMap.put(21, (Object)"minecraft:looting");
        int2ObjectOpenHashMap.put(22, (Object)"minecraft:sweeping");
        int2ObjectOpenHashMap.put(32, (Object)"minecraft:efficiency");
        int2ObjectOpenHashMap.put(33, (Object)"minecraft:silk_touch");
        int2ObjectOpenHashMap.put(34, (Object)"minecraft:unbreaking");
        int2ObjectOpenHashMap.put(35, (Object)"minecraft:fortune");
        int2ObjectOpenHashMap.put(48, (Object)"minecraft:power");
        int2ObjectOpenHashMap.put(49, (Object)"minecraft:punch");
        int2ObjectOpenHashMap.put(50, (Object)"minecraft:flame");
        int2ObjectOpenHashMap.put(51, (Object)"minecraft:infinity");
        int2ObjectOpenHashMap.put(61, (Object)"minecraft:luck_of_the_sea");
        int2ObjectOpenHashMap.put(62, (Object)"minecraft:lure");
        int2ObjectOpenHashMap.put(65, (Object)"minecraft:loyalty");
        int2ObjectOpenHashMap.put(66, (Object)"minecraft:impaling");
        int2ObjectOpenHashMap.put(67, (Object)"minecraft:riptide");
        int2ObjectOpenHashMap.put(68, (Object)"minecraft:channeling");
        int2ObjectOpenHashMap.put(70, (Object)"minecraft:mending");
        int2ObjectOpenHashMap.put(71, (Object)"minecraft:vanishing_curse");
    });

    public ItemStackEnchantmentNamesFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemStackEnchantmentFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixTag)));
    }

    private Dynamic<?> fixTag(Dynamic<?> dynamic2) {
        Optional optional = dynamic2.get("ench").asStreamOpt().map(stream -> stream.map(dynamic -> dynamic.set("id", dynamic.createString((String)MAP.getOrDefault(dynamic.get("id").asInt(0), (Object)"null"))))).map(dynamic2::createList).result();
        if (optional.isPresent()) {
            dynamic2 = dynamic2.remove("ench").set("Enchantments", (Dynamic)optional.get());
        }
        return dynamic2.update("StoredEnchantments", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asStreamOpt().map(stream -> stream.map(dynamic -> dynamic.set("id", dynamic.createString((String)MAP.getOrDefault(dynamic.get("id").asInt(0), (Object)"null"))))).map(((Dynamic)dynamic)::createList).result(), (Object)dynamic));
    }
}

