/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
    public ResourceLocation getId();

    public void addPlayerListener(PlayerAdvancements var1, Listener<T> var2);

    public void removePlayerListener(PlayerAdvancements var1, Listener<T> var2);

    public void removePlayerListeners(PlayerAdvancements var1);

    public T createInstance(JsonObject var1, DeserializationContext var2);

    public static class Listener<T extends CriterionTriggerInstance> {
        private final T trigger;
        private final Advancement advancement;
        private final String criterion;

        public Listener(T t, Advancement advancement, String string) {
            this.trigger = t;
            this.advancement = advancement;
            this.criterion = string;
        }

        public T getTriggerInstance() {
            return this.trigger;
        }

        public void run(PlayerAdvancements playerAdvancements) {
            playerAdvancements.award(this.advancement, this.criterion);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Listener listener = (Listener)object;
            if (!this.trigger.equals(listener.trigger)) {
                return false;
            }
            if (!this.advancement.equals(listener.advancement)) {
                return false;
            }
            return this.criterion.equals(listener.criterion);
        }

        public int hashCode() {
            int n = this.trigger.hashCode();
            n = 31 * n + this.advancement.hashCode();
            n = 31 * n + this.criterion.hashCode();
            return n;
        }
    }

}

