package exception;

import jakarta.servlet.http.HttpServletResponse;

public class ErrorFactory {

    public static AppException internal(String message) {
        return new AppException(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    public static AppException notFound(String message) {
        return new AppException(message, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND_ERROR");
    }

    public static AppException validation(String message) {
        return new AppException(message, HttpServletResponse.SC_BAD_REQUEST, "VALIDATION_ERROR");
    }

    public static AppException duplicate(String message) {
        return new AppException(message, HttpServletResponse.SC_CONFLICT, "DUPLICATE_ERROR"); // 409 Conflict
    }

    public static AppException unauthorized(String message) {
        return new AppException(message, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED_ERROR"); // 401
    }
    
    public static AppException forbidden(String message) {
        return new AppException(message, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN_ERROR"); // 403
    }
    
    public static AppException badRequest(String message) {
        return new AppException(message, HttpServletResponse.SC_BAD_REQUEST, "BAD_REQUEST_ERROR"); // 400
    }
    
}