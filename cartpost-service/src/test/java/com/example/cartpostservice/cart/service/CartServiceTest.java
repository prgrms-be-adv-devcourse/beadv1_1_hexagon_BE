package com.example.cartpostservice.cart.service;

import com.example.cartpostservice.cart.controller.dto.response.CartItemsGetResponse;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.model.vo.ContractStatus;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
import com.example.cartpostservice.cart.infra.kafka.publisher.KafkaCartEventPublisher;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartsRepository cartsRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private KafkaCartEventPublisher kafkaCartEventPublisher;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testGetCartItems_withItems() {
        // given
        String xCode = UUID.randomUUID().toString();
        CartsEntity cart = CartsEntity.builder()
                .memberCode(xCode)
                .build();

        cart.generateCode();
        String cartCode = cart.getCode();

        CartItemsEntity item = CartItemsEntity.builder()
                .code(UUID.randomUUID().toString())
                .cartCode(cartCode)
                .contractCode("CONTRACT123")
                .status(ContractStatus.CONFIRMED)
                .startedAt(Instant.now())
                .endedAt(Instant.now().plusSeconds(86400)) // +1 day
                .paymentType(PaymentType.PER_JOB)
                .amount("100")
                .build();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCartCode(cartCode)).thenReturn(List.of(item));

        // when
        List<CartItemsGetResponse> responses = cartService.getCartItems(xCode);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).contractCode()).isEqualTo("CONTRACT123");
        verify(cartsRepository, times(1)).findByMemberCode(xCode);
        verify(cartItemsRepository, times(1)).findByCartCode(cartCode);
    }

    @Test
    void testGetCartItems_emptyCart() {
        String xCode = UUID.randomUUID().toString();

        CartsEntity cart = CartsEntity.builder()
                .memberCode(xCode)
                .build();

        cart.generateCode();
        String cartCode = cart.getCode();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCartCode(cartCode)).thenReturn(List.of());

        List<CartItemsGetResponse> responses = cartService.getCartItems(xCode);
        assertThat(responses).isEmpty();
    }

    @Test
    void testGetCartItems_notFoundMember() {
        String xCode = UUID.randomUUID().toString();
        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCartItems(xCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CustomStatusCode.NOT_FOUND_CART.getMessage());
    }

    @Test
    void testDeleteCartItems_success() {
        // given
        String xCode = UUID.randomUUID().toString();
        String itemCode = UUID.randomUUID().toString();
        CartsEntity cart = CartsEntity.builder()
                .memberCode(xCode)
                .build();

        cart.generateCode();
        String cartCode = cart.getCode();

        CartItemsEntity item = CartItemsEntity.builder()
                .code(itemCode)
                .cartCode(cartCode)
                .build();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCode(itemCode)).thenReturn(Optional.of(item));

        // when
        Empty response = cartService.deleteCartItems(xCode, itemCode);

        // then
        assertThat(response).isNotNull();
        verify(kafkaCartEventPublisher, times(1)).deleteProducer(any());
        verify(cartItemsRepository, times(1)).delete(item);
    }

    @Test
    void testDeleteCartItems_notFoundMember() {
        String xCode = UUID.randomUUID().toString();
        String itemCode = UUID.randomUUID().toString();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCartItems(xCode, itemCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CustomStatusCode.NOT_FOUND_CART.getMessage());
    }

    @Test
    void testDeleteCartItems_notFoundItem() {
        String xCode = UUID.randomUUID().toString();
        String itemCode = UUID.randomUUID().toString();

        CartsEntity cart = CartsEntity.builder()
                .memberCode(xCode)
                .build();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCode(itemCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCartItems(xCode, itemCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CustomStatusCode.BAD_REQUEST_ITEM.getMessage());
    }

    @Test
    void testDeleteCartItems_forbiddenItem() {
        String xCode = UUID.randomUUID().toString();
        String itemCode = UUID.randomUUID().toString();

        CartsEntity cart = CartsEntity.builder()
                .memberCode(xCode)
                .build();

        cart.generateCode();

        CartItemsEntity item = CartItemsEntity.builder()
                .code(itemCode)
                .cartCode(UUID.randomUUID().toString()) // cart와 다름
                .build();

        when(cartsRepository.findByMemberCode(xCode)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCode(itemCode)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartService.deleteCartItems(xCode, itemCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CustomStatusCode.FORBIDDEN_ITEM.getMessage());
    }
}
