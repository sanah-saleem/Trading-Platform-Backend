package com.learn.trading_platform.Service;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.ForgetPasswordToken;
import com.learn.trading_platform.Model.User;

public interface ForgetPasswordService {

    ForgetPasswordToken createToken(User user, String id, String otp, VERIFICATION_TYPE verificationType, String sendTo);

    ForgetPasswordToken findById(String id);

    ForgetPasswordToken findByUser(Long userId);

    void deleteToken(ForgetPasswordToken forgetPasswordToken);
    
}
