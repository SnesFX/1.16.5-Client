/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.scores;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData
extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    private Scoreboard scoreboard;
    private CompoundTag delayLoad;

    public ScoreboardSaveData() {
        super("scoreboard");
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        if (this.delayLoad != null) {
            this.load(this.delayLoad);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            this.delayLoad = compoundTag;
            return;
        }
        this.loadObjectives(compoundTag.getList("Objectives", 10));
        this.scoreboard.loadPlayerScores(compoundTag.getList("PlayerScores", 10));
        if (compoundTag.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(compoundTag.getCompound("DisplaySlots"));
        }
        if (compoundTag.contains("Teams", 9)) {
            this.loadTeams(compoundTag.getList("Teams", 10));
        }
    }

    protected void loadTeams(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            Object object;
            CompoundTag compoundTag = listTag.getCompound(i);
            String string = compoundTag.getString("Name");
            if (string.length() > 16) {
                string = string.substring(0, 16);
            }
            PlayerTeam playerTeam = this.scoreboard.addPlayerTeam(string);
            MutableComponent mutableComponent = Component.Serializer.fromJson(compoundTag.getString("DisplayName"));
            if (mutableComponent != null) {
                playerTeam.setDisplayName(mutableComponent);
            }
            if (compoundTag.contains("TeamColor", 8)) {
                playerTeam.setColor(ChatFormatting.getByName(compoundTag.getString("TeamColor")));
            }
            if (compoundTag.contains("AllowFriendlyFire", 99)) {
                playerTeam.setAllowFriendlyFire(compoundTag.getBoolean("AllowFriendlyFire"));
            }
            if (compoundTag.contains("SeeFriendlyInvisibles", 99)) {
                playerTeam.setSeeFriendlyInvisibles(compoundTag.getBoolean("SeeFriendlyInvisibles"));
            }
            if (compoundTag.contains("MemberNamePrefix", 8) && (object = Component.Serializer.fromJson(compoundTag.getString("MemberNamePrefix"))) != null) {
                playerTeam.setPlayerPrefix((Component)object);
            }
            if (compoundTag.contains("MemberNameSuffix", 8) && (object = Component.Serializer.fromJson(compoundTag.getString("MemberNameSuffix"))) != null) {
                playerTeam.setPlayerSuffix((Component)object);
            }
            if (compoundTag.contains("NameTagVisibility", 8) && (object = Team.Visibility.byName(compoundTag.getString("NameTagVisibility"))) != null) {
                playerTeam.setNameTagVisibility((Team.Visibility)((Object)object));
            }
            if (compoundTag.contains("DeathMessageVisibility", 8) && (object = Team.Visibility.byName(compoundTag.getString("DeathMessageVisibility"))) != null) {
                playerTeam.setDeathMessageVisibility((Team.Visibility)((Object)object));
            }
            if (compoundTag.contains("CollisionRule", 8) && (object = Team.CollisionRule.byName(compoundTag.getString("CollisionRule"))) != null) {
                playerTeam.setCollisionRule((Team.CollisionRule)((Object)object));
            }
            this.loadTeamPlayers(playerTeam, compoundTag.getList("Players", 8));
        }
    }

    protected void loadTeamPlayers(PlayerTeam playerTeam, ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            this.scoreboard.addPlayerToTeam(listTag.getString(i), playerTeam);
        }
    }

    protected void loadDisplaySlots(CompoundTag compoundTag) {
        for (int i = 0; i < 19; ++i) {
            if (!compoundTag.contains("slot_" + i, 8)) continue;
            String string = compoundTag.getString("slot_" + i);
            Objective objective = this.scoreboard.getObjective(string);
            this.scoreboard.setDisplayObjective(i, objective);
        }
    }

    protected void loadObjectives(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            ObjectiveCriteria.byName(compoundTag.getString("CriteriaName")).ifPresent(objectiveCriteria -> {
                String string = compoundTag.getString("Name");
                if (string.length() > 16) {
                    string = string.substring(0, 16);
                }
                MutableComponent mutableComponent = Component.Serializer.fromJson(compoundTag.getString("DisplayName"));
                ObjectiveCriteria.RenderType renderType = ObjectiveCriteria.RenderType.byId(compoundTag.getString("RenderType"));
                this.scoreboard.addObjective(string, (ObjectiveCriteria)objectiveCriteria, mutableComponent, renderType);
            });
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
            return compoundTag;
        }
        compoundTag.put("Objectives", this.saveObjectives());
        compoundTag.put("PlayerScores", this.scoreboard.savePlayerScores());
        compoundTag.put("Teams", this.saveTeams());
        this.saveDisplaySlots(compoundTag);
        return compoundTag;
    }

    protected ListTag saveTeams() {
        ListTag listTag = new ListTag();
        Collection<PlayerTeam> collection = this.scoreboard.getPlayerTeams();
        for (PlayerTeam playerTeam : collection) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", playerTeam.getName());
            compoundTag.putString("DisplayName", Component.Serializer.toJson(playerTeam.getDisplayName()));
            if (playerTeam.getColor().getId() >= 0) {
                compoundTag.putString("TeamColor", playerTeam.getColor().getName());
            }
            compoundTag.putBoolean("AllowFriendlyFire", playerTeam.isAllowFriendlyFire());
            compoundTag.putBoolean("SeeFriendlyInvisibles", playerTeam.canSeeFriendlyInvisibles());
            compoundTag.putString("MemberNamePrefix", Component.Serializer.toJson(playerTeam.getPlayerPrefix()));
            compoundTag.putString("MemberNameSuffix", Component.Serializer.toJson(playerTeam.getPlayerSuffix()));
            compoundTag.putString("NameTagVisibility", playerTeam.getNameTagVisibility().name);
            compoundTag.putString("DeathMessageVisibility", playerTeam.getDeathMessageVisibility().name);
            compoundTag.putString("CollisionRule", playerTeam.getCollisionRule().name);
            ListTag listTag2 = new ListTag();
            for (String string : playerTeam.getPlayers()) {
                listTag2.add(StringTag.valueOf(string));
            }
            compoundTag.put("Players", listTag2);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    protected void saveDisplaySlots(CompoundTag compoundTag) {
        CompoundTag compoundTag2 = new CompoundTag();
        boolean bl = false;
        for (int i = 0; i < 19; ++i) {
            Objective objective = this.scoreboard.getDisplayObjective(i);
            if (objective == null) continue;
            compoundTag2.putString("slot_" + i, objective.getName());
            bl = true;
        }
        if (bl) {
            compoundTag.put("DisplaySlots", compoundTag2);
        }
    }

    protected ListTag saveObjectives() {
        ListTag listTag = new ListTag();
        Collection<Objective> collection = this.scoreboard.getObjectives();
        for (Objective objective : collection) {
            if (objective.getCriteria() == null) continue;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", objective.getName());
            compoundTag.putString("CriteriaName", objective.getCriteria().getName());
            compoundTag.putString("DisplayName", Component.Serializer.toJson(objective.getDisplayName()));
            compoundTag.putString("RenderType", objective.getRenderType().getId());
            listTag.add(compoundTag);
        }
        return listTag;
    }
}

