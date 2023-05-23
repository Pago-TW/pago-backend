package tw.pago.pagobackend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyIfPresentValidator implements ConstraintValidator<NotEmptyIfPresent, String> {

  @Override
  public void initialize(NotEmptyIfPresent constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(value == null) {
      return true;
    }
    return !value.trim().isEmpty();
  }
}

