package com.saft.pack_generator.service;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.utils.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileUploadService {

    private static final String UPLOAD_SWU_FILE = "C:/ProgramData/CubePackGenerator/";
    private static final String UPLOAD_S19_FILE = "C:/ProgramData/CubePackGenerator/";
    private static final String FILE_PATH = "C:/ProgramData/CubePackGenerator/uploadFile.swu";


    public SuccessMsgResponse uploadSwuFile(MultipartFile file) {
        try {
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
            // Define the path of the file to be downloaded
            Path filePath = Paths.get(FILE_PATH);
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


}
