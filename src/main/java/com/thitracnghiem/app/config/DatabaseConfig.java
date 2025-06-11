package com.thitracnghiem.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Tạo DataSource mặc định (sẽ không được sử dụng trực tiếp)
     */
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(jdbcUrl);
        return dataSource;
    }

    /**
     * Tạo DataSource với thông tin đăng nhập của người dùng
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return DataSource đã được cấu hình
     */
    public DataSource createUserDataSource(String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        
        // Đảm bảo URL kết nối có các tham số cần thiết
        String url = jdbcUrl;
        if (!url.contains("encrypt=")) {
            url += ";encrypt=true";
        }
        if (!url.contains("trustServerCertificate=")) {
            url += ";trustServerCertificate=true";
        }
        if (!url.contains("integratedSecurity=")) {
            url += ";integratedSecurity=false";
        }
        
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        System.out.println("Tạo kết nối với username: " + username);
        System.out.println("URL kết nối: " + url);
        
        return dataSource;
    }
} 