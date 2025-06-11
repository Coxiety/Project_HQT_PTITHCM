package com.thitracnghiem.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.model.UserCredentials;
import com.thitracnghiem.app.model.UserInfo;
import com.thitracnghiem.app.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @GetMapping("/")
    public String index() {
        if (sessionManager.isLoggedIn()) {
            // Phân biệt redirect theo role
            if (sessionManager.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm(Model model) {
        if (sessionManager.isLoggedIn()) {
            // Phân biệt redirect theo role
            if (sessionManager.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }
        model.addAttribute("userCredentials", new UserCredentials());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute UserCredentials credentials, Model model) {
        UserInfo userInfo = authService.authenticate(credentials);
        
        if (userInfo != null) {
            // Phân biệt redirect theo role
            if ("PGV".equals(userInfo.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        authService.logout();
        
        // Hủy phiên
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        return "redirect:/login";
    }
    
    /**
     * Dashboard cho user thường (giáo viên và sinh viên)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        if (!sessionManager.isLoggedIn()) {
            return "redirect:/login";
        }
        
        // Nếu admin truy cập /dashboard, redirect về admin area
        if (sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("user", sessionManager.getCurrentUser());
        
        if (sessionManager.isTeacher()) {
            return "dashboard/teacher";
        } else if (sessionManager.isStudent()) {
            return "dashboard/student";
        } else {
            return "dashboard/default";
        }
    }
    
    /**
     * Dashboard cho admin (PGV)
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        if (!sessionManager.isLoggedIn()) {
            return "redirect:/login";
        }
        
        // Chỉ admin mới được truy cập
        if (!sessionManager.isAdmin()) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("user", sessionManager.getCurrentUser());
        return "dashboard/admin";
    }
} 