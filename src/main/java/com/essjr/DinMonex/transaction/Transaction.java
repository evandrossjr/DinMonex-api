package com.essjr.DinMonex.transaction;


import com.essjr.DinMonex.transaction.enuns.TransactionType;
import com.essjr.DinMonex.user.AppUser;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(name = "transaction_value", precision = 38, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * A LIGAÇÃO CRUCIAL PARA A SEGURANÇA:
     * Cada transação pertence a um único utilizador (AppUser).
     * A coluna 'app_user_id' no banco de dados não pode ser nula.
     * FetchType.LAZY é uma otimização de performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    public Transaction() {
    }

    public Transaction(Long id, String description, BigDecimal value, LocalDate dueDate, boolean isRecurring, TransactionType type, AppUser appUser) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.dueDate = dueDate;
        this.isRecurring = isRecurring;
        this.type = type;
        this.appUser = appUser;
    }

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
        isRecurring = recurring;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
