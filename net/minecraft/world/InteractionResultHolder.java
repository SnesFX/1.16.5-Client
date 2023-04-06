/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world;

import net.minecraft.world.InteractionResult;

public class InteractionResultHolder<T> {
    private final InteractionResult result;
    private final T object;

    public InteractionResultHolder(InteractionResult interactionResult, T t) {
        this.result = interactionResult;
        this.object = t;
    }

    public InteractionResult getResult() {
        return this.result;
    }

    public T getObject() {
        return this.object;
    }

    public static <T> InteractionResultHolder<T> success(T t) {
        return new InteractionResultHolder<T>(InteractionResult.SUCCESS, t);
    }

    public static <T> InteractionResultHolder<T> consume(T t) {
        return new InteractionResultHolder<T>(InteractionResult.CONSUME, t);
    }

    public static <T> InteractionResultHolder<T> pass(T t) {
        return new InteractionResultHolder<T>(InteractionResult.PASS, t);
    }

    public static <T> InteractionResultHolder<T> fail(T t) {
        return new InteractionResultHolder<T>(InteractionResult.FAIL, t);
    }

    public static <T> InteractionResultHolder<T> sidedSuccess(T t, boolean bl) {
        return bl ? InteractionResultHolder.success(t) : InteractionResultHolder.consume(t);
    }
}

