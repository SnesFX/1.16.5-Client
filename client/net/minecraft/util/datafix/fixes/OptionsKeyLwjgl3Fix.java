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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class OptionsKeyLwjgl3Fix
extends DataFix {
    private static final Int2ObjectMap<String> MAP = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(0, (Object)"key.unknown");
        int2ObjectOpenHashMap.put(11, (Object)"key.0");
        int2ObjectOpenHashMap.put(2, (Object)"key.1");
        int2ObjectOpenHashMap.put(3, (Object)"key.2");
        int2ObjectOpenHashMap.put(4, (Object)"key.3");
        int2ObjectOpenHashMap.put(5, (Object)"key.4");
        int2ObjectOpenHashMap.put(6, (Object)"key.5");
        int2ObjectOpenHashMap.put(7, (Object)"key.6");
        int2ObjectOpenHashMap.put(8, (Object)"key.7");
        int2ObjectOpenHashMap.put(9, (Object)"key.8");
        int2ObjectOpenHashMap.put(10, (Object)"key.9");
        int2ObjectOpenHashMap.put(30, (Object)"key.a");
        int2ObjectOpenHashMap.put(40, (Object)"key.apostrophe");
        int2ObjectOpenHashMap.put(48, (Object)"key.b");
        int2ObjectOpenHashMap.put(43, (Object)"key.backslash");
        int2ObjectOpenHashMap.put(14, (Object)"key.backspace");
        int2ObjectOpenHashMap.put(46, (Object)"key.c");
        int2ObjectOpenHashMap.put(58, (Object)"key.caps.lock");
        int2ObjectOpenHashMap.put(51, (Object)"key.comma");
        int2ObjectOpenHashMap.put(32, (Object)"key.d");
        int2ObjectOpenHashMap.put(211, (Object)"key.delete");
        int2ObjectOpenHashMap.put(208, (Object)"key.down");
        int2ObjectOpenHashMap.put(18, (Object)"key.e");
        int2ObjectOpenHashMap.put(207, (Object)"key.end");
        int2ObjectOpenHashMap.put(28, (Object)"key.enter");
        int2ObjectOpenHashMap.put(13, (Object)"key.equal");
        int2ObjectOpenHashMap.put(1, (Object)"key.escape");
        int2ObjectOpenHashMap.put(33, (Object)"key.f");
        int2ObjectOpenHashMap.put(59, (Object)"key.f1");
        int2ObjectOpenHashMap.put(68, (Object)"key.f10");
        int2ObjectOpenHashMap.put(87, (Object)"key.f11");
        int2ObjectOpenHashMap.put(88, (Object)"key.f12");
        int2ObjectOpenHashMap.put(100, (Object)"key.f13");
        int2ObjectOpenHashMap.put(101, (Object)"key.f14");
        int2ObjectOpenHashMap.put(102, (Object)"key.f15");
        int2ObjectOpenHashMap.put(103, (Object)"key.f16");
        int2ObjectOpenHashMap.put(104, (Object)"key.f17");
        int2ObjectOpenHashMap.put(105, (Object)"key.f18");
        int2ObjectOpenHashMap.put(113, (Object)"key.f19");
        int2ObjectOpenHashMap.put(60, (Object)"key.f2");
        int2ObjectOpenHashMap.put(61, (Object)"key.f3");
        int2ObjectOpenHashMap.put(62, (Object)"key.f4");
        int2ObjectOpenHashMap.put(63, (Object)"key.f5");
        int2ObjectOpenHashMap.put(64, (Object)"key.f6");
        int2ObjectOpenHashMap.put(65, (Object)"key.f7");
        int2ObjectOpenHashMap.put(66, (Object)"key.f8");
        int2ObjectOpenHashMap.put(67, (Object)"key.f9");
        int2ObjectOpenHashMap.put(34, (Object)"key.g");
        int2ObjectOpenHashMap.put(41, (Object)"key.grave.accent");
        int2ObjectOpenHashMap.put(35, (Object)"key.h");
        int2ObjectOpenHashMap.put(199, (Object)"key.home");
        int2ObjectOpenHashMap.put(23, (Object)"key.i");
        int2ObjectOpenHashMap.put(210, (Object)"key.insert");
        int2ObjectOpenHashMap.put(36, (Object)"key.j");
        int2ObjectOpenHashMap.put(37, (Object)"key.k");
        int2ObjectOpenHashMap.put(82, (Object)"key.keypad.0");
        int2ObjectOpenHashMap.put(79, (Object)"key.keypad.1");
        int2ObjectOpenHashMap.put(80, (Object)"key.keypad.2");
        int2ObjectOpenHashMap.put(81, (Object)"key.keypad.3");
        int2ObjectOpenHashMap.put(75, (Object)"key.keypad.4");
        int2ObjectOpenHashMap.put(76, (Object)"key.keypad.5");
        int2ObjectOpenHashMap.put(77, (Object)"key.keypad.6");
        int2ObjectOpenHashMap.put(71, (Object)"key.keypad.7");
        int2ObjectOpenHashMap.put(72, (Object)"key.keypad.8");
        int2ObjectOpenHashMap.put(73, (Object)"key.keypad.9");
        int2ObjectOpenHashMap.put(78, (Object)"key.keypad.add");
        int2ObjectOpenHashMap.put(83, (Object)"key.keypad.decimal");
        int2ObjectOpenHashMap.put(181, (Object)"key.keypad.divide");
        int2ObjectOpenHashMap.put(156, (Object)"key.keypad.enter");
        int2ObjectOpenHashMap.put(141, (Object)"key.keypad.equal");
        int2ObjectOpenHashMap.put(55, (Object)"key.keypad.multiply");
        int2ObjectOpenHashMap.put(74, (Object)"key.keypad.subtract");
        int2ObjectOpenHashMap.put(38, (Object)"key.l");
        int2ObjectOpenHashMap.put(203, (Object)"key.left");
        int2ObjectOpenHashMap.put(56, (Object)"key.left.alt");
        int2ObjectOpenHashMap.put(26, (Object)"key.left.bracket");
        int2ObjectOpenHashMap.put(29, (Object)"key.left.control");
        int2ObjectOpenHashMap.put(42, (Object)"key.left.shift");
        int2ObjectOpenHashMap.put(219, (Object)"key.left.win");
        int2ObjectOpenHashMap.put(50, (Object)"key.m");
        int2ObjectOpenHashMap.put(12, (Object)"key.minus");
        int2ObjectOpenHashMap.put(49, (Object)"key.n");
        int2ObjectOpenHashMap.put(69, (Object)"key.num.lock");
        int2ObjectOpenHashMap.put(24, (Object)"key.o");
        int2ObjectOpenHashMap.put(25, (Object)"key.p");
        int2ObjectOpenHashMap.put(209, (Object)"key.page.down");
        int2ObjectOpenHashMap.put(201, (Object)"key.page.up");
        int2ObjectOpenHashMap.put(197, (Object)"key.pause");
        int2ObjectOpenHashMap.put(52, (Object)"key.period");
        int2ObjectOpenHashMap.put(183, (Object)"key.print.screen");
        int2ObjectOpenHashMap.put(16, (Object)"key.q");
        int2ObjectOpenHashMap.put(19, (Object)"key.r");
        int2ObjectOpenHashMap.put(205, (Object)"key.right");
        int2ObjectOpenHashMap.put(184, (Object)"key.right.alt");
        int2ObjectOpenHashMap.put(27, (Object)"key.right.bracket");
        int2ObjectOpenHashMap.put(157, (Object)"key.right.control");
        int2ObjectOpenHashMap.put(54, (Object)"key.right.shift");
        int2ObjectOpenHashMap.put(220, (Object)"key.right.win");
        int2ObjectOpenHashMap.put(31, (Object)"key.s");
        int2ObjectOpenHashMap.put(70, (Object)"key.scroll.lock");
        int2ObjectOpenHashMap.put(39, (Object)"key.semicolon");
        int2ObjectOpenHashMap.put(53, (Object)"key.slash");
        int2ObjectOpenHashMap.put(57, (Object)"key.space");
        int2ObjectOpenHashMap.put(20, (Object)"key.t");
        int2ObjectOpenHashMap.put(15, (Object)"key.tab");
        int2ObjectOpenHashMap.put(22, (Object)"key.u");
        int2ObjectOpenHashMap.put(200, (Object)"key.up");
        int2ObjectOpenHashMap.put(47, (Object)"key.v");
        int2ObjectOpenHashMap.put(17, (Object)"key.w");
        int2ObjectOpenHashMap.put(45, (Object)"key.x");
        int2ObjectOpenHashMap.put(21, (Object)"key.y");
        int2ObjectOpenHashMap.put(44, (Object)"key.z");
    });

    public OptionsKeyLwjgl3Fix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsKeyLwjgl3Fix", this.getInputSchema().getType(References.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.getMapValues().map(map -> dynamic.createMap(map.entrySet().stream().map(entry -> {
            if (((Dynamic)entry.getKey()).asString("").startsWith("key_")) {
                int n = Integer.parseInt(((Dynamic)entry.getValue()).asString(""));
                if (n < 0) {
                    int n2 = n + 100;
                    String string = n2 == 0 ? "key.mouse.left" : (n2 == 1 ? "key.mouse.right" : (n2 == 2 ? "key.mouse.middle" : "key.mouse." + (n2 + 1)));
                    return Pair.of(entry.getKey(), (Object)((Dynamic)entry.getValue()).createString(string));
                }
                String string = (String)MAP.getOrDefault(n, (Object)"key.unknown");
                return Pair.of(entry.getKey(), (Object)((Dynamic)entry.getValue()).createString(string));
            }
            return Pair.of(entry.getKey(), entry.getValue());
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))).result().orElse(dynamic)));
    }
}

