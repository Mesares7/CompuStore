package com.compustore.productsservice.repository;

import com.compustore.productsservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}