package com.learn.trading_platform.Service;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Model.VerificationCode;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VERIFICATION_TYPE verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByuserId(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);
    
}
