package com.essjr.DinMonex.transaction.dtos;

import java.math.BigDecimal;

public class ResumeTransactionDTO{

    private BigDecimal totalGeral;
    private BigDecimal totalPago;
    private BigDecimal totalPendente;

    public ResumeTransactionDTO(BigDecimal totalGeral, BigDecimal totalPago, BigDecimal totalPendente) {
        this.totalGeral = totalGeral;
        this.totalPago = totalPago;
        this.totalPendente = totalPendente;
    }


    public BigDecimal getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getTotalPendent() {
        return totalPendente;
    }

    public void setTotalPendent(BigDecimal totalPendent) {
        this.totalPendente = totalPendent;
    }
}
