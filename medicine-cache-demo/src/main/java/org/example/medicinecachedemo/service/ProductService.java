package org.example.medicinecachedemo.service;

import org.example.medicinecachedemo.entity.Product;
import org.example.medicinecachedemo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Service - Cache-aside Pattern với TTL 30 phút
 * Quản lý vòng đời dữ liệu trong Cache
 */
@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Cache-aside Pattern:
     * - TTL 30 phút (được cấu hình trong application.properties)
     * - Lần 1: ~200ms (DB query)
     * - Lần 2: <10ms (Redis cache)
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("=== ĐANG TRUY VẤN DATABASE CHO PRODUCT ID: {} ===", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /**
     * Cập nhật product - XÓA CACHE ngay khi có thay đổi
     * Đảm bảo lấy dữ liệu mới từ DB lần sau
     */
    @CacheEvict(value = "products", key = "#product.id")
    @Transactional
    public Product updateProduct(Product product) {
        log.info("=== XÓA CACHE CHO PRODUCT ID: {} KHI CẬP NHẬT ===", product.getId());
        return productRepository.save(product);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}