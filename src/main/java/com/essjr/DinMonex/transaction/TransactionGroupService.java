package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.security.AuthenticationHelper;
import com.essjr.DinMonex.transaction.dtos.TransactionGroupRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionGroupResponseDTO;
import com.essjr.DinMonex.user.AppUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionGroupService {

    private final TransactionGroupRepository transactionGroupRepository;
    private final AuthenticationHelper authenticationHelper;


    public TransactionGroupService(TransactionGroupRepository transactionGroupRepository, AuthenticationHelper authenticationHelper) {
        this.transactionGroupRepository = transactionGroupRepository;
        this.authenticationHelper = authenticationHelper;
    }

    public List<TransactionGroupResponseDTO> findAllGroups(){
        AppUser currentUser = authenticationHelper.getCurrentUser();

        List<TransactionGroup> groups = transactionGroupRepository.findAllByAppUser(currentUser);

        return groups.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    public TransactionGroupResponseDTO createGroup(TransactionGroupRequestDTO dto){
        AppUser currentUser = authenticationHelper.getCurrentUser();

        TransactionGroup entity = new TransactionGroup();

        entity.setAppUser(currentUser);
        entity.setName(dto.name());
        entity.setHexColor(dto.hexColor());


        TransactionGroup saved = transactionGroupRepository.save(entity);

        return convertToResponseDTO(saved);
    }
    private TransactionGroupResponseDTO convertToResponseDTO(TransactionGroup group) {
        return new TransactionGroupResponseDTO(
                group.getId(),
                group.getName(),
                group.getHexColor()
        );
    }

}
