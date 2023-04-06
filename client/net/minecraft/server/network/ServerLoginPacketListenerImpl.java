/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLoginPacketListenerImpl
implements ServerLoginPacketListener {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private final byte[] nonce = new byte[4];
    private final MinecraftServer server;
    public final Connection connection;
    private State state = State.HELLO;
    private int tick;
    private GameProfile gameProfile;
    private final String serverId = "";
    private SecretKey secretKey;
    private ServerPlayer delayedAcceptPlayer;

    public ServerLoginPacketListenerImpl(MinecraftServer minecraftServer, Connection connection) {
        this.server = minecraftServer;
        this.connection = connection;
        RANDOM.nextBytes(this.nonce);
    }

    public void tick() {
        ServerPlayer serverPlayer;
        if (this.state == State.READY_TO_ACCEPT) {
            this.handleAcceptedLogin();
        } else if (this.state == State.DELAY_ACCEPT && (serverPlayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId())) == null) {
            this.state = State.READY_TO_ACCEPT;
            this.server.getPlayerList().placeNewPlayer(this.connection, this.delayedAcceptPlayer);
            this.delayedAcceptPlayer = null;
        }
        if (this.tick++ == 600) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    public void disconnect(Component component) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getUserName(), (Object)component.getString());
            this.connection.send(new ClientboundLoginDisconnectPacket(component));
            this.connection.disconnect(component);
        }
        catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
        }
    }

    public void handleAcceptedLogin() {
        Component component;
        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.createFakeProfile(this.gameProfile);
        }
        if ((component = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile)) != null) {
            this.disconnect(component);
        } else {
            this.state = State.ACCEPTED;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), (GenericFutureListener<? extends Future<? super Void>>)((ChannelFutureListener)channelFuture -> this.connection.setupCompression(this.server.getCompressionThreshold())));
            }
            this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
            ServerPlayer serverPlayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
            if (serverPlayer != null) {
                this.state = State.DELAY_ACCEPT;
                this.delayedAcceptPlayer = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
            } else {
                this.server.getPlayerList().placeNewPlayer(this.connection, this.server.getPlayerList().getPlayerForLogin(this.gameProfile));
            }
        }
    }

    @Override
    public void onDisconnect(Component component) {
        LOGGER.info("{} lost connection: {}", (Object)this.getUserName(), (Object)component.getString());
    }

    public String getUserName() {
        if (this.gameProfile != null) {
            return (Object)this.gameProfile + " (" + this.connection.getRemoteAddress() + ")";
        }
        return String.valueOf(this.connection.getRemoteAddress());
    }

    @Override
    public void handleHello(ServerboundHelloPacket serverboundHelloPacket) {
        Validate.validState((boolean)(this.state == State.HELLO), (String)"Unexpected hello packet", (Object[])new Object[0]);
        this.gameProfile = serverboundHelloPacket.getGameProfile();
        if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
        } else {
            this.state = State.READY_TO_ACCEPT;
        }
    }

    @Override
    public void handleKey(ServerboundKeyPacket serverboundKeyPacket) {
        String string;
        Object object;
        Validate.validState((boolean)(this.state == State.KEY), (String)"Unexpected key packet", (Object[])new Object[0]);
        PrivateKey privateKey = this.server.getKeyPair().getPrivate();
        try {
            if (!Arrays.equals(this.nonce, serverboundKeyPacket.getNonce(privateKey))) {
                throw new IllegalStateException("Protocol error");
            }
            this.secretKey = serverboundKeyPacket.getSecretKey(privateKey);
            object = Crypt.getCipher(2, this.secretKey);
            Cipher cipher = Crypt.getCipher(1, this.secretKey);
            string = new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), this.secretKey)).toString(16);
            this.state = State.AUTHENTICATING;
            this.connection.setEncryptionKey((Cipher)object, cipher);
        }
        catch (CryptException cryptException) {
            throw new IllegalStateException("Protocol error", cryptException);
        }
        object = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()){

            @Override
            public void run() {
                GameProfile gameProfile = ServerLoginPacketListenerImpl.this.gameProfile;
                try {
                    ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.getAddress());
                    if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                        LOGGER.info("UUID of player {} is {}", (Object)ServerLoginPacketListenerImpl.this.gameProfile.getName(), (Object)ServerLoginPacketListenerImpl.this.gameProfile.getId());
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(gameProfile);
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    } else {
                        ServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameProfile.getName());
                    }
                }
                catch (AuthenticationUnavailableException authenticationUnavailableException) {
                    if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(gameProfile);
                        ServerLoginPacketListenerImpl.this.state = State.READY_TO_ACCEPT;
                    }
                    ServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketAddress = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
                return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
            }
        };
        ((Thread)object).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        ((Thread)object).start();
    }

    @Override
    public void handleCustomQueryPacket(ServerboundCustomQueryPacket serverboundCustomQueryPacket) {
        this.disconnect(new TranslatableComponent("multiplayer.disconnect.unexpected_query_response"));
    }

    protected GameProfile createFakeProfile(GameProfile gameProfile) {
        UUID uUID = Player.createPlayerUUID(gameProfile.getName());
        return new GameProfile(uUID, gameProfile.getName());
    }

    static enum State {
        HELLO,
        KEY,
        AUTHENTICATING,
        NEGOTIATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;
        
    }

}

