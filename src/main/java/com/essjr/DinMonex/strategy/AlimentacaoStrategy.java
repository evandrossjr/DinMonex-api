package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AlimentacaoStrategy implements ClassificacaoStrategy{

    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Alimentação
        return desc.contains("ifood") ||
                desc.contains("mercado") ||
                desc.contains("pão") ||
                desc.contains("fast-food") ||
                desc.contains("restaurante");
    }

    @Override
    public String getCategoryName() {
        return "Alimentação";
    }
}
