package com.saft.pack_generator.filepaths;

public enum FilePath {


    SWU_FILE("C:/ProgramData/CubePackGenerator/uploadFile.swu"),
    S19_FILE("C:/ProgramData/CubePackGenerator/uploadFile.s19"),
    UPLOAD_FILE("C:/ProgramData/CubePackGenerator/"),
    METADATA_FILE("C:/ProgramData/CubePackGenerator/metadata.json"),
    ZIP_FILE_PATH("C:/ProgramData/CubePackGenerator/generateFile.zip"),
    DOWNLOAD_FILE("C:/ProgramData/CubePackGenerator/extracted");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
