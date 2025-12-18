package com.example.cartpostservice.cart.controller.dto.response;

import java.util.List;

public record PaidResultResponse(List<String> success, List<String> fail) {

}
