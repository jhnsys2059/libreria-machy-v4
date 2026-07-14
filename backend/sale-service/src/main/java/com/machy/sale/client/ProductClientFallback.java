package com.machy.sale.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProductClientFallback implements FallbackFactory<ProductClient> {

    private static final Logger log = LoggerFactory.getLogger(ProductClientFallback.class);

    @Override
    public ProductClient create(Throwable cause) {
        log.error("Product service fallback triggered: {}", cause.getMessage());
        return new ProductClient() {
            @Override
            public Map<String, Object> getProductById(String id) {
                return Map.of("success", false, "message", "Product service no disponible");
            }

            @Override
            public Map<String, Object> ajustarStock(String id, Map<String, Integer> body) {
                return Map.of("success", false, "message", "Product service no disponible");
            }
        };
    }
}
