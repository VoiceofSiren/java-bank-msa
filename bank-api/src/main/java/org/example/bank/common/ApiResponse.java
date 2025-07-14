package org.example.bank.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private Error err;

    public ApiResponse(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public ApiResponse(Boolean success, String message, T data, Error err) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.err = err;
    }

    public ApiResponse() {

    }

    public ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", data));
    }

    public ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    public ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.badRequest().body(
          new ApiResponse<>(false, message, null)
        );
    }

    public ResponseEntity<ApiResponse<T>> error(
            String message,
            String errCode,
            Object details,
            String path
    ) {
        return ResponseEntity.badRequest().body(
          new ApiResponse<>(false, message, null, new Error(errCode, details, path))
        );
    }

    static class Error {
        private String code;
        private Object details;
        private String path;

        public Error() {
        }
        public Error(String code, Object details, String path) {
            this.code = code;
            this.details = details;
            this.path = path;
        }
    }
}
