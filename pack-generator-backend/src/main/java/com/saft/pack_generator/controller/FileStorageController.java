package com.saft.pack_generator.controller;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.filepaths.FilePath;
import com.saft.pack_generator.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@RestController
@RequestMapping("/api")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadSwuData")
    public ResponseEntity<SuccessMsgResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        SuccessMsgResponse response = this.fileStorageService.uploadSwuFile(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadS19Data")
    public ResponseEntity<SuccessMsgResponse> uploadS19Req(@RequestParam("file") MultipartFile file) {
        SuccessMsgResponse response = this.fileStorageService.uploadS19File(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile() {
        // Delegate the download logic to the service
        return this.fileStorageService.downloadFile();
    }

    @GetMapping("/generateZip")
    public ResponseEntity<byte[]> generateZip() {
        return this.fileStorageService.generateZipResponse();
    }

    @GetMapping("/getList")
    public ResponseEntity<Set<String>> getFileList() {
        return this.fileStorageService.getFileList();
    }

    @GetMapping("/downloadAuditLog")
    public ResponseEntity<?> downloadFile(String fileName) {
        // Delegate the download logic to the service
        return this.fileStorageService.downloadAuditLogFile(fileName);
    }


}