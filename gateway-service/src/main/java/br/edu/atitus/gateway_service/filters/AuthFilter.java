package br.edu.atitus.gateway_service.filters;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import br.edu.atitus.gateway_service.components.JwtUtil;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

	private static final List<String> AUTH_REQUIRED_PATHS = List.of("/ws/");

	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		var httpRequest = exchange.getRequest();
		String requestPath = httpRequest.getURI().getPath();

		if (!AUTH_REQUIRED_PATHS.stream().anyMatch(requestPath::startsWith))
			return chain.filter(exchange);

		String authorizationHeader = httpRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			Claims tokenClaims = JwtUtil.validateToken(token);
			if (tokenClaims != null) {
				ServerHttpRequest enrichedRequest = httpRequest.mutate()
						.header("X-User-Id", String.valueOf(tokenClaims.get("id", Long.class)))
						.header("X-User-Type", String.valueOf(tokenClaims.get("type", Integer.class)))
						.header("X-User-Email", tokenClaims.get("email", String.class))
						.build();
				return chain.filter(exchange.mutate().request(enrichedRequest).build());
			}
		}

		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}
}
