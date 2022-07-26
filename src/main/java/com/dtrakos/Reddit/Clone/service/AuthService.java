package com.dtrakos.Reddit.Clone.service;


import com.dtrakos.Reddit.Clone.dto.AuthenticationResponse;
import com.dtrakos.Reddit.Clone.dto.LoginRequest;
import com.dtrakos.Reddit.Clone.dto.RefreshTokenRequest;
import com.dtrakos.Reddit.Clone.dto.RegisterRequest;
import com.dtrakos.Reddit.Clone.exceptions.SpringRedditException;
import com.dtrakos.Reddit.Clone.model.NotificationEmail;
import com.dtrakos.Reddit.Clone.model.User;
import com.dtrakos.Reddit.Clone.model.VerificationToken;
import com.dtrakos.Reddit.Clone.repository.UserRepository;
import com.dtrakos.Reddit.Clone.repository.VerificationTokenRepository;
import com.dtrakos.Reddit.Clone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.dtrakos.Reddit.Clone.UtilityClass.Constants.ACTIVATION_EMAIL;

@Service
@AllArgsConstructor
public class AuthService {

    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user= new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token=generateVerificationToken(user);
        String message= mailContentBuilder.build("Thank you for signing up to Spring Reddit, please click on the below url to activate your account : "
                + ACTIVATION_EMAIL + "/" + token);

        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), message));

    }
    @Transactional(readOnly = true)
    public User getCurrentUser(){
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
    }


    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
        verificationTokenOptional.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationTokenOptional.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User Not Found with id - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }



    public AuthenticationResponse login(LoginRequest loginRequest) {
      Authentication authenticate=  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
        ,loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authenticate);
      String token= jwtProvider.generateToken(authenticate);

      return  AuthenticationResponse.builder()
              .authenticationToken(token)
              .refreshToken(refreshTokenService.generateRefreshToken().getToken())
              .expiresAt(String.valueOf(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis())))
              .username(loginRequest.getUsername())
              .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token =jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(String.valueOf(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis())))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }



}
