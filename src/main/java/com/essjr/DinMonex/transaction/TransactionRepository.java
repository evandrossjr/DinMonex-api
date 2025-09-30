package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Encontra todas as transações que pertencem a um utilizador específico.
     * O Spring Data JPA cria a consulta automaticamente a partir do nome do método.
     *
     * @param appUser O utilizador "dono" das transações.
     * @return Uma lista de transações.
     */
    List<Transaction> findAllByAppUser(AppUser appUser);


    /**
     * NOVO MÉTODO: Encontra todas as transações recorrentes dentro de um intervalo de datas.
     * @param isRecurring Deve ser 'true'.
     * @param startDate A data de início do período.
     * @param endDate A data de fim do período.
     * @return Uma lista de transações recorrentes.
     */
    List<Transaction> findByIsRecurringTrueAndDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * NOVO MÉTODO: Verifica se uma transação com a mesma descrição, data e dono já existe.
     * Usado para evitar a criação de duplicados.
     *
     * @param description A descrição da transação.
     * @param dueDate     A data de vencimento da transação.
     * @param appUser     O utilizador dono da transação.
     * @return 'true' se a transação já existir, 'false' caso contrário.
     */
    boolean existsByDescriptionAndDueDateAndAppUser(String description, LocalDate dueDate, AppUser appUser);

    /**
     * Encontra todas as transações de um utilizador específico e de um tipo específico.
     */
    List<Transaction> findAllByAppUserAndType(AppUser appUser, TransactionType type);

    /**
     * Encontra uma transação pelo seu ID, mas apenas se pertencer ao utilizador fornecido.
     * Este é um método de segurança crucial para garantir o isolamento dos dados.
     */
    Optional<Transaction> findByIdAndAppUser(Long id, AppUser appUser);
}
