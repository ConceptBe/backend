package kr.co.conceptbe.common.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlingServerErrorException(Exception exception) {
        log.error("서버에서 알 수 없는 오류가 발생했습니다.", exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR.value(),
                        String.format("서버에서 알 수 없는 오류가 발생했습니다 : %s", exception.getMessage())));
    }

    @ExceptionHandler(ConceptBeException.class)
    public ResponseEntity<ErrorResponse> handlingApplicationException(ConceptBeException exception) {
        log.warn("사용자 Exception이 발생했습니다.", exception);
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

    private Map<String, String> createNotValidExceptionResponse(MethodArgumentNotValidException ex) {
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        return allErrors.stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        ObjectError::getDefaultMessage
                ));
    }
}
