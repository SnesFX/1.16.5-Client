/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.datafixers.util.Pair
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerFunctionLibrary
implements PreparableReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PATH_PREFIX_LENGTH = "functions/".length();
    private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
    private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
    private final TagLoader<CommandFunction> tagsLoader = new TagLoader(this::getFunction, "tags/functions", "function");
    private volatile TagCollection<CommandFunction> tags = TagCollection.empty();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public Optional<CommandFunction> getFunction(ResourceLocation resourceLocation) {
        return Optional.ofNullable(this.functions.get(resourceLocation));
    }

    public Map<ResourceLocation, CommandFunction> getFunctions() {
        return this.functions;
    }

    public TagCollection<CommandFunction> getTags() {
        return this.tags;
    }

    public Tag<CommandFunction> getTag(ResourceLocation resourceLocation) {
        return this.tags.getTagOrEmpty(resourceLocation);
    }

    public ServerFunctionLibrary(int n, CommandDispatcher<CommandSourceStack> commandDispatcher) {
        this.functionCompilationLevel = n;
        this.dispatcher = commandDispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        CompletableFuture<Map<ResourceLocation, Tag.Builder>> completableFuture = this.tagsLoader.prepare(resourceManager, executor);
        CompletionStage completionStage = CompletableFuture.supplyAsync(() -> resourceManager.listResources("functions", string -> string.endsWith(".mcfunction")), executor).thenCompose(collection -> {
            HashMap hashMap = Maps.newHashMap();
            CommandSourceStack commandSourceStack = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", TextComponent.EMPTY, null, null);
            for (ResourceLocation resourceLocation : collection) {
                String string = resourceLocation.getPath();
                ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - PATH_SUFFIX_LENGTH));
                hashMap.put(resourceLocation2, CompletableFuture.supplyAsync(() -> {
                    List<String> list = ServerFunctionLibrary.readLines(resourceManager, resourceLocation);
                    return CommandFunction.fromLines(resourceLocation2, this.dispatcher, commandSourceStack, list);
                }, executor));
            }
            CompletableFuture[] arrcompletableFuture = hashMap.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(arrcompletableFuture).handle((void_, throwable) -> hashMap);
        });
        return ((CompletableFuture)((CompletableFuture)completableFuture.thenCombine(completionStage, (arg_0, arg_1) -> Pair.of(arg_0, arg_1))).thenCompose(preparationBarrier::wait)).thenAcceptAsync(pair -> {
            Map map = (Map)pair.getSecond();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach((resourceLocation, completableFuture) -> ((CompletableFuture)completableFuture.handle((commandFunction, throwable) -> {
                if (throwable != null) {
                    LOGGER.error("Failed to load function {}", resourceLocation, throwable);
                } else {
                    builder.put(resourceLocation, commandFunction);
                }
                return null;
            })).join());
            this.functions = builder.build();
            this.tags = this.tagsLoader.load((Map)pair.getFirst());
        }, executor2);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> readLines(ResourceManager resourceManager, ResourceLocation resourceLocation) {
        try {
            try (Resource resource = resourceManager.getResource(resourceLocation);){
                List list = IOUtils.readLines((InputStream)resource.getInputStream(), (Charset)StandardCharsets.UTF_8);
                return list;
            }
        }
        catch (IOException iOException) {
            throw new CompletionException(iOException);
        }
    }
}

