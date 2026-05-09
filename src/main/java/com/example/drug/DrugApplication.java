package com.example.drug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.mybatis.spring.annotation.MapperScan;

import java.awt.*;
import java.net.URI;

// 开启定时任务
@EnableScheduling
// 扫描mapper包
@MapperScan("com.example.drug.mapper")
@SpringBootApplication
public class DrugApplication {
	public static void main(String[] args) {
		SpringApplication.run(DrugApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("\n==========================================");
			System.out.println("项目启动成功！请访问以下链接：");
			System.out.println("http://localhost:8080/login.html");
			System.out.println("==========================================\n");
		};
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowser() {
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				String url = "http://localhost:8080/login.html";
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI(url));
				}
			} catch (Exception ignored) {}
		}).start();
	}
}
