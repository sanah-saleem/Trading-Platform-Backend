package com.learn.trading_platform.Service;

import com.learn.trading_platform.Model.TwoFactorOtp;
import com.learn.trading_platform.Model.User;

public interface TwoFactorOtpService {

    TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt);

    TwoFactorOtp findByUser(Long userId);

    TwoFactorOtp findById(String id);

    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp);

    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);
    
}
