/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.StatusLine
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.User;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUpload {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File file;
    private final long worldId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final UploadStatus uploadStatus;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private CompletableFuture<UploadResult> uploadTask;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

    public FileUpload(File file, long l, int n, UploadInfo uploadInfo, User user, String string, UploadStatus uploadStatus) {
        this.file = file;
        this.worldId = l;
        this.slotId = n;
        this.uploadInfo = uploadInfo;
        this.sessionId = user.getSessionId();
        this.username = user.getName();
        this.clientVersion = string;
        this.uploadStatus = uploadStatus;
    }

    public void upload(Consumer<UploadResult> consumer) {
        if (this.uploadTask != null) {
            return;
        }
        this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0));
        this.uploadTask.thenAccept((Consumer)consumer);
    }

    public void cancel() {
        this.cancelled.set(true);
        if (this.uploadTask != null) {
            this.uploadTask.cancel(false);
            this.uploadTask = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UploadResult requestUpload(int n) {
        UploadResult.Builder builder = new UploadResult.Builder();
        if (this.cancelled.get()) {
            return builder.build();
        }
        this.uploadStatus.totalBytes = this.file.length();
        HttpPost httpPost = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId));
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
        try {
            this.setupRequest(httpPost);
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute((HttpUriRequest)httpPost);
            long l = this.getRetryDelaySeconds((HttpResponse)closeableHttpResponse);
            if (this.shouldRetry(l, n)) {
                UploadResult uploadResult = this.retryUploadAfter(l, n);
                return uploadResult;
            }
            this.handleResponse((HttpResponse)closeableHttpResponse, builder);
        }
        catch (Exception exception) {
            if (!this.cancelled.get()) {
                LOGGER.error("Caught exception while uploading: ", (Throwable)exception);
            }
        }
        finally {
            this.cleanup(httpPost, closeableHttpClient);
        }
        return builder.build();
    }

    private void cleanup(HttpPost httpPost, CloseableHttpClient closeableHttpClient) {
        httpPost.releaseConnection();
        if (closeableHttpClient != null) {
            try {
                closeableHttpClient.close();
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to close Realms upload client");
            }
        }
    }

    private void setupRequest(HttpPost httpPost) throws FileNotFoundException {
        httpPost.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
        CustomInputStreamEntity customInputStreamEntity = new CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
        customInputStreamEntity.setContentType("application/octet-stream");
        httpPost.setEntity((HttpEntity)customInputStreamEntity);
    }

    private void handleResponse(HttpResponse httpResponse, UploadResult.Builder builder) throws IOException {
        String string;
        int n = httpResponse.getStatusLine().getStatusCode();
        if (n == 401) {
            LOGGER.debug("Realms server returned 401: " + (Object)httpResponse.getFirstHeader("WWW-Authenticate"));
        }
        builder.withStatusCode(n);
        if (httpResponse.getEntity() != null && (string = EntityUtils.toString((HttpEntity)httpResponse.getEntity(), (String)"UTF-8")) != null) {
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(string).getAsJsonObject().get("errorMsg");
                Optional<String> optional = Optional.ofNullable(jsonElement).map(JsonElement::getAsString);
                builder.withErrorMessage(optional.orElse(null));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private boolean shouldRetry(long l, int n) {
        return l > 0L && n + 1 < 5;
    }

    private UploadResult retryUploadAfter(long l, int n) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(l).toMillis());
        return this.requestUpload(n + 1);
    }

    private long getRetryDelaySeconds(HttpResponse httpResponse) {
        return Optional.ofNullable(httpResponse.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
    }

    public boolean isFinished() {
        return this.uploadTask.isDone() || this.uploadTask.isCancelled();
    }

    static class CustomInputStreamEntity
    extends InputStreamEntity {
        private final long length;
        private final InputStream content;
        private final UploadStatus uploadStatus;

        public CustomInputStreamEntity(InputStream inputStream, long l, UploadStatus uploadStatus) {
            super(inputStream);
            this.content = inputStream;
            this.length = l;
            this.uploadStatus = uploadStatus;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void writeTo(OutputStream outputStream) throws IOException {
            block7 : {
                Args.notNull((Object)outputStream, (String)"Output stream");
                try (InputStream inputStream = this.content;){
                    int n;
                    byte[] arrby = new byte[4096];
                    if (this.length < 0L) {
                        int n2;
                        while ((n2 = inputStream.read(arrby)) != -1) {
                            outputStream.write(arrby, 0, n2);
                            this.uploadStatus.bytesWritten += (long)n2;
                        }
                        break block7;
                    }
                    for (long i = this.length; i > 0L; i -= (long)n) {
                        n = inputStream.read(arrby, 0, (int)Math.min(4096L, i));
                        if (n == -1) {
                            break;
                        }
                        outputStream.write(arrby, 0, n);
                        this.uploadStatus.bytesWritten += (long)n;
                        outputStream.flush();
                    }
                }
            }
        }
    }

}

