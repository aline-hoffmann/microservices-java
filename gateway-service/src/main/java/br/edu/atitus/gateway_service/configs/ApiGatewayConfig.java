package br.edu.atitus.gateway_service.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

	@Bean
	RouteLocator buildRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				// Rota de teste para httpbin
				.route(p -> p.path("/get")
						.filters(f -> f.addRequestHeader("X-USER-NAME", "username")
								.addRequestParameter("name", "fulano"))
						.uri("http://httpbin.org:80"))
				// Rotas públicas de produtos
				.route(p -> p.path("/products/**").uri("lb://product-service"))
				// Rotas protegidas de produtos (requerem autenticação)
				.route(p -> p.path("/ws/products/**").uri("lb://product-service"))
				// Serviço de conversão de moedas
				.route(p -> p.path("/currency/**").uri("lb://currency-service"))
				// Serviço de saudação
				.route(p -> p.path("/greeting/**").uri("lb://greeting-service"))
				// Serviço de autenticação
				.route(p -> p.path("/auth/**").uri("lb://auth-service"))
				// Rotas protegidas de pedidos (requerem autenticação)
				.route(p -> p.path("/ws/orders/**").uri("lb://order-service"))
				.build();
	}

}
