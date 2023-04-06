/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDataFetcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private volatile boolean stopped = true;
    private final Runnable serverListUpdateTask = new ServerListUpdateTask();
    private final Runnable pendingInviteUpdateTask = new PendingInviteUpdateTask();
    private final Runnable trialAvailabilityTask = new TrialAvailabilityTask();
    private final Runnable liveStatsTask = new LiveStatsTask();
    private final Runnable unreadNewsTask = new UnreadNewsTask();
    private final Set<RealmsServer> removedServers = Sets.newHashSet();
    private List<RealmsServer> servers = Lists.newArrayList();
    private RealmsServerPlayerLists livestats;
    private int pendingInvitesCount;
    private boolean trialAvailable;
    private boolean hasUnreadNews;
    private String newsLink;
    private ScheduledFuture<?> serverListScheduledFuture;
    private ScheduledFuture<?> pendingInviteScheduledFuture;
    private ScheduledFuture<?> trialAvailableScheduledFuture;
    private ScheduledFuture<?> liveStatsScheduledFuture;
    private ScheduledFuture<?> unreadNewsScheduledFuture;
    private final Map<Task, Boolean> fetchStatus = new ConcurrentHashMap<Task, Boolean>(Task.values().length);

    public boolean isStopped() {
        return this.stopped;
    }

    public synchronized void init() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.scheduleTasks();
        }
    }

    public synchronized void initWithSpecificTaskList() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.fetchStatus.put(Task.PENDING_INVITE, false);
            this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
            this.fetchStatus.put(Task.TRIAL_AVAILABLE, false);
            this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
            this.fetchStatus.put(Task.UNREAD_NEWS, false);
            this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
        }
    }

    public boolean isFetchedSinceLastTry(Task task) {
        Boolean bl = this.fetchStatus.get((Object)task);
        return bl == null ? false : bl;
    }

    public void markClean() {
        for (Task task : this.fetchStatus.keySet()) {
            this.fetchStatus.put(task, false);
        }
    }

    public synchronized void forceUpdate() {
        this.stop();
        this.init();
    }

    public synchronized List<RealmsServer> getServers() {
        return Lists.newArrayList(this.servers);
    }

    public synchronized int getPendingInvitesCount() {
        return this.pendingInvitesCount;
    }

    public synchronized boolean isTrialAvailable() {
        return this.trialAvailable;
    }

    public synchronized RealmsServerPlayerLists getLivestats() {
        return this.livestats;
    }

    public synchronized boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }

    public synchronized String newsLink() {
        return this.newsLink;
    }

    public synchronized void stop() {
        this.stopped = true;
        this.cancelTasks();
    }

    private void scheduleTasks() {
        for (Task task : Task.values()) {
            this.fetchStatus.put(task, false);
        }
        this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
        this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
        this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
        this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
        this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
    }

    private void cancelTasks() {
        try {
            if (this.serverListScheduledFuture != null) {
                this.serverListScheduledFuture.cancel(false);
            }
            if (this.pendingInviteScheduledFuture != null) {
                this.pendingInviteScheduledFuture.cancel(false);
            }
            if (this.trialAvailableScheduledFuture != null) {
                this.trialAvailableScheduledFuture.cancel(false);
            }
            if (this.liveStatsScheduledFuture != null) {
                this.liveStatsScheduledFuture.cancel(false);
            }
            if (this.unreadNewsScheduledFuture != null) {
                this.unreadNewsScheduledFuture.cancel(false);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to cancel Realms tasks", (Throwable)exception);
        }
    }

    private synchronized void setServers(List<RealmsServer> list) {
        int n = 0;
        for (RealmsServer realmsServer : this.removedServers) {
            if (!list.remove(realmsServer)) continue;
            ++n;
        }
        if (n == 0) {
            this.removedServers.clear();
        }
        this.servers = list;
    }

    public synchronized void removeItem(RealmsServer realmsServer) {
        this.servers.remove(realmsServer);
        this.removedServers.add(realmsServer);
    }

    private boolean isActive() {
        return !this.stopped;
    }

    public static enum Task {
        SERVER_LIST,
        PENDING_INVITE,
        TRIAL_AVAILABLE,
        LIVE_STATS,
        UNREAD_NEWS;
        
    }

    class UnreadNewsTask
    implements Runnable {
        private UnreadNewsTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getUnreadNews();
            }
        }

        private void getUnreadNews() {
            try {
                String string;
                RealmsClient realmsClient = RealmsClient.create();
                RealmsNews realmsNews = null;
                try {
                    realmsNews = realmsClient.getNews();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                if (realmsNews != null && (string = realmsNews.newsLink) != null && !string.equals(realmsPersistenceData.newsLink)) {
                    realmsPersistenceData.hasUnreadNews = true;
                    realmsPersistenceData.newsLink = string;
                    RealmsPersistence.writeFile(realmsPersistenceData);
                }
                RealmsDataFetcher.this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
                RealmsDataFetcher.this.newsLink = realmsPersistenceData.newsLink;
                RealmsDataFetcher.this.fetchStatus.put(Task.UNREAD_NEWS, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get unread news", (Throwable)exception);
            }
        }
    }

    class LiveStatsTask
    implements Runnable {
        private LiveStatsTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getLiveStats();
            }
        }

        private void getLiveStats() {
            try {
                RealmsClient realmsClient = RealmsClient.create();
                RealmsDataFetcher.this.livestats = realmsClient.getLiveStats();
                RealmsDataFetcher.this.fetchStatus.put(Task.LIVE_STATS, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get live stats", (Throwable)exception);
            }
        }
    }

    class TrialAvailabilityTask
    implements Runnable {
        private TrialAvailabilityTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getTrialAvailable();
            }
        }

        private void getTrialAvailable() {
            try {
                RealmsClient realmsClient = RealmsClient.create();
                RealmsDataFetcher.this.trialAvailable = realmsClient.trialAvailable();
                RealmsDataFetcher.this.fetchStatus.put(Task.TRIAL_AVAILABLE, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get trial availability", (Throwable)exception);
            }
        }
    }

    class PendingInviteUpdateTask
    implements Runnable {
        private PendingInviteUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updatePendingInvites();
            }
        }

        private void updatePendingInvites() {
            try {
                RealmsClient realmsClient = RealmsClient.create();
                RealmsDataFetcher.this.pendingInvitesCount = realmsClient.pendingInvitesCount();
                RealmsDataFetcher.this.fetchStatus.put(Task.PENDING_INVITE, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get pending invite count", (Throwable)exception);
            }
        }
    }

    class ServerListUpdateTask
    implements Runnable {
        private ServerListUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updateServersList();
            }
        }

        private void updateServersList() {
            try {
                RealmsClient realmsClient = RealmsClient.create();
                List<RealmsServer> list = realmsClient.listWorlds().servers;
                if (list != null) {
                    list.sort(new RealmsServer.McoServerComparator(Minecraft.getInstance().getUser().getName()));
                    RealmsDataFetcher.this.setServers(list);
                    RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                } else {
                    LOGGER.warn("Realms server list was null or empty");
                }
            }
            catch (Exception exception) {
                RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                LOGGER.error("Couldn't get server list", (Throwable)exception);
            }
        }
    }

}

