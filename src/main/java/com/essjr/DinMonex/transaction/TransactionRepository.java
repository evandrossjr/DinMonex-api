package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.dtos.ResumeTransactionDTO;
import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    List<Transaction> findAllByAppUserAndDueDateBetween(AppUser appUser, LocalDate startDate, LocalDate endDate);


    /**
     * NOVO MÉTODO: Encontra todas as transações recorrentes dentro de um intervalo de datas.
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


    @Query("""
            SELECT new com.essjr.DinMonex.transaction.dtos.ResumeTransactionDTO(
                COALESCE(SUM(v.value), 0),
                COALESCE(SUM(CASE WHEN v.status = 'PAID' THEN v.value ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN v.status = 'PENDING' THEN v.value ELSE 0 END), 0)    
            )
            FROM Transaction v
            WHERE v.dueDate >= :inicio AND v.dueDate <= :fim AND v.appUser =:user      
            """)
    ResumeTransactionDTO resumeTransaction(@Param("inicio") LocalDate inicio,
                                          @Param("fim") LocalDate fim,
                                           @Param("user") AppUser user);
}
