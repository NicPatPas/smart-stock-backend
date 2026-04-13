package com.smartstock.backend.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegistrationRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        RoleName selectedRole = request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")
                ? RoleName.ROLE_ADMIN
                : RoleName.ROLE_USER;

        Role role = roleRepository.findByName(selectedRole)
                .orElseThrow(() -> new IllegalStateException("Required role not found: " + selectedRole));

        User user = new User(request.getUsername().trim(), passwordEncoder.encode(request.getPassword()), LocalDateTime.now(), LocalDateTime.now());
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);
    }
}
