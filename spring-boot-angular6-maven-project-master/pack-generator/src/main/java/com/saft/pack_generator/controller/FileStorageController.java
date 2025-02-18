package com.saft.pack_generator.controller;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.exception.FileStorageException;
import com.saft.pack_generator.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileStorageController {

    private static final String UPLOAD_SWU_FILE = "C:/ProgramData/CubePackGenerator/uploadFile.swu";

@Autowired
private FileUploadService fileUploadService;

//    @PostMapping("/uploadSwuData")
//    public ResponseEntity<SuccessMsgResponse> uploadSwuReq(@RequestBody byte[] fileContent) {
//        SuccessMsgResponse response = this.fileUploadService.uploadSwuFile(fileContent);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/uploadSwuData")
    public ResponseEntity<SuccessMsgResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        SuccessMsgResponse response =    this.fileUploadService.uploadSwuFile(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/uploadS19Data")
    public ResponseEntity<SuccessMsgResponse> uploadS19Req(@RequestBody byte[] fileContent) {
        SuccessMsgResponse response = this.fileUploadService.uploadS19File(fileContent);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() {
        return this.fileUploadService.downloadFile();
    }
}
