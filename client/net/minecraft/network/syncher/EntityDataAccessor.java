/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.syncher;

import net.minecraft.network.syncher.EntityDataSerializer;

public class EntityDataAccessor<T> {
    private final int id;
    private final EntityDataSerializer<T> serializer;

    public EntityDataAccessor(int n, EntityDataSerializer<T> entityDataSerializer) {
        this.id = n;
        this.serializer = entityDataSerializer;
    }

    public int getId() {
        return this.id;
    }

    public EntityDataSerializer<T> getSerializer() {
        return this.serializer;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        EntityDataAccessor entityDataAccessor = (EntityDataAccessor)object;
        return this.id == entityDataAccessor.id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "<entity data: " + this.id + ">";
    }
}

