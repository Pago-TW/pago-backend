// package tw.pago.pagobackend.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RestController;

// import tw.pago.pagobackend.dto.EmailRequestDto;
// import tw.pago.pagobackend.service.SesEmailService;

// @RestController
// public class SesEmailController {

//     @Autowired
//     private SesEmailService sesEmailService;


//     @PostMapping("/send-test-email")
//     public ResponseEntity<String> sendTestEmail() {
//         System.out.println("Sending test email...");

//         EmailRequestDto emailRequestDto = new EmailRequestDto();
//         emailRequestDto.setTo("youremail@gmail.com"); //your email
//         emailRequestDto.setSubject("Test email");
//         emailRequestDto.setBody("Hello, this is a test email.");
        
//         sesEmailService.sendEmail(emailRequestDto);
//         return ResponseEntity.ok("Test email sent.");
//     }
// }
