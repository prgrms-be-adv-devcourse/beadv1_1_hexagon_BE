package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import java.util.List;
import org.hexagon.core.dto.Empty;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    List<CartItemsGetResponse> getCartItems(String xCode);

    Empty deleteCartItems(String xCode, String itemCode);
}
