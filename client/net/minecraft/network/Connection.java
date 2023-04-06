/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  io.netty.bootstrap.AbstractBootstrap
 *  io.netty.bootstrap.Bootstrap
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
 *  io.netty.channel.DefaultEventLoopGroup
 *  io.netty.channel.EventLoop
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollSocketChannel
 *  io.netty.channel.local.LocalChannel
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  io.netty.handler.timeout.TimeoutException
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
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
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Connection
extends SimpleChannelInboundHandler<Packet<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker ROOT_MARKER = MarkerManager.getMarker((String)"NETWORK");
    public static final Marker PACKET_MARKER = MarkerManager.getMarker((String)"NETWORK_PACKETS", (Marker)ROOT_MARKER);
    public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf((String)"protocol");
    public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue<DefaultEventLoopGroup>(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
    private final PacketFlow receiving;
    private final Queue<PacketHolder> queue = Queues.newConcurrentLinkedQueue();
    private Channel channel;
    private SocketAddress address;
    private PacketListener packetListener;
    private Component disconnectedReason;
    private boolean encrypted;
    private boolean disconnectionHandled;
    private int receivedPackets;
    private int sentPackets;
    private float averageReceivedPackets;
    private float averageSentPackets;
    private int tickCount;
    private boolean handlingFault;

    public Connection(PacketFlow packetFlow) {
        this.receiving = packetFlow;
    }

    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.address = this.channel.remoteAddress();
        try {
            this.setProtocol(ConnectionProtocol.HANDSHAKING);
        }
        catch (Throwable throwable) {
            LOGGER.fatal((Object)throwable);
        }
    }

    public void setProtocol(ConnectionProtocol connectionProtocol) {
        this.channel.attr(ATTRIBUTE_PROTOCOL).set((Object)connectionProtocol);
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        this.disconnect(new TranslatableComponent("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        if (throwable instanceof SkipPacketException) {
            LOGGER.debug("Skipping packet due to errors", throwable.getCause());
            return;
        }
        boolean bl = !this.handlingFault;
        this.handlingFault = true;
        if (!this.channel.isOpen()) {
            return;
        }
        if (throwable instanceof TimeoutException) {
            LOGGER.debug("Timeout", throwable);
            this.disconnect(new TranslatableComponent("disconnect.timeout"));
        } else {
            TranslatableComponent translatableComponent = new TranslatableComponent("disconnect.genericReason", "Internal Exception: " + throwable);
            if (bl) {
                LOGGER.debug("Failed to sent packet", throwable);
                this.send(new ClientboundDisconnectPacket(translatableComponent), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.disconnect(translatableComponent)));
                this.setReadOnly();
            } else {
                LOGGER.debug("Double fault", throwable);
                this.disconnect(translatableComponent);
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                Connection.genericsFtw(packet, this.packetListener);
            }
            catch (RunningOnDifferentThreadException runningOnDifferentThreadException) {
                // empty catch block
            }
            ++this.receivedPackets;
        }
    }

    private static <T extends PacketListener> void genericsFtw(Packet<T> packet, PacketListener packetListener) {
        packet.handle(packetListener);
    }

    public void setListener(PacketListener packetListener) {
        Validate.notNull((Object)packetListener, (String)"packetListener", (Object[])new Object[0]);
        this.packetListener = packetListener;
    }

    public void send(Packet<?> packet) {
        this.send(packet, null);
    }

    public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacket(packet, genericFutureListener);
        } else {
            this.queue.add(new PacketHolder(packet, genericFutureListener));
        }
    }

    private void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
        ConnectionProtocol connectionProtocol = ConnectionProtocol.getProtocolForPacket(packet);
        ConnectionProtocol connectionProtocol2 = (ConnectionProtocol)((Object)this.channel.attr(ATTRIBUTE_PROTOCOL).get());
        ++this.sentPackets;
        if (connectionProtocol2 != connectionProtocol) {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (connectionProtocol != connectionProtocol2) {
                this.setProtocol(connectionProtocol);
            }
            ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
            if (genericFutureListener != null) {
                channelFuture.addListener(genericFutureListener);
            }
            channelFuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (connectionProtocol != connectionProtocol2) {
                    this.setProtocol(connectionProtocol);
                }
                ChannelFuture channelFuture = this.channel.writeAndFlush((Object)packet);
                if (genericFutureListener != null) {
                    channelFuture.addListener(genericFutureListener);
                }
                channelFuture.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flushQueue() {
        if (this.channel == null || !this.channel.isOpen()) {
            return;
        }
        Queue<PacketHolder> queue = this.queue;
        synchronized (queue) {
            PacketHolder packetHolder;
            while ((packetHolder = this.queue.poll()) != null) {
                this.sendPacket(packetHolder.packet, (GenericFutureListener<? extends Future<? super Void>>)packetHolder.listener);
            }
        }
    }

    public void tick() {
        this.flushQueue();
        if (this.packetListener instanceof ServerLoginPacketListenerImpl) {
            ((ServerLoginPacketListenerImpl)this.packetListener).tick();
        }
        if (this.packetListener instanceof ServerGamePacketListenerImpl) {
            ((ServerGamePacketListenerImpl)this.packetListener).tick();
        }
        if (this.channel != null) {
            this.channel.flush();
        }
        if (this.tickCount++ % 20 == 0) {
            this.tickSecond();
        }
    }

    protected void tickSecond() {
        this.averageSentPackets = Mth.lerp(0.75f, this.sentPackets, this.averageSentPackets);
        this.averageReceivedPackets = Mth.lerp(0.75f, this.receivedPackets, this.averageReceivedPackets);
        this.sentPackets = 0;
        this.receivedPackets = 0;
    }

    public SocketAddress getRemoteAddress() {
        return this.address;
    }

    public void disconnect(Component component) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = component;
        }
    }

    public boolean isMemoryConnection() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public static Connection connectToServer(InetAddress inetAddress, int n, boolean bl) {
        LazyLoadedValue<NioEventLoopGroup> lazyLoadedValue;
        Class<NioSocketChannel> class_;
        final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
        if (Epoll.isAvailable() && bl) {
            class_ = EpollSocketChannel.class;
            lazyLoadedValue = NETWORK_EPOLL_WORKER_GROUP;
        } else {
            class_ = NioSocketChannel.class;
            lazyLoadedValue = NETWORK_WORKER_GROUP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)lazyLoadedValue.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new Varint21FrameDecoder()).addLast("decoder", (ChannelHandler)new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new Varint21LengthFieldPrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)connection);
            }
        })).channel(class_)).connect(inetAddress, n).syncUninterruptibly();
        return connection;
    }

    public static Connection connectToLocalServer(SocketAddress socketAddress) {
        final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler((ChannelHandler)new ChannelInitializer<Channel>(){

            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast("packet_handler", (ChannelHandler)connection);
            }
        })).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return connection;
    }

    public void setEncryptionKey(Cipher cipher, Cipher cipher2) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new CipherDecoder(cipher));
        this.channel.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new CipherEncoder(cipher2));
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public Component getDisconnectedReason() {
        return this.disconnectedReason;
    }

    public void setReadOnly() {
        this.channel.config().setAutoRead(false);
    }

    public void setupCompression(int n) {
        if (n >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(n);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new CompressionDecoder(n));
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(n);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", (ChannelHandler)new CompressionEncoder(n));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection() {
        if (this.channel == null || this.channel.isOpen()) {
            return;
        }
        if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
        } else {
            this.disconnectionHandled = true;
            if (this.getDisconnectedReason() != null) {
                this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if (this.getPacketListener() != null) {
                this.getPacketListener().onDisconnect(new TranslatableComponent("multiplayer.disconnect.generic"));
            }
        }
    }

    public float getAverageReceivedPackets() {
        return this.averageReceivedPackets;
    }

    public float getAverageSentPackets() {
        return this.averageSentPackets;
    }

    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.channelRead0(channelHandlerContext, (Packet)object);
    }

    static class PacketHolder {
        private final Packet<?> packet;
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> listener;

        public PacketHolder(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
            this.packet = packet;
            this.listener = genericFutureListener;
        }
    }

}

