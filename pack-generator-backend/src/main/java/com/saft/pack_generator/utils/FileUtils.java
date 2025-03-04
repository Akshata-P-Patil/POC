package com.saft.pack_generator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.filepaths.FilePath;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
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
                throw new FileStorageException("Failed to create swu file directory.");
            }
        }
    }

    public static void createMetadataFile(String metadataPath, String sourceFolder) {
        try {
            File file = new File(metadataPath);
            if (!file.exists()) {
                file.createNewFile(); // Create file if missing
            }
            writeMetadataToFile(metadataPath, sourceFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Error creating metadata file: " + metadataPath, e);
        }
    }

    public static void writeMetadataToFile(String filePath, String sourceFolder) {
        try {
            File dir = new File(sourceFolder);
            // List of files with .swu and .s19 extensions
            File[] files = dir.listFiles((d, name) -> name.endsWith(".swu") || name.endsWith(".s19"));
            if (files == null || files.length == 0) {
                throw new FileNotFoundException("No SWU or S19 files found in the directory.");
            }
            List<Map<String, String>> checksumInfo = createChecksumInfo(Arrays.asList(files));
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File(filePath);
            objectMapper.writeValue(jsonFile, checksumInfo);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to write or wrong metadata file");
        }
    }

    public static List<Map<String, String>> createChecksumInfo(List<File> files) {
        List<Map<String, String>> checksumInfo = new ArrayList<>();
        for (File file : files) {
            String checksum = calculateChecksum(file);
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("filename", file.getName());
            fileInfo.put("checksum", checksum);
            checksumInfo.add(fileInfo);
        }
        return checksumInfo;
    }

    public static String calculateChecksum(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (IOException e) {
           throw new FileStorageException("Failed to calculateChecksum file");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipFiles(String sourceFolder, String zipFilePath) throws IOException {
        String metadataFile = FilePath.METADATA_FILE.getPath();

        createMetadataFile(metadataFile, sourceFolder);
        File folder = new File(sourceFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Source folder not found: " + sourceFolder);
        }

        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".swu") || name.endsWith(".s19") || name.endsWith(".json"));

        if (files == null || files.length == 0) {
            throw new FileNotFoundException("No matching files found in folder: " + sourceFolder);
        }

        for (File file : files) {
            addToZip(file, zos);
        }

        zos.close();
        fos.close();
    }

    public static void addToZip(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, bytesRead);
        }
        zos.closeEntry();
        fis.close();
    }
}