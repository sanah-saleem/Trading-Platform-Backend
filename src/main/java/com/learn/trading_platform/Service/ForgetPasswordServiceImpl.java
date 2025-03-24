package com.learn.trading_platform.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.ForgetPasswordToken;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Repository.ForgetPasswordRepository;

@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService{

    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;

    @Override
    public ForgetPasswordToken createToken(User user, String id, String otp, 
                                    VERIFICATION_TYPE verificationType, String sendTo) {
                
                ForgetPasswordToken token = new ForgetPasswordToken();
                token.setUser(user);
                token.setSendTo(sendTo);
                token.setOtp(otp);
                token.setVerificationType(verificationType);
                token.setId(id);
                return forgetPasswordRepository.save(token);

    }

    @Override
    public ForgetPasswordToken findById(String id) {
        Optional<ForgetPasswordToken> token = forgetPasswordRepository.findById(id);
        return token.orElse(null);
    }

    @Override
    public ForgetPasswordToken findByUser(Long userId) {
        return forgetPasswordRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgetPasswordToken forgetPasswordToken) {
        forgetPasswordRepository.delete(forgetPasswordToken);
    }
    
}
