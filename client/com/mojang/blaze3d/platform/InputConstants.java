/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWCharModsCallbackI
 *  org.lwjgl.glfw.GLFWCursorPosCallbackI
 *  org.lwjgl.glfw.GLFWDropCallbackI
 *  org.lwjgl.glfw.GLFWKeyCallbackI
 *  org.lwjgl.glfw.GLFWMouseButtonCallbackI
 *  org.lwjgl.glfw.GLFWScrollCallbackI
 */
package com.mojang.blaze3d.platform;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.LazyLoadedValue;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class InputConstants {
    @Nullable
    private static final MethodHandle glfwRawMouseMotionSupported;
    private static final int GLFW_RAW_MOUSE_MOTION;
    public static final Key UNKNOWN;

    public static Key getKey(int n, int n2) {
        if (n == -1) {
            return Type.SCANCODE.getOrCreate(n2);
        }
        return Type.KEYSYM.getOrCreate(n);
    }

    public static Key getKey(String string) {
        if (Key.NAME_MAP.containsKey(string)) {
            return (Key)Key.NAME_MAP.get(string);
        }
        for (Type type : Type.values()) {
            if (!string.startsWith(type.defaultPrefix)) continue;
            String string2 = string.substring(type.defaultPrefix.length() + 1);
            return type.getOrCreate(Integer.parseInt(string2));
        }
        throw new IllegalArgumentException("Unknown key name: " + string);
    }

    public static boolean isKeyDown(long l, int n) {
        return GLFW.glfwGetKey((long)l, (int)n) == 1;
    }

    public static void setupKeyboardCallbacks(long l, GLFWKeyCallbackI gLFWKeyCallbackI, GLFWCharModsCallbackI gLFWCharModsCallbackI) {
        GLFW.glfwSetKeyCallback((long)l, (GLFWKeyCallbackI)gLFWKeyCallbackI);
        GLFW.glfwSetCharModsCallback((long)l, (GLFWCharModsCallbackI)gLFWCharModsCallbackI);
    }

    public static void setupMouseCallbacks(long l, GLFWCursorPosCallbackI gLFWCursorPosCallbackI, GLFWMouseButtonCallbackI gLFWMouseButtonCallbackI, GLFWScrollCallbackI gLFWScrollCallbackI, GLFWDropCallbackI gLFWDropCallbackI) {
        GLFW.glfwSetCursorPosCallback((long)l, (GLFWCursorPosCallbackI)gLFWCursorPosCallbackI);
        GLFW.glfwSetMouseButtonCallback((long)l, (GLFWMouseButtonCallbackI)gLFWMouseButtonCallbackI);
        GLFW.glfwSetScrollCallback((long)l, (GLFWScrollCallbackI)gLFWScrollCallbackI);
        GLFW.glfwSetDropCallback((long)l, (GLFWDropCallbackI)gLFWDropCallbackI);
    }

    public static void grabOrReleaseMouse(long l, int n, double d, double d2) {
        GLFW.glfwSetCursorPos((long)l, (double)d, (double)d2);
        GLFW.glfwSetInputMode((long)l, (int)208897, (int)n);
    }

    public static boolean isRawMouseInputSupported() {
        try {
            return glfwRawMouseMotionSupported != null && glfwRawMouseMotionSupported.invokeExact();
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void updateRawMouseInput(long l, boolean bl) {
        if (InputConstants.isRawMouseInputSupported()) {
            GLFW.glfwSetInputMode((long)l, (int)GLFW_RAW_MOUSE_MOTION, (int)(bl ? 1 : 0));
        }
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Boolean.TYPE);
        MethodHandle methodHandle = null;
        int n = 0;
        try {
            methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
            MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
            n = methodHandle2.invokeExact();
        }
        catch (NoSuchFieldException | NoSuchMethodException reflectiveOperationException) {
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        glfwRawMouseMotionSupported = methodHandle;
        GLFW_RAW_MOUSE_MOTION = n;
        UNKNOWN = Type.KEYSYM.getOrCreate(-1);
    }

    public static final class Key {
        private final String name;
        private final Type type;
        private final int value;
        private final LazyLoadedValue<Component> displayName;
        private static final Map<String, Key> NAME_MAP = Maps.newHashMap();

        private Key(String string, Type type, int n) {
            this.name = string;
            this.type = type;
            this.value = n;
            this.displayName = new LazyLoadedValue<Component>(() -> (Component)type.displayTextSupplier.apply(n, string));
            NAME_MAP.put(string, this);
        }

        public Type getType() {
            return this.type;
        }

        public int getValue() {
            return this.value;
        }

        public String getName() {
            return this.name;
        }

        public Component getDisplayName() {
            return this.displayName.get();
        }

        public OptionalInt getNumericKeyValue() {
            if (this.value >= 48 && this.value <= 57) {
                return OptionalInt.of(this.value - 48);
            }
            if (this.value >= 320 && this.value <= 329) {
                return OptionalInt.of(this.value - 320);
            }
            return OptionalInt.empty();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Key key = (Key)object;
            return this.value == key.value && this.type == key.type;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.value});
        }

        public String toString() {
            return this.name;
        }
    }

    public static enum Type {
        KEYSYM("key.keyboard", (n, string) -> {
            String string2 = GLFW.glfwGetKeyName((int)n, (int)-1);
            return string2 != null ? new TextComponent(string2) : new TranslatableComponent((String)string);
        }),
        SCANCODE("scancode", (n, string) -> {
            String string2 = GLFW.glfwGetKeyName((int)-1, (int)n);
            return string2 != null ? new TextComponent(string2) : new TranslatableComponent((String)string);
        }),
        MOUSE("key.mouse", (n, string) -> Language.getInstance().has((String)string) ? new TranslatableComponent((String)string) : new TranslatableComponent("key.mouse", n + 1));
        
        private final Int2ObjectMap<Key> map = new Int2ObjectOpenHashMap();
        private final String defaultPrefix;
        private final BiFunction<Integer, String, Component> displayTextSupplier;

        private static void addKey(Type type, String string, int n) {
            Key key = new Key(string, type, n);
            type.map.put(n, (Object)key);
        }

        private Type(String string2, BiFunction<Integer, String, Component> biFunction) {
            this.defaultPrefix = string2;
            this.displayTextSupplier = biFunction;
        }

        public Key getOrCreate(int n2) {
            return (Key)this.map.computeIfAbsent(n2, n -> {
                int n2 = n;
                if (this == MOUSE) {
                    ++n2;
                }
                String string = this.defaultPrefix + "." + n2;
                return new Key(string, this, n);
            });
        }

        static {
            Type.addKey(KEYSYM, "key.keyboard.unknown", -1);
            Type.addKey(MOUSE, "key.mouse.left", 0);
            Type.addKey(MOUSE, "key.mouse.right", 1);
            Type.addKey(MOUSE, "key.mouse.middle", 2);
            Type.addKey(MOUSE, "key.mouse.4", 3);
            Type.addKey(MOUSE, "key.mouse.5", 4);
            Type.addKey(MOUSE, "key.mouse.6", 5);
            Type.addKey(MOUSE, "key.mouse.7", 6);
            Type.addKey(MOUSE, "key.mouse.8", 7);
            Type.addKey(KEYSYM, "key.keyboard.0", 48);
            Type.addKey(KEYSYM, "key.keyboard.1", 49);
            Type.addKey(KEYSYM, "key.keyboard.2", 50);
            Type.addKey(KEYSYM, "key.keyboard.3", 51);
            Type.addKey(KEYSYM, "key.keyboard.4", 52);
            Type.addKey(KEYSYM, "key.keyboard.5", 53);
            Type.addKey(KEYSYM, "key.keyboard.6", 54);
            Type.addKey(KEYSYM, "key.keyboard.7", 55);
            Type.addKey(KEYSYM, "key.keyboard.8", 56);
            Type.addKey(KEYSYM, "key.keyboard.9", 57);
            Type.addKey(KEYSYM, "key.keyboard.a", 65);
            Type.addKey(KEYSYM, "key.keyboard.b", 66);
            Type.addKey(KEYSYM, "key.keyboard.c", 67);
            Type.addKey(KEYSYM, "key.keyboard.d", 68);
            Type.addKey(KEYSYM, "key.keyboard.e", 69);
            Type.addKey(KEYSYM, "key.keyboard.f", 70);
            Type.addKey(KEYSYM, "key.keyboard.g", 71);
            Type.addKey(KEYSYM, "key.keyboard.h", 72);
            Type.addKey(KEYSYM, "key.keyboard.i", 73);
            Type.addKey(KEYSYM, "key.keyboard.j", 74);
            Type.addKey(KEYSYM, "key.keyboard.k", 75);
            Type.addKey(KEYSYM, "key.keyboard.l", 76);
            Type.addKey(KEYSYM, "key.keyboard.m", 77);
            Type.addKey(KEYSYM, "key.keyboard.n", 78);
            Type.addKey(KEYSYM, "key.keyboard.o", 79);
            Type.addKey(KEYSYM, "key.keyboard.p", 80);
            Type.addKey(KEYSYM, "key.keyboard.q", 81);
            Type.addKey(KEYSYM, "key.keyboard.r", 82);
            Type.addKey(KEYSYM, "key.keyboard.s", 83);
            Type.addKey(KEYSYM, "key.keyboard.t", 84);
            Type.addKey(KEYSYM, "key.keyboard.u", 85);
            Type.addKey(KEYSYM, "key.keyboard.v", 86);
            Type.addKey(KEYSYM, "key.keyboard.w", 87);
            Type.addKey(KEYSYM, "key.keyboard.x", 88);
            Type.addKey(KEYSYM, "key.keyboard.y", 89);
            Type.addKey(KEYSYM, "key.keyboard.z", 90);
            Type.addKey(KEYSYM, "key.keyboard.f1", 290);
            Type.addKey(KEYSYM, "key.keyboard.f2", 291);
            Type.addKey(KEYSYM, "key.keyboard.f3", 292);
            Type.addKey(KEYSYM, "key.keyboard.f4", 293);
            Type.addKey(KEYSYM, "key.keyboard.f5", 294);
            Type.addKey(KEYSYM, "key.keyboard.f6", 295);
            Type.addKey(KEYSYM, "key.keyboard.f7", 296);
            Type.addKey(KEYSYM, "key.keyboard.f8", 297);
            Type.addKey(KEYSYM, "key.keyboard.f9", 298);
            Type.addKey(KEYSYM, "key.keyboard.f10", 299);
            Type.addKey(KEYSYM, "key.keyboard.f11", 300);
            Type.addKey(KEYSYM, "key.keyboard.f12", 301);
            Type.addKey(KEYSYM, "key.keyboard.f13", 302);
            Type.addKey(KEYSYM, "key.keyboard.f14", 303);
            Type.addKey(KEYSYM, "key.keyboard.f15", 304);
            Type.addKey(KEYSYM, "key.keyboard.f16", 305);
            Type.addKey(KEYSYM, "key.keyboard.f17", 306);
            Type.addKey(KEYSYM, "key.keyboard.f18", 307);
            Type.addKey(KEYSYM, "key.keyboard.f19", 308);
            Type.addKey(KEYSYM, "key.keyboard.f20", 309);
            Type.addKey(KEYSYM, "key.keyboard.f21", 310);
            Type.addKey(KEYSYM, "key.keyboard.f22", 311);
            Type.addKey(KEYSYM, "key.keyboard.f23", 312);
            Type.addKey(KEYSYM, "key.keyboard.f24", 313);
            Type.addKey(KEYSYM, "key.keyboard.f25", 314);
            Type.addKey(KEYSYM, "key.keyboard.num.lock", 282);
            Type.addKey(KEYSYM, "key.keyboard.keypad.0", 320);
            Type.addKey(KEYSYM, "key.keyboard.keypad.1", 321);
            Type.addKey(KEYSYM, "key.keyboard.keypad.2", 322);
            Type.addKey(KEYSYM, "key.keyboard.keypad.3", 323);
            Type.addKey(KEYSYM, "key.keyboard.keypad.4", 324);
            Type.addKey(KEYSYM, "key.keyboard.keypad.5", 325);
            Type.addKey(KEYSYM, "key.keyboard.keypad.6", 326);
            Type.addKey(KEYSYM, "key.keyboard.keypad.7", 327);
            Type.addKey(KEYSYM, "key.keyboard.keypad.8", 328);
            Type.addKey(KEYSYM, "key.keyboard.keypad.9", 329);
            Type.addKey(KEYSYM, "key.keyboard.keypad.add", 334);
            Type.addKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
            Type.addKey(KEYSYM, "key.keyboard.keypad.enter", 335);
            Type.addKey(KEYSYM, "key.keyboard.keypad.equal", 336);
            Type.addKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
            Type.addKey(KEYSYM, "key.keyboard.keypad.divide", 331);
            Type.addKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
            Type.addKey(KEYSYM, "key.keyboard.down", 264);
            Type.addKey(KEYSYM, "key.keyboard.left", 263);
            Type.addKey(KEYSYM, "key.keyboard.right", 262);
            Type.addKey(KEYSYM, "key.keyboard.up", 265);
            Type.addKey(KEYSYM, "key.keyboard.apostrophe", 39);
            Type.addKey(KEYSYM, "key.keyboard.backslash", 92);
            Type.addKey(KEYSYM, "key.keyboard.comma", 44);
            Type.addKey(KEYSYM, "key.keyboard.equal", 61);
            Type.addKey(KEYSYM, "key.keyboard.grave.accent", 96);
            Type.addKey(KEYSYM, "key.keyboard.left.bracket", 91);
            Type.addKey(KEYSYM, "key.keyboard.minus", 45);
            Type.addKey(KEYSYM, "key.keyboard.period", 46);
            Type.addKey(KEYSYM, "key.keyboard.right.bracket", 93);
            Type.addKey(KEYSYM, "key.keyboard.semicolon", 59);
            Type.addKey(KEYSYM, "key.keyboard.slash", 47);
            Type.addKey(KEYSYM, "key.keyboard.space", 32);
            Type.addKey(KEYSYM, "key.keyboard.tab", 258);
            Type.addKey(KEYSYM, "key.keyboard.left.alt", 342);
            Type.addKey(KEYSYM, "key.keyboard.left.control", 341);
            Type.addKey(KEYSYM, "key.keyboard.left.shift", 340);
            Type.addKey(KEYSYM, "key.keyboard.left.win", 343);
            Type.addKey(KEYSYM, "key.keyboard.right.alt", 346);
            Type.addKey(KEYSYM, "key.keyboard.right.control", 345);
            Type.addKey(KEYSYM, "key.keyboard.right.shift", 344);
            Type.addKey(KEYSYM, "key.keyboard.right.win", 347);
            Type.addKey(KEYSYM, "key.keyboard.enter", 257);
            Type.addKey(KEYSYM, "key.keyboard.escape", 256);
            Type.addKey(KEYSYM, "key.keyboard.backspace", 259);
            Type.addKey(KEYSYM, "key.keyboard.delete", 261);
            Type.addKey(KEYSYM, "key.keyboard.end", 269);
            Type.addKey(KEYSYM, "key.keyboard.home", 268);
            Type.addKey(KEYSYM, "key.keyboard.insert", 260);
            Type.addKey(KEYSYM, "key.keyboard.page.down", 267);
            Type.addKey(KEYSYM, "key.keyboard.page.up", 266);
            Type.addKey(KEYSYM, "key.keyboard.caps.lock", 280);
            Type.addKey(KEYSYM, "key.keyboard.pause", 284);
            Type.addKey(KEYSYM, "key.keyboard.scroll.lock", 281);
            Type.addKey(KEYSYM, "key.keyboard.menu", 348);
            Type.addKey(KEYSYM, "key.keyboard.print.screen", 283);
            Type.addKey(KEYSYM, "key.keyboard.world.1", 161);
            Type.addKey(KEYSYM, "key.keyboard.world.2", 162);
        }
    }

}

