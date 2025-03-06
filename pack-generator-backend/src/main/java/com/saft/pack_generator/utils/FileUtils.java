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

    public static void writeFileForUpload(MultipartFile file,String originalFilename) {
        try {
            String UPLOAD_FILE = FilePath.UPLOAD_FILE.getPath();
            String destinationPath = UPLOAD_FILE + originalFilename;

            File destinationFile = new File(destinationPath);
            // Save the file using BufferedOutputStream (Better for large files)
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {
                byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            }
        }catch (IOException e){
            throw new FileStorageException("File Error writing or uploading file: ");
        }
    }

    private static void createMetadataFile(String metadataPath, String sourceFolder) {
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

    private static void writeMetadataToFile(String filePath, String sourceFolder) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            File dir = new File(sourceFolder);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".swu") || name.endsWith(".s19"));
            if (files == null || files.length == 0) {
                throw new FileNotFoundException("No SWU or S19 files found in the directory.");
            }
            List<Map<String, String>> checksumInfo = createChecksumInfo(Arrays.asList(files));
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(writer, checksumInfo);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to write or wrong metadata file");
        }
    }

    private static List<Map<String, String>> createChecksumInfo(List<File> files) {
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

    private static String calculateChecksum(File file) {
        try (InputStream fis = new BufferedInputStream(new FileInputStream(file))) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];  // 4KB buffer
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to calculate checksum for file: " + file.getName(), e);
        }
    }

    public static void zipFiles(String sourceFolder, String zipFilePath) throws IOException {
        String metadataFile = FilePath.METADATA_FILE.getPath();

        createMetadataFile(metadataFile, sourceFolder);

        File folder = new File(sourceFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Source folder not found: " + sourceFolder);
        }

        // Ensure try-with-resources to handle stream closing automatically
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            File[] files = folder.listFiles((dir, name) -> name.endsWith(".swu") || name.endsWith(".s19") || name.endsWith(".json"));

            if (files == null || files.length == 0) {
                throw new FileNotFoundException("No matching files found in folder: " + sourceFolder);
            }

            // Add files to the zip
            for (File file : files) {
                addToZip(file, zos);
            }
        } catch (IOException e) {
            e.printStackTrace();  // You can add logging here if needed
            throw new FileStorageException("Error occurred while creating zip file: " + e.getMessage(), e);
        }
    }

    private static void addToZip(File file, ZipOutputStream zos)  {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[4096];  // 4KB buffer
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
        } catch (IOException e) {
            throw new FileStorageException("creating zip failed");
        }
    }
}