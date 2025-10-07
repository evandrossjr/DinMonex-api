package com.essjr.DinMonex.transaction.dtos;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para enviar os dados de uma transação para o frontend.
 * Expõe apenas os campos que o frontend precisa de ver.
 */
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private boolean isRecurring;
    private String type; // É bom enviar o tipo como String
    private Integer totalInstallments;

    // Uma lista de DTOs de parcela.
    private List<InstallmentDTO> installments;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public boolean isRecurring() {
        return isRecurring;
    }
    public void setRecurring(boolean recurring) {
        this.isRecurring = recurring;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getTotalInstallments() {
        return totalInstallments;
    }
    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }
    public List<InstallmentDTO> getInstallments() {
        return installments;
    }
    public void setInstallments(List<InstallmentDTO> installments) {
        this.installments = installments;
    }
}