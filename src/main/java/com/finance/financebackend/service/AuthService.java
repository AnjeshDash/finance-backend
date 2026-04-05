package com.finance.financebackend.service;

import com.finance.financebackend.dto.request.LoginRequestDTO;
import com.finance.financebackend.dto.request.RegisterRequestDTO;
import com.finance.financebackend.dto.response.AuthResponseDTO;
import com.finance.financebackend.entity.Role;
import com.finance.financebackend.entity.User;
import com.finance.financebackend.repository.RoleRepository;
import com.finance.financebackend.repository.UserRepository;
import com.finance.financebackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role role = roleRepository.findByName(request.getRoleName().toUpperCase())
                .orElseThrow(() -> new RuntimeException(
                        "Role not found: " + request.getRoleName()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status("active")
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", request.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), role.getName());

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(role.getName())
                .message("Registration successful!")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getStatus().equals("active")) {
            throw new RuntimeException("Account is inactive. Contact admin.");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );

        log.info("User logged in: {}", request.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().getName())
                .message("Login successful!")
                .build();
    }
}