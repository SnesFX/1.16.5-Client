/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import net.minecraft.client.resources.language.I18n;

public class RealmsServiceException
extends Exception {
    public final int httpResultCode;
    public final String httpResponseContent;
    public final int errorCode;
    public final String errorMsg;

    public RealmsServiceException(int n, String string, RealmsError realmsError) {
        super(string);
        this.httpResultCode = n;
        this.httpResponseContent = string;
        this.errorCode = realmsError.getErrorCode();
        this.errorMsg = realmsError.getErrorMessage();
    }

    public RealmsServiceException(int n, String string, int n2, String string2) {
        super(string);
        this.httpResultCode = n;
        this.httpResponseContent = string;
        this.errorCode = n2;
        this.errorMsg = string2;
    }

    @Override
    public String toString() {
        if (this.errorCode == -1) {
            return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
        }
        String string = "mco.errorMessage." + this.errorCode;
        String string2 = I18n.get(string, new Object[0]);
        return (string2.equals(string) ? this.errorMsg : string2) + " - " + this.errorCode;
    }
}

