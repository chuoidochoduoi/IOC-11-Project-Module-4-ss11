package org.example.medicinecachedemo.controller.v1;

import org.example.medicinecachedemo.service.MedicineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/sale")
@Slf4j
public class SaleController {

    @Autowired
    private MedicineService medicineService;


    @PostMapping("/{id}")
    public ResponseEntity<?> sellMedicine(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer quantity) {

        MedicineService.SellResult result = medicineService.sellMedicine(id, quantity);

        if (result.isSuccess()) {
            return ResponseEntity.ok()
                    .body("{\"success\": true, \"message\": \"" + result.getMessage() +
                          "\", \"remaining\": " + result.getRemainingStock() + "}");
        }
        return ResponseEntity.ok()
                .body("{\"success\": false, \"message\": \"" + result.getMessage() + "\"}");
    }

    @PostMapping("/test-race/{id}")
    public ResponseEntity<String> testRaceCondition(@PathVariable Long id) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        resetStock(id, 1);

        executor.submit(() -> {
            medicineService.sellMedicine(id, 1);
            log.info("=== NHÂN VIÊN 1 Xử lý xong ===");
        });

        executor.submit(() -> {
            try {
                Thread.sleep(100); // Gần đồng thời
                medicineService.sellMedicine(id, 1);
            } catch (Exception e) {}
            log.info("=== NHÂN VIÊN 2 Xử lý xong ===");
        });

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        return ResponseEntity.ok("Kiểm tra log để xem kết quả race condition");
    }

    private void resetStock(Long id, int stock) {
    }
}