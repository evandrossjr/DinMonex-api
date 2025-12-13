package com.essjr.DinMonex.transaction;

import com.essjr.DinMonex.transaction.dtos.TransactionGroupRequestDTO;
import com.essjr.DinMonex.transaction.dtos.TransactionGroupResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/groups")
public class TransactionGropuController {

    private final TransactionGroupService transactionGroupService;

    public TransactionGropuController(TransactionGroupService transactionGroupService) {
        this.transactionGroupService = transactionGroupService;
    }


    @GetMapping
    public ResponseEntity<List<TransactionGroupResponseDTO>> getAllGroups(){
        return ResponseEntity.ok(transactionGroupService.findAllGroups());
    }

    @PostMapping
    public ResponseEntity<TransactionGroupResponseDTO> createGroup(@RequestBody TransactionGroupRequestDTO dto) {
        return ResponseEntity.ok(transactionGroupService.createGroup(dto));
    }
}
