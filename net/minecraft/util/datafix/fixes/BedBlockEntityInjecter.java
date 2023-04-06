/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.types.templates.TaggedChoice
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class BedBlockEntityInjecter
extends DataFix {
    public BedBlockEntityInjecter(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(References.CHUNK);
        Type type2 = type.findFieldType("Level");
        Type type3 = type2.findFieldType("TileEntities");
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        List.ListType listType = (List.ListType)type3;
        return this.cap(type2, listType);
    }

    private <TE> TypeRewriteRule cap(Type<?> type, List.ListType<TE> listType) {
        Type type2 = listType.getElement();
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", type);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"TileEntities", listType);
        int n = 416;
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere("InjectBedBlockEntityType", (Type)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), (Type)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), dynamicOps -> pair -> pair), (TypeRewriteRule)this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), typed -> {
            Typed typed2 = typed.getTyped(opticFinder);
            Dynamic dynamic2 = (Dynamic)typed2.get(DSL.remainderFinder());
            int n = dynamic2.get("xPos").asInt(0);
            int n2 = dynamic2.get("zPos").asInt(0);
            ArrayList arrayList = Lists.newArrayList((Iterable)((Iterable)typed2.getOrCreate(opticFinder2)));
            List list = dynamic2.get("Sections").asList(Function.identity());
            for (int i = 0; i < list.size(); ++i) {
                Dynamic dynamic3 = (Dynamic)list.get(i);
                int n3 = dynamic3.get("Y").asInt(0);
                Stream<Integer> stream = dynamic3.get("Blocks").asStream().map(dynamic -> dynamic.asInt(0));
                int n4 = 0;
                Iterator iterator = ((Iterable)stream::iterator).iterator();
                while (iterator.hasNext()) {
                    int n5 = (Integer)iterator.next();
                    if (416 == (n5 & 0xFF) << 4) {
                        int n6 = n4 & 0xF;
                        int n7 = n4 >> 8 & 0xF;
                        int n8 = n4 >> 4 & 0xF;
                        HashMap hashMap = Maps.newHashMap();
                        hashMap.put(dynamic3.createString("id"), dynamic3.createString("minecraft:bed"));
                        hashMap.put(dynamic3.createString("x"), dynamic3.createInt(n6 + (n << 4)));
                        hashMap.put(dynamic3.createString("y"), dynamic3.createInt(n7 + (n3 << 4)));
                        hashMap.put(dynamic3.createString("z"), dynamic3.createInt(n8 + (n2 << 4)));
                        hashMap.put(dynamic3.createString("color"), dynamic3.createShort((short)14));
                        arrayList.add(((Pair)type2.read(dynamic3.createMap((Map)hashMap)).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity."))).getFirst());
                    }
                    ++n4;
                }
            }
            if (!arrayList.isEmpty()) {
                return typed.set(opticFinder, typed2.set(opticFinder2, (Object)arrayList));
            }
            return typed;
        }));
    }
}

