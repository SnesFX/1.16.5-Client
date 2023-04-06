/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (commandContext, suggestionsBuilder) -> {
        Collection<Advancement> collection = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements();
        return SharedSuggestionProvider.suggestResource(collection.stream().map(Advancement::getId), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommands.performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.GRANT, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements())))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommands.performCriterion((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, AdvancementCommands.getAdvancements(ResourceLocationArgument.getAdvancement((CommandContext<CommandSourceStack>)commandContext, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(commandContext -> AdvancementCommands.perform((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Action.REVOKE, ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAllAdvancements()))))));
    }

    private static int perform(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Action action, Collection<Advancement> collection2) {
        int n = 0;
        for (ServerPlayer serverPlayer : collection) {
            n += action.perform(serverPlayer, collection2);
        }
        if (n == 0) {
            if (collection2.size() == 1) {
                if (collection.size() == 1) {
                    throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".one.to.one.failure", collection2.iterator().next().getChatComponent(), collection.iterator().next().getDisplayName()));
                }
                throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".one.to.many.failure", collection2.iterator().next().getChatComponent(), collection.size()));
            }
            if (collection.size() == 1) {
                throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".many.to.one.failure", collection2.size(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".many.to.many.failure", collection2.size(), collection.size()));
        }
        if (collection2.size() == 1) {
            if (collection.size() == 1) {
                commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".one.to.one.success", collection2.iterator().next().getChatComponent(), collection.iterator().next().getDisplayName()), true);
            } else {
                commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".one.to.many.success", collection2.iterator().next().getChatComponent(), collection.size()), true);
            }
        } else if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".many.to.one.success", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".many.to.many.success", collection2.size(), collection.size()), true);
        }
        return n;
    }

    private static int performCriterion(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Action action, Advancement advancement, String string) {
        int n = 0;
        if (!advancement.getCriteria().containsKey(string)) {
            throw new CommandRuntimeException(new TranslatableComponent("commands.advancement.criterionNotFound", advancement.getChatComponent(), string));
        }
        for (ServerPlayer serverPlayer : collection) {
            if (!action.performCriterion(serverPlayer, advancement, string)) continue;
            ++n;
        }
        if (n == 0) {
            if (collection.size() == 1) {
                throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".criterion.to.one.failure", string, advancement.getChatComponent(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandRuntimeException(new TranslatableComponent(action.getKey() + ".criterion.to.many.failure", string, advancement.getChatComponent(), collection.size()));
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".criterion.to.one.success", string, advancement.getChatComponent(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent(action.getKey() + ".criterion.to.many.success", string, advancement.getChatComponent(), collection.size()), true);
        }
        return n;
    }

    private static List<Advancement> getAdvancements(Advancement advancement, Mode mode) {
        ArrayList arrayList = Lists.newArrayList();
        if (mode.parents) {
            for (Advancement advancement2 = advancement.getParent(); advancement2 != null; advancement2 = advancement2.getParent()) {
                arrayList.add(advancement2);
            }
        }
        arrayList.add(advancement);
        if (mode.children) {
            AdvancementCommands.addChildren(advancement, arrayList);
        }
        return arrayList;
    }

    private static void addChildren(Advancement advancement, List<Advancement> list) {
        for (Advancement advancement2 : advancement.getChildren()) {
            list.add(advancement2);
            AdvancementCommands.addChildren(advancement2, list);
        }
    }

    static enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);
        
        private final boolean parents;
        private final boolean children;

        private Mode(boolean bl, boolean bl2) {
            this.parents = bl;
            this.children = bl2;
        }
    }

    static enum Action {
        GRANT("grant"){

            @Override
            protected boolean perform(ServerPlayer serverPlayer, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (advancementProgress.isDone()) {
                    return false;
                }
                for (String string : advancementProgress.getRemainingCriteria()) {
                    serverPlayer.getAdvancements().award(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer serverPlayer, Advancement advancement, String string) {
                return serverPlayer.getAdvancements().award(advancement, string);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean perform(ServerPlayer serverPlayer, Advancement advancement) {
                AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                if (!advancementProgress.hasProgress()) {
                    return false;
                }
                for (String string : advancementProgress.getCompletedCriteria()) {
                    serverPlayer.getAdvancements().revoke(advancement, string);
                }
                return true;
            }

            @Override
            protected boolean performCriterion(ServerPlayer serverPlayer, Advancement advancement, String string) {
                return serverPlayer.getAdvancements().revoke(advancement, string);
            }
        };
        
        private final String key;

        private Action(String string2) {
            this.key = "commands.advancement." + string2;
        }

        public int perform(ServerPlayer serverPlayer, Iterable<Advancement> iterable) {
            int n = 0;
            for (Advancement advancement : iterable) {
                if (!this.perform(serverPlayer, advancement)) continue;
                ++n;
            }
            return n;
        }

        protected abstract boolean perform(ServerPlayer var1, Advancement var2);

        protected abstract boolean performCriterion(ServerPlayer var1, Advancement var2, String var3);

        protected String getKey() {
            return this.key;
        }

    }

}

