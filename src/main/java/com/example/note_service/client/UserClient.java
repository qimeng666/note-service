package com.example.note_service.client;

import com.example.note_service.Dto.UserDto;
import com.example.note_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${user.service.url}",
        fallbackFactory = UserClientFallbackFactory.class,
        configuration = FeignConfig.class
)
public interface UserClient {

    @GetMapping("/users/{userId}")
    UserDto getById(@PathVariable("userId") Long userId);
}
