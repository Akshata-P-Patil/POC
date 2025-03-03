package com.saft.pack_generator.service;
import com.saft.pack_generator.Entity.FileData;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.filepaths.FilePath;
import com.saft.pack_generator.utils.FileUtils;
import org.springframework.core.io.InputStreamResource;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileStorageService {
    public SuccessMsgResponse uploadSwuFile(MultipartFile file) {
        try {
            String UPLOAD_SWU_FILE = FilePath.UPLOAD_FILE.getPath();
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
            String UPLOAD_S19_FILE = FilePath.UPLOAD_FILE.getPath();
            FileUtils.validateAndEnsureUploadDirectory(file, UPLOAD_S19_FILE);
            // Create file path and save it
            File destinationFile = new File(UPLOAD_S19_FILE + "uploadFile.s19");
            Files.copy(file.getInputStream(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new SuccessMsgResponse(true, "S19 File uploaded successfully!", UPLOAD_S19_FILE);
        } catch (IOException e) {
            throw new FileStorageException("Error saving file: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<byte[]> generateZipResponse()  {
        String swuFilePath = FilePath.SWU_FILE.getPath();
        String s19FilePath = FilePath.S19_FILE.getPath();
        String metdataFilePath = FilePath.UPLOAD_FILE.getPath();
        String metdataFileDataPath = FilePath.METADATA_FILE.getPath();

        FileUtils.createMetadataFile(metdataFilePath, metdataFileDataPath);
        // Prepare metadata content
        List<FileData> fileData = Arrays.asList(
                new FileData("uploadFile.swu", "32656523173djdj21y38"),
                new FileData("uploadFile.s19", "32656523173djdj21y38")
        );

        // Write metadata JSON file
        FileUtils.writeMetadataToFile(metdataFileDataPath, fileData);
        byte[] zipBytes = generateZip(swuFilePath, s19FilePath, metdataFileDataPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }

    public byte[] generateZip(String file1Path, String file2Path, String metdataFile) {
        if (!Files.exists(Paths.get(file1Path)) || !Files.exists(Paths.get(file2Path)) || !Files.exists(Paths.get(metdataFile))) {
            throw new FileStorageException("File not found: " + file1Path);
        }
        try {
            String ZIP_FILE_PATH = FilePath.ZIP_FILE_PATH.getPath();
            zipFiles(ZIP_FILE_PATH, file1Path, file2Path, metdataFile);
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

    public ResponseEntity<Set<String>> getFileList() {
        String UPLOAD_S19_FILE = FilePath.UPLOAD_FILE.getPath();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<Set<String>>(Stream.of(new File(UPLOAD_S19_FILE).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
                .collect(Collectors.toSet()), headers, HttpStatus.OK);
    }

    public ResponseEntity<Resource> downloadAuditLogFile(String fileName) {
        try {
            String AUDIT_LOG_FILE =  FilePath.UPLOAD_FILE.getPath() + fileName;        // Define the path of the file to be downloaded
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
        }catch (IOException e){
            e.printStackTrace();
            throw new FileStorageException("download failed");
        }

    }


}
