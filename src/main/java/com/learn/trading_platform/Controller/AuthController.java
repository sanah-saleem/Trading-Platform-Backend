package com.learn.trading_platform.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learn.trading_platform.Config.JwtProvider;
import com.learn.trading_platform.Model.TwoFactorOtp;
import com.learn.trading_platform.Model.User;
import com.learn.trading_platform.Repository.UserRepository;
import com.learn.trading_platform.Response.AuthResponse;
import com.learn.trading_platform.Service.CustomUserDetailsService;
import com.learn.trading_platform.Service.EmailService;
import com.learn.trading_platform.Service.TwoFactorOtpService;
import com.learn.trading_platform.Utils.OtpUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    TwoFactorOtpService twoFactorOtpService;

    @Autowired
    EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isUserPresent = userRepository.findByEmail(user.getEmail());

        if(isUserPresent != null) {
            throw new Exception("email already exists with another acccount");
        }
        
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());
        newUser.setMobile(user.getMobile());

        userRepository.save(newUser);

        //creating the authentication abject
        // Authentication auth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());

        //pass the authentication object to the security context holder 
        //this step is unnecessary in the signup stage
        // SecurityContextHolder.getContext().setAuthentication(auth);

        //no need to pass the jwt token during register
        // String jwt = JwtProvider.generateToken(auth);

        AuthResponse authResponse = new AuthResponse();
        // authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Register Successfull");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(userName, password);
        
        SecurityContextHolder.getContext().setAuthentication(auth);

        //generating jwt token
        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(userName);

        if(user.getTwoFactorAuth().isEnabled()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("two factor auth is enabled");
            authResponse.setTwoFactorAuthEnabled(true);

            //util function to generate otp
            String otp = OtpUtils.generateOTP();

            //checking if any old otp is present and then delete it
            TwoFactorOtp oldTwoFactorOtp = twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOtp != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }

            TwoFactorOtp twoFactorOtp = twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt);

            //service function call to send otp via email 
            emailService.sendVerificationOtpEmail(authUser.getEmail(), otp);

            authResponse.setSession(twoFactorOtp.getId());
            return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);

        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Login Successfull");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

    }

    //function to authenticate if the username and password is matching
    private Authentication authenticate(String userName, String password) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOTP(@PathVariable String otp, @RequestParam String id) throws Exception {
        TwoFactorOtp twoFactorOtp = twoFactorOtpService.findById(id);
        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp, otp)) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two factor authentication verified");
            authResponse.setStatus(true);
            authResponse.setJwt(twoFactorOtp.getJwt());
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        throw new Exception("Invalid OTP");
    }

}
