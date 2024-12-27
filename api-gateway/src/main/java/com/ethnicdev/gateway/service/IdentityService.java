package com.ethnicdev.gateway.service;

import org.springframework.stereotype.Service;

import com.ethnicdev.gateway.dto.request.IntrospectRequest;
import com.ethnicdev.gateway.dto.response.ApiResponse;
import com.ethnicdev.gateway.dto.response.IntrospectResponse;
import com.ethnicdev.gateway.repository.IdentityClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {

  IdentityClient identityClient;

  public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
    return this.identityClient.introspect(IntrospectRequest.builder().token(token).build());
  }
}
