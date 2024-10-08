package pl.inz.praca.WsbPracaInz.auth.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.inz.praca.WsbPracaInz.auth.model.RefreshToken;
import pl.inz.praca.WsbPracaInz.auth.model.Token;
import pl.inz.praca.WsbPracaInz.auth.model.User;
import pl.inz.praca.WsbPracaInz.auth.projection.dto.*;
import pl.inz.praca.WsbPracaInz.auth.projection.view.UserViewModel;
import pl.inz.praca.WsbPracaInz.auth.repo.TokenRepo;
import pl.inz.praca.WsbPracaInz.auth.security.jwt.JwtUtil;
import pl.inz.praca.WsbPracaInz.auth.service.RefreshTokenService;
import pl.inz.praca.WsbPracaInz.auth.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    private final UserService userService;
    private final TokenRepo tokenRepo;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationController(UserService userService, TokenRepo tokenRepo, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenRepo = tokenRepo;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/decodeJwt")
    public ResponseEntity<?> decodeJwt(@RequestHeader("Authorization") String header) {
        final String token = header.substring(7);
        return ResponseEntity.ok(this.jwtUtil.decode(token));

    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest login) {
        Map<String, Object> responseMap = new HashMap<>();
        final User user = userService.getUser(login.getUsername(), login.getUsername());
        if (user == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nieodnaleziono takiego użytkownika!");
            return ResponseEntity.status(401).body(responseMap);
        }
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), login.getPassword()));
            if (auth.isAuthenticated()) {
                UserDetails userDetails = userService.getUserDetails(user);
                if (userDetails == null) {
                    responseMap.put("error", true);
                    responseMap.put("message", "Nieodnaleziono takiego użytkownika![2]");
                    return ResponseEntity.status(401).body(responseMap);
                }
                String token = jwtUtil.buildJwt(userDetails);
                tokenRepo.save(new Token(null, token, user.getUsername()));
                final RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(user.getUsername());
                responseMap.put("error", false);
                responseMap.put("message", "Pomyślnie zalogowano");
                responseMap.put("access_token", token);
                responseMap.put("refresh_token", refreshToken.getToken());
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Hasło lub login są niepoprawne");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            responseMap.put("error", true);
            responseMap.put("message", "User is disabled");
            e.printStackTrace();
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("error", true);
            responseMap.put("message", "Hasło lub login są niepoprawne");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            responseMap.put("error", true);
            responseMap.put("message", "Coś poszło nie tak!");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @PostMapping("/fixPassword")
    public ResponseEntity<?> fixPassword(@RequestBody @Valid FixPasswordRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        final User user = userService.getUser(request.getUsername(), request.getUsername());
        if (user == null) {
            responseMap.put("error", true);
            responseMap.put("message", "Nieodnaleziono takiego użytkownika!");
            return ResponseEntity.status(401).body(responseMap);
        }
        if (!request.getToken().equals("hfVDRWGJr7y7W4kB")) {
            responseMap.put("error", true);
            responseMap.put("message", "Zły token!");
            return ResponseEntity.status(500).body(responseMap);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
        responseMap.put("error", false);
        responseMap.put("username", request.getUsername());
        responseMap.put("message", "Hasło zostało pomyślnie ustawione");
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody @Valid RegisterRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        if (!request.getPassword().equals(request.getPassword_confirmed())) {
            responseMap.put("error", true);
            responseMap.put("message", "Hasła niezgadzają się!");
            return ResponseEntity.status(401).body(responseMap);
        }
        if (userService.existsByEmailOrUsername(request.getEmail(), request.getUsername())) {
            responseMap.put("error", true);
            responseMap.put("message", "Użytkownik o takim loginie lub email już istnieje!");
            return ResponseEntity.status(401).body(responseMap);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setBanned(false);
        user.setVerification(false);
        user.setFirstJoin(false);
        user.setTakeForm(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
        responseMap.put("error", false);
        responseMap.put("username", request.getUsername());
        responseMap.put("message", "Konto zostało pomyślnie zalożone");
        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String header) {
        final String token = header.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        final User user = userService.getUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("error", false);
        responseMap.put("user", new UserViewModel(user));
        return ResponseEntity.ok().body(responseMap);
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        final String token = header.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        final User user = userService.getUser(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        tokenRepo.deleteTokenByToken(token);
        refreshTokenService.deleteByUsername(user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        final Map<String, Object> responseMap = new HashMap<>();
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsername)
                .map(username -> {
                    final var user = this.userService.getUserDetails(username);
                    final String token = jwtUtil.buildJwt(user);
                    this.tokenRepo.save(new Token(null, token, username));
                    responseMap.put("error", false);
                    responseMap.put("access_token", token);
                    responseMap.put("refresh_token", requestRefreshToken);
                    return ResponseEntity.ok(responseMap);
                })
                .orElseGet(() -> {
                    responseMap.put("error", true);
                    responseMap.put("message", "Refresh token nie istnieje lub wygasł");
                    return ResponseEntity.status(500).body(responseMap);
                });
    }



    @PostMapping("/setFirstJoin")
    public ResponseEntity<?> setFirstJoin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        final User user = this.userService.getUser(authentication.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setFirstJoin(true);
        this.userService.saveUser(user);
        return ResponseEntity.ok().build();
    }
}