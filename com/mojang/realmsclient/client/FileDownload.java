/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.StatusLine
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileDownload {
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile File tempFile;
    private volatile File resourcePackPath;
    private volatile HttpGet request;
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String string) {
        CloseableHttpClient closeableHttpClient = null;
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(string);
            closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpGet);
            long l = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
            return l;
        }
        catch (Throwable throwable) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        }
        finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                }
                catch (IOException iOException) {
                    LOGGER.error("Could not close http client", (Throwable)iOException);
                }
            }
        }
    }

    public void download(WorldDownload worldDownload, String string, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, LevelStorageSource levelStorageSource) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            CloseableHttpClient closeableHttpClient = null;
            try {
                this.tempFile = File.createTempFile("backup", ".tar.gz");
                this.request = new HttpGet(worldDownload.downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)this.request);
                downloadStatus.totalBytes = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.request.abort();
                    return;
                }
                FileOutputStream fileOutputStream = new FileOutputStream(this.tempFile);
                ActionListener actionListener = new ProgressListener(string.trim(), this.tempFile, levelStorageSource, downloadStatus);
                DownloadCountingOutputStream downloadCountingOutputStream = new DownloadCountingOutputStream(fileOutputStream);
                downloadCountingOutputStream.setListener(actionListener);
                IOUtils.copy((InputStream)closeableHttpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream));
                return;
            }
            catch (Exception exception) {
                LOGGER.error("Caught exception while downloading: " + exception.getMessage());
                this.error = true;
                return;
            }
            finally {
                block40 : {
                    block41 : {
                        CloseableHttpResponse closeableHttpResponse;
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                            this.tempFile.delete();
                        }
                        if (this.error) break block40;
                        if (worldDownload.resourcePackUrl.isEmpty() || worldDownload.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.tempFile = File.createTempFile("resources", ".tar.gz");
                            this.request = new HttpGet(worldDownload.resourcePackUrl);
                            closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)this.request);
                            downloadStatus.totalBytes = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
                            if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.request.abort();
                                return;
                            }
                        }
                        catch (Exception exception) {
                            LOGGER.error("Caught exception while downloading: " + exception.getMessage());
                            this.error = true;
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(this.tempFile);
                        ResourcePackProgressListener resourcePackProgressListener = new ResourcePackProgressListener(this.tempFile, downloadStatus, worldDownload);
                        DownloadCountingOutputStream downloadCountingOutputStream = new DownloadCountingOutputStream(fileOutputStream);
                        downloadCountingOutputStream.setListener(resourcePackProgressListener);
                        IOUtils.copy((InputStream)closeableHttpResponse.getEntity().getContent(), (OutputStream)((Object)downloadCountingOutputStream));
                        break block40;
                        finally {
                            this.request.releaseConnection();
                            if (this.tempFile != null) {
                                this.tempFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if (closeableHttpClient != null) {
                    try {
                        closeableHttpClient.close();
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.request != null) {
            this.request.abort();
        }
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String string) {
        string = string.replaceAll("[\\./\"]", "_");
        for (String string2 : INVALID_FILE_NAMES) {
            if (!string.equalsIgnoreCase(string2)) continue;
            string = "_" + string + "_";
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void untarGzipArchive(String string, File file, LevelStorageSource levelStorageSource) throws IOException {
        Object object;
        String string2;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        block72 : {
            Pattern pattern = Pattern.compile(".*-([0-9]+)$");
            int n = 1;
            object4 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
            object = ((char[])object4).length;
            for (object3 = 0; object3 < object; string = string.replace((char)object2, (char)'_'), ++object3) {
                object2 = object4[object3];
            }
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = "Realm";
            }
            string = FileDownload.findAvailableFolderName(string);
            try {
                object4 = levelStorageSource.getLevelList().iterator();
                while (object4.hasNext()) {
                    object = (LevelSummary)object4.next();
                    if (!((LevelSummary)object).getLevelId().toLowerCase(Locale.ROOT).startsWith(string.toLowerCase(Locale.ROOT))) continue;
                    object3 = pattern.matcher(((LevelSummary)object).getLevelId());
                    if (((Matcher)object3).matches()) {
                        if (Integer.valueOf(((Matcher)object3).group(1)) <= n) continue;
                        n = Integer.valueOf(((Matcher)object3).group(1));
                        continue;
                    }
                    ++n;
                }
            }
            catch (Exception exception) {
                LOGGER.error("Error getting level list", (Throwable)exception);
                this.error = true;
                return;
            }
            if (!levelStorageSource.isNewLevelIdAcceptable(string) || n > 1) {
                string2 = string + (n == 1 ? "" : "-" + n);
                if (!levelStorageSource.isNewLevelIdAcceptable(string2)) {
                    boolean bl = false;
                    while (!bl) {
                        string2 = string + (++n == 1 ? "" : "-" + n);
                        if (!levelStorageSource.isNewLevelIdAcceptable(string2)) continue;
                        bl = true;
                    }
                }
            } else {
                string2 = string;
            }
            object4 = null;
            object = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
            try {
                ((File)object).mkdir();
                object4 = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream(file))));
                object3 = object4.getNextTarEntry();
                while (object3 != null) {
                    object2 = new File((File)object, object3.getName().replace("world", string2));
                    if (object3.isDirectory()) {
                        ((File)object2).mkdirs();
                    } else {
                        ((File)object2).createNewFile();
                        object5 = new FileOutputStream((File)object2);
                        Throwable throwable = null;
                        try {
                            IOUtils.copy((InputStream)object4, (OutputStream)object5);
                        }
                        catch (Throwable throwable2) {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        finally {
                            if (object5 != null) {
                                if (throwable != null) {
                                    try {
                                        ((FileOutputStream)object5).close();
                                    }
                                    catch (Throwable throwable3) {
                                        throwable.addSuppressed(throwable3);
                                    }
                                } else {
                                    ((FileOutputStream)object5).close();
                                }
                            }
                        }
                    }
                    object3 = object4.getNextTarEntry();
                }
                if (object4 == null) break block72;
            }
            catch (Exception exception) {
                LOGGER.error("Error extracting world", (Throwable)exception);
                this.error = true;
                return;
            }
            object4.close();
        }
        if (file != null) {
            file.delete();
        }
        try {
            object3 = levelStorageSource.createAccess(string2);
            object2 = null;
            try {
                ((LevelStorageSource.LevelStorageAccess)object3).renameLevel(string2.trim());
                object5 = ((LevelStorageSource.LevelStorageAccess)object3).getLevelPath(LevelResource.LEVEL_DATA_FILE);
                FileDownload.deletePlayerTag(object5.toFile());
            }
            catch (Throwable throwable) {
                Throwable throwable4 = throwable;
                throw throwable;
            }
            finally {
                if (object3 != null) {
                    if (object2 != null) {
                        try {
                            ((LevelStorageSource.LevelStorageAccess)object3).close();
                        }
                        catch (Throwable throwable) {
                            ((Throwable)object2).addSuppressed(throwable);
                        }
                    } else {
                        ((LevelStorageSource.LevelStorageAccess)object3).close();
                    }
                }
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to rename unpacked realms level {}", (Object)string2, (Object)iOException);
        }
        this.resourcePackPath = new File((File)object, string2 + File.separator + "resources.zip");
        return;
        finally {
            if (object4 != null) {
                object4.close();
            }
            if (file != null) {
                file.delete();
            }
            try {
                try (LevelStorageSource.LevelStorageAccess levelStorageAccess = levelStorageSource.createAccess(string2);){
                    levelStorageAccess.renameLevel(string2.trim());
                    Path path = levelStorageAccess.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                    FileDownload.deletePlayerTag(path.toFile());
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to rename unpacked realms level {}", (Object)string2, (Object)iOException);
            }
            this.resourcePackPath = new File((File)object, string2 + File.separator + "resources.zip");
        }
    }

    private static void deletePlayerTag(File file) {
        if (file.exists()) {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(file);
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.remove("Player");
                NbtIo.writeCompressed(compoundTag, file);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class DownloadCountingOutputStream
    extends CountingOutputStream {
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream outputStream) {
            super(outputStream);
        }

        public void setListener(ActionListener actionListener) {
            this.listener = actionListener;
        }

        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object)this, 0, null));
            }
        }
    }

    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        private ResourcePackProgressListener(File file, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
            this.tempFile = file;
            this.downloadStatus = downloadStatus;
            this.worldDownload = worldDownload;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)actionEvent.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String string = Hashing.sha1().hashBytes(Files.toByteArray((File)this.tempFile)).toString();
                    if (string.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File)this.tempFile, (File)FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + string + "). Deleting it.");
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file", (Object)iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        private ProgressListener(String string, File file, LevelStorageSource levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
            this.worldName = string;
            this.tempFile = file;
            this.levelStorageSource = levelStorageSource;
            this.downloadStatus = downloadStatus;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)actionEvent.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                }
                catch (IOException iOException) {
                    LOGGER.error("Error extracting archive", (Throwable)iOException);
                    FileDownload.this.error = true;
                }
            }
        }
    }

}

