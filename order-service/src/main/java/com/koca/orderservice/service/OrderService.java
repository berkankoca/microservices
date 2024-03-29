package com.koca.orderservice.service;

import com.koca.orderservice.dto.InventoryStockResponse;
import com.koca.orderservice.dto.OrderLineItemDto;
import com.koca.orderservice.dto.OrderRequest;
import com.koca.orderservice.model.Order;
import com.koca.orderservice.model.OrderLineItem;
import com.koca.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
//    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        //Call Inventory Service, and place order if product is in stock
        InventoryStockResponse[] inventoryStockResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryStockResponse[].class)
                .share().block();

        boolean allProductIsInStock = Arrays.stream(inventoryStockResponses)
                .allMatch(InventoryStockResponse::isInStock);

        if (allProductIsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Product is not in stock, please try again later.");
        }
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());

        return orderLineItem;
    }

}
