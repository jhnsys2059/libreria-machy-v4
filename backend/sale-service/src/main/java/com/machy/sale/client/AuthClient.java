package com.machy.sale.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${app.auth-service-url}", fallbackFactory = AuthClientFallback.class)
public interface AuthClient {

    @GetMapping("/api/auth/user/id/{id}")
    Map<String, Object> getUserById(@PathVariable("id") String id);

    @GetMapping("/api/auth/user/{username}")
    Map<String, Object> getUserByUsername(@PathVariable("username") String username);
}
