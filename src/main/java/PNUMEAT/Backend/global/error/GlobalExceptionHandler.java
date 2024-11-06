package PNUMEAT.Backend.global.error;

import PNUMEAT.Backend.global.error.dto.response.ApiResponse;
import PNUMEAT.Backend.global.error.response.ErrorResult;
import PNUMEAT.Backend.global.error.response.ErrorValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<?>> invalidRequestHandler(MethodArgumentNotValidException e) {
    ErrorValidationResult errorValidationResult = new ErrorValidationResult();

    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      errorValidationResult.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ApiResponse.validationErrorResponse(errorValidationResult));
  }

  @ExceptionHandler(Team24Exception.class)
  public ResponseEntity<ApiResponse<?>> team24ExceptionHandler(Team24Exception e) {
    ErrorResult errorResult = new ErrorResult(e.getStatusCode(), e.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponse.errorResponse(errorResult));
  }
}
