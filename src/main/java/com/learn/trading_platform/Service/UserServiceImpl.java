package com.learn.trading_platform.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learn.trading_platform.Config.JwtProvider;
import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.TwoFactorAuth;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        return findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new Exception("User not found");
        }
        return user.get();
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public User enableTwoFactorAuthentication(VERIFICATION_TYPE type, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(type);

        user.setTwoFactorAuth(twoFactorAuth);
        return userRepository.save(user);
    }
    
}
