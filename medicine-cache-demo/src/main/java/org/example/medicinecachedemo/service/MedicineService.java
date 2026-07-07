package org.example.medicinecachedemo.service;

import org.example.medicinecachedemo.entity.Medicine;
import org.example.medicinecachedemo.repository.MedicineRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private RedissonClient redissonClient;


    @Cacheable(value = "medicines", key = "#id")
    @Transactional(readOnly = true)
    public Medicine getMedicineById(Long id) {
        log.info("=== ĐANG TRUY VẤN DATABASE CHO THUỐC ID: {} ===", id);
        // Log này chỉ xuất hiện khi cache MISS (không có trong Redis)
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id));
    }


    @Cacheable(value = "medicines", key = "'all'")
    @Transactional(readOnly = true)
    public List<Medicine> getAllMedicines() {
        log.info("=== ĐANG TRUY VẤN DATABASE LẤY TẤT CẢ THUỐC ===");
        return medicineRepository.findAll();
    }


    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }


    @CacheEvict(value = "medicines", key = "#medicine.id")
    @Transactional
    public Medicine updateMedicine(Medicine medicine) {
        log.info("=== XÓA CACHE CHO THUỐC ID: {} KHI CẬP NHẬT ===", medicine.getId());
        Medicine existing = medicineRepository.findById(medicine.getId())
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + medicine.getId()));
        return medicineRepository.save(medicine);
    }

    /**
     * Bán thuốc - SỬ DỤNG REDISSON LOCK ĐỂ TRÁNH RACE CONDITION
     *
     * Quy trình:
     * 1. Lấy Lock với tryLock(wait=3s, lease=5s)
     * 2. Kiểm tra tồn kho
     * 3. Nếu còn hàng -> trừ số lượng và trả về thành công
     * 4. Nếu hết hàng -> trả về thất bại
     * 5. Luôn luôn release lock trong finally
     *
     * Chỉ một thread có thể mua hàng thành công khi cùng thời điểm!
     */
    @Transactional
    public SellResult sellMedicine(Long id, Integer quantity) {
        String lockKey = "lock:medicine:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // tryLock: wait 3 giây, lease 5 giây
            if (!lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                return SellResult.failed("Không thể lấy lock - hệ thống đang bận");
            }

            log.info("=== ĐÃ LẤY ĐƯỢC LOCK CHO THUỐC ID: {} ===", id);

            Medicine medicine = medicineRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            if (medicine.getStock() < quantity) {
                log.info("=== HẾT HÀNG - STOCK: {} < REQUIRED: {} ===", medicine.getStock(), quantity);
                return SellResult.failed("Hết hàng");
            }

            // Trừ số lượng
            medicine.setStock(medicine.getStock() - quantity);
            medicineRepository.save(medicine);

            log.info("=== BÁN THÀNH CÔNG - CÒN LẠI: {} ===", medicine.getStock());
            return SellResult.success(medicine.getStock());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SellResult.failed("Bị gián đoạn");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("=== ĐÃ RELEASE LOCK CHO THUỐC ID: {} ===", id);
            }
        }
    }

    /**
     * Kết quả bán hàng
     */
    public static class SellResult {
        private boolean success;
        private String message;
        private Integer remainingStock;

        public SellResult(boolean success, String message, Integer remainingStock) {
            this.success = success;
            this.message = message;
            this.remainingStock = remainingStock;
        }

        public static SellResult success(Integer remainingStock) {
            return new SellResult(true, "Bán hàng thành công", remainingStock);
        }

        public static SellResult failed(String message) {
            return new SellResult(false, message, null);
        }

        // getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Integer getRemainingStock() { return remainingStock; }
    }
}