/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.AmbiguityConsumer
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateBiomeCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReplaceItemCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher();

    public Commands(CommandSelection commandSelection) {
        AdvancementCommands.register(this.dispatcher);
        AttributeCommand.register(this.dispatcher);
        ExecuteCommand.register(this.dispatcher);
        BossBarCommands.register(this.dispatcher);
        ClearInventoryCommands.register(this.dispatcher);
        CloneCommands.register(this.dispatcher);
        DataCommands.register(this.dispatcher);
        DataPackCommand.register(this.dispatcher);
        DebugCommand.register(this.dispatcher);
        DefaultGameModeCommands.register(this.dispatcher);
        DifficultyCommand.register(this.dispatcher);
        EffectCommands.register(this.dispatcher);
        EmoteCommands.register(this.dispatcher);
        EnchantCommand.register(this.dispatcher);
        ExperienceCommand.register(this.dispatcher);
        FillCommand.register(this.dispatcher);
        ForceLoadCommand.register(this.dispatcher);
        FunctionCommand.register(this.dispatcher);
        GameModeCommand.register(this.dispatcher);
        GameRuleCommand.register(this.dispatcher);
        GiveCommand.register(this.dispatcher);
        HelpCommand.register(this.dispatcher);
        KickCommand.register(this.dispatcher);
        KillCommand.register(this.dispatcher);
        ListPlayersCommand.register(this.dispatcher);
        LocateCommand.register(this.dispatcher);
        LocateBiomeCommand.register(this.dispatcher);
        LootCommand.register(this.dispatcher);
        MsgCommand.register(this.dispatcher);
        ParticleCommand.register(this.dispatcher);
        PlaySoundCommand.register(this.dispatcher);
        ReloadCommand.register(this.dispatcher);
        RecipeCommand.register(this.dispatcher);
        ReplaceItemCommand.register(this.dispatcher);
        SayCommand.register(this.dispatcher);
        ScheduleCommand.register(this.dispatcher);
        ScoreboardCommand.register(this.dispatcher);
        SeedCommand.register(this.dispatcher, commandSelection != CommandSelection.INTEGRATED);
        SetBlockCommand.register(this.dispatcher);
        SetSpawnCommand.register(this.dispatcher);
        SetWorldSpawnCommand.register(this.dispatcher);
        SpectateCommand.register(this.dispatcher);
        SpreadPlayersCommand.register(this.dispatcher);
        StopSoundCommand.register(this.dispatcher);
        SummonCommand.register(this.dispatcher);
        TagCommand.register(this.dispatcher);
        TeamCommand.register(this.dispatcher);
        TeamMsgCommand.register(this.dispatcher);
        TeleportCommand.register(this.dispatcher);
        TellRawCommand.register(this.dispatcher);
        TimeCommand.register(this.dispatcher);
        TitleCommand.register(this.dispatcher);
        TriggerCommand.register(this.dispatcher);
        WeatherCommand.register(this.dispatcher);
        WorldBorderCommand.register(this.dispatcher);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            TestCommand.register(this.dispatcher);
        }
        if (commandSelection.includeDedicated) {
            BanIpCommands.register(this.dispatcher);
            BanListCommands.register(this.dispatcher);
            BanPlayerCommands.register(this.dispatcher);
            DeOpCommands.register(this.dispatcher);
            OpCommand.register(this.dispatcher);
            PardonCommand.register(this.dispatcher);
            PardonIpCommand.register(this.dispatcher);
            SaveAllCommand.register(this.dispatcher);
            SaveOffCommand.register(this.dispatcher);
            SaveOnCommand.register(this.dispatcher);
            SetPlayerIdleTimeoutCommand.register(this.dispatcher);
            StopCommand.register(this.dispatcher);
            WhitelistCommand.register(this.dispatcher);
        }
        if (commandSelection.includeIntegrated) {
            PublishCommand.register(this.dispatcher);
        }
        this.dispatcher.findAmbiguities((commandNode, commandNode2, commandNode3, collection) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", (Object)this.dispatcher.getPath(commandNode2), (Object)this.dispatcher.getPath(commandNode3), (Object)collection));
        this.dispatcher.setConsumer((commandContext, bl, n) -> ((CommandSourceStack)commandContext.getSource()).onCommandComplete((CommandContext<CommandSourceStack>)commandContext, bl, n));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int performCommand(CommandSourceStack commandSourceStack, String string) {
        StringReader stringReader = new StringReader(string);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        commandSourceStack.getServer().getProfiler().push(string);
        try {
            int n = this.dispatcher.execute(stringReader, (Object)commandSourceStack);
            return n;
        }
        catch (CommandRuntimeException commandRuntimeException) {
            commandSourceStack.sendFailure(commandRuntimeException.getComponent());
            int n = 0;
            return n;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            int n;
            commandSourceStack.sendFailure(ComponentUtils.fromMessage(commandSyntaxException.getRawMessage()));
            if (commandSyntaxException.getInput() != null && commandSyntaxException.getCursor() >= 0) {
                n = Math.min(commandSyntaxException.getInput().length(), commandSyntaxException.getCursor());
                MutableComponent mutableComponent = new TextComponent("").withStyle(ChatFormatting.GRAY).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string)));
                if (n > 10) {
                    mutableComponent.append("...");
                }
                mutableComponent.append(commandSyntaxException.getInput().substring(Math.max(0, n - 10), n));
                if (n < commandSyntaxException.getInput().length()) {
                    MutableComponent mutableComponent2 = new TextComponent(commandSyntaxException.getInput().substring(n)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
                    mutableComponent.append(mutableComponent2);
                }
                mutableComponent.append(new TranslatableComponent("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                commandSourceStack.sendFailure(mutableComponent);
            }
            n = 0;
            return n;
        }
        catch (Exception exception) {
            TextComponent textComponent = new TextComponent(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Command exception: {}", (Object)string, (Object)exception);
                StackTraceElement[] arrstackTraceElement = exception.getStackTrace();
                for (int i = 0; i < Math.min(arrstackTraceElement.length, 3); ++i) {
                    textComponent.append("\n\n").append(arrstackTraceElement[i].getMethodName()).append("\n ").append(arrstackTraceElement[i].getFileName()).append(":").append(String.valueOf(arrstackTraceElement[i].getLineNumber()));
                }
            }
            commandSourceStack.sendFailure(new TranslatableComponent("command.failed").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent))));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                commandSourceStack.sendFailure(new TextComponent(Util.describeError(exception)));
                LOGGER.error("'" + string + "' threw an exception", (Throwable)exception);
            }
            int n = 0;
            return n;
        }
        finally {
            commandSourceStack.getServer().getProfiler().pop();
        }
    }

    public void sendCommands(ServerPlayer serverPlayer) {
        HashMap hashMap = Maps.newHashMap();
        RootCommandNode rootCommandNode = new RootCommandNode();
        hashMap.put(this.dispatcher.getRoot(), rootCommandNode);
        this.fillUsableCommands((CommandNode<CommandSourceStack>)this.dispatcher.getRoot(), (CommandNode<SharedSuggestionProvider>)rootCommandNode, serverPlayer.createCommandSourceStack(), hashMap);
        serverPlayer.connection.send(new ClientboundCommandsPacket((RootCommandNode<SharedSuggestionProvider>)rootCommandNode));
    }

    private void fillUsableCommands(CommandNode<CommandSourceStack> commandNode, CommandNode<SharedSuggestionProvider> commandNode2, CommandSourceStack commandSourceStack, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map) {
        for (CommandNode commandNode3 : commandNode.getChildren()) {
            RequiredArgumentBuilder requiredArgumentBuilder;
            if (!commandNode3.canUse((Object)commandSourceStack)) continue;
            ArgumentBuilder argumentBuilder = commandNode3.createBuilder();
            argumentBuilder.requires(sharedSuggestionProvider -> true);
            if (argumentBuilder.getCommand() != null) {
                argumentBuilder.executes(commandContext -> 0);
            }
            if (argumentBuilder instanceof RequiredArgumentBuilder && (requiredArgumentBuilder = (RequiredArgumentBuilder)argumentBuilder).getSuggestionsProvider() != null) {
                requiredArgumentBuilder.suggests(SuggestionProviders.safelySwap((SuggestionProvider<SharedSuggestionProvider>)requiredArgumentBuilder.getSuggestionsProvider()));
            }
            if (argumentBuilder.getRedirect() != null) {
                argumentBuilder.redirect(map.get((Object)argumentBuilder.getRedirect()));
            }
            requiredArgumentBuilder = argumentBuilder.build();
            map.put((CommandNode<CommandSourceStack>)commandNode3, (CommandNode<SharedSuggestionProvider>)requiredArgumentBuilder);
            commandNode2.addChild((CommandNode)requiredArgumentBuilder);
            if (commandNode3.getChildren().isEmpty()) continue;
            this.fillUsableCommands((CommandNode<CommandSourceStack>)commandNode3, (CommandNode<SharedSuggestionProvider>)requiredArgumentBuilder, commandSourceStack, map);
        }
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String string) {
        return LiteralArgumentBuilder.literal((String)string);
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String string, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument((String)string, argumentType);
    }

    public static Predicate<String> createValidator(ParseFunction parseFunction) {
        return string -> {
            try {
                parseFunction.parse(new StringReader(string));
                return true;
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return false;
            }
        };
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.dispatcher;
    }

    @Nullable
    public static <S> CommandSyntaxException getParseException(ParseResults<S> parseResults) {
        if (!parseResults.getReader().canRead()) {
            return null;
        }
        if (parseResults.getExceptions().size() == 1) {
            return (CommandSyntaxException)((Object)parseResults.getExceptions().values().iterator().next());
        }
        if (parseResults.getContext().getRange().isEmpty()) {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
        }
        return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
    }

    public static void validate() {
        RootCommandNode rootCommandNode = new Commands(CommandSelection.ALL).getDispatcher().getRoot();
        Set<ArgumentType<?>> set = ArgumentTypes.findUsedArgumentTypes(rootCommandNode);
        Set set2 = set.stream().filter(argumentType -> !ArgumentTypes.isTypeRegistered(argumentType)).collect(Collectors.toSet());
        if (!set2.isEmpty()) {
            LOGGER.warn("Missing type registration for following arguments:\n {}", (Object)set2.stream().map(argumentType -> "\t" + (Object)argumentType).collect(Collectors.joining(",\n")));
            throw new IllegalStateException("Unregistered argument types");
        }
    }

    public static enum CommandSelection {
        ALL(true, true),
        DEDICATED(false, true),
        INTEGRATED(true, false);
        
        private final boolean includeIntegrated;
        private final boolean includeDedicated;

        private CommandSelection(boolean bl, boolean bl2) {
            this.includeIntegrated = bl;
            this.includeDedicated = bl2;
        }
    }

    @FunctionalInterface
    public static interface ParseFunction {
        public void parse(StringReader var1) throws CommandSyntaxException;
    }

}

