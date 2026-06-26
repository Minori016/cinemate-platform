package com.cinema.cinemate.exception;

import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.enums.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(Exception exception) {
        log.error("Exception caught by GlobalExceptionHandler: ", exception);
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse<Object>> handlingMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error("MaxUploadSizeExceededException: ", exception);
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(400);
        apiResponse.setMessage("Kích thước ảnh vượt quá giới hạn cho phép. Vui lòng tải lên ảnh dưới 5MB.");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Object>> handlingDataIntegrityViolationException(
            DataIntegrityViolationException exception) {
        log.error("DataIntegrityViolationException caught: ", exception);
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage("Database constraint violation: This record is in use and cannot be modified/deleted.");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiResponse<Object>> handlingFormatAndParse(Exception exception) {
        log.error("Format/Parse Exception caught: ", exception);
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(400);
        apiResponse.setMessage("Dữ liệu gửi lên không đúng định dạng. (Ví dụ: sai định dạng UUID, ngày giờ).");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getFieldError().getDefaultMessage();
        
        ApiResponse<Object> apiResponse = new ApiResponse<>();

        apiResponse.setCode(1001); // INVALID_KEY code as generic validation code
        apiResponse.setMessage(errorMessage);

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
