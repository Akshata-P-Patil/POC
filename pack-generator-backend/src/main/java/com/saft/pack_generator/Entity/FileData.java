package com.saft.pack_generator.Entity;

public class FileData {
    private String fileName;
    private String checksum;

    public FileData(String fileName, String checksum) {
        this.fileName = fileName;
        this.checksum = checksum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
