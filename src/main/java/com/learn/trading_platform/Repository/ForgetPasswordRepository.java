package com.learn.trading_platform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learn.trading_platform.Model.ForgetPasswordToken;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken, String>{

    ForgetPasswordToken findByUserId(Long userId);
    
}
