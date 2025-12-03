package com.example.contractservice.contract.repository;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;

import com.example.contractservice.contract.common.Order;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.entity.QContractEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContractRepository {

    private final ContractJpaRepository contractJpaRepository;
    private final JPAQueryFactory queryFactory;

    public ContractEntity saveContract(ContractEntity contractEntity) {
        return contractJpaRepository.save(contractEntity);
    }

    public ContractEntity findByCode(String code) {
        return contractJpaRepository.findByCode(code)
                .orElseThrow(() -> new ContractException(NO_CONTRACT));
    }

    public List<ContractEntity> findAllByCodes(List<String> codes) {
        return contractJpaRepository.findAllByCodes(codes);
    }

    public List<ContractEntity> findAllBy(String memberCode, Instant cursorDate, String cursorCode, Order order, int limit) {
        QContractEntity qContractEntity = QContractEntity.contractEntity;

        BooleanExpression requestorPredicate = qContractEntity.clientCode.eq(memberCode); // 클라이언트에서 SELECT
        BooleanExpression contractorPredicate = qContractEntity.freelancerCode.eq(memberCode); // 프리랜서에서 SELECT

        // 커서 기반 WHERE
        if (cursorDate != null && cursorCode != null) {
            BooleanExpression cursorPredicate = getCursorPredicate(cursorDate, cursorCode, order, qContractEntity);

            requestorPredicate = requestorPredicate.and(cursorPredicate);
            contractorPredicate = contractorPredicate.and(cursorPredicate);
        }

        // ORDER BY 동적 설정
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpec(qContractEntity, order);

        // requestor에서 SELECT
        List<ContractEntity> requestorFetch = fetch(qContractEntity, requestorPredicate, orderSpecifiers, limit);
        // contractor에서 SELECT
        List<ContractEntity> contractorFetch = fetch(qContractEntity, contractorPredicate, orderSpecifiers, limit);

        // 둘을 UNION ALL
        return unionAndSort(requestorFetch, contractorFetch, order, limit);
    }

    private List<ContractEntity> fetch(QContractEntity qContractEntity, BooleanExpression predicate, OrderSpecifier<?>[] orderSpecifiers, int limit) {
        return queryFactory
                .selectFrom(qContractEntity)
                .where(predicate)
                .orderBy(orderSpecifiers)
                .limit(limit + 1L) // hasNext를 위한 + 1
                .fetch();
    }

    private List<ContractEntity> unionAndSort(List<ContractEntity> requestorFetch,
            List<ContractEntity> contractorFetch, Order order, int limit) {

        return ListUtils.union(requestorFetch, contractorFetch)
                .stream()
                .sorted(switch (order) {
                    case ASC -> Comparator.comparing(ContractEntity::getCreatedAt).thenComparing(ContractEntity::getCode);
                    case DESC -> Comparator.comparing(ContractEntity::getCreatedAt).reversed().thenComparing(ContractEntity::getCode);
                })
                .limit(limit + 1L)
                .toList();
    }

    private BooleanExpression getCursorPredicate(Instant cursor, String cursorCode, Order order, QContractEntity qContractEntity) {

        return switch (order) {
            case ASC -> qContractEntity
                    .createdAt.gt(cursor) // created_at > :cursor
                    .or(qContractEntity.createdAt.eq(cursor) // OR (created_at = :cursor
                            .and(qContractEntity.code.gt(cursorCode)) // AND code > :cursorCode)
                    );
            case DESC -> qContractEntity
                    .createdAt.lt(cursor) // created_at > :cursor
                    .or(qContractEntity.createdAt.eq(cursor) // OR (created_at = :cursor
                            .and(qContractEntity.code.gt(cursorCode)) // AND code > :cursorCode)
                    );
        };
    }

    private OrderSpecifier<?>[] getOrderSpec(QContractEntity qContractEntity, Order order) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (order == Order.ASC) {
            orderSpecifiers.add(qContractEntity.createdAt.asc());
        } else {
            orderSpecifiers.add(qContractEntity.createdAt.desc());
        }
        orderSpecifiers.add(qContractEntity.code.asc());

        return orderSpecifiers.toArray(OrderSpecifier<?>[]::new);
    }

}
