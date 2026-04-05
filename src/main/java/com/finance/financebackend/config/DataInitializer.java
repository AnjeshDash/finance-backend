package com.finance.financebackend.config;

import com.finance.financebackend.entity.Role;
import com.finance.financebackend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("ANALYST");
        createRoleIfNotExists("VIEWER");
        log.info("✅ Default roles initialized");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            roleRepository.save(Role.builder().name(roleName).build());
            log.info("Created role: {}", roleName);
        }
    }
}