package com.essjr.DinMonex.controllers;


import com.essjr.DinMonex.dtos.LoginRequestDTO;
import com.essjr.DinMonex.dtos.LoginResponseDTO;
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
import org.springframework.security.core.AuthenticationException;


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

    /**
     * Endpoint para autenticar um utilizador.
     * @param loginRequestDTO Corpo do pedido com e-mail e senha.
     * @return Um token JWT em caso de sucesso ou um erro 401 em caso de falha.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            String token = authService.login(loginRequestDTO);
            // Retorna o token dentro de um objeto JSON, uma prática comum.
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            // Se o AuthenticationManager lançar uma exceção (credenciais erradas),
            // retornamos um erro 401 Unauthorized.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }

}
