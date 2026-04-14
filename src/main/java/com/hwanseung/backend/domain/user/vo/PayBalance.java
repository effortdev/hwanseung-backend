package com.hwanseung.backend.domain.user.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pay_balance")
@Data
public class PayBalance {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "hwanseung_pay")
    private int hwanseungPay;
}