package com.learn.trading_platform.Service;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.User;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws Exception;

    public User findUserByEmail(String email) throws Exception;

    public User findUserById(Long userId) throws Exception;

    public User enableTwoFactorAuthentication(VERIFICATION_TYPE type, String sendTo, User user);

    public User updatePassword(User user, String newPassword);

}
