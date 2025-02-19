package com.saft.pack_generator.service;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.filepaths.FilePath;
import com.saft.pack_generator.utils.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileStorageService {
        public SuccessMsgResponse uploadSwuFile(MultipartFile file) {
        try {
            String UPLOAD_SWU_FILE = FilePath.UPLOAD_SWU_FILE.getPath();
            // Call the common utility method for validation and ensuring the upload directory
            FileUtils.validateAndEnsureUploadDirectory(file, UPLOAD_SWU_FILE);
            // Create file path and save it
            File destinationFile = new File(UPLOAD_SWU_FILE + "uploadFile.swu");
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "SWU File uploaded successfully!", UPLOAD_SWU_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Failed to upload");
        }
    }

    public SuccessMsgResponse uploadS19File(MultipartFile file) {
        try {
            String UPLOAD_S19_FILE = FilePath.UPLOAD_S19_FILE.getPath();
            FileUtils.validateAndEnsureUploadDirectory(file, UPLOAD_S19_FILE);
            // Create file path and save it
            File destinationFile = new File(UPLOAD_S19_FILE + "uploadFile.s19");
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "S19 File uploaded successfully!", UPLOAD_S19_FILE);
        } catch (IOException e) {
            throw new FileStorageException("Error saving file: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Resource> downloadFile() {
        try {
            String UPLOAD_SWU_FILE = FilePath.SWU_FILE.getPath();
            // Define the path of the file to be downloaded
            Path filePath = Paths.get(UPLOAD_SWU_FILE);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Return the file as a download response
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM) // Forces browser to download
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"uploadFile.swu\"")
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

    public ResponseEntity<byte[]> generateZipResponse() {
        String swuFilePath = FilePath.SWU_FILE.getPath();
        String s19FilePath = FilePath.S19_FILE.getPath();
        byte[] zipBytes = generateZip(swuFilePath, s19FilePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }

    public byte[] generateZip(String file1Path, String file2Path) {
        if (!Files.exists(Paths.get(file1Path)) || !Files.exists(Paths.get(file2Path))) {
            throw new FileStorageException("File not found: " + file1Path);
        }
        try {
            String ZIP_FILE_PATH = FilePath.ZIP_FILE_PATH.getPath();
            zipFiles(ZIP_FILE_PATH, file1Path, file2Path);
            return Files.readAllBytes(Paths.get(ZIP_FILE_PATH)); // Read zip file as byte array
        } catch (IOException e) {
            throw new FileStorageException("Error generating ZIP file: " + e.getMessage(), e);
        }
    }

    private void zipFiles(String zipFilePath, String... files) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (String filePath : files) {
                File fileToZip = new File(filePath);
                if (!fileToZip.exists()) {
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zipOut.write(buffer, 0, length);
                    }
                }
            }
        }
    }
}
