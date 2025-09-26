package com.essjr.DinMonex.dtos;

public class LoginResponseDTO {
    private String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    // Getter e Setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
