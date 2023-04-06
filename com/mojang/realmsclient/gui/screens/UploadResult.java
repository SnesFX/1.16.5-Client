/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui.screens;

public class UploadResult {
    public final int statusCode;
    public final String errorMessage;

    private UploadResult(int n, String string) {
        this.statusCode = n;
        this.errorMessage = string;
    }

    public static class Builder {
        private int statusCode = -1;
        private String errorMessage;

        public Builder withStatusCode(int n) {
            this.statusCode = n;
            return this;
        }

        public Builder withErrorMessage(String string) {
            this.errorMessage = string;
            return this;
        }

        public UploadResult build() {
            return new UploadResult(this.statusCode, this.errorMessage);
        }
    }

}

