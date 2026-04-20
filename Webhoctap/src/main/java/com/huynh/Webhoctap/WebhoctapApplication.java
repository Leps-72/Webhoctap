package com.huynh.Webhoctap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class WebhoctapApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(WebhoctapApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			System.out.println("--- Kết nối Database thành công! ---");
			System.out.println("Database Product Name: " + connection.getMetaData().getDatabaseProductName());
		} catch (Exception e) {
			System.err.println("--- Kết nối Database THẤT BẠI: " + e.getMessage());
		}
	}
}