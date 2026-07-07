package org.example.medicinecachedemo.config;

import org.example.medicinecachedemo.entity.Product;
import org.example.medicinecachedemo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Tạo dữ liệu Product mẫu
 */
@Configuration
public class ProductDataInitializer {

    @Autowired
    private ProductRepository productRepository;

    @Bean
    public CommandLineRunner initProducts() {
        return args -> {
            for (long i = 1; i <= 10; i++) {
                if (!productRepository.existsById(i)) {
                    Product p = new Product();
                    p.setId(i);
                    p.setName("Sản phẩm " + i);
                    p.setDescription("Mô tả sản phẩm " + i);
                    p.setImage("image-" + i + ".jpg");
                    p.setPrice(new BigDecimal("100000" + (i % 10)));
                    p.setStock(100 + (int) i * 10);
                    productRepository.save(p);
                }
            }
            System.out.println("=== ĐÃ TẠO DỮ LIỆU PRODUCT MẪU - SẴN SÀNG TEST CACHE ===");
        };
    }
}