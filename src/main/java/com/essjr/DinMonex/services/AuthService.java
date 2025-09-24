package com.essjr.DinMonex.services;


import com.essjr.DinMonex.dtos.AppUserRegistrationDTO;
import com.essjr.DinMonex.dtos.RegisterRequestDTO;
import com.essjr.DinMonex.model.AppUser;
import com.essjr.DinMonex.model.enuns.AppUserRole;
import com.essjr.DinMonex.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;


    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
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

}
