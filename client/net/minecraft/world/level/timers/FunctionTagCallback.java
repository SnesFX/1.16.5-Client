/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.timers;

import java.util.List;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class FunctionTagCallback
implements TimerCallback<MinecraftServer> {
    private final ResourceLocation tagId;

    public FunctionTagCallback(ResourceLocation resourceLocation) {
        this.tagId = resourceLocation;
    }

    @Override
    public void handle(MinecraftServer minecraftServer, TimerQueue<MinecraftServer> timerQueue, long l) {
        ServerFunctionManager serverFunctionManager = minecraftServer.getFunctions();
        Tag<CommandFunction> tag = serverFunctionManager.getTag(this.tagId);
        for (CommandFunction commandFunction : tag.getValues()) {
            serverFunctionManager.execute(commandFunction, serverFunctionManager.getGameLoopSender());
        }
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
        public Serializer() {
            super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
        }

        @Override
        public void serialize(CompoundTag compoundTag, FunctionTagCallback functionTagCallback) {
            compoundTag.putString("Name", functionTagCallback.tagId.toString());
        }

        @Override
        public FunctionTagCallback deserialize(CompoundTag compoundTag) {
            ResourceLocation resourceLocation = new ResourceLocation(compoundTag.getString("Name"));
            return new FunctionTagCallback(resourceLocation);
        }

        @Override
        public /* synthetic */ TimerCallback deserialize(CompoundTag compoundTag) {
            return this.deserialize(compoundTag);
        }
    }

}

