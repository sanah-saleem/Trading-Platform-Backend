package com.learn.trading_platform.Controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.trading_platform.Domain.VERIFICATION_TYPE;
import com.learn.trading_platform.Model.ForgetPasswordToken;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Model.VerificationCode;
import com.learn.trading_platform.Request.ForgotPasswordTokenRequest;
import com.learn.trading_platform.Request.ResetPasswordRequest;
import com.learn.trading_platform.Response.ApiResponse;
import com.learn.trading_platform.Response.AuthResponse;
import com.learn.trading_platform.Service.EmailService;
import com.learn.trading_platform.Service.ForgetPasswordService;
import com.learn.trading_platform.Service.UserService;
import com.learn.trading_platform.Service.VerificationCodeService;
import com.learn.trading_platform.Utils.OtpUtils;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgetPasswordService forgetPasswordService;

    //To get the user profile
    @GetMapping("/api/users/Profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //To send the otp for the TFA
    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOTP(
            @RequestHeader("Authorization") String jwt, 
            @PathVariable VERIFICATION_TYPE verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByuserId(user.getId());
        if(verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }
        if(verificationType.equals(VERIFICATION_TYPE.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        return new ResponseEntity<>("Verification OTP sent successfully", HttpStatus.OK);
    }

    //To verify otp for TFA
    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
                    @PathVariable String otp,    
                    @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByuserId(user.getId());
        String sendTo = verificationCode.getVerificationType().
                    equals(VERIFICATION_TYPE.EMAIL) ? verificationCode.getEmail() : verificationCode.getMobile();
        Boolean isVerified = verificationCode.getOtp().equals(otp);
        if(isVerified) {
            User verifiedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(verifiedUser, HttpStatus.OK);
        }
        throw new Exception("Wrong otp");
    }

    //For sending otp for reset password
    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendforgotPasswordOTP( 
            @RequestBody ForgotPasswordTokenRequest request) throws Exception {

        User user = userService.findUserByEmail(request.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgetPasswordToken token = forgetPasswordService.findByUser(user.getId());
        if(token == null) {
            token = forgetPasswordService.createToken(user, id, otp, request.getVerificationType(), request.getSendTo());
        }

        if(request.getVerificationType().equals(VERIFICATION_TYPE.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset otp sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //To verify the otp for reset password
    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
                    @RequestParam String id,
                    @RequestBody ResetPasswordRequest request,
                    @RequestHeader("Authorization") String jwt) throws Exception {

        ForgetPasswordToken forgetPasswordToken = forgetPasswordService.findById(id);

        boolean isVerified = forgetPasswordToken.getOtp().equals(request.getOtp());

        if(isVerified) {
            userService.updatePassword(forgetPasswordToken.getUser(), request.getPassword());
            ApiResponse response = new ApiResponse();
            response.setMessage("Password updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        throw new Exception("Wrong OTP");

    }
    
}
