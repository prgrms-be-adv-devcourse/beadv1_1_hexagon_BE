package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.infra.clinet.internal.ContractClient;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.ContractBriefWithNicknameResponse;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.events.contract.ContractEvent;
import org.hexagon.core.vo.PaymentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartPostKafkaService {

    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ContractClient contractClient;

    @Transactional
    public void createCart(String memberCode) {
        CartsEntity cart = CartsEntity.builder()
                .memberCode(memberCode)
                .build();

        if (!cartsRepository.existsByMemberCode(memberCode)) {
            cartsRepository.save(cart);
        }
    }

    @Transactional
    public void addCartItem(ContractEvent event) {
        if (event.status().equals("REQUIRED")) {

            CartsEntity cart = cartsRepository.findByMemberCode(event.clientCode())
                    .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_CART));

            List<String> contractCode = List.of(event.contractCode());
            ResponseDto<List<ContractBriefWithNicknameResponse>> response = contractClient.getBriefInfo(contractCode);

            if (!cartItemsRepository.existsByContractCode(event.contractCode())) {

                List<ContractBriefWithNicknameResponse> contractList = response.data();

                if (contractList == null || contractList.isEmpty()) {
                    throw new BusinessException(CustomStatusCode.BAD_REQUEST_ITEM);
                }

                ContractBriefWithNicknameResponse contractInfo = contractList.stream()
                        .filter(info -> info.code().equals(event.contractCode()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_ITEM));

                CartItemsEntity item = CartItemsEntity.builder()
                        .contractCode(event.contractCode())
                        .cartCode(cart.getCode())
                        .commissionCode(event.commissionCode())
                        .clientCode(event.clientCode())
                        .freelancerCode(event.freelancerCode())
                        .startedAt(contractInfo.startedAt())
                        .endedAt(contractInfo.endedAt())
                        .paymentType(PaymentType.valueOf(contractInfo.paymentType()))
                        .amount(String.valueOf(contractInfo.unitAmount()))
                        .clientName(contractInfo.clientName())
                        .freelancerName(contractInfo.freelancerName())
                        .contractTitle(contractInfo.name())
                        .build();

                cartItemsRepository.save(item);
            }
        }
    }
}
