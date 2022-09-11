package com.example.atipera_task.Exception;
import java.util.HashMap;

public class ApiException extends Exception {
    private Integer httpCode;
    private String message;

    public ApiException(Integer httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public HashMap<String, String> getExceptionMessage() {
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put("Status", httpCode.toString());
        errorMap.put("Message", message);
        return errorMap;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
