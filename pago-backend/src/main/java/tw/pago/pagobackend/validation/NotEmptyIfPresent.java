package tw.pago.pagobackend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyIfPresentValidator.class)
public @interface NotEmptyIfPresent {


  String message() default "Field must not be empty";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

