/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType$Function
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.scores.Team;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.teams", object, object2, object3, object4));
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType((object, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.entities", object, object2, object3, object4));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg((float)0.0f)).then(((RequiredArgumentBuilder)Commands.argument("maxRange", FloatArgumentType.floatArg((float)1.0f)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes(commandContext -> SpreadPlayersCommand.spreadPlayers((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)commandContext, "center"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"maxRange"), 256, BoolArgumentType.getBool((CommandContext)commandContext, (String)"respectTeams"), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))))).then(Commands.literal("under").then(Commands.argument("maxHeight", IntegerArgumentType.integer((int)0)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes(commandContext -> SpreadPlayersCommand.spreadPlayers((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)commandContext, "center"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"spreadDistance"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"maxRange"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"maxHeight"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"respectTeams"), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))))))))));
    }

    private static int spreadPlayers(CommandSourceStack commandSourceStack, Vec2 vec2, float f, float f2, int n, boolean bl, Collection<? extends Entity> collection) throws CommandSyntaxException {
        Random random = new Random();
        double d = vec2.x - f2;
        double d2 = vec2.y - f2;
        double d3 = vec2.x + f2;
        double d4 = vec2.y + f2;
        Position[] arrposition = SpreadPlayersCommand.createInitialPositions(random, bl ? SpreadPlayersCommand.getNumberOfTeams(collection) : collection.size(), d, d2, d3, d4);
        SpreadPlayersCommand.spreadPositions(vec2, f, commandSourceStack.getLevel(), random, d, d2, d3, d4, n, arrposition, bl);
        double d5 = SpreadPlayersCommand.setPlayerPositions(collection, commandSourceStack.getLevel(), arrposition, n, bl);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.spreadplayers.success." + (bl ? "teams" : "entities"), arrposition.length, Float.valueOf(vec2.x), Float.valueOf(vec2.y), String.format(Locale.ROOT, "%.2f", d5)), true);
        return arrposition.length;
    }

    private static int getNumberOfTeams(Collection<? extends Entity> collection) {
        HashSet hashSet = Sets.newHashSet();
        for (Entity entity : collection) {
            if (entity instanceof Player) {
                hashSet.add(entity.getTeam());
                continue;
            }
            hashSet.add(null);
        }
        return hashSet.size();
    }

    private static void spreadPositions(Vec2 vec2, double d, ServerLevel serverLevel, Random random, double d2, double d3, double d4, double d5, int n, Position[] arrposition, boolean bl) throws CommandSyntaxException {
        int n2;
        boolean bl2 = true;
        double d6 = 3.4028234663852886E38;
        for (n2 = 0; n2 < 10000 && bl2; ++n2) {
            bl2 = false;
            d6 = 3.4028234663852886E38;
            for (int i = 0; i < arrposition.length; ++i) {
                Object object = arrposition[i];
                int n3 = 0;
                Position position = new Position();
                for (int j = 0; j < arrposition.length; ++j) {
                    if (i == j) continue;
                    Position position2 = arrposition[j];
                    double d7 = object.dist(position2);
                    d6 = Math.min(d7, d6);
                    if (!(d7 < d)) continue;
                    ++n3;
                    position.x = position.x + (position2.x - object.x);
                    position.z = position.z + (position2.z - object.z);
                }
                if (n3 > 0) {
                    position.x = position.x / (double)n3;
                    position.z = position.z / (double)n3;
                    double d8 = position.getLength();
                    if (d8 > 0.0) {
                        position.normalize();
                        object.moveAway(position);
                    } else {
                        object.randomize(random, d2, d3, d4, d5);
                    }
                    bl2 = true;
                }
                if (!object.clamp(d2, d3, d4, d5)) continue;
                bl2 = true;
            }
            if (bl2) continue;
            for (Position position : arrposition) {
                if (position.isSafe(serverLevel, n)) continue;
                position.randomize(random, d2, d3, d4, d5);
                bl2 = true;
            }
        }
        if (d6 == 3.4028234663852886E38) {
            d6 = 0.0;
        }
        if (n2 >= 10000) {
            if (bl) {
                throw ERROR_FAILED_TO_SPREAD_TEAMS.create((Object)arrposition.length, (Object)Float.valueOf(vec2.x), (Object)Float.valueOf(vec2.y), (Object)String.format(Locale.ROOT, "%.2f", d6));
            }
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create((Object)arrposition.length, (Object)Float.valueOf(vec2.x), (Object)Float.valueOf(vec2.y), (Object)String.format(Locale.ROOT, "%.2f", d6));
        }
    }

    private static double setPlayerPositions(Collection<? extends Entity> collection, ServerLevel serverLevel, Position[] arrposition, int n, boolean bl) {
        double d = 0.0;
        int n2 = 0;
        HashMap hashMap = Maps.newHashMap();
        for (Entity entity : collection) {
            Position position;
            if (bl) {
                Team team;
                Team team2 = team = entity instanceof Player ? entity.getTeam() : null;
                if (!hashMap.containsKey(team)) {
                    hashMap.put(team, arrposition[n2++]);
                }
                position = (Position)hashMap.get(team);
            } else {
                position = arrposition[n2++];
            }
            entity.teleportToWithTicket((double)Mth.floor(position.x) + 0.5, position.getSpawnY(serverLevel, n), (double)Mth.floor(position.z) + 0.5);
            double d2 = Double.MAX_VALUE;
            for (Position position2 : arrposition) {
                if (position == position2) continue;
                double d3 = position.dist(position2);
                d2 = Math.min(d3, d2);
            }
            d += d2;
        }
        if (collection.size() < 2) {
            return 0.0;
        }
        return d /= (double)collection.size();
    }

    private static Position[] createInitialPositions(Random random, int n, double d, double d2, double d3, double d4) {
        Position[] arrposition = new Position[n];
        for (int i = 0; i < arrposition.length; ++i) {
            Position position = new Position();
            position.randomize(random, d, d2, d3, d4);
            arrposition[i] = position;
        }
        return arrposition;
    }

    static class Position {
        private double x;
        private double z;

        Position() {
        }

        double dist(Position position) {
            double d = this.x - position.x;
            double d2 = this.z - position.z;
            return Math.sqrt(d * d + d2 * d2);
        }

        void normalize() {
            double d = this.getLength();
            this.x /= d;
            this.z /= d;
        }

        float getLength() {
            return Mth.sqrt(this.x * this.x + this.z * this.z);
        }

        public void moveAway(Position position) {
            this.x -= position.x;
            this.z -= position.z;
        }

        public boolean clamp(double d, double d2, double d3, double d4) {
            boolean bl = false;
            if (this.x < d) {
                this.x = d;
                bl = true;
            } else if (this.x > d3) {
                this.x = d3;
                bl = true;
            }
            if (this.z < d2) {
                this.z = d2;
                bl = true;
            } else if (this.z > d4) {
                this.z = d4;
                bl = true;
            }
            return bl;
        }

        public int getSpawnY(BlockGetter blockGetter, int n) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(this.x, (double)(n + 1), this.z);
            boolean bl = blockGetter.getBlockState(mutableBlockPos).isAir();
            mutableBlockPos.move(Direction.DOWN);
            boolean bl2 = blockGetter.getBlockState(mutableBlockPos).isAir();
            while (mutableBlockPos.getY() > 0) {
                mutableBlockPos.move(Direction.DOWN);
                boolean bl3 = blockGetter.getBlockState(mutableBlockPos).isAir();
                if (!bl3 && bl2 && bl) {
                    return mutableBlockPos.getY() + 1;
                }
                bl = bl2;
                bl2 = bl3;
            }
            return n + 1;
        }

        public boolean isSafe(BlockGetter blockGetter, int n) {
            BlockPos blockPos = new BlockPos(this.x, (double)(this.getSpawnY(blockGetter, n) - 1), this.z);
            BlockState blockState = blockGetter.getBlockState(blockPos);
            Material material = blockState.getMaterial();
            return blockPos.getY() < n && !material.isLiquid() && material != Material.FIRE;
        }

        public void randomize(Random random, double d, double d2, double d3, double d4) {
            this.x = Mth.nextDouble(random, d, d3);
            this.z = Mth.nextDouble(random, d2, d4);
        }
    }

}

