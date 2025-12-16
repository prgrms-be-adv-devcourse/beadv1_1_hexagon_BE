package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class) //Mockito + JUnit 연동
class CommissionsServiceTest {

    @Mock
    private CommissionsRepository commissionsRepository;

    @InjectMocks
    private DomainCompositeService commissionsService;

    private String xCode;
    private String commissionCode;

    @BeforeEach
    void setUp() {
        xCode = UUID.randomUUID().toString();
        commissionCode = UUID.randomUUID().toString();
    }

    @Test
    void createCommission_success() {

        /*
        // given
        when(commissionsRepository.save(any())).thenReturn(new CommissionsEntity());

        // when
        CommissionCreateResult result = commissionsService.createCommission(xCode);

        // then
        assertThat(result).isNotNull();
        verify(commissionsRepository, times(1)).save(any());
         */
    }

    @Test
    void readCommission_success() {

    }

    @Test
    void updateCommission_success() {

    }

    @Test
    void deleteCommission_success() {

    }

    @Test
    void finishCommission_success() {

    }

    @Test
    void readOwnCommissions_success() {

    }
}

