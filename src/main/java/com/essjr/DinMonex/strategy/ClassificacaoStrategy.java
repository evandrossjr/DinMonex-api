package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public interface ClassificacaoStrategy {


    boolean matches(TransactionRequestDTO dto);

    String getCategoryName();
}
