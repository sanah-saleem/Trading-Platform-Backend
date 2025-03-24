package com.learn.trading_platform.Model;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ForgetPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @OneToOne
    private User user;

    private String otp;

    private VERIFICATION_TYPE verificationType;

    private String sendTo;
    
}
