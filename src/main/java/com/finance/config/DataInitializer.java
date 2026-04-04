package com.finance.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.finance.model.UserEntity;
import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;
import com.finance.repository.UserRepository;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== ADMIN USER SEED ==========
    @PostConstruct
    public void initAdmin() {

        if (userRepository.findByEmailAndDeletedFalse("admin@gmail.com").isEmpty()) {

            UserEntity admin = UserEntity.builder()
                    .name("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin12345"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(admin);

            System.out.println("Admin user created");
        }
    }
}