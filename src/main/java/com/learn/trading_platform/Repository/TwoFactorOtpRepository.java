package com.learn.trading_platform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.trading_platform.Model.TwoFactorOtp;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp, String>{
    TwoFactorOtp findByUserId(Long userId);
}
