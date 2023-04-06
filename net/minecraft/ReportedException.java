/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft;

import net.minecraft.CrashReport;

public class ReportedException
extends RuntimeException {
    private final CrashReport report;

    public ReportedException(CrashReport crashReport) {
        this.report = crashReport;
    }

    public CrashReport getReport() {
        return this.report;
    }

    @Override
    public Throwable getCause() {
        return this.report.getException();
    }

    @Override
    public String getMessage() {
        return this.report.getTitle();
    }
}

