package com.saft.pack_generator.service;

import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.utils.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Service
public class FileUploadService {

    private static final String UPLOAD_SWU_FILE = "C:/ProgramData/CubePackGenerator/";
    private static final String UPLOAD_S19_FILE = "C:/ProgramData/CubePackGenerator/uploadFile.s19";

    public SuccessMsgResponse uploadSwuFile(MultipartFile file) {
        try {
            if (file.isEmpty() || file.getName() != "uploadFile.swu") {
                throw new IOException("Cannot upload an empty file.");
            }
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_SWU_FILE);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // Create file path and save it
            File destinationFile = new File(UPLOAD_SWU_FILE + file.getOriginalFilename());
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "SWU File uploaded successfully!", UPLOAD_SWU_FILE);
        } catch (IOException e) {
            throw new FileStorageException("Error saving file: " + e.getMessage(), e);
        }
    }


    public SuccessMsgResponse uploadS19File(byte[] fileContent) {
        try {
            FileUtils.createFileIfNotExists(UPLOAD_S19_FILE, fileContent);
            return new SuccessMsgResponse(true, "S19 File uploaded successfully!", UPLOAD_S19_FILE);
        } catch (IOException e) {
            throw new FileStorageException("Error saving file: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Resource> downloadFile() {

        File file = new File(UPLOAD_SWU_FILE);
        if (!file.exists()) {
            throw new FileStorageException("File not found at path: " + UPLOAD_SWU_FILE);
        }
        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}
