package com.thitracnghiem.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.GIAOVIEN;
import com.thitracnghiem.app.model.GIAOVIEN_DANGKY;
import com.thitracnghiem.app.model.LOP;
import com.thitracnghiem.app.model.MONHOC;
import com.thitracnghiem.app.service.GiaoVienDangKyService;
import com.thitracnghiem.app.service.GiaoVienService;
import com.thitracnghiem.app.service.LopService;
import com.thitracnghiem.app.service.MonHocService;

@Controller
@RequestMapping("/admin/chuanbithi")
public class ChuanBiThiController {    @Autowired
    private MonHocService monHocService;
    
    @Autowired
    private GiaoVienService giaoVienService;
    
    @Autowired
    private LopService lopService;
    
    @Autowired
    private GiaoVienDangKyService dangKyService;
    
    @Autowired
    private SessionManager sessionManager;
    
    /**
     * Hiển thị trang chuẩn bị thi với danh sách môn học
     */
    @GetMapping
    public String showChuanBiThi(Model model) {
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            List<MONHOC> danhSachMonHoc = monHocService.getAllMonHoc();
            model.addAttribute("danhSachMonHoc", danhSachMonHoc);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/chuanbithi/list";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "admin/chuanbithi/list";
        }
    }
    
    /**
     * API: Lấy danh sách giáo viên
     */
    @GetMapping("/api/giaovien")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllGiaoVien() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            List<GIAOVIEN> danhSachGiaoVien = giaoVienService.getAllGiaoVien();
            response.put("success", true);
            response.put("giaoviens", danhSachGiaoVien);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * API: Lấy danh sách lớp học
     */
    @GetMapping("/api/lop")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLop() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            List<LOP> danhSachLop = lopService.getAllLop();
            response.put("success", true);
            response.put("lops", danhSachLop);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * API: Lấy danh sách môn học
     */
    @GetMapping("/api/monhoc")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllMonHoc() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            List<MONHOC> danhSachMonHoc = monHocService.getAllMonHoc();
            response.put("success", true);
            response.put("monhocs", danhSachMonHoc);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
      /**
     * API: Đăng ký thi
     */
    @PostMapping("/api/dangky")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dangKyThi(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Tạo đối tượng GIAOVIEN_DANGKY từ dữ liệu form
            GIAOVIEN_DANGKY dangKy = new GIAOVIEN_DANGKY();
            dangKy.setMAGV(requestData.get("magv"));
            dangKy.setMALOP(requestData.get("malop"));
            dangKy.setMAMH(requestData.get("mamh"));
            dangKy.setTRINHDO(requestData.get("trinhdo"));
            dangKy.setLAN(Short.parseShort(requestData.get("lan")));
            dangKy.setSOCAUTHI(Short.parseShort(requestData.get("socauthi")));
            dangKy.setTHOIGIAN(Short.parseShort(requestData.get("thoigian")));
            
            // Parse ngày thi
            String ngayThiStr = requestData.get("ngaythi");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date ngayThi = formatter.parse(ngayThiStr);
            dangKy.setNGAYTHI(ngayThi);
            
            // Lưu vào database
            boolean success = dangKyService.addDangKyThi(dangKy);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Đăng ký thi thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Không thể đăng ký thi. Vui lòng thử lại.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
      /**
     * API: Lấy danh sách đăng ký thi
     */
    @GetMapping("/api/dangky")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllDangKyThi() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            List<GIAOVIEN_DANGKY> danhSachDangKy = dangKyService.getAllDangKyThi();
            response.put("success", true);
            response.put("registrations", danhSachDangKy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * API: Xóa đăng ký thi
     */
    @DeleteMapping("/api/dangky/{magv}/{malop}/{mamh}/{lan}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDangKyThi(
            @PathVariable String magv,
            @PathVariable String malop,
            @PathVariable String mamh,
            @PathVariable int lan) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            boolean success = dangKyService.deleteDangKyThi(magv, malop, mamh, (short) lan);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Xóa đăng ký thi thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa đăng ký thi. Vui lòng thử lại.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 