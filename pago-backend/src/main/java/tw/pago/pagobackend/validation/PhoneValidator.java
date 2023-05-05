package tw.pago.pagobackend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{8}$");

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) {
            return false;
        }

        return PHONE_PATTERN.matcher(phone).matches();
    }
}