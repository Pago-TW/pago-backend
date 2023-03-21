package tw.pago.pagobackend.controller;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import tw.pago.pagobackend.model.UploadFile;
import tw.pago.pagobackend.service.UploadFileService;

@RestController
public class FileController {
    @Autowired
    private UploadFileService uploadFileService;

    private static final String BUCKET_NAME = "pago-file-storage";

    @Autowired
    private AmazonS3 amazonS3;
    
    @PostMapping("/files")
    public ResponseEntity<URL> uploadFile(@RequestParam("file") MultipartFile file) throws IOException{
        // try {
            // Upload the file to S3
            URL fileUrl = uploadFileService.createUploadFile(file);
            
            // Return the file URL as a response
            return ResponseEntity.ok(fileUrl);
        // } catch (AmazonS3Exception e) {
        //     // Log the error message and status code
        //     System.err.println("Amazon S3 error: " + e.getErrorCode());
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Amazon S3 error");
        // } catch (Exception e) {
        //     // Log the error message and status code
        //     System.err.println("Unknown error: " + e.getMessage());
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error");
        // }
    }
    

    @GetMapping ("/files/get")
    public ResponseEntity<URL> getFile() {

        URL resUrl = amazonS3.getUrl(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
        amazonS3.getObject(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
        return ResponseEntity.status(HttpStatus.OK).body(resUrl);
    }
}



