/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 *  org.apache.commons.compress.utils.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Component VERIFYING_TEXT = new TranslatableComponent("mco.upload.verifying");
    private final RealmsResetWorldScreen lastScreen;
    private final LevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    private volatile Component[] errorMessage;
    private volatile Component status = new TranslatableComponent("mco.upload.preparing");
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private Button backButton;
    private Button cancelButton;
    private int tickCount;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private final Runnable callback;

    public RealmsUploadScreen(long l, int n, RealmsResetWorldScreen realmsResetWorldScreen, LevelSummary levelSummary, Runnable runnable) {
        this.worldId = l;
        this.slotId = n;
        this.lastScreen = realmsResetWorldScreen;
        this.selectedLevel = levelSummary;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create((double)0.10000000149011612);
        this.callback = runnable;
    }

    @Override
    public void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.backButton = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_BACK, button -> this.onBack()));
        this.backButton.visible = false;
        this.cancelButton = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, button -> this.onCancel()));
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.upload();
            } else {
                this.lastScreen.switchSlot(() -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.minecraft.setScreen(this);
                        this.upload();
                    }
                });
            }
        }
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void onBack() {
        this.callback.run();
    }

    private void onCancel() {
        this.cancelled = true;
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        RealmsUploadScreen.drawCenteredString(poseStack, this.font, this.status, this.width / 2, 50, 16777215);
        if (this.showDots) {
            this.drawDots(poseStack);
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(poseStack);
            this.drawUploadSpeed(poseStack);
        }
        if (this.errorMessage != null) {
            for (int i = 0; i < this.errorMessage.length; ++i) {
                RealmsUploadScreen.drawCenteredString(poseStack, this.font, this.errorMessage[i], this.width / 2, 110 + 12 * i, 16711680);
            }
        }
        super.render(poseStack, n, n2, f);
    }

    private void drawDots(PoseStack poseStack) {
        int n = this.font.width(this.status);
        this.font.draw(poseStack, DOTS[this.tickCount / 10 % DOTS.length], (float)(this.width / 2 + n / 2 + 5), 50.0f, 16777215);
    }

    private void drawProgressBar(PoseStack poseStack) {
        double d = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        double d2 = this.width / 2 - 100;
        double d3 = 0.5;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(d2 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        bufferBuilder.vertex(d2 + 200.0 * d + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        bufferBuilder.vertex(d2 + 200.0 * d + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        bufferBuilder.vertex(d2 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        bufferBuilder.vertex(d2, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        bufferBuilder.vertex(d2 + 200.0 * d, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        bufferBuilder.vertex(d2 + 200.0 * d, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        bufferBuilder.vertex(d2, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tesselator.end();
        RenderSystem.enableTexture();
        RealmsUploadScreen.drawCenteredString(poseStack, this.font, this.progress + " %", this.width / 2, 84, 16777215);
    }

    private void drawUploadSpeed(PoseStack poseStack) {
        if (this.tickCount % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMillis() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawUploadSpeed0(poseStack, this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMillis();
        } else {
            this.drawUploadSpeed0(poseStack, this.bytesPersSecond);
        }
    }

    private void drawUploadSpeed0(PoseStack poseStack, long l) {
        if (l > 0L) {
            int n = this.font.width(this.progress);
            String string = "(" + Unit.humanReadable(l) + "/s)";
            this.font.draw(poseStack, string, (float)(this.width / 2 + n / 2 + 15), 84.0f, 16777215);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickCount;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            ArrayList arrayList = Lists.newArrayList();
            arrayList.add(this.status.getString());
            if (this.progress != null) {
                arrayList.add(this.progress + "%");
            }
            if (this.errorMessage != null) {
                Stream.of(this.errorMessage).map(Component::getString).forEach(arrayList::add);
            }
            NarrationHelper.now(String.join((CharSequence)System.lineSeparator(), arrayList));
        }
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(() -> {
            File file = null;
            RealmsClient realmsClient = RealmsClient.create();
            long l = this.worldId;
            try {
                if (!UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = new TranslatableComponent("mco.upload.close.failure");
                    return;
                }
                UploadInfo uploadInfo = null;
                for (int i = 0; i < 20; ++i) {
                    block35 : {
                        if (!this.cancelled) break block35;
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        uploadInfo = realmsClient.requestUploadInfo(l, UploadTokenCache.get(l));
                        if (uploadInfo == null) continue;
                        break;
                    }
                    catch (RetryCallException retryCallException) {
                        Thread.sleep(retryCallException.delaySeconds * 1000);
                    }
                }
                if (uploadInfo == null) {
                    this.status = new TranslatableComponent("mco.upload.close.failure");
                    return;
                }
                UploadTokenCache.put(l, uploadInfo.getToken());
                if (!uploadInfo.isWorldClosed()) {
                    this.status = new TranslatableComponent("mco.upload.close.failure");
                    return;
                }
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                File file2 = new File(this.minecraft.gameDirectory.getAbsolutePath(), "saves");
                file = this.tarGzipArchive(new File(file2, this.selectedLevel.getLevelId()));
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                if (!this.verify(file)) {
                    long l2 = file.length();
                    Unit unit = Unit.getLargest(l2);
                    Unit unit2 = Unit.getLargest(5368709120L);
                    if (Unit.humanReadable(l2, unit).equals(Unit.humanReadable(5368709120L, unit2)) && unit != Unit.B) {
                        Unit unit3 = Unit.values()[unit.ordinal() - 1];
                        this.setErrorMessage(new TranslatableComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslatableComponent("mco.upload.size.failure.line2", Unit.humanReadable(l2, unit3), Unit.humanReadable(5368709120L, unit3)));
                        return;
                    }
                    this.setErrorMessage(new TranslatableComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslatableComponent("mco.upload.size.failure.line2", Unit.humanReadable(l2, unit), Unit.humanReadable(5368709120L, unit2)));
                    return;
                }
                this.status = new TranslatableComponent("mco.upload.uploading", this.selectedLevel.getLevelName());
                FileUpload fileUpload = new FileUpload(file, this.worldId, this.slotId, uploadInfo, this.minecraft.getUser(), SharedConstants.getCurrentVersion().getName(), this.uploadStatus);
                fileUpload.upload(uploadResult -> {
                    if (uploadResult.statusCode >= 200 && uploadResult.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = new TranslatableComponent("mco.upload.done");
                        this.backButton.setMessage(CommonComponents.GUI_DONE);
                        UploadTokenCache.invalidate(l);
                    } else if (uploadResult.statusCode == 400 && uploadResult.errorMessage != null) {
                        this.setErrorMessage(new TranslatableComponent("mco.upload.failed", uploadResult.errorMessage));
                    } else {
                        this.setErrorMessage(new TranslatableComponent("mco.upload.failed", uploadResult.statusCode));
                    }
                });
                while (!fileUpload.isFinished()) {
                    if (this.cancelled) {
                        fileUpload.cancel();
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedException) {
                        LOGGER.error("Failed to check Realms file upload status");
                    }
                }
            }
            catch (IOException iOException) {
                this.setErrorMessage(new TranslatableComponent("mco.upload.failed", iOException.getMessage()));
            }
            catch (RealmsServiceException realmsServiceException) {
                this.setErrorMessage(new TranslatableComponent("mco.upload.failed", realmsServiceException.toString()));
            }
            catch (InterruptedException interruptedException) {
                LOGGER.error("Could not acquire upload lock");
            }
            finally {
                this.uploadFinished = true;
                if (!UPLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                UPLOAD_LOCK.unlock();
                this.showDots = false;
                this.backButton.visible = true;
                this.cancelButton.visible = false;
                if (file != null) {
                    LOGGER.debug("Deleting file " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }).start();
    }

    private void setErrorMessage(Component ... arrcomponent) {
        this.errorMessage = arrcomponent;
    }

    private void uploadCancelled() {
        this.status = new TranslatableComponent("mco.upload.cancelled");
        LOGGER.debug("Upload was cancelled");
    }

    private boolean verify(File file) {
        return file.length() < 5368709120L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive(File file) throws IOException {
        try (TarArchiveOutputStream tarArchiveOutputStream = null;){
            File file2 = File.createTempFile("realms-upload-file", ".tar.gz");
            tarArchiveOutputStream = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream(new FileOutputStream(file2)));
            tarArchiveOutputStream.setLongFileMode(3);
            this.addFileToTarGz(tarArchiveOutputStream, file.getAbsolutePath(), "world", true);
            tarArchiveOutputStream.finish();
            File file3 = file2;
            return file3;
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tarArchiveOutputStream, String string, String string2, boolean bl) throws IOException {
        if (this.cancelled) {
            return;
        }
        File file = new File(string);
        String string3 = bl ? string2 : string2 + file.getName();
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string3);
        tarArchiveOutputStream.putArchiveEntry((ArchiveEntry)tarArchiveEntry);
        if (file.isFile()) {
            IOUtils.copy((InputStream)new FileInputStream(file), (OutputStream)tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } else {
            tarArchiveOutputStream.closeArchiveEntry();
            File[] arrfile = file.listFiles();
            if (arrfile != null) {
                for (File file2 : arrfile) {
                    this.addFileToTarGz(tarArchiveOutputStream, file2.getAbsolutePath(), string3 + "/", false);
                }
            }
        }
    }
}

