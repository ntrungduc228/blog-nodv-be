package nodv.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex, ServletWebRequest request) {
        return new ErrorResponse(404, HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequest().getRequestURI());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException ex, ServletWebRequest request) {
        return new ErrorResponse(403, HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequest().getRequestURI());
    }

    // handle extra exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerException(Exception ex, ServletWebRequest request) {
        log.info("error: " + ex.getMessage());
        return new ErrorResponse(500, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequest().getRequestURI());
    }
}
