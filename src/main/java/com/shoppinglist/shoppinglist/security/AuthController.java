package com.shoppinglist.shoppinglist.security;

import com.shoppinglist.shoppinglist.user.UserEntity;
import com.shoppinglist.shoppinglist.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest authRequest) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(authRequest.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this username already exists!");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        newUser.setEmail(authRequest.getEmail());
        userRepository.save(newUser);

        return "User registered successfully!";
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest authRequest) {
        UserEntity user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(authRequest.getUsername());
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }
}
