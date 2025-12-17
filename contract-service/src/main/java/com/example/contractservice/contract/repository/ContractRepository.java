package com.example.contractservice.contract.repository;

import static com.example.contractservice.contract.common.ContractStatus.IN_PROGRESS;
import static com.example.contractservice.contract.common.ContractStatus.PAID;
import static com.example.contractservice.contract.common.ContractStatus.REQUESTED;
import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;
import static com.example.contractservice.contract.service.mapper.ContractMapper.applyToEntity;
import static com.example.contractservice.contract.service.mapper.ContractMapper.toDomain;
import static com.example.contractservice.contract.service.mapper.ContractMapper.toEntity;

import com.example.contractservice.contract.common.Order;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.entity.QContractEntity;
import com.example.contractservice.contract.service.mapper.ContractMapper;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContractRepository {

    private final ContractJpaRepository contractJpaRepository;
    private final JPAQueryFactory queryFactory;

    /** 도메인에 해당하는 엔티티를 Repository에 저장합니다. 이미 엔티티로 존재한다면 그 값을 수정합니다.
     *
     * @param contract 저장/수정할 계약 도메인 인스턴스
     * @return 저장/수정된 계약 도메인 인스턴스
     */
    public Contract saveContract(Contract contract) {
        Optional<ContractEntity> optionalEntity = contractJpaRepository.findByCode(contract.getCode());

        if (optionalEntity.isPresent()) { // 이미 존재
            ContractEntity contractEntity = optionalEntity.get();
            applyToEntity(contract, contractEntity);
            return toDomain(contractJpaRepository.save(contractEntity));
        }

        return toDomain(contractJpaRepository.save(toEntity(contract)));
    }

    public Contract findByCode(String code) {
        ContractEntity contractEntity = contractJpaRepository.findByCode(code)
                .orElseThrow(() -> new ContractException(NO_CONTRACT));

        return toDomain(contractEntity);
    }

    public List<Contract> findAllByCodes(List<String> codes) {
        return contractJpaRepository.findAllByCodes(codes).stream()
                .map(ContractMapper::toDomain)
                .toList();
    }

    public List<Contract> findAllBy(String memberCode, Instant cursorDate, String cursorCode, Order order, int limit) {
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
        return unionAndSort(requestorFetch, contractorFetch, order, limit).stream()
                .map(ContractMapper::toDomain)
                .toList();
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

    public boolean existsClientContractBy(String memberCode) {
        return findFirstContract(memberCode, this::getClientWhereClause) != null;
    }

    public boolean existsFreelancerContractBy(String memberCode) {
        return findFirstContract(memberCode, this::getFreelancerWhereClause) != null;
    }

    private ContractEntity findFirstContract(String memberCode, BiFunction<QContractEntity, String, Predicate> whereClause) {
        QContractEntity qContractEntity = QContractEntity.contractEntity;

        return queryFactory.selectFrom(qContractEntity)
                .where(whereClause.apply(qContractEntity, memberCode))
                .fetchOne();
    }

    private Predicate getClientWhereClause(QContractEntity qContractEntity, String memberCode) {
        return qContractEntity.clientCode.eq(memberCode).and(qContractEntity.status.in(PAID, IN_PROGRESS));
    }

    private Predicate getFreelancerWhereClause(QContractEntity qContractEntity, String memberCode) {
        return qContractEntity.freelancerCode.eq(memberCode).and(qContractEntity.status.in(REQUESTED, PAID, IN_PROGRESS));
    }

}
