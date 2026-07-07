package org.example.medicinecachedemo.controller;

import org.example.medicinecachedemo.entity.Medicine;
import org.example.medicinecachedemo.service.MedicineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@Slf4j
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        Medicine medicine = medicineService.getMedicineById(id);
        long endTime = System.currentTimeMillis();

        log.info("Thời gian phản hồi: {} ms", (endTime - startTime));
        return ResponseEntity.ok(medicine);
    }

    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        Medicine saved = medicineService.createMedicine(medicine);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        medicine.setId(id);
        Medicine updated = medicineService.updateMedicine(medicine);
        log.info("=== ĐÃ CẬP NHẬT THUỐC ID: {} - CACHE ĐÃ XÓA ===", id);
        return ResponseEntity.ok(updated);
    }
}