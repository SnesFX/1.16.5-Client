/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.rcon.NetworkDataOutputStream;
import net.minecraft.server.rcon.PktUtils;
import net.minecraft.server.rcon.thread.GenericThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryThreadGs4
extends GenericThread {
    private static final Logger LOGGER = LogManager.getLogger();
    private long lastChallengeCheck;
    private final int port;
    private final int serverPort;
    private final int maxPlayers;
    private final String serverName;
    private final String worldName;
    private DatagramSocket socket;
    private final byte[] buffer = new byte[1460];
    private String hostIp;
    private String serverIp;
    private final Map<SocketAddress, RequestChallenge> validChallenges;
    private final NetworkDataOutputStream rulesResponse;
    private long lastRulesResponse;
    private final ServerInterface serverInterface;

    private QueryThreadGs4(ServerInterface serverInterface, int n) {
        super("Query Listener");
        this.serverInterface = serverInterface;
        this.port = n;
        this.serverIp = serverInterface.getServerIp();
        this.serverPort = serverInterface.getServerPort();
        this.serverName = serverInterface.getServerName();
        this.maxPlayers = serverInterface.getMaxPlayers();
        this.worldName = serverInterface.getLevelIdName();
        this.lastRulesResponse = 0L;
        this.hostIp = "0.0.0.0";
        if (this.serverIp.isEmpty() || this.hostIp.equals(this.serverIp)) {
            this.serverIp = "0.0.0.0";
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                this.hostIp = inetAddress.getHostAddress();
            }
            catch (UnknownHostException unknownHostException) {
                LOGGER.warn("Unable to determine local host IP, please set server-ip in server.properties", (Throwable)unknownHostException);
            }
        } else {
            this.hostIp = this.serverIp;
        }
        this.rulesResponse = new NetworkDataOutputStream(1460);
        this.validChallenges = Maps.newHashMap();
    }

    @Nullable
    public static QueryThreadGs4 create(ServerInterface serverInterface) {
        int n = serverInterface.getProperties().queryPort;
        if (0 >= n || 65535 < n) {
            LOGGER.warn("Invalid query port {} found in server.properties (queries disabled)", (Object)n);
            return null;
        }
        QueryThreadGs4 queryThreadGs4 = new QueryThreadGs4(serverInterface, n);
        if (!queryThreadGs4.start()) {
            return null;
        }
        return queryThreadGs4;
    }

    private void sendTo(byte[] arrby, DatagramPacket datagramPacket) throws IOException {
        this.socket.send(new DatagramPacket(arrby, arrby.length, datagramPacket.getSocketAddress()));
    }

    private boolean processPacket(DatagramPacket datagramPacket) throws IOException {
        byte[] arrby = datagramPacket.getData();
        int n = datagramPacket.getLength();
        SocketAddress socketAddress = datagramPacket.getSocketAddress();
        LOGGER.debug("Packet len {} [{}]", (Object)n, (Object)socketAddress);
        if (3 > n || -2 != arrby[0] || -3 != arrby[1]) {
            LOGGER.debug("Invalid packet [{}]", (Object)socketAddress);
            return false;
        }
        LOGGER.debug("Packet '{}' [{}]", (Object)PktUtils.toHexString(arrby[2]), (Object)socketAddress);
        switch (arrby[2]) {
            case 9: {
                this.sendChallenge(datagramPacket);
                LOGGER.debug("Challenge [{}]", (Object)socketAddress);
                return true;
            }
            case 0: {
                if (!this.validChallenge(datagramPacket).booleanValue()) {
                    LOGGER.debug("Invalid challenge [{}]", (Object)socketAddress);
                    return false;
                }
                if (15 == n) {
                    this.sendTo(this.buildRuleResponse(datagramPacket), datagramPacket);
                    LOGGER.debug("Rules [{}]", (Object)socketAddress);
                    break;
                }
                NetworkDataOutputStream networkDataOutputStream = new NetworkDataOutputStream(1460);
                networkDataOutputStream.write(0);
                networkDataOutputStream.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
                networkDataOutputStream.writeString(this.serverName);
                networkDataOutputStream.writeString("SMP");
                networkDataOutputStream.writeString(this.worldName);
                networkDataOutputStream.writeString(Integer.toString(this.serverInterface.getPlayerCount()));
                networkDataOutputStream.writeString(Integer.toString(this.maxPlayers));
                networkDataOutputStream.writeShort((short)this.serverPort);
                networkDataOutputStream.writeString(this.hostIp);
                this.sendTo(networkDataOutputStream.toByteArray(), datagramPacket);
                LOGGER.debug("Status [{}]", (Object)socketAddress);
            }
        }
        return true;
    }

    private byte[] buildRuleResponse(DatagramPacket datagramPacket) throws IOException {
        String[] arrstring;
        long l = Util.getMillis();
        if (l < this.lastRulesResponse + 5000L) {
            byte[] arrby = this.rulesResponse.toByteArray();
            byte[] arrby2 = this.getIdentBytes(datagramPacket.getSocketAddress());
            arrby[1] = arrby2[0];
            arrby[2] = arrby2[1];
            arrby[3] = arrby2[2];
            arrby[4] = arrby2[3];
            return arrby;
        }
        this.lastRulesResponse = l;
        this.rulesResponse.reset();
        this.rulesResponse.write(0);
        this.rulesResponse.writeBytes(this.getIdentBytes(datagramPacket.getSocketAddress()));
        this.rulesResponse.writeString("splitnum");
        this.rulesResponse.write(128);
        this.rulesResponse.write(0);
        this.rulesResponse.writeString("hostname");
        this.rulesResponse.writeString(this.serverName);
        this.rulesResponse.writeString("gametype");
        this.rulesResponse.writeString("SMP");
        this.rulesResponse.writeString("game_id");
        this.rulesResponse.writeString("MINECRAFT");
        this.rulesResponse.writeString("version");
        this.rulesResponse.writeString(this.serverInterface.getServerVersion());
        this.rulesResponse.writeString("plugins");
        this.rulesResponse.writeString(this.serverInterface.getPluginNames());
        this.rulesResponse.writeString("map");
        this.rulesResponse.writeString(this.worldName);
        this.rulesResponse.writeString("numplayers");
        this.rulesResponse.writeString("" + this.serverInterface.getPlayerCount());
        this.rulesResponse.writeString("maxplayers");
        this.rulesResponse.writeString("" + this.maxPlayers);
        this.rulesResponse.writeString("hostport");
        this.rulesResponse.writeString("" + this.serverPort);
        this.rulesResponse.writeString("hostip");
        this.rulesResponse.writeString(this.hostIp);
        this.rulesResponse.write(0);
        this.rulesResponse.write(1);
        this.rulesResponse.writeString("player_");
        this.rulesResponse.write(0);
        for (String string : arrstring = this.serverInterface.getPlayerNames()) {
            this.rulesResponse.writeString(string);
        }
        this.rulesResponse.write(0);
        return this.rulesResponse.toByteArray();
    }

    private byte[] getIdentBytes(SocketAddress socketAddress) {
        return this.validChallenges.get(socketAddress).getIdentBytes();
    }

    private Boolean validChallenge(DatagramPacket datagramPacket) {
        SocketAddress socketAddress = datagramPacket.getSocketAddress();
        if (!this.validChallenges.containsKey(socketAddress)) {
            return false;
        }
        byte[] arrby = datagramPacket.getData();
        return this.validChallenges.get(socketAddress).getChallenge() == PktUtils.intFromNetworkByteArray(arrby, 7, datagramPacket.getLength());
    }

    private void sendChallenge(DatagramPacket datagramPacket) throws IOException {
        RequestChallenge requestChallenge = new RequestChallenge(datagramPacket);
        this.validChallenges.put(datagramPacket.getSocketAddress(), requestChallenge);
        this.sendTo(requestChallenge.getChallengeBytes(), datagramPacket);
    }

    private void pruneChallenges() {
        if (!this.running) {
            return;
        }
        long l = Util.getMillis();
        if (l < this.lastChallengeCheck + 30000L) {
            return;
        }
        this.lastChallengeCheck = l;
        this.validChallenges.values().removeIf(requestChallenge -> requestChallenge.before(l));
    }

    @Override
    public void run() {
        LOGGER.info("Query running on {}:{}", (Object)this.serverIp, (Object)this.port);
        this.lastChallengeCheck = Util.getMillis();
        DatagramPacket datagramPacket = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            while (this.running) {
                try {
                    this.socket.receive(datagramPacket);
                    this.pruneChallenges();
                    this.processPacket(datagramPacket);
                }
                catch (SocketTimeoutException socketTimeoutException) {
                    this.pruneChallenges();
                }
                catch (PortUnreachableException portUnreachableException) {
                }
                catch (IOException iOException) {
                    this.recoverSocketError(iOException);
                }
            }
        }
        finally {
            LOGGER.debug("closeSocket: {}:{}", (Object)this.serverIp, (Object)this.port);
            this.socket.close();
        }
    }

    @Override
    public boolean start() {
        if (this.running) {
            return true;
        }
        if (!this.initSocket()) {
            return false;
        }
        return super.start();
    }

    private void recoverSocketError(Exception exception) {
        if (!this.running) {
            return;
        }
        LOGGER.warn("Unexpected exception", (Throwable)exception);
        if (!this.initSocket()) {
            LOGGER.error("Failed to recover from exception, shutting down!");
            this.running = false;
        }
    }

    private boolean initSocket() {
        try {
            this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
            this.socket.setSoTimeout(500);
            return true;
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to initialise query system on {}:{}", (Object)this.serverIp, (Object)this.port, (Object)exception);
            return false;
        }
    }

    static class RequestChallenge {
        private final long time = new Date().getTime();
        private final int challenge;
        private final byte[] identBytes;
        private final byte[] challengeBytes;
        private final String ident;

        public RequestChallenge(DatagramPacket datagramPacket) {
            byte[] arrby = datagramPacket.getData();
            this.identBytes = new byte[4];
            this.identBytes[0] = arrby[3];
            this.identBytes[1] = arrby[4];
            this.identBytes[2] = arrby[5];
            this.identBytes[3] = arrby[6];
            this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
            this.challenge = new Random().nextInt(16777216);
            this.challengeBytes = String.format("\t%s%d\u0000", this.ident, this.challenge).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean before(long l) {
            return this.time < l;
        }

        public int getChallenge() {
            return this.challenge;
        }

        public byte[] getChallengeBytes() {
            return this.challengeBytes;
        }

        public byte[] getIdentBytes() {
            return this.identBytes;
        }
    }

}

