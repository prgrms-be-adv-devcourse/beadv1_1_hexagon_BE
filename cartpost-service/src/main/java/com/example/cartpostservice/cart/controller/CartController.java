package com.example.cartpostservice.cart.controller;


import com.example.cartpostservice.cart.controller.dto.request.ContractPayRequest;
import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import com.example.cartpostservice.cart.service.CartService;
import com.example.cartpostservice.common.dto.EmptyResponse;
import com.example.cartpostservice.common.dto.ResponseDto;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController implements CartApi {

    private final CartService cartService;

    @Override
    @GetMapping("/items")
    public ResponseEntity<ResponseDto<List<CartItemsGetResponse>>> getCartItems(
            @RequestHeader(name = "X-CODE") String xCode) {

        List<CartItemsGetResponse> cartItemsGetResponses = cartService.getCartItems(xCode);

        if (cartItemsGetResponses.isEmpty()) {
            return new ResponseEntity<>(ResponseDto.success(CustomStatusCode.SUCCESS_NO_DATA, cartItemsGetResponses),
                    CustomStatusCode.SUCCESS_NO_DATA.getStatus());
        }

        return new ResponseEntity<>(ResponseDto.success(CustomStatusCode.SUCCESS, cartItemsGetResponses),
                CustomStatusCode.SUCCESS.getStatus());
    }

    @Override
    @DeleteMapping("/items/{item-code}")
    public ResponseEntity<ResponseDto<EmptyResponse>> deleteCartItem(@RequestHeader(name = "X-CODE") String xCode,
            @PathVariable(name = "item-code") String itemCode) {

        EmptyResponse emptyResponse = cartService.deleteCartItems(xCode, itemCode);

        return new ResponseEntity<>(ResponseDto.success(CustomStatusCode.SUCCESS, emptyResponse),
                CustomStatusCode.SUCCESS.getStatus());
    }

    @Override
    @GetMapping("/pay/items")
    public ResponseEntity<ResponseDto<EmptyResponse>> payRequest(String xCode, ContractPayRequest requests) {

        EmptyResponse emptyResponse = cartService.payCartItems(xCode, requests);

        return new ResponseEntity<>(ResponseDto.success(CustomStatusCode.SUCCESS, emptyResponse),
                CustomStatusCode.SUCCESS.getStatus());
    }
}

