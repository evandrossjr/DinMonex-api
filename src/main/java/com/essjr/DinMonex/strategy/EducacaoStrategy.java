package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;

public class EducacaoStrategy implements ClassificacaoStrategy{


    @Override
    public boolean matches(TransactionRequestDTO dto) {
        if (dto.getDescription() == null) return false;

        String desc = dto.getDescription().toLowerCase();
        // Regras para Educação
        return desc.contains("curso") ||
                desc.contains("faculdade") ||
                desc.contains("universidade") ||
                desc.contains("pós") ||
                desc.contains("pós gradução") ||
                desc.contains("mensalidade") ||
                desc.contains("udemy") ||
                desc.contains("alura") ||
                desc.contains("coursera") ||
                desc.contains("edx") ||
                desc.contains("dio") ||
                desc.contains("senai") ||
                desc.contains("livro");
    }

    @Override
    public String getCategoryName() {
        return "Educação";
    }
}
