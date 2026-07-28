package com.essjr.DinMonex.strategy;

import com.essjr.DinMonex.IA.OllamaService;
import com.essjr.DinMonex.transaction.TransactionGroup;
import com.essjr.DinMonex.transaction.TransactionGroupRepository;
import com.essjr.DinMonex.transaction.dtos.TransactionRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoClassificationService {

    private final List<ClassificacaoStrategy> strategies;
    private final TransactionGroupRepository transactionGroupRepository;
    private final OllamaService ollamaService;

    public AutoClassificationService(List<ClassificacaoStrategy> strategies, TransactionGroupRepository transactionGroupRepository, OllamaService ollamaService) {
        this.strategies = strategies;
        this.transactionGroupRepository = transactionGroupRepository;
        this.ollamaService = ollamaService;
    }

    public TransactionGroup classify(TransactionRequestDTO dto){

        if(dto.getGroupId() != null){
            return transactionGroupRepository.findById(dto.getGroupId()).orElseThrow(()-> new RuntimeException("Grupo não encontrado"));
        }

        for (ClassificacaoStrategy strategy : strategies){
            if (strategy.matches(dto)){
                String categoryName = strategy.getCategoryName();

                Optional<TransactionGroup> groupOptional = transactionGroupRepository.findByNameIgnoreCase(categoryName);
                if(groupOptional.isPresent()){
                    return groupOptional.get();
                }
            }
        }
        String categoriaIA = ollamaService.classificar(dto.getDescription());

        return transactionGroupRepository
                .findByNameIgnoreCase(categoriaIA)
                .orElse(
                        transactionGroupRepository
                                .findByNameIgnoreCase("Outros")
                                .orElse(null)
                );
    }
}
