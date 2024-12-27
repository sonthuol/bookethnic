package com.ethnicdev.gateway.configuration;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ethnicdev.gateway.dto.response.ApiResponse;
import com.ethnicdev.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

  IdentityService identityService;

  ObjectMapper objectMapper;

  @Override
  public int getOrder() {
    return -1;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("Enter authentication filter...");
    List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || authHeader.isEmpty()) {
      return this.unauthenticated(exchange.getResponse());
    }
    String token = authHeader.get(0).replace("Bearer", "");
    this.identityService.introspect(token).subscribe(t -> log.info("Token valid: {}", t.getResult().isValid()));
    return this.identityService.introspect(token).flatMap(introspectResponse -> {
      if (!introspectResponse.getResult().isValid()) {
        return this.unauthenticated(exchange.getResponse());
      }
      return chain.filter(exchange);
    }).onErrorResume(throwable -> this.unauthenticated(exchange.getResponse()));
  }

  Mono<Void> unauthenticated(ServerHttpResponse response) {

    ApiResponse<?> apiResponse = ApiResponse.builder()
        .code(1401)
        .message("Unauthenticated")
        .build();

    String body;
    try {
      body = this.objectMapper.writeValueAsString(apiResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
  }
}
