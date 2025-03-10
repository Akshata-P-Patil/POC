package com.saft.pack_generator.controller;
import com.saft.pack_generator.apiresponse.SuccessMsgResponse;
import com.saft.pack_generator.service.AuditLogFile;
import com.saft.pack_generator.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;



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
    public ResponseEntity<List<AuditLogFile>> getFileList() throws IOException {

        return this.fileStorageService.getAuditLogsList();
    }

    @GetMapping("/downloadAuditLog")
    public ResponseEntity<Resource> downloadFile(String fileName) {
        // Delegate the download logic to the service
        return this.fileStorageService.downloadAuditLogFile(fileName);
    }


    static {
        disableSslVerification();
    }

    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }




}