package com.example.note_service.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return userId -> {
            if (cause instanceof FeignException.NotFound) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId, cause);
            }
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "User service unavailable",
                    cause
            );
        };
    }
}
