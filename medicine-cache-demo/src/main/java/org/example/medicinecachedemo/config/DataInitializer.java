package org.example.medicinecachedemo.config;

import org.example.medicinecachedemo.entity.Medicine;
import org.example.medicinecachedemo.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;


@Configuration
public class DataInitializer {

    @Autowired
    private MedicineRepository medicineRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Tạo 10 loại thuốc mẫu nếu chưa có
            for (long i = 1; i <= 10; i++) {
                if (!medicineRepository.existsById(i)) {
                    Medicine medicine = new Medicine();
                    medicine.setId(i);
                    medicine.setName("Paracetamol " + i);
                    medicine.setDescription("Thuốc giảm đau, hạ sốt loại " + i);
                    medicine.setManufacturer("Nhà sản xuất " + ((i % 3) + 1));
                    medicine.setPrice(new BigDecimal("50000" + (i % 10)));
                    medicine.setStock(100 + (int) (i * 10));
                    medicine.setDosageForm(i % 2 == 0 ? "VIỀN" : "HŨI");
                    medicine.setUnit("BOX");
                    medicineRepository.save(medicine);
                }
            }
            System.out.println("=== ĐÃ TẠO DỮ LIỆU THUỐC MẪU - SẴN SÀNG TEST CACHE ===");
        };
    }
}