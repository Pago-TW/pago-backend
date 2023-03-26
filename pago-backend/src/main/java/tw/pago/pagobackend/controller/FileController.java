// package tw.pago.pagobackend.controller;

// import java.io.IOException;
// import java.net.URL;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import org.springframework.web.multipart.MultipartFile;

// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.AmazonS3Client;
// import com.amazonaws.services.s3.model.AmazonS3Exception;
// import com.amazonaws.services.s3.model.ObjectMetadata;

// import tw.pago.pagobackend.config.AwsS3Config;
// import tw.pago.pagobackend.model.File;
// import tw.pago.pagobackend.service.FileService;

// @RestController
// public class FileController {
//     @Autowired
//     private FileService fileService;
    
//     // @PostMapping("/files")
//     // public ResponseEntity<URL> uploadFile(@RequestParam("file") MultipartFile file) {

//     //     try {
//     //         String fileName = file.getOriginalFilename();
//     //         ObjectMetadata metadata = new ObjectMetadata();
//     //         metadata.setContentType(file.getContentType());
//     //         metadata.setContentLength(file.getSize());

//     //         amazonS3.putObject(BUCKET_NAME, fileName, file.getInputStream(), metadata);
//     //         System.out.println(2);
//     //         URL resUrl = amazonS3.getUrl(BUCKET_NAME, fileName);
//     //         return ResponseEntity.status(HttpStatus.OK).body(resUrl);
//     //     } catch (AmazonS3Exception e) {
//     //         // Log the error message and status code
//     //         System.err.println("Amazon S3 error: " + e.getErrorCode());
//     //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//     //     } catch (Exception e) {
//     //         // Log the error message and status code
//     //         System.err.println("Unknown error: " + e.getMessage());
//     //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//     //     } 
//     // }
    
//     @PostMapping("/users/{userId}/files")
//     public ResponseEntity<URL> uploadFile(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
//         URL resUrl = fileService.uploadFile(userId, file);  
//         return ResponseEntity.status(HttpStatus.OK).body(resUrl);
//     }
    
    
//     // @GetMapping ("/files/get")
//     // public ResponseEntity<URL> getFile() {
//     //     System.out.println(1);
//     //     URL resUrl = amazonS3.getUrl(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
//     //     System.out.println(2);
//     //     amazonS3.getObject(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
//     //     System.out.println(3);
//     //     return ResponseEntity.status(HttpStatus.OK).body(resUrl);
//     // }

//     // @GetMapping ("/files/get")
//     // public String getFile() {
//     //     try {
//     //         return testvar;
//     //     } catch (Exception e) {
//     //         return "error";
//     //     }
        
//     // }
// }



