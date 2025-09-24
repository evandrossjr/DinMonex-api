package com.essjr.DinMonex.repositories;

import com.essjr.DinMonex.model.AppUser;
import com.essjr.DinMonex.model.enuns.AppUserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    @DisplayName("Deve salvar um usuário com sucesso e retornar pelo ID")
    void SaveUserAndFindById() {
        AppUser appUser = new AppUser();
        appUser.setName("Usuário Teste");
        appUser.setEmail("teste@email.com");
        appUser.setPassword("hash123");
        appUser.setRole(AppUserRole.REGULAR);


        AppUser appUserSaved = appUserRepository.save(appUser);
        Optional<AppUser> appUserFindedOpt = appUserRepository.findById(appUserSaved.getId());

        assertThat(appUserFindedOpt).isPresent();

        AppUser appUserFinded = appUserFindedOpt.get();
        assertThat(appUserFinded.getId()).isNotNull();
        assertThat(appUserFinded.getName()).isEqualTo("Usuário Teste");
        assertThat(appUserFinded.getEmail()).isEqualTo("teste@email.com");
        assertThat(appUserFinded.getPassword()).isEqualTo("hash123");
        assertThat(appUserFinded.getRole()).isEqualTo(AppUserRole.REGULAR);
    }
}