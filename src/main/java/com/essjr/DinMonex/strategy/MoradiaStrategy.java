package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public class MoradiaStrategy implements ClassificacaoStrategy{


    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Moradia
        return desc.contains("aluguel") ||
                desc.contains("condominio") ||
                desc.contains("energia") ||
                desc.contains("luz") ||
                desc.contains("energia") ||
                desc.contains("água") ||
                desc.contains("internet") ||
                desc.contains("gás") ||
                desc.contains("iptu");
    }

    @Override
    public String getCategoryName() {
        return "Moradia";
    }
}
