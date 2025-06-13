package com.thitracnghiem.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.GIAOVIEN;
import com.thitracnghiem.app.model.SINHVIEN;
import com.thitracnghiem.app.service.GiaoVienService;
import com.thitracnghiem.app.service.SinhVienService;
import com.thitracnghiem.app.service.TaiKhoanService;

@Controller
@RequestMapping("/admin/taikhoan")
public class TaiKhoanController {

    @Autowired
    private GiaoVienService giaoVienService;
    
    @Autowired
    private SinhVienService sinhVienService;
    
    @Autowired
    private TaiKhoanService taiKhoanService;
    
    @Autowired
    private SessionManager sessionManager;

    /**
     * Hiển thị trang quản lý tài khoản
     */
    @GetMapping
    public String listTaiKhoan(Model model) {
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            // Lấy danh sách tài khoản SQL Server theo role
            List<TaiKhoanService.SqlAccount> teacherAccounts = taiKhoanService.getTeacherAccounts();
            List<TaiKhoanService.SqlAccount> studentAccounts = taiKhoanService.getStudentAccounts();
            
            // Lấy danh sách giáo viên và sinh viên từ database (để tạo tài khoản mới)
            List<GIAOVIEN> allGiaoVien = giaoVienService.getAllGiaoVien();
            List<SINHVIEN> allSinhVien = sinhVienService.getAllSinhVien();
            
            // Thêm dữ liệu vào model
            model.addAttribute("teacherAccounts", teacherAccounts);
            model.addAttribute("studentAccounts", studentAccounts);
            model.addAttribute("allGiaoVien", allGiaoVien);
            model.addAttribute("allSinhVien", allSinhVien);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/taikhoan/list";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "admin/taikhoan/list";
        }
    }

    /**
     * API: Tạo tài khoản cho giáo viên
     */
    @PostMapping("/api/create-teacher-account")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTeacherAccount(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String username = requestData.get("username");
            String password = requestData.get("password");
            
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username và password không được để trống");
                return ResponseEntity.ok(response);
            }
            
            boolean success = taiKhoanService.createTeacherAccount(username.trim(), password.trim());
            if (success) {
                response.put("success", true);
                response.put("message", "Tạo tài khoản giáo viên thành công: " + username);
            } else {
                response.put("success", false);
                response.put("message", "Không thể tạo tài khoản giáo viên");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tạo tài khoản: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Tạo tài khoản cho sinh viên
     */
    @PostMapping("/api/create-student-account")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createStudentAccount(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String username = requestData.get("username");
            String password = requestData.get("password");
            
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username và password không được để trống");
                return ResponseEntity.ok(response);
            }
            
            boolean success = taiKhoanService.createStudentAccount(username.trim(), password.trim());
            if (success) {
                response.put("success", true);
                response.put("message", "Tạo tài khoản sinh viên thành công: " + username);
            } else {
                response.put("success", false);
                response.put("message", "Không thể tạo tài khoản sinh viên");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tạo tài khoản: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Reset mật khẩu
     */
    @PostMapping("/api/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // JavaScript gửi 'userid' và 'newpassword'
            String username = requestData.get("userid");
            String newPassword = requestData.get("newpassword");
            
            System.out.println("🔧 Reset password request: userid=" + username + ", newpassword=" + (newPassword != null ? "***" : "null"));
            
            if (username == null || username.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username và mật khẩu mới không được để trống");
                return ResponseEntity.ok(response);
            }
            
            boolean success = taiKhoanService.resetPassword(username.trim(), newPassword.trim());
            if (success) {
                response.put("success", true);
                response.put("message", "Reset mật khẩu thành công cho: " + username);
            } else {
                response.put("success", false);
                response.put("message", "Không thể reset mật khẩu");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Lỗi reset mật khẩu: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi reset mật khẩu: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Xóa tài khoản
     */
    @PostMapping("/api/delete-account/{username}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username không được để trống");
                return ResponseEntity.ok(response);
            }
            
            boolean success = taiKhoanService.deleteAccount(username.trim());
            if (success) {
                response.put("success", true);
                response.put("message", "Xóa tài khoản thành công: " + username);
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa tài khoản");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xóa tài khoản: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
