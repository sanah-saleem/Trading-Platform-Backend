package com.learn.trading_platform.Model;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VERIFICATION_TYPE sendTo;
}
