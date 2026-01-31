package com.example.thunder.controller;

import com.example.thunder.config.JWTUserData;
import com.example.thunder.config.TokenConfig;
import com.example.thunder.dto.request.LoginRequest;
import com.example.thunder.dto.request.ProfileRequest;
import com.example.thunder.dto.request.RegisterRequest;
import com.example.thunder.dto.response.LoginResponse;
import com.example.thunder.dto.response.ProfileResponse;
import com.example.thunder.dto.response.RegisterResponse;
import com.example.thunder.model.User;
import com.example.thunder.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenConfig tokenConfig;

    public AuthController(UserRepository userRepository,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          TokenConfig tokenConfig) {


        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);
        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token, user.getName(), user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User newUser = new User();
        newUser.setEmail(registerRequest.email());
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        newUser.setName(registerRequest.name());

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(newUser.getEmail(), newUser.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileRequest profileRequest,
            @AuthenticationPrincipal JWTUserData userData) {

        User user = userRepository.findById(userData.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setName(profileRequest.name());
        user.setEmail(profileRequest.email());

        userRepository.save(user);

        return ResponseEntity.ok(
                new ProfileResponse(user.getName(), user.getEmail())
        );
    }
}

