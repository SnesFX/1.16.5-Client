/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ThreadLocalRandom
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;

    public AttributeModifier(String string, double d, Operation operation) {
        this(Mth.createInsecureUUID((Random)ThreadLocalRandom.current()), () -> string, d, operation);
    }

    public AttributeModifier(UUID uUID, String string, double d, Operation operation) {
        this(uUID, () -> string, d, operation);
    }

    public AttributeModifier(UUID uUID, Supplier<String> supplier, double d, Operation operation) {
        this.id = uUID;
        this.nameGetter = supplier;
        this.amount = d;
        this.operation = operation;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        AttributeModifier attributeModifier = (AttributeModifier)object;
        return Objects.equals(this.id, attributeModifier.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + (Object)((Object)this.operation) + ", name='" + this.nameGetter.get() + '\'' + ", id=" + this.id + '}';
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", this.getName());
        compoundTag.putDouble("Amount", this.amount);
        compoundTag.putInt("Operation", this.operation.toValue());
        compoundTag.putUUID("UUID", this.id);
        return compoundTag;
    }

    @Nullable
    public static AttributeModifier load(CompoundTag compoundTag) {
        try {
            UUID uUID = compoundTag.getUUID("UUID");
            Operation operation = Operation.fromValue(compoundTag.getInt("Operation"));
            return new AttributeModifier(uUID, compoundTag.getString("Name"), compoundTag.getDouble("Amount"), operation);
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }

    public static enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);
        
        private static final Operation[] OPERATIONS;
        private final int value;

        private Operation(int n2) {
            this.value = n2;
        }

        public int toValue() {
            return this.value;
        }

        public static Operation fromValue(int n) {
            if (n < 0 || n >= OPERATIONS.length) {
                throw new IllegalArgumentException("No operation with value " + n);
            }
            return OPERATIONS[n];
        }

        static {
            OPERATIONS = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }

}

