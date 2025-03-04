package com.saft.pack_generator.service;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.filepaths.FilePath;
import com.saft.pack_generator.utils.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class FileStorageService {
    public SuccessMsgResponse uploadSwuFile(MultipartFile file) {
        try {
            String UPLOAD_SWU_FILE = FilePath.UPLOAD_FILE.getPath();
            // Call the common utility method for validation and ensuring the upload directory
            FileUtils.validateAndEnsureUploadDirectory(file, UPLOAD_SWU_FILE);
            // Get the original filename and its extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return new SuccessMsgResponse(false, "Invalid file name!", null);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // Get file extension
            String destinationPath = "";
            if (extension.equalsIgnoreCase(".swu")) {
                destinationPath = UPLOAD_SWU_FILE + originalFilename;
            }
            // Save the file
            File destinationFile = new File(destinationPath);
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "SWU File uploaded successfully!", UPLOAD_SWU_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to upload, check file extension or file should not empty" +e.getMessage(), e);
        }
    }

    public SuccessMsgResponse uploadS19File(MultipartFile file) {
        try {
            String UPLOAD_S19_FILE = FilePath.UPLOAD_FILE.getPath();
            FileUtils.validateAndEnsureUploadDirectory(file, UPLOAD_S19_FILE);
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return new SuccessMsgResponse(false, "Invalid file name!", null);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // Get file extension
            String destinationPath = "";
            if (extension.equalsIgnoreCase(".s19")) {
                destinationPath = UPLOAD_S19_FILE + originalFilename;
            }
            // Save the file
            File destinationFile = new File(destinationPath);
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "S19 File uploaded successfully!", UPLOAD_S19_FILE);
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload, check file extension or file should not empty" + e.getMessage(), e);
        }
    }

    public ResponseEntity<byte[]> generateZipResponse() {
        String zipSourceFolder = FilePath.UPLOAD_FILE.getPath(); // 📂 Folder containing files
        String zipFilePath = FilePath.ZIP_FILE_PATH.getPath(); // 📂 Output ZIP path
        try {
           FileUtils.zipFiles(zipSourceFolder, zipFilePath);
            byte[] zipFileBytes = Files.readAllBytes(Paths.get(zipFilePath));
            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename("generateFile.zip").build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipFileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to create ZIP file: " + e.getMessage()).getBytes());
        }
    }

    public ResponseEntity<Set<String>> getFileList() {
        String UPLOAD_S19_FILE = FilePath.UPLOAD_FILE.getPath();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<Set<String>>(Stream.of(new File(UPLOAD_S19_FILE).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
                .collect(Collectors.toSet()), headers, HttpStatus.OK);
    }

    public ResponseEntity<Resource> downloadAuditLogFile(String fileName) {
        try {
            String AUDIT_LOG_FILE = FilePath.UPLOAD_FILE.getPath() + fileName;        // Define the path of the file to be downloaded
            Path filePath = Paths.get(AUDIT_LOG_FILE);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // Return the file as a download response
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM) // Forces browser to download
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                // Return 404 if the file does not exist
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Return 500 in case of any error
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Resource> downloadFile() {
        try {
            String generateZipFile = FilePath.ZIP_FILE_PATH.getPath();
            File zipFile = new File(generateZipFile);
            if (!zipFile.exists()) {
                throw new FileStorageException("ZIP file not found: " + generateZipFile);
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("download failed");
        }
    }


}
