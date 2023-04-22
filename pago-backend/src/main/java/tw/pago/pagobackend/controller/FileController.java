 package tw.pago.pagobackend.controller;

 import java.net.URL;
 import java.util.List;
 import lombok.AllArgsConstructor;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 import org.springframework.web.multipart.MultipartFile;
 import tw.pago.pagobackend.dto.CreateFileRequestDto;
 import tw.pago.pagobackend.service.FileService;
 import tw.pago.pagobackend.util.CurrentUserInfoProvider;

 @RestController
 @AllArgsConstructor
 public class FileController {

     private final FileService fileService;
     private final CurrentUserInfoProvider currentUserInfoProvider;
    
     // @PostMapping("/files")
     // public ResponseEntity<URL> uploadFile(@RequestParam("file") MultipartFile file) {

     //     try {
     //         String fileName = file.getOriginalFilename();
     //         ObjectMetadata metadata = new ObjectMetadata();
     //         metadata.setContentType(file.getContentType());
     //         metadata.setContentLength(file.getSize());

     //         amazonS3.putObject(BUCKET_NAME, fileName, file.getInputStream(), metadata);
     //         System.out.println(2);
     //         URL resUrl = amazonS3.getUrl(BUCKET_NAME, fileName);
     //         return ResponseEntity.status(HttpStatus.OK).body(resUrl);
     //     } catch (AmazonS3Exception e) {
     //         // Log the error message and status code
     //         System.err.println("Amazon S3 error: " + e.getErrorCode());
     //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     //     } catch (Exception e) {
     //         // Log the error message and status code
     //         System.err.println("Unknown error: " + e.getMessage());
     //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     //     }
     // }


   @PostMapping("/files")
   public ResponseEntity<List<URL>> uploadFile(
       @RequestParam("file") List<MultipartFile> files,
       @RequestParam("objectId") String objectId,
       @RequestParam("objectType") String objectType) {

     String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

     CreateFileRequestDto createFileRequestDto = new CreateFileRequestDto();
     createFileRequestDto.setFileCreator(currentLoginUserId);
     createFileRequestDto.setObjectId(objectId);
     createFileRequestDto.setObjectType(objectType);

     List<URL> fileList =  fileService.uploadFile(files, createFileRequestDto);

     return ResponseEntity.status(HttpStatus.OK).body(fileList);
   }

    
//     @PostMapping("/users/{userId}/files")
//     public ResponseEntity<URL> uploadFile(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
//         URL resUrl = fileService.uploadFile(userId, file);
//         return ResponseEntity.status(HttpStatus.OK).body(resUrl);
//     }
    
    
     // @GetMapping ("/files/get")
     // public ResponseEntity<URL> getFile() {
     //     System.out.println(1);
     //     URL resUrl = amazonS3.getUrl(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
     //     System.out.println(2);
     //     amazonS3.getObject(BUCKET_NAME, "425de133-dae7-425f-852f-6b8fd03a030a.jpeg");
     //     System.out.println(3);
     //     return ResponseEntity.status(HttpStatus.OK).body(resUrl);
     // }

     // @GetMapping ("/files/get")
     // public String getFile() {
     //     try {
     //         return testvar;
     //     } catch (Exception e) {
     //         return "error";
     //     }
        
     // }
 }



