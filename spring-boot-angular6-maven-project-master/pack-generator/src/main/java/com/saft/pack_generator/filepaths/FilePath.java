package com.saft.pack_generator.filepaths;

public enum FilePath {


    SWU_FILE("C:/ProgramData/CubePackGenerator/uploadFile.swu"),
    S19_FILE("C:/ProgramData/CubePackGenerator/uploadFile.s19"),
    UPLOAD_SWU_FILE("C:/ProgramData/CubePackGenerator/"),
    UPLOAD_S19_FILE("C:/ProgramData/CubePackGenerator/"),
    ZIP_FILE_PATH("C:/ProgramData/CubePackGenerator/files.zip");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
