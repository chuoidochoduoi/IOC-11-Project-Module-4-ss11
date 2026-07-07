package org.example.medicinecachedemo.controller;

import org.example.medicinecachedemo.entity.Product;
import org.example.medicinecachedemo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * API lấy chi tiết sản phẩm
     * - Lần 1: ~200ms (query DB)
     * - Lần 2: <10ms (redis cache)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        Product product = productService.getProductById(id);
        long end = System.currentTimeMillis();

        log.info("Thời gian phản hồi: {} ms", (end - start));
        return ResponseEntity.ok(product);
    }

    /**
     * API cập nhật sản phẩm - XÓA CACHE
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        Product updated = productService.updateProduct(product);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }
}