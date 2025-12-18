package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import com.example.cartpostservice.cart.controller.dto.response.ContractInfo;
import com.example.cartpostservice.cart.controller.dto.response.PaidResultResponse;
import com.example.cartpostservice.cart.infra.clinet.internal.ContractClient;
import com.example.cartpostservice.cart.infra.clinet.internal.RecommendClient;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.request.ContractPayRequest;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.ContractPayResponse;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.FreelancerRecommendListResponse;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.FreelancerRecommendResponse;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.events.cartpost.CartItemDeletedEvent;
import org.hexagon.core.vo.PaymentType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ContractClient contractClient;
    private final RecommendClient recommendClient;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    @Transactional(readOnly = true)
    public List<CartItemsGetResponse> getCartItems(String xCode) {

        CartsEntity cart = cartsRepository.findByMemberCode(xCode).orElseThrow(() -> new BusinessException(
                CustomStatusCode.NOT_FOUND_CART));

        List<CartItemsEntity> cartItems = cartItemsRepository.findByCartCode(cart.getCode());

        if (cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        return cartItems.stream().collect(Collectors.groupingBy(CartItemsEntity::getCommissionCode))
                .entrySet().stream()
                .map(entry -> {
                    String commissionCode = entry.getKey();
                    List<CartItemsEntity> cartItemEntities = entry.getValue();

                    CartItemsEntity cartItem = cartItemEntities.get(0);

                    int recommendCount = Math.min(entry.getValue().size() / 3, 5);
                    ResponseDto<FreelancerRecommendListResponse> freelancerRecommend = recommendClient.recommendFreelancers(
                            commissionCode, recommendCount);

                    Set<String> recommendationCodes = freelancerRecommend.data().recommendations().stream()
                            .map(FreelancerRecommendResponse::freelancerCode)
                            .collect(Collectors.toSet());

                    List<ContractInfo> contractInfos = cartItemEntities.stream()
                            .map(entity -> new ContractInfo(
                                    entity.getContractCode(),
                                    entity.getCode(),
                                    entity.getClientName(),
                                    entity.getFreelancerName(),
                                    entity.getFreelancerCode(),
                                    recommendationCodes.contains(entity.getFreelancerCode()),
                                    entity.getContractTitle()
                            ))
                            .toList();

                    return new CartItemsGetResponse(
                            commissionCode,
                            contractInfos,
                            cartItem.getStartedAt(),
                            cartItem.getEndedAt(),
                            cartItem.getPaymentType().name(),
                            Long.parseLong(cartItem.getAmount())
                    );


                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Empty deleteCartItems(String xCode, String itemCode) {

        CartsEntity cart = cartsRepository.findByMemberCode(xCode).orElseThrow(() -> new BusinessException(
                CustomStatusCode.NOT_FOUND_CART));

        CartItemsEntity cartItem = cartItemsRepository.findByCode(itemCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_ITEM));

        if (!cart.getCode().equals(cartItem.getCartCode())) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_ITEM);
        }

        cartItemsRepository.delete(cartItem);

        applicationEventPublisher.publishEvent(new CartItemDeletedEvent(cartItem.getContractCode()));
        return Empty.getInstance();
    }

    @Override
    public PaidResultResponse payCartItems(String xCode, ContractPayRequest requests) {

        ResponseDto<ContractPayResponse> response = contractClient.payContract(requests);

        for (String succeed : response.data().success()) {
            cartItemsRepository.deleteByContractCode(succeed);
        }

        return new PaidResultResponse(response.data().success(), response.data().fail());
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
