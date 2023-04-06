/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.context.CommandContext
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock
implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = new TextComponent("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    private Component name = DEFAULT_NAME;

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int n) {
        this.successCount = n;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? TextComponent.EMPTY : this.lastOutput;
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putString("Command", this.command);
        compoundTag.putInt("SuccessCount", this.successCount);
        compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
        compoundTag.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            compoundTag.putString("LastOutput", Component.Serializer.toJson(this.lastOutput));
        }
        compoundTag.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            compoundTag.putLong("LastExecution", this.lastExecution);
        }
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        this.command = compoundTag.getString("Command");
        this.successCount = compoundTag.getInt("SuccessCount");
        if (compoundTag.contains("CustomName", 8)) {
            this.setName(Component.Serializer.fromJson(compoundTag.getString("CustomName")));
        }
        if (compoundTag.contains("TrackOutput", 1)) {
            this.trackOutput = compoundTag.getBoolean("TrackOutput");
        }
        if (compoundTag.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Component.Serializer.fromJson(compoundTag.getString("LastOutput"));
            }
            catch (Throwable throwable) {
                this.lastOutput = new TextComponent(throwable.getMessage());
            }
        } else {
            this.lastOutput = null;
        }
        if (compoundTag.contains("UpdateLastExecution")) {
            this.updateLastExecution = compoundTag.getBoolean("UpdateLastExecution");
        }
        this.lastExecution = this.updateLastExecution && compoundTag.contains("LastExecution") ? compoundTag.getLong("LastExecution") : -1L;
    }

    public void setCommand(String string) {
        this.command = string;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level level) {
        if (level.isClientSide || level.getGameTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new TextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        MinecraftServer minecraftServer = this.getLevel().getServer();
        if (minecraftServer.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
                this.lastOutput = null;
                CommandSourceStack commandSourceStack = this.createCommandSourceStack().withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)(commandContext, bl, n) -> {
                    if (bl) {
                        ++this.successCount;
                    }
                }));
                minecraftServer.getCommands().performCommand(commandSourceStack, this.command);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Executing command block");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Command to be executed");
                crashReportCategory.setDetail("Command", this::getCommand);
                crashReportCategory.setDetail("Name", () -> this.getName().getString());
                throw new ReportedException(crashReport);
            }
        }
        this.lastExecution = this.updateLastExecution ? level.getGameTime() : -1L;
        return true;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(@Nullable Component component) {
        this.name = component != null ? component : DEFAULT_NAME;
    }

    @Override
    public void sendMessage(Component component, UUID uUID) {
        if (this.trackOutput) {
            this.lastOutput = new TextComponent("[" + TIME_FORMAT.format(new Date()) + "] ").append(component);
            this.onUpdated();
        }
    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component component) {
        this.lastOutput = component;
    }

    public void setTrackOutput(boolean bl) {
        this.trackOutput = bl;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player player) {
        if (!player.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        }
        if (player.getCommandSenderWorld().isClientSide) {
            player.openMinecartCommandBlock(this);
        }
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    @Override
    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    @Override
    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }
}

