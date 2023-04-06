/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.exception.RealmsServiceException;

public class RetryCallException
extends RealmsServiceException {
    public final int delaySeconds;

    public RetryCallException(int n, int n2) {
        super(n2, "Retry operation", -1, "");
        this.delaySeconds = n < 0 || n > 120 ? 5 : n;
    }
}

