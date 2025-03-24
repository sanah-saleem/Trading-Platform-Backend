package com.learn.trading_platform.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learn.trading_platform.Model.TwoFactorOtp;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Repository.TwoFactorOtpRepository;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService{

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    public TwoFactorOtp createTwoFactorOtp(User user, String otp, String jwt) {

        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        TwoFactorOtp twoFactorOtp = new TwoFactorOtp();

        twoFactorOtp.setUser(user);
        twoFactorOtp.setOtp(otp);
        twoFactorOtp.setJwt(jwt);
        twoFactorOtp.setId(id);

        return twoFactorOtpRepository.save(twoFactorOtp);
    }

    @Override
    public TwoFactorOtp findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOtp findById(String id) {
        return twoFactorOtpRepository.findById(id).orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp) {
        return twoFactorOtp.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp) {
        twoFactorOtpRepository.delete(twoFactorOtp);
    }
    
}
