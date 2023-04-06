/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity
extends BlockEntity {
    private final Component[] messages = new Component[]{TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY};
    private boolean isEditable = true;
    private Player playerWhoMayEdit;
    private final FormattedCharSequence[] renderMessages = new FormattedCharSequence[4];
    private DyeColor color = DyeColor.BLACK;

    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        for (int i = 0; i < 4; ++i) {
            String string = Component.Serializer.toJson(this.messages[i]);
            compoundTag.putString("Text" + (i + 1), string);
        }
        compoundTag.putString("Color", this.color.getName());
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        this.isEditable = false;
        super.load(blockState, compoundTag);
        this.color = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);
        for (int i = 0; i < 4; ++i) {
            String string = compoundTag.getString("Text" + (i + 1));
            MutableComponent mutableComponent = Component.Serializer.fromJson(string.isEmpty() ? "\"\"" : string);
            if (this.level instanceof ServerLevel) {
                try {
                    this.messages[i] = ComponentUtils.updateForEntity(this.createCommandSourceStack(null), mutableComponent, null, 0);
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    this.messages[i] = mutableComponent;
                }
            } else {
                this.messages[i] = mutableComponent;
            }
            this.renderMessages[i] = null;
        }
    }

    public Component getMessage(int n) {
        return this.messages[n];
    }

    public void setMessage(int n, Component component) {
        this.messages[n] = component;
        this.renderMessages[n] = null;
    }

    @Nullable
    public FormattedCharSequence getRenderMessage(int n, Function<Component, FormattedCharSequence> function) {
        if (this.renderMessages[n] == null && this.messages[n] != null) {
            this.renderMessages[n] = function.apply(this.messages[n]);
        }
        return this.renderMessages[n];
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 9, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean bl) {
        this.isEditable = bl;
        if (!bl) {
            this.playerWhoMayEdit = null;
        }
    }

    public void setAllowedPlayerEditor(Player player) {
        this.playerWhoMayEdit = player;
    }

    public Player getPlayerWhoMayEdit() {
        return this.playerWhoMayEdit;
    }

    public boolean executeClickCommands(Player player) {
        for (Component component : this.messages) {
            ClickEvent clickEvent;
            Style style;
            Style style2 = style = component == null ? null : component.getStyle();
            if (style == null || style.getClickEvent() == null || (clickEvent = style.getClickEvent()).getAction() != ClickEvent.Action.RUN_COMMAND) continue;
            player.getServer().getCommands().performCommand(this.createCommandSourceStack((ServerPlayer)player), clickEvent.getValue());
        }
        return true;
    }

    public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer serverPlayer) {
        String string = serverPlayer == null ? "Sign" : serverPlayer.getName().getString();
        Component component = serverPlayer == null ? new TextComponent("Sign") : serverPlayer.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel)this.level, 2, string, component, this.level.getServer(), serverPlayer);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public boolean setColor(DyeColor dyeColor) {
        if (dyeColor != this.getColor()) {
            this.color = dyeColor;
            this.setChanged();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            return true;
        }
        return false;
    }
}

