package com.essjr.DinMonex.auth;


import com.essjr.DinMonex.security.JwtService;
import com.essjr.DinMonex.auth.dtos.LoginRequestDTO;
import com.essjr.DinMonex.auth.dtos.RegisterRequestDTO;
import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import com.essjr.DinMonex.user.enums.AppUserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }



    public void registerUser(RegisterRequestDTO dto, AppUserRole role) {
        if (appUserRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email já cadastrado.");
        }

        AppUser newUser = new AppUser();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole(role); // Define o papel passado como parâmetro

        appUserRepository.save(newUser);

    }

    /**
     * Autentica um utilizador e, se as credenciais forem válidas, gera um token JWT.
     * @param loginRequestDTO DTO contendo e-mail e senha.
     * @return Uma string contendo o token JWT.
     */
    public String login(LoginRequestDTO loginRequestDTO) {
        // O AuthenticationManager usa o JpaUserDetailsService e o PasswordEncoder
        // para validar as credenciais. Se forem inválidas, ele lança uma AuthenticationException.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        // Se a autenticação for bem-sucedida, buscamos o utilizador e geramos o token.
        var user = appUserRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalStateException("Utilizador não encontrado após autenticação bem-sucedida."));

        return jwtService.generateToken(user);
    }

}
