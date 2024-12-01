package com.ethnicdev.identity.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ethnicdev.identity.dto.request.AuthenticationRequest;
import com.ethnicdev.identity.dto.request.IntrospectRequest;
import com.ethnicdev.identity.dto.request.LogoutRequest;
import com.ethnicdev.identity.dto.request.RefreshRequest;
import com.ethnicdev.identity.dto.response.ApiResponse;
import com.ethnicdev.identity.dto.response.AuthenticationResponse;
import com.ethnicdev.identity.dto.response.IntrospectResponse;
import com.ethnicdev.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(this.authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {
        return ApiResponse.<IntrospectResponse>builder()
                .result(this.authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        this.authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws JOSEException, ParseException {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(this.authenticationService.refreshToken(request))
                .build();
    }
}
