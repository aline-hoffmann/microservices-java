package br.edu.atitus.product_service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.product_service.clients.CurrencyClient;
import br.edu.atitus.product_service.clients.CurrencyResponse;
import br.edu.atitus.product_service.entities.ProductEntity;
import br.edu.atitus.product_service.repositories.ProductRepository;

@RestController
@RequestMapping("products")
public class OpenProductController {
	
	private final ProductRepository repository;
	private final CurrencyClient currencyClient;
	private final CacheManager cacheManager;

	public OpenProductController(ProductRepository repository, CurrencyClient currencyClient, CacheManager cacheManager) {
		super();
		this.repository = repository;
		this.currencyClient = currencyClient;
		this.cacheManager = cacheManager;
	}
	
	@Value("${server.port}")
	private int serverPort;
	
	@GetMapping("/{idProduct}/{targetCurrency}")
	public ResponseEntity<ProductEntity> getProduct(
			@PathVariable Long idProduct,
			@PathVariable String targetCurrency
 			) throws Exception {
		
		ProductEntity product = repository.findById(idProduct)
				.orElseThrow(() -> new Exception("Product not found"));
		
		String dataSource = "None";
		
		if (targetCurrency.equalsIgnoreCase(product.getCurrency())) {
			product.setConvertedPrice(product.getPrice());
			dataSource = "Same Currency";
		} else {
			// Verificar cache primeiro
			String cacheKey = idProduct + "_" + targetCurrency;
			String cacheName = "ProductConversion";
			
			Double cachedPrice = cacheManager.getCache(cacheName).get(cacheKey, Double.class);
			
			if (cachedPrice != null) {
				product.setConvertedPrice(cachedPrice);
				dataSource = "Cache";
			} else {
				CurrencyResponse currency = currencyClient.getCurrency(
						product.getPrice(), 
						product.getCurrency(), 
						targetCurrency);
				
				// Verificar se o fallback foi ativado (valor -1)
				if (currency.getConvertedValue() == -1) {
					product.setConvertedPrice(-1);
					dataSource = "Currency service unavailable (Fallback)";
				} else {
					product.setConvertedPrice(currency.getConvertedValue());
					dataSource = "Currency Service - " + currency.getEnviroment();
					
					// Armazenar no cache
					cacheManager.getCache(cacheName).put(cacheKey, currency.getConvertedValue());
				}
			}
		}
		
		product.setEnviroment("Product-service running on Port: " + serverPort + " - DataSource: " + dataSource);
		
		return ResponseEntity.ok(product);
	}

}
