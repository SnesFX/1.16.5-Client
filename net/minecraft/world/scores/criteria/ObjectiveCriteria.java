/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;

public class ObjectiveCriteria {
    public static final Map<String, ObjectiveCriteria> CRITERIA_BY_NAME = Maps.newHashMap();
    public static final ObjectiveCriteria DUMMY = new ObjectiveCriteria("dummy");
    public static final ObjectiveCriteria TRIGGER = new ObjectiveCriteria("trigger");
    public static final ObjectiveCriteria DEATH_COUNT = new ObjectiveCriteria("deathCount");
    public static final ObjectiveCriteria KILL_COUNT_PLAYERS = new ObjectiveCriteria("playerKillCount");
    public static final ObjectiveCriteria KILL_COUNT_ALL = new ObjectiveCriteria("totalKillCount");
    public static final ObjectiveCriteria HEALTH = new ObjectiveCriteria("health", true, RenderType.HEARTS);
    public static final ObjectiveCriteria FOOD = new ObjectiveCriteria("food", true, RenderType.INTEGER);
    public static final ObjectiveCriteria AIR = new ObjectiveCriteria("air", true, RenderType.INTEGER);
    public static final ObjectiveCriteria ARMOR = new ObjectiveCriteria("armor", true, RenderType.INTEGER);
    public static final ObjectiveCriteria EXPERIENCE = new ObjectiveCriteria("xp", true, RenderType.INTEGER);
    public static final ObjectiveCriteria LEVEL = new ObjectiveCriteria("level", true, RenderType.INTEGER);
    public static final ObjectiveCriteria[] TEAM_KILL = new ObjectiveCriteria[]{new ObjectiveCriteria("teamkill." + ChatFormatting.BLACK.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_BLUE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_GREEN.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_AQUA.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_RED.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_PURPLE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GOLD.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GRAY.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.DARK_GRAY.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.BLUE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.GREEN.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.AQUA.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.RED.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.LIGHT_PURPLE.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.YELLOW.getName()), new ObjectiveCriteria("teamkill." + ChatFormatting.WHITE.getName())};
    public static final ObjectiveCriteria[] KILLED_BY_TEAM = new ObjectiveCriteria[]{new ObjectiveCriteria("killedByTeam." + ChatFormatting.BLACK.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_BLUE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_GREEN.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_AQUA.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_RED.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_PURPLE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GOLD.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GRAY.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.DARK_GRAY.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.BLUE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.GREEN.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.AQUA.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.RED.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.LIGHT_PURPLE.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.YELLOW.getName()), new ObjectiveCriteria("killedByTeam." + ChatFormatting.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final RenderType renderType;

    public ObjectiveCriteria(String string) {
        this(string, false, RenderType.INTEGER);
    }

    protected ObjectiveCriteria(String string, boolean bl, RenderType renderType) {
        this.name = string;
        this.readOnly = bl;
        this.renderType = renderType;
        CRITERIA_BY_NAME.put(string, this);
    }

    public static Optional<ObjectiveCriteria> byName(String string) {
        if (CRITERIA_BY_NAME.containsKey(string)) {
            return Optional.of(CRITERIA_BY_NAME.get(string));
        }
        int n = string.indexOf(58);
        if (n < 0) {
            return Optional.empty();
        }
        return Registry.STAT_TYPE.getOptional(ResourceLocation.of(string.substring(0, n), '.')).flatMap(statType -> ObjectiveCriteria.getStat(statType, ResourceLocation.of(string.substring(n + 1), '.')));
    }

    private static <T> Optional<ObjectiveCriteria> getStat(StatType<T> statType, ResourceLocation resourceLocation) {
        return statType.getRegistry().getOptional(resourceLocation).map(statType::get);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.renderType;
    }

    public static enum RenderType {
        INTEGER("integer"),
        HEARTS("hearts");
        
        private final String id;
        private static final Map<String, RenderType> BY_ID;

        private RenderType(String string2) {
            this.id = string2;
        }

        public String getId() {
            return this.id;
        }

        public static RenderType byId(String string) {
            return BY_ID.getOrDefault(string, INTEGER);
        }

        static {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (RenderType renderType : RenderType.values()) {
                builder.put((Object)renderType.id, (Object)renderType);
            }
            BY_ID = builder.build();
        }
    }

}

