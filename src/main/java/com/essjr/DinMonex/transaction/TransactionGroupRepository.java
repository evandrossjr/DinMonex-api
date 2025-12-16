package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, Long> {

    List<TransactionGroup> findAllByAppUser(AppUser appUser);

    Optional<TransactionGroup> findByNameIgnoreCase(String name);



}
