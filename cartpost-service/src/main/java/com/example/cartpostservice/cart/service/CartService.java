package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.controller.dto.request.ContractPayRequest;
import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import com.example.cartpostservice.common.dto.EmptyResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    public List<CartItemsGetResponse> getCartItems(String xCode);

    public EmptyResponse deleteCartItems(String xCode, String itemCode);

    public EmptyResponse payCartItems(String xCode, ContractPayRequest requests);
}
