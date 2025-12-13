package com.example.contractservice.contract.service.event;

import java.util.concurrent.CompletableFuture;
import org.hexagon.core.events.contract.CommissionOpenCloseEvent;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.kafka.support.SendResult;

public interface ContractEventProducer {
    CompletableFuture<SendResult<String, Object>> sendEvent(ContractEvent event);
    CompletableFuture<SendResult<String, Object>> sendEvent(CommissionOpenCloseEvent event);
}
