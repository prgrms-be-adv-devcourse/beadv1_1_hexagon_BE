package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.model.vo.ContractStatus;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.vo.PaymentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartItemsGetResponse> getCartItems(String xCode) {

        CartsEntity cart = cartsRepository.findByMemberCode(xCode).orElseThrow(() -> new BusinessException(
                CustomStatusCode.NOT_FOUND_MEMBER));

        List<CartItemsEntity> cartItems = cartItemsRepository.findByCartCode(cart.getCode());

        if (cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<CartItemsGetResponse> cartItemsGetResponses = cartItems.stream()
                .filter(entity -> entity.getStatus() == ContractStatus.CONFIRMED)
                .map(entity -> new CartItemsGetResponse(
                        entity.getCode(),
                        entity.getContractCode(),
                        entity.getStartedAt(),
                        entity.getEndedAt(),
                        entity.getPaymentType().toString(),
                        calculateTotalAmount(entity.getStartedAt(), entity.getEndedAt(), entity.getPaymentType(),
                                entity.getAmount())
                ))
                .toList();

        return cartItemsGetResponses;
    }

    @Override
    @Transactional
    public Empty deleteCartItems(String xCode, String itemCode) {

        CartsEntity cart = cartsRepository.findByMemberCode(xCode).orElseThrow(() -> new BusinessException(
                CustomStatusCode.NOT_FOUND_MEMBER));

        CartItemsEntity cartItem = cartItemsRepository.findByCode(itemCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_ITEM));

        if (!cart.getCode().equals(cartItem.getCartCode())) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_ITEM);
        }

        cartItemsRepository.delete(cartItem);

        return Empty.getInstance();
    }

    private Long calculateTotalAmount(Instant startedAt, Instant endedAt, PaymentType paymentType, String amount) {
        Duration duration = Duration.between(startedAt, endedAt);
        long days = duration.toDays();

        if (paymentType == PaymentType.PER_JOB) {
            return Long.parseLong(amount);
        }

        return days * Long.parseLong(amount);
    }
}
