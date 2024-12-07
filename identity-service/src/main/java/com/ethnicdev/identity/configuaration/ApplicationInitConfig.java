package com.ethnicdev.identity.configuaration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ethnicdev.identity.constant.PredefinedRole;
import com.ethnicdev.identity.entity.Role;
import com.ethnicdev.identity.entity.User;
import com.ethnicdev.identity.repository.RoleRepository;
import com.ethnicdev.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                Set<Role> roles = new HashSet<Role>();
                roles.add(adminRole);
                User user = User.builder()
                        .username("admin")
                        .password(this.passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default passwird: admin, please change it");
            }
        };
    }
}
