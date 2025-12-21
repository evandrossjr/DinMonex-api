package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ComprasStrategy implements ClassificacaoStrategy{


    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Compras
        return desc.contains("roupa") ||
                desc.contains("sapato") ||
                desc.contains("tenis") ||
                desc.contains("amazon") ||
                desc.contains("mercado livre") ||
                desc.contains("shopee") ||
                desc.contains("shein");
    }

    @Override
    public String getCategoryName() {
        return "Compras";
    }
}
