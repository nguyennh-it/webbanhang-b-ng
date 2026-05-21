package com.example.demo;

import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository) {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedPassword = encoder.encode("123456");

			var existing = userRepository.findByUsername("admin");
			if (existing.isEmpty()) {
				// Chưa có → tạo mới
				User admin = User.builder()
						.username("admin")
						.password(encodedPassword)
						.role("ADMIN")
						.build();
				userRepository.save(admin);
				System.out.println("=== ĐÃ TẠO admin / 123456 ===");
			} else {
				// Đã có → force update password để chắc chắn đúng
				User admin = existing.get();
				admin.setPassword(encodedPassword);
				admin.setRole("ADMIN");
				userRepository.save(admin);
				System.out.println("=== ĐÃ RESET password admin → 123456 ===");
			}
		};
	}
}
