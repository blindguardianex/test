package ru.smartech.app.resources;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.smartech.app.exceptions.EntityAlreadyExist;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @SneakyThrows
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Map<String,String>>  handleBindException(HttpServletRequest request, ConstraintViolationException exception) {
        var errorMap = processException(
                MessageFormat.format("Недопустимый аргумент: {0}", exception.getMessage()),
                exception
        );
        return new ResponseEntity<>(
                errorMap,
                getErrorHttpHeaders(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityAlreadyExist.class)
    public ResponseEntity<Map<String,String>> entityAlreadyExist(EntityAlreadyExist exception) {
        return new ResponseEntity<>(
                processException(
                        MessageFormat.format("Сущность уже существует: {0}", exception.getMessage()),
                        exception),
                getErrorHttpHeaders(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<Map<String,String>> psqlException(PSQLException exception) {
        return new ResponseEntity<>(
                processException(
                        MessageFormat.format("Ошибка добавления в БД: {0},\n{1}", exception.getMessage()),
                        exception),
                getErrorHttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        return new ResponseEntity<>(
                processException(
                        MessageFormat.format("Недопустимый аргумент: {0}", exception.getMessage()),
                        exception),
                getErrorHttpHeaders(),
                HttpStatus.BAD_REQUEST
        );
    }

    private Map<String,String> processException(String message, Exception exception) {
        return processException(message, null, exception);
    }

    private Map<String,String> processException(String message, String detailedMessage, Exception exception) {
        log.error(message, exception);
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("Exception", exception.getClass().getName());
        errorMap.put("Error class name", getErrorClassName(exception));
        errorMap.put("Message", message);
        if (StringUtils.isNotEmpty(detailedMessage)) {
            errorMap.put("Detailed message", detailedMessage);
        }

        return errorMap;
    }

    private HttpHeaders getErrorHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    private String getErrorClassName(Exception exception) {
        return exception.getStackTrace().length>0 ?
                exception.getStackTrace()[0].getClassName() : AccessDeniedException.class.getName();
    }
}
