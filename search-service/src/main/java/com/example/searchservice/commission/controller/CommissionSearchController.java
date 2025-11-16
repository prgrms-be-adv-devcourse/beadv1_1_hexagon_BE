package com.example.searchservice.commission.controller;

import com.example.searchservice.commission.common.PaymentType;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.commission.controller.swagger.CommissionSearchControllerSwagger;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/commissions")
public class CommissionSearchController implements CommissionSearchControllerSwagger {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> search(String query, SearchScope scope, List<String> tags, PaymentType paymentType,
            Long minPay, LocalDate startedAt, LocalDate endedAt, int page, int size) {
        return null;
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> suggest(String query, int size) {
        return null;
    }
}
