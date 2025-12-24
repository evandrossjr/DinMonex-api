package com.essjr.DinMonex.strategy;


import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class LazerStrategy implements ClassificacaoStrategy{



    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Lazer
        return desc.contains("cinema") ||
                desc.contains("show") ||
                desc.contains("evento") ||
                desc.contains("ingresso") ||
                desc.contains("bar") ||
                desc.contains("balada") ||
                desc.contains("jogo") ||
                desc.contains("steam") ||
                desc.contains("playstation") ||
                desc.contains("xbox");
    }

    @Override
    public String getCategoryName() {
        return "Lazer";
    }

}
