package com.saft.pack_generator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    // Utility method to create a file and ensure its directory exists
    public static File createFileIfNotExists(String filePath, byte[] fileContent) throws IOException {
        File destinationFile = new File(filePath);

        // Ensure the parent directory exists
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs();
        }
        // Create the file if it does not exist
        if (!destinationFile.exists()) {
            destinationFile.createNewFile();
        }
        Files.write(destinationFile.toPath(), fileContent, StandardOpenOption.TRUNCATE_EXISTING);
        return destinationFile;
    }

}
