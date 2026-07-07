package org.example.medicinecachedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching // Kích hoạt Spring Cache
public class MedicineCacheDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineCacheDemoApplication.class, args);
    }

}
