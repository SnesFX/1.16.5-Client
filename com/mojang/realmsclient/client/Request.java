/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.client;

import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public abstract class Request<T extends Request<T>> {
    protected HttpURLConnection connection;
    private boolean connected;
    protected String url;

    public Request(String string, int n, int n2) {
        try {
            this.url = string;
            Proxy proxy = RealmsClientConfig.getProxy();
            this.connection = proxy != null ? (HttpURLConnection)new URL(string).openConnection(proxy) : (HttpURLConnection)new URL(string).openConnection();
            this.connection.setConnectTimeout(n);
            this.connection.setReadTimeout(n2);
        }
        catch (MalformedURLException malformedURLException) {
            throw new RealmsHttpException(malformedURLException.getMessage(), malformedURLException);
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), iOException);
        }
    }

    public void cookie(String string, String string2) {
        Request.cookie(this.connection, string, string2);
    }

    public static void cookie(HttpURLConnection httpURLConnection, String string, String string2) {
        String string3 = httpURLConnection.getRequestProperty("Cookie");
        if (string3 == null) {
            httpURLConnection.setRequestProperty("Cookie", string + "=" + string2);
        } else {
            httpURLConnection.setRequestProperty("Cookie", string3 + ";" + string + "=" + string2);
        }
    }

    public int getRetryAfterHeader() {
        return Request.getRetryAfterHeader(this.connection);
    }

    public static int getRetryAfterHeader(HttpURLConnection httpURLConnection) {
        String string = httpURLConnection.getHeaderField("Retry-After");
        try {
            return Integer.valueOf(string);
        }
        catch (Exception exception) {
            return 5;
        }
    }

    public int responseCode() {
        try {
            this.connect();
            return this.connection.getResponseCode();
        }
        catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
        }
    }

    public String text() {
        try {
            this.connect();
            String string = null;
            string = this.responseCode() >= 400 ? this.read(this.connection.getErrorStream()) : this.read(this.connection.getInputStream());
            this.dispose();
            return string;
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), iOException);
        }
    }

    private String read(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        StringBuilder stringBuilder = new StringBuilder();
        int n = inputStreamReader.read();
        while (n != -1) {
            stringBuilder.append((char)n);
            n = inputStreamReader.read();
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dispose() {
        byte[] arrby = new byte[1024];
        try {
            InputStream inputStream = this.connection.getInputStream();
            while (inputStream.read(arrby) > 0) {
            }
            inputStream.close();
        }
        catch (Exception exception) {
            InputStream inputStream;
            block13 : {
                inputStream = this.connection.getErrorStream();
                if (inputStream != null) break block13;
                return;
            }
            try {
                while (inputStream.read(arrby) > 0) {
                }
                inputStream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.disconnect();
            }
        }
    }

    protected T connect() {
        if (this.connected) {
            return (T)this;
        }
        T t = this.doConnect();
        this.connected = true;
        return t;
    }

    protected abstract T doConnect();

    public static Request<?> get(String string) {
        return new Get(string, 5000, 60000);
    }

    public static Request<?> get(String string, int n, int n2) {
        return new Get(string, n, n2);
    }

    public static Request<?> post(String string, String string2) {
        return new Post(string, string2, 5000, 60000);
    }

    public static Request<?> post(String string, String string2, int n, int n2) {
        return new Post(string, string2, n, n2);
    }

    public static Request<?> delete(String string) {
        return new Delete(string, 5000, 60000);
    }

    public static Request<?> put(String string, String string2) {
        return new Put(string, string2, 5000, 60000);
    }

    public static Request<?> put(String string, String string2, int n, int n2) {
        return new Put(string, string2, n, n2);
    }

    public String getHeader(String string) {
        return Request.getHeader(this.connection, string);
    }

    public static String getHeader(HttpURLConnection httpURLConnection, String string) {
        try {
            return httpURLConnection.getHeaderField(string);
        }
        catch (Exception exception) {
            return "";
        }
    }

    public static class Post
    extends Request<Post> {
        private final String content;

        public Post(String string, String string2, int n, int n2) {
            super(string, n, n2);
            this.content = string2;
        }

        @Override
        public Post doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("POST");
                OutputStream outputStream = this.connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.content);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ Request doConnect() {
            return this.doConnect();
        }
    }

    public static class Put
    extends Request<Put> {
        private final String content;

        public Put(String string, String string2, int n, int n2) {
            super(string, n, n2);
            this.content = string2;
        }

        @Override
        public Put doConnect() {
            try {
                if (this.content != null) {
                    this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.connection.setDoOutput(true);
                this.connection.setDoInput(true);
                this.connection.setRequestMethod("PUT");
                OutputStream outputStream = this.connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.content);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ Request doConnect() {
            return this.doConnect();
        }
    }

    public static class Get
    extends Request<Get> {
        public Get(String string, int n, int n2) {
            super(string, n, n2);
        }

        @Override
        public Get doConnect() {
            try {
                this.connection.setDoInput(true);
                this.connection.setDoOutput(true);
                this.connection.setUseCaches(false);
                this.connection.setRequestMethod("GET");
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ Request doConnect() {
            return this.doConnect();
        }
    }

    public static class Delete
    extends Request<Delete> {
        public Delete(String string, int n, int n2) {
            super(string, n, n2);
        }

        @Override
        public Delete doConnect() {
            try {
                this.connection.setDoOutput(true);
                this.connection.setRequestMethod("DELETE");
                this.connection.connect();
                return this;
            }
            catch (Exception exception) {
                throw new RealmsHttpException(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ Request doConnect() {
            return this.doConnect();
        }
    }

}

