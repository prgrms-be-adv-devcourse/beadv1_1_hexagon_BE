package com.example.searchservice.saga.service;

import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO : Conflict 방지를 위해 saga 안에 별도의 service 클래스 생성
//  -> 이전 PR 머지 후 SelfPromotionServiceImpl에 메소드 copy & paste, 현재 클래스 삭제
@Service
@RequiredArgsConstructor
public class SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;

    public void saveAll(List<SelfPromotionDocumentEntity> selfPromotions) {
        selfPromotionRepository.saveAll(selfPromotions);
    }

    public void save(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    public void update(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    public void delete(String code) {
        selfPromotionRepository.deleteById(code);
    }
}
