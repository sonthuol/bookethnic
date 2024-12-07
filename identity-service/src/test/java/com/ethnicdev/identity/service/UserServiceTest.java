package com.ethnicdev.identity.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.ethnicdev.identity.dto.request.UserCreationRequest;
import com.ethnicdev.identity.dto.response.UserResponse;
import com.ethnicdev.identity.entity.User;
import com.ethnicdev.identity.exception.AppException;
import com.ethnicdev.identity.exception.ErrorCode;
import com.ethnicdev.identity.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;

    private User user;

    private LocalDate dob;

    @BeforeEach
    void initData() {
        this.dob = LocalDate.of(2000, 1, 24);
        this.userCreationRequest = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();

        this.user = User.builder().id("cf03123482342342342").username("john").build();
    }

    @Test
    void createUser_validRequest_success() {
        when(this.userRepository.existsByUsername(anyString())).thenReturn(false);
        when(this.userRepository.save(any())).thenReturn(this.user);
        UserResponse response = this.userService.createUser(this.userCreationRequest);
        Assertions.assertThat(response.getId()).isEqualTo("cf03123482342342342");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void createUser_userExists_fail() {
        when(this.userRepository.existsByUsername(anyString())).thenReturn(true);
        AppException exception =
                assertThrows(AppException.class, () -> this.userService.createUser(userCreationRequest));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_EXISTED.getCode());
    }

    @Test
    @WithMockUser(username = "john")
    void getMyInfo_valid_success() {
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.of(this.user));
        UserResponse response = this.userService.getMyInfo();
        Assertions.assertThat(response.getId()).isEqualTo("cf03123482342342342");
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    @WithMockUser(username = "john")
    void getMyInfo_usernameNotFound_fail() {
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        AppException exception = assertThrows(AppException.class, () -> this.userService.getMyInfo());
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_EXISTED.getCode());
    }
}
