package com.example.contractservice.deposit.repository;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.NO_DEPOSIT_ENTITY;

import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.entity.DepositHistoryEntity;
import com.example.contractservice.deposit.entity.QDepositHistoryEntity;
import com.example.contractservice.deposit.service.mapper.DepositHistoryMapper;
import com.example.contractservice.deposit.service.mapper.DepositMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepositRepository {
    private final DepositJpaRepository depositJpaRepository;
    private final DepositHistoryJpaRepository depositHistoryJpaRepository;
    private final JPAQueryFactory queryFactory;

    public Deposit findDepositByMemberCode(String memberCode) {
        return DepositMapper.toDomain(depositJpaRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new DepositException(NO_DEPOSIT_ENTITY)));
    }

    public Deposit saveDeposit(Deposit deposit) {
        Optional<DepositEntity> optionalEntity = depositJpaRepository.findByMemberCode(deposit.getMemberCode());

        if (optionalEntity.isPresent()) {
            DepositEntity depositEntity = optionalEntity.get();
            DepositMapper.applyToEntity(deposit, depositEntity);
            return DepositMapper.toDomain(depositJpaRepository.save(depositEntity));
        }

        DepositEntity depositEntity = DepositMapper.toEntity(deposit);
        return DepositMapper.toDomain(depositJpaRepository.save(depositEntity));
    }

    public DepositHistory saveDepositHistory(DepositHistory depositHistory) {
        DepositHistoryEntity historyEntity = DepositHistoryMapper.toEntity(depositHistory);

        return DepositHistoryMapper.toDomain(depositHistoryJpaRepository.save(historyEntity));
    }

    public boolean existMemberDeposit(String memberCode) {
        return depositJpaRepository.existsByMemberCode(memberCode);
    }

    public List<DepositHistory> findAllBy(String depositCode, Instant cursorDate, String cursorCode, int limit) {
        QDepositHistoryEntity history = QDepositHistoryEntity.depositHistoryEntity;

        BooleanExpression predicate = history.depositCode.eq(depositCode);

        if (cursorDate != null && cursorCode != null) {
            BooleanExpression cursorPredicate = history.createdAt.lt(cursorDate)
                    .or(history.createdAt.eq(cursorDate)
                            .and(history.code.gt(cursorCode))
                    );
            predicate = predicate.and(cursorPredicate);
        }

        return queryFactory.selectFrom(history)
                .where(predicate)
                .orderBy(history.createdAt.desc(), history.code.asc())
                .limit(limit + 1L) // hasNext 판별
                .fetch()
                .stream()
                .map(DepositHistoryMapper::toDomain)
                .toList();
    }
}
