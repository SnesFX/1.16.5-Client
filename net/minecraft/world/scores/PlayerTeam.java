/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class PlayerTeam
extends Team {
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> players = Sets.newHashSet();
    private Component displayName;
    private Component playerPrefix = TextComponent.EMPTY;
    private Component playerSuffix = TextComponent.EMPTY;
    private boolean allowFriendlyFire = true;
    private boolean seeFriendlyInvisibles = true;
    private Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
    private Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
    private ChatFormatting color = ChatFormatting.RESET;
    private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;
    private final Style displayNameStyle;

    public PlayerTeam(Scoreboard scoreboard, String string) {
        this.scoreboard = scoreboard;
        this.name = string;
        this.displayName = new TextComponent(string);
        this.displayNameStyle = Style.EMPTY.withInsertion(string).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(string)));
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public MutableComponent getFormattedDisplayName() {
        MutableComponent mutableComponent = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
        ChatFormatting chatFormatting = this.getColor();
        if (chatFormatting != ChatFormatting.RESET) {
            mutableComponent.withStyle(chatFormatting);
        }
        return mutableComponent;
    }

    public void setDisplayName(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.displayName = component;
        this.scoreboard.onTeamChanged(this);
    }

    public void setPlayerPrefix(@Nullable Component component) {
        this.playerPrefix = component == null ? TextComponent.EMPTY : component;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }

    public void setPlayerSuffix(@Nullable Component component) {
        this.playerSuffix = component == null ? TextComponent.EMPTY : component;
        this.scoreboard.onTeamChanged(this);
    }

    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }

    @Override
    public Collection<String> getPlayers() {
        return this.players;
    }

    @Override
    public MutableComponent getFormattedName(Component component) {
        MutableComponent mutableComponent = new TextComponent("").append(this.playerPrefix).append(component).append(this.playerSuffix);
        ChatFormatting chatFormatting = this.getColor();
        if (chatFormatting != ChatFormatting.RESET) {
            mutableComponent.withStyle(chatFormatting);
        }
        return mutableComponent;
    }

    public static MutableComponent formatNameForTeam(@Nullable Team team, Component component) {
        if (team == null) {
            return component.copy();
        }
        return team.getFormattedName(component);
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean bl) {
        this.allowFriendlyFire = bl;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisibles(boolean bl) {
        this.seeFriendlyInvisibles = bl;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public Team.Visibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public Team.Visibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Team.Visibility visibility) {
        this.nameTagVisibility = visibility;
        this.scoreboard.onTeamChanged(this);
    }

    public void setDeathMessageVisibility(Team.Visibility visibility) {
        this.deathMessageVisibility = visibility;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public Team.CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    public void setCollisionRule(Team.CollisionRule collisionRule) {
        this.collisionRule = collisionRule;
        this.scoreboard.onTeamChanged(this);
    }

    public int packOptions() {
        int n = 0;
        if (this.isAllowFriendlyFire()) {
            n |= true;
        }
        if (this.canSeeFriendlyInvisibles()) {
            n |= 2;
        }
        return n;
    }

    public void unpackOptions(int n) {
        this.setAllowFriendlyFire((n & 1) > 0);
        this.setSeeFriendlyInvisibles((n & 2) > 0);
    }

    public void setColor(ChatFormatting chatFormatting) {
        this.color = chatFormatting;
        this.scoreboard.onTeamChanged(this);
    }

    @Override
    public ChatFormatting getColor() {
        return this.color;
    }
}

