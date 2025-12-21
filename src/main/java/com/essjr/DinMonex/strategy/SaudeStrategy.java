package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public class SaudeStrategy implements ClassificacaoStrategy{


    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Saúde
        return desc.contains("farmacia") ||
                desc.contains("remedio") ||
                desc.contains("plano de saude") ||
                desc.contains("consulta") ||
                desc.contains("exame") ||
                desc.contains("hospital") ||
                desc.contains("academia") ||
                desc.contains("clinica");
    }

    @Override
    public String getCategoryName() {
        return "Saúde";
    }
}
