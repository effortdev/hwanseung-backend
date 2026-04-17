package com.hwanseung.backend.domain.user.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pay_balance")
public class PayBalance {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hwanseung_pay")
    private int hwanseungPay;

    private String username;
}