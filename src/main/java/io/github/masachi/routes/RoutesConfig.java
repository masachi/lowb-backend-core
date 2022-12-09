package io.github.masachi.routes;

//import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

/**
 * only for demo not production scenario
 */
//@Configuration
//public class RoutesConfig {

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("path_route", r -> r.path("/get")
//                        .uri("http://httpbin.org"))
//                .route("host_route", r -> r.host("*.myhost.org")
//                        .uri("http://httpbin.org"))
//                .route("rewrite_route", r -> r.host("*.rewrite.org")
//                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)",
//                                "/${segment}"))
//                        .uri("http://httpbin.org"))
//                .route("circuitbreaker_route", r -> r.host("*.circuitbreaker.org")
//                        .filters(f -> f.circuitBreaker(c -> c.setName("slowcmd")))
//                        .uri("http://httpbin.org"))
//                .route("circuitbreaker_fallback_route", r -> r.host("*.circuitbreakerfallback.org")
//                        .filters(f -> f.circuitBreaker(c -> c.setName("slowcmd").setFallbackUri("forward:/circuitbreakerfallback")))
//                        .uri("http://httpbin.org"))
//                .route("limit_route", r -> r
//                        .host("*.limited.org").and().path("/anything/**")
//                        .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(new RedisRateLimiter(1, 2))))
//                        .uri("http://httpbin.org"))
//                .route("websocket_route", r -> r.path("/echo")
//                        .uri("ws://localhost:9000"))
//                .build();
//    }
//}
