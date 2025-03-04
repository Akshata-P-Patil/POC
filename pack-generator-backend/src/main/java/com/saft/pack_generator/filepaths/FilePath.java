package com.saft.pack_generator.filepaths;

public enum FilePath {

    UPLOAD_FILE("C:/ProgramData/CubePackGenerator/"),
    METADATA_FILE("C:/ProgramData/CubePackGenerator/metadata.json"),
    ZIP_FILE_PATH("C:/ProgramData/CubePackGenerator/generateFile.zip");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
