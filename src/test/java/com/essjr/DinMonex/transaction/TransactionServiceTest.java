package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.CreditCardTransactionRequestDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a classe TransactionService.
 * @ExtendWith(MockitoExtension.class) ativa a integração do JUnit 5 com o Mockito.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionServiceTest {

    // @Mock cria uma versão "falsa" (um mock) das dependências.
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    // @InjectMocks cria uma instância real do TransactionService e injeta os mocks.
    @InjectMocks
    private TransactionService transactionService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        // Cria um utilizador de teste padrão para os nossos testes.
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("test@user.com");
    }


}

