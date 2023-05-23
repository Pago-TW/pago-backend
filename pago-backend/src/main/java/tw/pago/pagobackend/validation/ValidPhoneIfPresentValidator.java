package tw.pago.pagobackend.validation;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPhoneIfPresentValidator implements
    ConstraintValidator<ValidPhoneIfPresent, String> {
  private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{8}$");

  @Override
  public void initialize(ValidPhoneIfPresent constraintAnnotation) {
  }

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
    if (phone == null) {
      return true;
    }

    String trimmedPhone = phone.trim();

    if (trimmedPhone.isEmpty()) {
      return false;
    }

    return PHONE_PATTERN.matcher(trimmedPhone).matches();
  }

}
