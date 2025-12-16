package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class TransporteStrategy implements ClassificacaoStrategy{
    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        return desc.contains("uber") ||
                desc.contains("99") ||
                desc.contains("posto") ||
                desc.contains("gasolina") ||
                desc.contains("metrô");
    }

    @Override
    public String getCategoryName() {
        return "Transporte";
    }
}
