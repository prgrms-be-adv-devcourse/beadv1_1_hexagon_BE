package com.example.cartpostservice.kafka;

import com.example.cartpostservice.cart.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.cartpostservice.cart.controller.internal.ContractClient;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.model.vo.ContractStatus;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
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

//        CartsEntity cart = cartsRepository.findByMemberCode(event.memberCode())
//                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_CART));
//
//        List<String> contractCode = List.of(event.contractCode());
//        ResponseDto<List<ContractBriefWithNicknameResponse>> response = contractClient.getBriefInfo(contractCode);
//
//        if (event.status().equals("CONFIRMED")) {
//            if (!cartItemsRepository.existsByContractCode(event.contractCode())) {
//
//                List<ContractBriefWithNicknameResponse> contractList = response.data();
//
//                if (contractList == null || contractList.isEmpty()) {
//                    throw new BusinessException(CustomStatusCode.BAD_REQUEST_ITEM);
//                }
//
//                // 4. 리스트 중에서 현재 이벤트의 contractCode와 일치하는 정보 찾기
//                ContractBriefWithNicknameResponse contractInfo = contractList.stream()
//                        .filter(info -> info.code().equals(event.contractCode()))
//                        .findFirst()
//                        .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_ITEM));
//
//                CartItemsEntity item = CartItemsEntity.builder()
//                        .contractCode(event.contractCode())
//                        .cartCode(cart.getCode())
//                        .status(ContractStatus.valueOf(event.status()))
//                        .startedAt(contractInfo.startedAt())
//                        .endedAt(contractInfo.endedAt())
//                        .paymentType(PaymentType.valueOf(contractInfo.paymentType()))
//                        .amount(String.valueOf(contractInfo.unitAmount()))
//                        .build();
//
//                cartItemsRepository.save(item);
        //           }
        //     }
    }
}
