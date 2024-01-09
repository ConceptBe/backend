package kr.co.conceptbe.common.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlingServerErrorException(Exception exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                String.format("서버에서 알 수 없는 오류가 발생했습니다 : %s", exception.getMessage())));
    }

    @ExceptionHandler(ConceptBeException.class)
    public ResponseEntity<ErrorResponse> handlingApplicationException(ConceptBeException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.status())
            .body(new ErrorResponse(errorCode.status().value(), errorCode.message()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        Map<String, String> errors = createNotValidExceptionResponse(ex);
        return ResponseEntity.status(status).body(errors);
    }

    private static Map<String, String> createNotValidExceptionResponse(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
