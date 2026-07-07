package org.example.medicinecachedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {
    private String type;     // IMPORT, EXPORT, PRICE_UPDATE...
    private String message;  // Nội dung thông báo
    private Long timestamp;  // Thời gian gửi

    public static AlertMessage of(String type, String message) {
        return new AlertMessage(type, message, System.currentTimeMillis());
    }
}