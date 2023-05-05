package tw.pago.pagobackend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class NotBeforeTodayValidator implements ConstraintValidator<NotBeforeToday, Date> {

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // Let @NotNull handle null values
        }

        LocalDate inputDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return !inputDate.isBefore(today);
    }
}
