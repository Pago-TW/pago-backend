package tw.pago.pagobackend.service.impl;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.PhoneVerificationDao;
import tw.pago.pagobackend.dto.PhoneVerificationDto;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.ConflictException;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.service.PhoneVerificationService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class PhoneVerificationServiceImpl implements PhoneVerificationService {
    
    private final UuidGenerator uuidGenerator;
    private final PhoneVerificationDao phoneVerificationDao;
    private final UserService userService;
    private final OtpService otpService;

    @Override
    @Transactional
    public boolean verifyPhone(String userId, ValidatePhoneRequestDto validatePhoneRequestDto) {
        String phone = validatePhoneRequestDto.getPhone();
        boolean isValid = otpService.validateOtp(validatePhoneRequestDto);

        if (!isValid) {
            throw new BadRequestException("Invalid OTP or OTP expired");
        }

        // Fetch the existing phone verification entry for the user
        PhoneVerification phoneVerification = phoneVerificationDao.getPhoneVerificationByUserId(userId);

        // If the entry already exists, throw an exception because this scenario should not occur
        if (phoneVerification != null) {
            throw new BadRequestException(phone + " is already verified (this should not happen and something must have went wrong)");
        }

        // Fetch the user that's being verified
        User verifier = userService.getUserById(userId);
        
        boolean isPhoneAlreadyRegistered = userService.isPhoneAlreadyRegistered(phone);
        
        // If the phone number is already registered, throw an exception
        if (isPhoneAlreadyRegistered) {
            throw new ConflictException(phone + " is already registered");
        }

        // Generate a unique verification ID
        String verificationId = uuidGenerator.getUuid();

        // Build a new phone verification DTO with the provided data
        PhoneVerificationDto phoneVerificationDto = PhoneVerificationDto.builder()
            .verificationId(verificationId)
            .userId(userId)
            .phone(phone)
            .isPhoneVerified(true)
            .build();

        // Save the new phone verification DTO into the database
        phoneVerificationDao.createPhoneVerification(phoneVerificationDto);



        // If the user does not have a phone number already set, we update it
        if (verifier.getPhone() == null) {
            // Build a DTO to update user's phone number
            UpdateUserRequestDto updateUserRequestDto = UpdateUserRequestDto.builder()
                .phone(phone)
                .build();
            // Fill in the rest of the DTO fields with the existing data
            updateUserRequestDto.fillEmptyFieldsWithOldData(verifier);

            // Update the user with the new phone number
            userService.updateUser(updateUserRequestDto);
        }

        System.out.println("Phone verification created (...verifyPhone)");
        return true;
    }

    @Override
    public PhoneVerification getPhoneVerificationByUserId(String userId) {
        return phoneVerificationDao.getPhoneVerificationByUserId(userId);
    }

}
