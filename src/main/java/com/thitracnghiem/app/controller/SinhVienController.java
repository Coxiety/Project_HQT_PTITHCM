package com.thitracnghiem.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.exception.SuccessCode;
import com.thitracnghiem.app.model.LOP;
import com.thitracnghiem.app.model.SINHVIEN;
import com.thitracnghiem.app.service.LopService;
import com.thitracnghiem.app.service.SinhVienService;

@Controller
@RequestMapping("/admin/sinhvien")
public class SinhVienController {

    @Autowired
    private SinhVienService sinhVienService;
    
    @Autowired
    private LopService lopService;
    
    @Autowired
    private SessionManager sessionManager;

    /**
     * Hiển thị danh sách sinh viên
     */
    @GetMapping
    public String listSinhVien(Model model) {
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            List<SINHVIEN> danhSachSinhVien = sinhVienService.getAllSinhVien();
            model.addAttribute("danhSachSinhVien", danhSachSinhVien);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/sinhvien/list";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "admin/sinhvien/list";
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
     * Hiển thị form thêm sinh viên
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("sinhvien", new SINHVIEN());
        model.addAttribute("user", sessionManager.getCurrentUser());
        return "admin/sinhvien/form";
    }

    /**
     * Xử lý thêm sinh viên
     */
    @PostMapping("/add")
    public String addSinhVien(@ModelAttribute SINHVIEN sinhvien, 
                             RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            boolean success = sinhVienService.addSinhVien(sinhvien);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.SV_CREATE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
        }

        return "redirect:/admin/sinhvien";
    }

    /**
     * API: Lấy thông tin sinh viên để xem chi tiết (Modal)
     */
    @GetMapping("/api/view/{maSV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinhVienDetails(@PathVariable String maSV) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhvien = sinhVienService.getSinhVienById(maSV);
            if (sinhvien == null) {
                response.put("success", false);
                response.put("message", ErrorCode.SV_NOT_FOUND_MSG);
                return ResponseEntity.ok(response);
            }

            Map<String, Object> svData = new HashMap<>();
            svData.put("masv", sinhvien.getMASV());
            svData.put("ho", sinhvien.getHO());
            svData.put("ten", sinhvien.getTEN());
            svData.put("ngaysinh", sinhvien.getNGAYSINH());
            svData.put("diachi", sinhvien.getDIACHI());
            svData.put("malop", sinhvien.getMALOP());

            response.put("success", true);
            response.put("sinhvien", svData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy thông tin sinh viên để chỉnh sửa (Modal)
     */
    @GetMapping("/api/edit/{maSV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinhVienForEdit(@PathVariable String maSV) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhvien = sinhVienService.getSinhVienById(maSV);
            if (sinhvien == null) {
                response.put("success", false);
                response.put("message", ErrorCode.SV_NOT_FOUND_MSG);
                return ResponseEntity.ok(response);
            }

            Map<String, Object> svData = new HashMap<>();
            svData.put("masv", sinhvien.getMASV());
            svData.put("ho", sinhvien.getHO());
            svData.put("ten", sinhvien.getTEN());
            svData.put("ngaysinh", sinhvien.getNGAYSINH());
            svData.put("diachi", sinhvien.getDIACHI());
            svData.put("malop", sinhvien.getMALOP());

            response.put("success", true);
            response.put("sinhvien", svData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Thêm sinh viên mới (Modal)
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addSinhVienApi(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhvien = new SINHVIEN();
            // Không set MASV vì sẽ được tạo tự động
            sinhvien.setHO(requestData.get("ho"));
            sinhvien.setTEN(requestData.get("ten"));
            
            // Xử lý ngày sinh (có thể null)
            if (requestData.get("ngaysinh") != null && !requestData.get("ngaysinh").isEmpty()) {
                sinhvien.setNGAYSINH(java.sql.Date.valueOf(requestData.get("ngaysinh")));
            }
            
            sinhvien.setDIACHI(requestData.get("diachi"));
            sinhvien.setMALOP(requestData.get("malop"));

            // Lấy năm nhập học
            int namNhapHoc = Integer.parseInt(requestData.get("namNhapHoc"));

            boolean success = sinhVienService.addSinhVien(sinhvien, namNhapHoc);
            if (success) {
                response.put("success", true);
                response.put("message", SuccessCode.SV_CREATE_SUCCESS_MSG);
                response.put("masv", sinhvien.getMASV()); // Trả về mã SV đã tạo
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.GENERAL_ERROR_MSG);
            }
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Năm nhập học phải là số nguyên");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Cập nhật sinh viên (Modal)
     */
    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateSinhVien(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhvien = new SINHVIEN();
            sinhvien.setMASV(requestData.get("masv")); // Không thay đổi mã SV khi update
            sinhvien.setHO(requestData.get("ho"));
            sinhvien.setTEN(requestData.get("ten"));
            
            // Xử lý ngày sinh (có thể null)
            if (requestData.get("ngaysinh") != null && !requestData.get("ngaysinh").isEmpty()) {
                sinhvien.setNGAYSINH(java.sql.Date.valueOf(requestData.get("ngaysinh")));
            }
            
            sinhvien.setDIACHI(requestData.get("diachi"));
            sinhvien.setMALOP(requestData.get("malop"));

            boolean success = sinhVienService.updateSinhVien(sinhvien);
            if (success) {
                response.put("success", true);
                response.put("message", SuccessCode.SV_UPDATE_SUCCESS_MSG);
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.GENERAL_ERROR_MSG);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Hiển thị form sửa sinh viên (Legacy - giữ lại cho tương thích)
     */
    @GetMapping("/edit/{maSV}")
    public String showEditForm(@PathVariable String maSV, Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            SINHVIEN sinhvien = sinhVienService.getSinhVienById(maSV);
            if (sinhvien == null) {
                model.addAttribute("error", ErrorCode.SV_NOT_FOUND_MSG);
                return "redirect:/admin/sinhvien";
            }

            model.addAttribute("sinhvien", sinhvien);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/sinhvien/form";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "redirect:/admin/sinhvien";
        }
    }

    /**
     * Xử lý cập nhật sinh viên (Legacy)
     */
    @PostMapping("/edit")
    public String updateSinhVien(@ModelAttribute SINHVIEN sinhvien,
                                RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            boolean success = sinhVienService.updateSinhVien(sinhvien);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.SV_UPDATE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
        }

        return "redirect:/admin/sinhvien";
    }

    /**
     * Xóa sinh viên
     */
    @PostMapping("/delete/{maSV}")
    public String deleteSinhVien(@PathVariable String maSV, RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            boolean success = sinhVienService.deleteSinhVien(maSV);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.SV_DELETE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/sinhvien";
    }

    /**
     * Xem chi tiết sinh viên (Legacy - giữ lại cho tương thích)
     */
    @GetMapping("/view/{maSV}")
    public String viewSinhVien(@PathVariable String maSV, Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            SINHVIEN sinhvien = sinhVienService.getSinhVienById(maSV);
            if (sinhvien == null) {
                model.addAttribute("error", ErrorCode.SV_NOT_FOUND_MSG);
                return "redirect:/admin/sinhvien";
            }

            model.addAttribute("sinhvien", sinhvien);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/sinhvien/view";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "redirect:/admin/sinhvien";
        }
    }

    /**
     * API: Lấy danh sách sinh viên chưa có SQL login
     */
    @GetMapping("/api/sinhvien/chua-co-login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinhVienChuaCoLogin() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            List<SINHVIEN> danhSachChuaCoLogin = sinhVienService.getSinhVienChuaCoLogin();
            response.put("success", true);
            response.put("data", danhSachChuaCoLogin);
            response.put("message", "Lấy danh sách sinh viên chưa có login thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Tạo SQL login cho sinh viên với password "test"
     */
    @PostMapping("/api/sinhvien/tao-login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> taoLoginSinhVien(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String masv = requestData.get("masv");
            boolean success = sinhVienService.taoSqlLoginVoiPasswordTest(masv);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Tạo tài khoản thành công cho sinh viên: " + masv);
            } else {
                response.put("success", false);
                response.put("message", "Tạo tài khoản thất bại cho sinh viên: " + masv);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Tạo SQL login hàng loạt cho sinh viên
     */
    @PostMapping("/api/sinhvien/tao-login-hang-loat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> taoLoginSinhVienHangLoat(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            @SuppressWarnings("unchecked")
            List<String> danhSachMaSV = (List<String>) requestData.get("danhSachMaSV");
            
            int thanhCong = 0;
            int thatBai = 0;
            List<String> ketQuaChiTiet = new ArrayList<>();

            for (String masv : danhSachMaSV) {
                try {
                    boolean success = sinhVienService.taoSqlLoginVoiPasswordTest(masv);
                    if (success) {
                        thanhCong++;
                        ketQuaChiTiet.add(masv + ": Thành công");
                    } else {
                        thatBai++;
                        ketQuaChiTiet.add(masv + ": Thất bại");
                    }
                } catch (Exception e) {
                    thatBai++;
                    ketQuaChiTiet.add(masv + ": Lỗi - " + e.getMessage());
                }
            }

            response.put("success", true);
            response.put("thanhCong", thanhCong);
            response.put("thatBai", thatBai);
            response.put("ketQuaChiTiet", ketQuaChiTiet);
            response.put("message", "Hoàn thành tạo login hàng loạt. Thành công: " + thanhCong + ", Thất bại: " + thatBai);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 