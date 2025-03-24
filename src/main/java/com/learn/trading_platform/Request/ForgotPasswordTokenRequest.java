package com.learn.trading_platform.Request;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;

import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private VERIFICATION_TYPE verificationType;
}
