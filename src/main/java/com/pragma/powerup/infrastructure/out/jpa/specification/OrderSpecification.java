package com.pragma.powerup.infrastructure.out.jpa.specification;

import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<OrderEntity> withFilters(Integer restaurantId, String state) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            if (restaurantId != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId));
            }
            if (state != null && !state.isEmpty()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("state"), state));
            }
            return predicates;
        };
    }
}
