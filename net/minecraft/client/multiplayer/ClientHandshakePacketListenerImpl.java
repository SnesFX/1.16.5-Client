/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.InsufficientPrivilegesException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandshakePacketListenerImpl
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft minecraft;
    @Nullable
    private final Screen parent;
    private final Consumer<Component> updateStatus;
    private final Connection connection;
    private GameProfile localGameProfile;

    public ClientHandshakePacketListenerImpl(Connection connection, Minecraft minecraft, @Nullable Screen screen, Consumer<Component> consumer) {
        this.connection = connection;
        this.minecraft = minecraft;
        this.parent = screen;
        this.updateStatus = consumer;
    }

    @Override
    public void handleHello(ClientboundHelloPacket clientboundHelloPacket) {
        Cipher cipher;
        Cipher cipher2;
        String string;
        ServerboundKeyPacket serverboundKeyPacket;
        try {
            SecretKey secretKey = Crypt.generateSecretKey();
            PublicKey publicKey = clientboundHelloPacket.getPublicKey();
            string = new BigInteger(Crypt.digestData(clientboundHelloPacket.getServerId(), publicKey, secretKey)).toString(16);
            cipher = Crypt.getCipher(2, secretKey);
            cipher2 = Crypt.getCipher(1, secretKey);
            serverboundKeyPacket = new ServerboundKeyPacket(secretKey, publicKey, clientboundHelloPacket.getNonce());
        }
        catch (CryptException cryptException) {
            throw new IllegalStateException("Protocol error", cryptException);
        }
        this.updateStatus.accept(new TranslatableComponent("connect.authorizing"));
        HttpUtil.DOWNLOAD_EXECUTOR.submit(() -> {
            Component component = this.authenticateServer(string);
            if (component != null) {
                if (this.minecraft.getCurrentServer() != null && this.minecraft.getCurrentServer().isLan()) {
                    LOGGER.warn(component.getString());
                } else {
                    this.connection.disconnect(component);
                    return;
                }
            }
            this.updateStatus.accept(new TranslatableComponent("connect.encrypting"));
            this.connection.send(serverboundKeyPacket, (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.connection.setEncryptionKey(cipher, cipher2)));
        });
    }

    @Nullable
    private Component authenticateServer(String string) {
        try {
            this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), string);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new TranslatableComponent("disconnect.loginFailedInfo.serversUnavailable"));
        }
        catch (InvalidCredentialsException invalidCredentialsException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new TranslatableComponent("disconnect.loginFailedInfo.invalidSession"));
        }
        catch (InsufficientPrivilegesException insufficientPrivilegesException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", new TranslatableComponent("disconnect.loginFailedInfo.insufficientPrivileges"));
        }
        catch (AuthenticationException authenticationException) {
            return new TranslatableComponent("disconnect.loginFailedInfo", authenticationException.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getMinecraftSessionService() {
        return this.minecraft.getMinecraftSessionService();
    }

    @Override
    public void handleGameProfile(ClientboundGameProfilePacket clientboundGameProfilePacket) {
        this.updateStatus.accept(new TranslatableComponent("connect.joining"));
        this.localGameProfile = clientboundGameProfilePacket.getGameProfile();
        this.connection.setProtocol(ConnectionProtocol.PLAY);
        this.connection.setListener(new ClientPacketListener(this.minecraft, this.parent, this.connection, this.localGameProfile));
    }

    @Override
    public void onDisconnect(Component component) {
        if (this.parent != null && this.parent instanceof RealmsScreen) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, CommonComponents.CONNECT_FAILED, component));
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, CommonComponents.CONNECT_FAILED, component));
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void handleDisconnect(ClientboundLoginDisconnectPacket clientboundLoginDisconnectPacket) {
        this.connection.disconnect(clientboundLoginDisconnectPacket.getReason());
    }

    @Override
    public void handleCompression(ClientboundLoginCompressionPacket clientboundLoginCompressionPacket) {
        if (!this.connection.isMemoryConnection()) {
            this.connection.setupCompression(clientboundLoginCompressionPacket.getCompressionThreshold());
        }
    }

    @Override
    public void handleCustomQuery(ClientboundCustomQueryPacket clientboundCustomQueryPacket) {
        this.updateStatus.accept(new TranslatableComponent("connect.negotiating"));
        this.connection.send(new ServerboundCustomQueryPacket(clientboundCustomQueryPacket.getTransactionId(), null));
    }
}

