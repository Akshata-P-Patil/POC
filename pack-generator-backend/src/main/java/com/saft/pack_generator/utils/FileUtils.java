package com.saft.pack_generator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saft.pack_generator.Entity.FileData;
import com.saft.pack_generator.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

    public static void createMetadataFile(String directoryPath, String filePath) {
        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if missing
            }

            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile(); // Create file if missing
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Error creating metadata file: " + filePath, e);
        }
    }

    public static void writeMetadataToFile(String filePath, List<FileData> fileDataList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(filePath), fileDataList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Error writing metadata JSON to file: " + filePath, e);
        }
    }

}
