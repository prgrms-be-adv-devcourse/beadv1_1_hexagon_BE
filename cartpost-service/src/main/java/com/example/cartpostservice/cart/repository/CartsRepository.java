package com.example.cartpostservice.cart.repository;

import com.example.cartpostservice.cart.model.CartsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartsRepository extends JpaRepository<CartsEntity, String> {

    Optional<CartsEntity> findByMemberCode(String xCode);

    boolean existsByMemberCode(String s);
}
