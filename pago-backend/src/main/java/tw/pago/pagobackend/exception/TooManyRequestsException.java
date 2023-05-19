package tw.pago.pagobackend.exception;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
    private ZonedDateTime createDate;
    private long secondsRemaining;

    public TooManyRequestsException(String message) {
        super(message);
    }

    // This is used to allow the front-end to calculate the time remaining before the otpCode can be resent.
    public TooManyRequestsException(String message, ZonedDateTime createDate, long secondsRemaining) {
        super(message);
        this.createDate = createDate;
        this.secondsRemaining = secondsRemaining;

    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public long getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(long secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }
}
