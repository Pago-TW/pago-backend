package tw.pago.pagobackend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {

  @Override
  public void initialize(NotBlankIfPresent constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(value == null) {
      return true;
    }
    return !value.isBlank();
  }
}
