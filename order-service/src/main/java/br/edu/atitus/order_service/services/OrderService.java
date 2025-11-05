package br.edu.atitus.order_service.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.atitus.order_service.clients.CurrencyClient;
import br.edu.atitus.order_service.clients.CurrencyResponse;
import br.edu.atitus.order_service.clients.ProductClient;
import br.edu.atitus.order_service.clients.ProductResponse;
import br.edu.atitus.order_service.entities.OrderEntity;
import br.edu.atitus.order_service.entities.OrderItemEntity;
import br.edu.atitus.order_service.repositories.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CurrencyClient currencyClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, CurrencyClient currencyClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
		this.currencyClient = currencyClient;
    }

    public OrderEntity createOrder(OrderEntity order, Long userId) {
        
        return orderRepository.save(order);
    }

	public Page<OrderEntity> findOrdersByCustomerId(Long customerId, String targetCurrency, Pageable pageable) {
		Page<OrderEntity> customerOrders = orderRepository.findByCustomerId(customerId, pageable);

		for (OrderEntity order : customerOrders) {
			double originalTotal = 0.0;
			double convertedTotal = 0.0;

			for (OrderItemEntity item : order.getItems()) {
				ProductResponse productInfo = productClient.getProductById(item.getProductId());
				item.setProduct(productInfo);
				originalTotal += item.getPriceAtPurchase() * item.getQuantity();

				CurrencyResponse conversion = currencyClient.getCurrency(item.getPriceAtPurchase(),
						item.getCurrencyAtPurchase(), targetCurrency);
				item.setConvertedPriceAtPurchase(conversion.getConvertedValue());
				convertedTotal += item.getConvertedPriceAtPurchase() * item.getQuantity();
			}
			order.setTotalPrice(originalTotal);
			order.setTotalConvertedPrice(convertedTotal);
		}
		return customerOrders;
	}
}
