package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public class PetStrategy implements ClassificacaoStrategy{

    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Pet
        return desc.contains("ração") ||
                desc.contains("granulado") ||
                desc.contains("areia") ||
                desc.contains("veterinário") ||
                desc.contains("vet");
    }

    @Override
    public String getCategoryName() {
        return "Pet";
    }
}
