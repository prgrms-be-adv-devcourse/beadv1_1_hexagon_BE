package com.example.profileservice.common.model.vo.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CompletedContractStore {

    private final Set<String> completedContracts = ConcurrentHashMap.newKeySet();

    public void markCompleted(String contractCode) {
        completedContracts.add(contractCode);
    }

    public boolean isCompleted(String contractCode) {
        return completedContracts.contains(contractCode);
    }
}
