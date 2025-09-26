package com.essjr.DinMonex.repositories;

import com.essjr.DinMonex.model.AppUser;
import com.essjr.DinMonex.model.Transaction;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Encontra todas as transações que pertencem a um utilizador específico.
     * O Spring Data JPA cria a consulta automaticamente a partir do nome do método.
     * @param appUser O utilizador "dono" das transações.
     * @return Uma lista de transações.
     */
    List<Transaction> findAllByAppUser (AppUser appUser);

}
