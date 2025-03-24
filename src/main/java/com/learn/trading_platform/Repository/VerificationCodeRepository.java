package com.learn.trading_platform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.trading_platform.Model.VerificationCode;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{
    public VerificationCode findByUserId(Long userId);
}
