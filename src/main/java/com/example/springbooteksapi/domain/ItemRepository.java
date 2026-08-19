package com.example.springbooteksapi.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Item}.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
