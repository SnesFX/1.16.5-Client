/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  io.netty.bootstrap.AbstractBootstrap
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelConfig
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatusPinger {
    private static final Splitter SPLITTER = Splitter.on((char)'\u0000').limit(6);
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Connection> connections = Collections.synchronizedList(Lists.newArrayList());

    public void pingServer(final ServerData serverData, final Runnable runnable) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parseString(serverData.ip);
        final Connection connection = Connection.connectToServer(InetAddress.getByName(serverAddress.getHost()), serverAddress.getPort(), false);
        this.connections.add(connection);
        serverData.motd = new TranslatableComponent("multiplayer.status.pinging");
        serverData.ping = -1L;
        serverData.playerList = null;
        connection.setListener(new ClientStatusPacketListener(){
            private boolean success;
            private boolean receivedPing;
            private long pingStart;

            @Override
            public void handleStatusResponse(ClientboundStatusResponsePacket clientboundStatusResponsePacket) {
                Object object;
                if (this.receivedPing) {
                    connection.disconnect(new TranslatableComponent("multiplayer.status.unrequested"));
                    return;
                }
                this.receivedPing = true;
                ServerStatus serverStatus = clientboundStatusResponsePacket.getStatus();
                serverData.motd = serverStatus.getDescription() != null ? serverStatus.getDescription() : TextComponent.EMPTY;
                if (serverStatus.getVersion() != null) {
                    serverData.version = new TextComponent(serverStatus.getVersion().getName());
                    serverData.protocol = serverStatus.getVersion().getProtocol();
                } else {
                    serverData.version = new TranslatableComponent("multiplayer.status.old");
                    serverData.protocol = 0;
                }
                if (serverStatus.getPlayers() != null) {
                    serverData.status = ServerStatusPinger.formatPlayerCount(serverStatus.getPlayers().getNumPlayers(), serverStatus.getPlayers().getMaxPlayers());
                    object = Lists.newArrayList();
                    if (ArrayUtils.isNotEmpty((Object[])serverStatus.getPlayers().getSample())) {
                        for (GameProfile gameProfile : serverStatus.getPlayers().getSample()) {
                            object.add(new TextComponent(gameProfile.getName()));
                        }
                        if (serverStatus.getPlayers().getSample().length < serverStatus.getPlayers().getNumPlayers()) {
                            object.add(new TranslatableComponent("multiplayer.status.and_more", serverStatus.getPlayers().getNumPlayers() - serverStatus.getPlayers().getSample().length));
                        }
                        serverData.playerList = object;
                    }
                } else {
                    serverData.status = new TranslatableComponent("multiplayer.status.unknown").withStyle(ChatFormatting.DARK_GRAY);
                }
                object = null;
                if (serverStatus.getFavicon() != null) {
                    GameProfile[] arrgameProfile = serverStatus.getFavicon();
                    if (arrgameProfile.startsWith("data:image/png;base64,")) {
                        object = arrgameProfile.substring("data:image/png;base64,".length());
                    } else {
                        LOGGER.error("Invalid server icon (unknown format)");
                    }
                }
                if (!Objects.equals(object, serverData.getIconB64())) {
                    serverData.setIconB64((String)object);
                    runnable.run();
                }
                this.pingStart = Util.getMillis();
                connection.send(new ServerboundPingRequestPacket(this.pingStart));
                this.success = true;
            }

            @Override
            public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {
                long l = this.pingStart;
                long l2 = Util.getMillis();
                serverData.ping = l2 - l;
                connection.disconnect(new TranslatableComponent("multiplayer.status.finished"));
            }

            @Override
            public void onDisconnect(Component component) {
                if (!this.success) {
                    LOGGER.error("Can't ping {}: {}", (Object)serverData.ip, (Object)component.getString());
                    serverData.motd = new TranslatableComponent("multiplayer.status.cannot_connect").withStyle(ChatFormatting.DARK_RED);
                    serverData.status = TextComponent.EMPTY;
                    ServerStatusPinger.this.pingLegacyServer(serverData);
                }
            }

            @Override
            public Connection getConnection() {
                return connection;
            }
        });
        try {
            connection.send(new ClientIntentionPacket(serverAddress.getHost(), serverAddress.getPort(), ConnectionProtocol.STATUS));
            connection.send(new ServerboundStatusRequestPacket());
        }
        catch (Throwable throwable) {
            LOGGER.error((Object)throwable);
        }
    }

    private void pingLegacyServer(final ServerData serverData) {
        final ServerAddress serverAddress = ServerAddress.parseString(serverData.ip);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                        super.channelActive(channelHandlerContext);
                        ByteBuf byteBuf = Unpooled.buffer();
                        try {
                            byteBuf.writeByte(254);
                            byteBuf.writeByte(1);
                            byteBuf.writeByte(250);
                            char[] arrc = "MC|PingHost".toCharArray();
                            byteBuf.writeShort(arrc.length);
                            for (char c : arrc) {
                                byteBuf.writeChar((int)c);
                            }
                            byteBuf.writeShort(7 + 2 * serverAddress.getHost().length());
                            byteBuf.writeByte(127);
                            arrc = serverAddress.getHost().toCharArray();
                            byteBuf.writeShort(arrc.length);
                            for (char c : arrc) {
                                byteBuf.writeChar((int)c);
                            }
                            byteBuf.writeInt(serverAddress.getPort());
                            channelHandlerContext.channel().writeAndFlush((Object)byteBuf).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                        }
                        finally {
                            byteBuf.release();
                        }
                    }

                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        short s = byteBuf.readUnsignedByte();
                        if (s == 255) {
                            String string = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                            String[] arrstring = (String[])Iterables.toArray((Iterable)SPLITTER.split((CharSequence)string), String.class);
                            if ("\u00a71".equals(arrstring[0])) {
                                int n = Mth.getInt(arrstring[1], 0);
                                String string2 = arrstring[2];
                                String string3 = arrstring[3];
                                int n2 = Mth.getInt(arrstring[4], -1);
                                int n3 = Mth.getInt(arrstring[5], -1);
                                serverData.protocol = -1;
                                serverData.version = new TextComponent(string2);
                                serverData.motd = new TextComponent(string3);
                                serverData.status = ServerStatusPinger.formatPlayerCount(n2, n3);
                            }
                        }
                        channelHandlerContext.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                        channelHandlerContext.close();
                    }

                    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
                        this.channelRead0(channelHandlerContext, (ByteBuf)object);
                    }
                }});
            }

        })).channel(NioSocketChannel.class)).connect(serverAddress.getHost(), serverAddress.getPort());
    }

    private static Component formatPlayerCount(int n, int n2) {
        return new TextComponent(Integer.toString(n)).append(new TextComponent("/").withStyle(ChatFormatting.DARK_GRAY)).append(Integer.toString(n2)).withStyle(ChatFormatting.GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator<Connection> iterator = this.connections.iterator();
            while (iterator.hasNext()) {
                Connection connection = iterator.next();
                if (connection.isConnected()) {
                    connection.tick();
                    continue;
                }
                iterator.remove();
                connection.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll() {
        List<Connection> list = this.connections;
        synchronized (list) {
            Iterator<Connection> iterator = this.connections.iterator();
            while (iterator.hasNext()) {
                Connection connection = iterator.next();
                if (!connection.isConnected()) continue;
                iterator.remove();
                connection.disconnect(new TranslatableComponent("multiplayer.status.cancelled"));
            }
        }
    }

}

