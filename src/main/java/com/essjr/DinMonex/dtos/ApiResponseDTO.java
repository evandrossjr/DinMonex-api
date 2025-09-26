package com.essjr.DinMonex.dtos;

public class ApiResponseDTO {
    private String message;

    public ApiResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
