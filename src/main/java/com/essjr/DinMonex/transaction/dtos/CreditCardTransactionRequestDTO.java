package com.essjr.DinMonex.transaction.dtos;


import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber os dados de uma nova transação de cartão de crédito do frontend.
 * Contém os campos básicos de uma transação mais o número total de parcelas.
 */
public class CreditCardTransactionRequestDTO {

    private String description;
    private BigDecimal value;
    private LocalDate dueDate;
    private Integer totalInstallments;

    public CreditCardTransactionRequestDTO() {
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

    public Integer getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }
}
