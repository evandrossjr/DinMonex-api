package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public class StreamingStrategy implements ClassificacaoStrategy{


    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Streaming
        return desc.contains("netflix") ||
                desc.contains("spotify") ||
                desc.contains("prime video") ||
                desc.contains("amazon prime") ||
                desc.contains("disney") ||
                desc.contains("hbo") ||
                desc.contains("globoplay") ||
                desc.contains("youtube premium") ||
                desc.contains("deezer") ||
                desc.contains("apple tv");
    }

    @Override
    public String getCategoryName() {
        return "Streaming";
    }
}
