package org.example.medicinecachedemo.controller.v1;

import org.example.medicinecachedemo.model.AlertMessage;
import org.example.medicinecachedemo.service.RedisMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alert")
@Slf4j
public class AlertController {

    @Autowired
    private RedisMessagePublisher messagePublisher;


    @PostMapping("/import")
    public ResponseEntity<String> sendImportAlert(
            @RequestParam(defaultValue = "Đã nhập hàng") String message) {
        messagePublisher.publishImport(message);
        return ResponseEntity.ok("Đã gửi alert: " + message);
    }


    @PostMapping
    public ResponseEntity<String> sendAlert(@RequestBody AlertMessage alert) {
        messagePublisher.publish(alert);
        return ResponseEntity.ok("Đã gửi alert kiểu: " + alert.getType());
    }
}