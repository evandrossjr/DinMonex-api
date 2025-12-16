package com.essjr.DinMonex.transaction.dtos;


import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber os dados de uma nova transação de cartão de crédito do frontend.
 * Contém os campos básicos de uma transação mais o número total de parcelas.
 */
public class CreditCardTransactionRequestDTO extends TransactionRequestDTO {


    private Integer totalInstallments;

    public CreditCardTransactionRequestDTO() {
    }


    public Integer getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }


}
