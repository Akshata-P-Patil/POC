/*
 * AuditLogFile.java
 * Description: This class serves as entity for AuditLogFile operations
 * Creation date: Wed Mar 11 14:31:28 2020
 * Author: Mickael.DALES.external@saftbatteries.com
 * Copyright (c) 2021
 * All rights reserved by Saft
 */

package com.saft.pack_generator.service;

import java.time.Instant;

public class AuditLogFile {
    private final String filename;
    private final Instant lastModificationDate;
    private final Long size;

    public AuditLogFile(String filename, Instant lastModificationDate, Long size) {
        this.filename = filename;
        this.lastModificationDate = lastModificationDate;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public Instant getLastModificationDate() {
        return lastModificationDate;
    }

    public Long getSize() {
        return size;
    }
}
