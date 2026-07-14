package com.machy.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SpaRouter {

    @Bean
    public RouterFunction<ServerResponse> spaRoutes() {
        ClassPathResource staticDir = new ClassPathResource("static/");
        return RouterFunctions.resources("/js/**", staticDir)
            .and(RouterFunctions.resources("/styles.css", staticDir))
            .and(RouterFunctions.resources("/machy-config.js", staticDir))
            .and(RouterFunctions.resources("/favicon.ico", staticDir))
            .and(RouterFunctions.resources("/remote-scan.html", staticDir))
            .and(RouterFunctions.resources("/index.html", staticDir))
            .andRoute(RequestPredicates.GET("/"),
                request -> ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(BodyInserters.fromResource(new ClassPathResource("static/index.html"))));
    }
}
