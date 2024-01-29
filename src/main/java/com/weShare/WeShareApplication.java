package com.weShare;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WeShareApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeShareApplication.class, args);
		while (true) {
			log.info("Test");
		}
	}
}
