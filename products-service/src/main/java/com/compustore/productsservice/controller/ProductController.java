package com.compustore.productsservice.controller;

import com.compustore.productsservice.model.Product;
import com.compustore.productsservice.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller exposing CRUD operations for products.  Access is
 * controlled via {@link PreAuthorize} annotations: only users with the
 * `ADMIN` role may create, update or delete products, whereas both
 * administrators and clients may view products.  This pattern follows the
 * example in the dev.to article where controller methods are annotated with
 * `@PreAuthorize("hasRole('ADMIN')")` to restrict access【923295981449190†L1555-L1605】.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * List all products.  Accessible to both ADMIN and CLIENT roles.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * Retrieve a product by its ID.  Accessible to both ADMIN and CLIENT roles.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * Create a new product.  Only users with the ADMIN role may access this
     * endpoint.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product created = productService.create(product);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update an existing product.  Only users with the ADMIN role may access
     * this endpoint.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a product by its ID.  Only users with the ADMIN role may access
     * this endpoint.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}