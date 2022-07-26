package com.dtrakos.Reddit.Clone.controller;

import com.dtrakos.Reddit.Clone.dto.AuthenticationResponse;
import com.dtrakos.Reddit.Clone.dto.LoginRequest;
import com.dtrakos.Reddit.Clone.dto.RefreshTokenRequest;
import com.dtrakos.Reddit.Clone.dto.RegisterRequest;
import com.dtrakos.Reddit.Clone.model.RefreshToken;
import com.dtrakos.Reddit.Clone.service.AuthService;
import com.dtrakos.Reddit.Clone.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path ="/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping(path ="signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful", OK);

    }

    @GetMapping(path ="accountVerification/{token}")
    public ResponseEntity<String>verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);

    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully !");

    }




}
