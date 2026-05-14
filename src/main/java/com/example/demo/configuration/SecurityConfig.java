package com.example.demo.configuration;

import com.example.demo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép truy cập công khai: Login, CSS, JS và Hình ảnh
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // 2. Cho phép xem sản phẩm và THỰC HIỆN thao tác giỏ hàng (Thêm/Xem) không cần Login
                        // Việc thêm "/cart/**" vào permitAll giúp nút "Thêm vào giỏ" hoạt động tự do
                        .requestMatchers("/store/products", "/cart/**").permitAll()

                        // 3. Chỉ Admin mới có quyền can thiệp vào kho hàng
                        .requestMatchers("/store/add/**", "/store/edit/**", "/store/delete/**").hasRole("ADMIN")

                        // 4. Các yêu cầu khác phải xác thực
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/store/products", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // QUAN TRỌNG: Vô hiệu hóa CSRF để các request POST từ form "Thêm vào giỏ" không bị chặn 403
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }
}