/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

public class ItemCooldowns {
    private final Map<Item, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    public boolean isOnCooldown(Item item) {
        return this.getCooldownPercent(item, 0.0f) > 0.0f;
    }

    public float getCooldownPercent(Item item, float f) {
        CooldownInstance cooldownInstance = this.cooldowns.get(item);
        if (cooldownInstance != null) {
            float f2 = cooldownInstance.endTime - cooldownInstance.startTime;
            float f3 = (float)cooldownInstance.endTime - ((float)this.tickCount + f);
            return Mth.clamp(f3 / f2, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<Item, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Item, CooldownInstance> entry = iterator.next();
                if (entry.getValue().endTime > this.tickCount) continue;
                iterator.remove();
                this.onCooldownEnded(entry.getKey());
            }
        }
    }

    public void addCooldown(Item item, int n) {
        this.cooldowns.put(item, new CooldownInstance(this.tickCount, this.tickCount + n));
        this.onCooldownStarted(item, n);
    }

    public void removeCooldown(Item item) {
        this.cooldowns.remove(item);
        this.onCooldownEnded(item);
    }

    protected void onCooldownStarted(Item item, int n) {
    }

    protected void onCooldownEnded(Item item) {
    }

    class CooldownInstance {
        private final int startTime;
        private final int endTime;

        private CooldownInstance(int n, int n2) {
            this.startTime = n;
            this.endTime = n2;
        }
    }

}

