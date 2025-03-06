package com.saft.pack_generator.controller;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/pack-generator")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadSwuFile")
    public ResponseEntity<SuccessMsgResponse> uploadSwuFileReq(@RequestParam("file") MultipartFile file) {
        SuccessMsgResponse response = this.fileStorageService.uploadSwuFile(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadS19File")
    public ResponseEntity<SuccessMsgResponse> uploadS19FileReq(@RequestParam("file") MultipartFile file) {
        SuccessMsgResponse response = this.fileStorageService.uploadS19File(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/generateZip")
    public ResponseEntity<InputStreamResource> generateZip(){
        return this.fileStorageService.generateZipResponse();
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile()  {
        // Delegate the download logic to the service
        return this.fileStorageService.downloadZip();
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