package com.essjr.DinMonex.user;


import com.essjr.DinMonex.user.enums.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
        appUser = new AppUser();
        appUser.setName("Utilizador Teste");
        appUser.setEmail("teste@email.com");
        appUser.setPassword("hash123");
        appUser.setRole(AppUserRole.REGULAR);
    }

    @Test
    @DisplayName("Deve salvar e buscar usuário por ID")
    void deveSalvarEBuscarPorId() {
        AppUser salvo = appUserRepository.save(appUser);
        var encontrado = appUserRepository.findById(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void deveEncontrarPorEmail() {
        appUserRepository.save(appUser);
        var encontrado = appUserRepository.findByEmail("teste@email.com");
        assertThat(encontrado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar vazio para email inexistente")
    void deveRetornarVazio() {
        var encontrado = appUserRepository.findByEmail("naoexiste@email.com");
        assertThat(encontrado).isEmpty();
    }
}
