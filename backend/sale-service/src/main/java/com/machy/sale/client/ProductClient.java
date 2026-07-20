package com.machy.sale.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "product-service", url = "${PRODUCT_SERVICE_URL:http://localhost:8082}", fallbackFactory = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Map<String, Object> getProductById(@PathVariable("id") String id);

    @PutMapping("/api/products/{id}/stock")
    Map<String, Object> ajustarStock(@PathVariable("id") String id, @RequestBody Map<String, Integer> body);
}
