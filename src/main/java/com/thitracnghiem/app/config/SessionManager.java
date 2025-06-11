package com.thitracnghiem.app.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.thitracnghiem.app.model.UserInfo;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionManager {
    
    private UserInfo currentUser;
    private JdbcTemplate jdbcTemplate;
    
    public UserInfo getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(UserInfo currentUser) {
        this.currentUser = currentUser;
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isAdmin() {
        return isLoggedIn() && "PGV".equals(currentUser.getRole());
    }
    
    public boolean isTeacher() {
        return isLoggedIn() && "Giangvien".equals(currentUser.getRole());
    }
    
    public boolean isStudent() {
        return isLoggedIn() && "Sinhvien".equals(currentUser.getRole());
    }
    
    public void logout() {
        this.currentUser = null;
        this.jdbcTemplate = null;
    }
} 