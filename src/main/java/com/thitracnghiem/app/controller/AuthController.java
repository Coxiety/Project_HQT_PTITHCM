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
import com.thitracnghiem.app.service.GiaoVienDangKyService;
import com.thitracnghiem.app.service.GiaoVienService;
import com.thitracnghiem.app.service.LopService;
import com.thitracnghiem.app.service.MonHocService;
import com.thitracnghiem.app.service.SinhVienService;
import com.thitracnghiem.app.service.TaiKhoanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private GiaoVienService giaoVienService;
    
    @Autowired
    private SinhVienService sinhVienService;
    
    @Autowired
    private MonHocService monHocService;
    
    @Autowired
    private LopService lopService;
    
    @Autowired
    private TaiKhoanService taiKhoanService;
    
    @Autowired
    private GiaoVienDangKyService dangKyService;
    
    @GetMapping("/")
    public String index() {
        if (sessionManager.isLoggedIn()) {
            // Phân biệt redirect theo role
            if (sessionManager.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else if (sessionManager.isTeacher()) {
                return "redirect:/teacher/dashboard";
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
            } else if (sessionManager.isTeacher()) {
                return "redirect:/teacher/dashboard";
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
            } else if ("Giangvien".equals(userInfo.getRole())) {
                return "redirect:/teacher/dashboard";
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
        
        try {
            // Thống kê số lượng
            int totalGiaoVien = giaoVienService.getAllGiaoVien().size();
            int totalSinhVien = sinhVienService.getAllSinhVien().size();
            int totalMonHoc = monHocService.getAllMonHoc().size();
            int totalLop = lopService.getAllLop().size();
            int totalDangKyThi = dangKyService.getAllDangKyThi().size();
            
            // Tính tổng tài khoản (có thể có lỗi với TaiKhoanService)
            int totalTaiKhoan = 0;
            try {
                int totalTeacherAccounts = taiKhoanService.getTeacherAccounts().size();
                int totalStudentAccounts = taiKhoanService.getStudentAccounts().size();
                totalTaiKhoan = totalTeacherAccounts + totalStudentAccounts;
            } catch (Exception e) {
                // Nếu TaiKhoanService có lỗi, ước tính từ tổng giáo viên và sinh viên
                System.err.println("⚠️ Không thể lấy số lượng tài khoản từ TaiKhoanService: " + e.getMessage());
                totalTaiKhoan = totalGiaoVien + totalSinhVien; // Ước tính
            }
            
            // Thêm vào model
            model.addAttribute("totalGiaoVien", totalGiaoVien);
            model.addAttribute("totalSinhVien", totalSinhVien);
            model.addAttribute("totalMonHoc", totalMonHoc);
            model.addAttribute("totalLop", totalLop);
            model.addAttribute("totalDangKyThi", totalDangKyThi);
            model.addAttribute("totalTaiKhoan", totalTaiKhoan);
            model.addAttribute("user", sessionManager.getCurrentUser());
            
        } catch (Exception e) {
            System.err.println("Lỗi lấy thống kê dashboard: " + e.getMessage());
            // Đặt giá trị mặc định nếu có lỗi
            model.addAttribute("totalGiaoVien", 0);
            model.addAttribute("totalSinhVien", 0);
            model.addAttribute("totalMonHoc", 0);
            model.addAttribute("totalLop", 0);
            model.addAttribute("totalDangKyThi", 0);
            model.addAttribute("totalTaiKhoan", 0);
            model.addAttribute("user", sessionManager.getCurrentUser());
        }
        
        return "dashboard/admin";
    }
    
    /**
     * Dashboard cho giáo viên
     */
    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Model model) {
        if (!sessionManager.isLoggedIn()) {
            return "redirect:/login";
        }
        
        // Chỉ giáo viên mới được truy cập
        if (!sessionManager.isTeacher()) {
            if (sessionManager.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }
        
        try {
            // Thống kê cho giáo viên
            String currentTeacherId = sessionManager.getCurrentUser().getUserId();
            
            // Đếm số đăng ký thi của giáo viên này
            int myDangKyThi = dangKyService.getAllDangKyThi().size(); // Tạm thời lấy tất cả, sau sẽ filter
            
            // Thống kê chung (giáo viên có thể xem tổng quan hệ thống)
            int totalSinhVien = sinhVienService.getAllSinhVien().size();
            int totalMonHoc = monHocService.getAllMonHoc().size();
            int totalLop = lopService.getAllLop().size();
            
            // Thêm vào model
            model.addAttribute("myDangKyThi", myDangKyThi);
            model.addAttribute("totalSinhVien", totalSinhVien);
            model.addAttribute("totalMonHoc", totalMonHoc);
            model.addAttribute("totalLop", totalLop);
            model.addAttribute("currentTeacher", currentTeacherId);
            model.addAttribute("user", sessionManager.getCurrentUser());
            
        } catch (Exception e) {
            System.err.println("Lỗi lấy thống kê teacher dashboard: " + e.getMessage());
            // Đặt giá trị mặc định nếu có lỗi
            model.addAttribute("myDangKyThi", 0);
            model.addAttribute("totalSinhVien", 0);
            model.addAttribute("totalMonHoc", 0);
            model.addAttribute("totalLop", 0);
            model.addAttribute("currentTeacher", "Unknown");
            model.addAttribute("user", sessionManager.getCurrentUser());
        }
        
        return "dashboard/teacher";
    }
} 