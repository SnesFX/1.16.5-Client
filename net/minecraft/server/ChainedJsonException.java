/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class ChainedJsonException
extends IOException {
    private final List<Entry> entries = Lists.newArrayList();
    private final String message;

    public ChainedJsonException(String string) {
        this.entries.add(new Entry());
        this.message = string;
    }

    public ChainedJsonException(String string, Throwable throwable) {
        super(throwable);
        this.entries.add(new Entry());
        this.message = string;
    }

    public void prependJsonKey(String string) {
        this.entries.get(0).addJsonKey(string);
    }

    public void setFilenameAndFlush(String string) {
        this.entries.get(0).filename = string;
        this.entries.add(0, new Entry());
    }

    @Override
    public String getMessage() {
        return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
    }

    public static ChainedJsonException forException(Exception exception) {
        if (exception instanceof ChainedJsonException) {
            return (ChainedJsonException)exception;
        }
        String string = exception.getMessage();
        if (exception instanceof FileNotFoundException) {
            string = "File not found";
        }
        return new ChainedJsonException(string, exception);
    }

    public static class Entry {
        @Nullable
        private String filename;
        private final List<String> jsonKeys = Lists.newArrayList();

        private Entry() {
        }

        private void addJsonKey(String string) {
            this.jsonKeys.add(0, string);
        }

        public String getJsonKeys() {
            return StringUtils.join(this.jsonKeys, (String)"->");
        }

        public String toString() {
            if (this.filename != null) {
                if (this.jsonKeys.isEmpty()) {
                    return this.filename;
                }
                return this.filename + " " + this.getJsonKeys();
            }
            if (this.jsonKeys.isEmpty()) {
                return "(Unknown file)";
            }
            return "(Unknown file) " + this.getJsonKeys();
        }
    }

}

