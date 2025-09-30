package com.essjr.DinMonex.repositories;

import com.essjr.DinMonex.user.AppUser;
import com.essjr.DinMonex.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testes de integração para o AppUserRepository a usar Testcontainers.
 * @DataJpaTest configura um ambiente de teste focado na camada de persistência.
 * @Testcontainers ativa a integração com a biblioteca Testcontainers.
 * @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) diz ao Spring
 * para NÃO substituir a nossa fonte de dados pela base de dados em memória H2.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {

    /**
     * @Container define e gere o ciclo de vida do nosso contentor PostgreSQL.
     * Ele será iniciado antes do primeiro teste e desligado após o último.
     * A anotação deve ser aplicada a um campo estático.
     */
    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    /**
     * @DynamicPropertySource permite-nos sobrepor dinamicamente as propriedades da aplicação
     * em tempo de execução. Aqui, configuramos o Spring para se ligar à base de dados
     * PostgreSQL que está a correr no nosso contentor de teste.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    // Injeta o repositório que queremos testar.
    @Autowired
    private AppUserRepository appUserRepository;

    // Utilizador de teste que será usado em vários métodos.
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        // Limpa a base de dados antes de cada teste para garantir o isolamento.
        appUserRepository.deleteAll();

        // Cria uma instância de AppUser para ser usada nos testes.
        appUser = new AppUser();
        appUser.setName("Utilizador Teste");
        appUser.setEmail("teste@email.com");
        appUser.setPassword("hash123");
        appUser.setRole(AppUser.AppUserRole.REGULAR);
    }

    @Test
    @DisplayName("Deve salvar um utilizador com sucesso e encontrá-lo pelo ID")
    void deveSalvarUtilizadorEEncontrarPeloId() {
        // Act (Ação)
        AppUser appUserSaved = appUserRepository.save(appUser);
        Optional<AppUser> appUserFindedOpt = appUserRepository.findById(appUserSaved.getId());

        // Assert (Verificação)
        assertThat(appUserFindedOpt).isPresent();
        AppUser appUserFinded = appUserFindedOpt.get();
        assertThat(appUserFinded.getId()).isNotNull();
        assertThat(appUserFinded.getName()).isEqualTo("Utilizador Teste");
        assertThat(appUserFinded.getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve encontrar um utilizador pelo e-mail após o salvar")
    void deveEncontrarUtilizadorPorEmail() {
        // Arrange (Preparação)
        appUserRepository.save(appUser);

        // Act (Ação)
        Optional<AppUser> foundUserOptional = appUserRepository.findByEmail("teste@email.com");

        // Assert (Verificação)
        assertThat(foundUserOptional).isPresent();
        assertThat(foundUserOptional.get().getName()).isEqualTo("Utilizador Teste");
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao procurar por um e-mail que não existe")
    void deveRetornarVazioParaEmailInexistente() {
        // Act (Ação)
        Optional<AppUser> foundUserOptional = appUserRepository.findByEmail("email.inexistente@email.com");

        // Assert (Verificação)
        assertThat(foundUserOptional).isNotPresent();
    }
}

