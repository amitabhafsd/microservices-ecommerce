package com.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        implements GatewayFilter {
    //Custom Gateway Filters can also be implemented using: AbstractGatewayFilterFactory
    //Instead of route filters, many companies use: GlobalFilter

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // Allow auth endpoints
        if (path.contains("/auth")) {
            return chain.filter(exchange);
        }

        List<String> headers = exchange
                .getRequest()
                .getHeaders()
                .get("Authorization");


        if (headers == null || headers.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


        String token = headers.get(0).replace("Bearer ", "");


        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
