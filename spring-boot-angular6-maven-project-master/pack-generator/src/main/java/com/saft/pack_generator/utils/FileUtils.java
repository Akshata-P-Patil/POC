package com.saft.pack_generator.utils;

import com.saft.pack_generator.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static void validateAndEnsureUploadDirectory(MultipartFile file, String uploadDirPath) throws IOException {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload an empty file.");
        }
        // Ensure the upload directory exists
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs(); // Create directories if not exist
            if (!created) {
                throw new FileStorageException("Failed to create upload directory.");
            }
        }
    }
}
