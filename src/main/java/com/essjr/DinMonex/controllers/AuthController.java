package com.essjr.DinMonex.controllers;


import com.essjr.DinMonex.dtos.RegisterRequestDTO;
import com.essjr.DinMonex.model.enuns.AppUserRole;
import com.essjr.DinMonex.services.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO dto){
        try {
            authService.registerUser(dto, AppUserRole.REGULAR);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(e.getMessage());        }
    }

}
