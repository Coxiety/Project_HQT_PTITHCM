package com.thitracnghiem.app.controller;

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
import com.thitracnghiem.app.model.LOP;
import com.thitracnghiem.app.model.SINHVIEN;
import com.thitracnghiem.app.service.LopService;
import com.thitracnghiem.app.service.SinhVienService;

@Controller
@RequestMapping("/admin/lophoc")
public class LopController {

    @Autowired
    private LopService lopService;
    
    @Autowired
    private SinhVienService sinhVienService;
    
    @Autowired
    private SessionManager sessionManager;

    /**
     * Hiển thị danh sách lớp học
     */
    @GetMapping
    public String listLop(Model model) {
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            List<LOP> danhSachLop = lopService.getAllLop();
            model.addAttribute("danhSachLop", danhSachLop);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/lophoc/list";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "admin/lophoc/list";
        }
    }

    /**
     * Quản lý sinh viên theo lớp
     */
    @GetMapping("/{maLop}/sinhvien")
    public String manageSinhVienByLop(@PathVariable String maLop, Model model) {
        // Kiểm tra quyền admin
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            // Kiểm tra lớp có tồn tại không
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                model.addAttribute("error", "Không tìm thấy lớp học với mã: " + maLop);
                return "redirect:/admin/lophoc";
            }

            // Lấy danh sách sinh viên trong lớp
            List<SINHVIEN> danhSachSinhVien = sinhVienService.getAllSinhVienByLop(maLop);
            
            model.addAttribute("danhSachSinhVien", danhSachSinhVien);
            model.addAttribute("lop", lop);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/lophoc/sinhvien";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "redirect:/admin/lophoc";
        }
    }

    /**
     * API: Lấy thông tin sinh viên để xem chi tiết (Modal)
     */
    @GetMapping("/{maLop}/sinhvien/api/view/{maSV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinhVienDetails(@PathVariable String maLop, @PathVariable String maSV) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhVien = sinhVienService.getSinhVienById(maSV);
            if (sinhVien == null || !sinhVien.getMALOP().equals(maLop)) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sinh viên trong lớp này");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> svData = new HashMap<>();
            svData.put("masv", sinhVien.getMASV());
            svData.put("ho", sinhVien.getHO());
            svData.put("ten", sinhVien.getTEN());
            svData.put("ngaysinh", sinhVien.getNGAYSINH() != null ? sinhVien.getNGAYSINH().toString() : "");
            svData.put("diachi", sinhVien.getDIACHI());
            svData.put("malop", sinhVien.getMALOP());

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
    @GetMapping("/{maLop}/sinhvien/api/edit/{maSV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSinhVienForEdit(@PathVariable String maLop, @PathVariable String maSV) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN sinhVien = sinhVienService.getSinhVienById(maSV);
            if (sinhVien == null || !sinhVien.getMALOP().equals(maLop)) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sinh viên trong lớp này");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> svData = new HashMap<>();
            svData.put("masv", sinhVien.getMASV());
            svData.put("ho", sinhVien.getHO());
            svData.put("ten", sinhVien.getTEN());
            svData.put("ngaysinh", sinhVien.getNGAYSINH() != null ? sinhVien.getNGAYSINH().toString() : "");
            svData.put("diachi", sinhVien.getDIACHI());
            svData.put("malop", sinhVien.getMALOP());

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
     * API: Thêm sinh viên mới vào lớp (Modal)
     */
    @PostMapping("/{maLop}/sinhvien/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addSinhVienToClass(@PathVariable String maLop, @RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Kiểm tra lớp có tồn tại không
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy lớp học");
                return ResponseEntity.ok(response);
            }

            SINHVIEN sinhVien = new SINHVIEN();
            sinhVien.setHO(requestData.get("ho"));
            sinhVien.setTEN(requestData.get("ten"));
            sinhVien.setDIACHI(requestData.get("diachi"));
            sinhVien.setMALOP(maLop); // Gán lớp cố định

            // Parse ngày sinh
            String ngaySinhStr = requestData.get("ngaysinh");
            if (ngaySinhStr != null && !ngaySinhStr.trim().isEmpty()) {
                try {
                    java.sql.Date ngaySinh = java.sql.Date.valueOf(ngaySinhStr);
                    sinhVien.setNGAYSINH(ngaySinh);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Định dạng ngày sinh không hợp lệ");
                    return ResponseEntity.ok(response);
                }
            }

            // Thêm sinh viên với năm hiện tại
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            boolean success = sinhVienService.addSinhVien(sinhVien, currentYear);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Thêm sinh viên thành công");
                response.put("masv", sinhVien.getMASV()); // Trả về mã sinh viên được tạo
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
     * API: Cập nhật sinh viên (Modal)
     */
    @PostMapping("/{maLop}/sinhvien/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateSinhVienInClass(@PathVariable String maLop, @RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maSV = requestData.get("masv");
            SINHVIEN existingSinhVien = sinhVienService.getSinhVienById(maSV);
            
            if (existingSinhVien == null || !existingSinhVien.getMALOP().equals(maLop)) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sinh viên trong lớp này");
                return ResponseEntity.ok(response);
            }

            SINHVIEN sinhVien = new SINHVIEN();
            sinhVien.setMASV(maSV);
            sinhVien.setHO(requestData.get("ho"));
            sinhVien.setTEN(requestData.get("ten"));
            sinhVien.setDIACHI(requestData.get("diachi"));
            sinhVien.setMALOP(maLop); // Giữ nguyên lớp

            // Parse ngày sinh
            String ngaySinhStr = requestData.get("ngaysinh");
            if (ngaySinhStr != null && !ngaySinhStr.trim().isEmpty()) {
                try {
                    java.sql.Date ngaySinh = java.sql.Date.valueOf(ngaySinhStr);
                    sinhVien.setNGAYSINH(ngaySinh);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Định dạng ngày sinh không hợp lệ");
                    return ResponseEntity.ok(response);
                }
            }

            boolean success = sinhVienService.updateSinhVien(sinhVien);
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật sinh viên thành công");
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
     * API: Xóa sinh viên khỏi lớp (Modal)
     */
    @PostMapping("/{maLop}/sinhvien/api/delete/{maSV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteSinhVienFromClass(@PathVariable String maLop, @PathVariable String maSV) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            SINHVIEN existingSinhVien = sinhVienService.getSinhVienById(maSV);
            
            if (existingSinhVien == null || !existingSinhVien.getMALOP().equals(maLop)) {
                response.put("success", false);
                response.put("message", "Không tìm thấy sinh viên trong lớp này");
                return ResponseEntity.ok(response);
            }

            boolean success = sinhVienService.deleteSinhVien(maSV);
            if (success) {
                response.put("success", true);
                response.put("message", "Xóa sinh viên thành công");
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
     * API: Lấy thông tin lớp để xem chi tiết (Modal)
     */
    @GetMapping("/api/view/{maLop}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLopDetails(@PathVariable String maLop) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy lớp học");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> lopData = new HashMap<>();
            lopData.put("malop", lop.getMALOP());
            lopData.put("tenlop", lop.getTENLOP());

            response.put("success", true);
            response.put("lop", lopData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy thông tin lớp để chỉnh sửa (Modal)
     */
    @GetMapping("/api/edit/{maLop}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLopForEdit(@PathVariable String maLop) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy lớp học");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> lopData = new HashMap<>();
            lopData.put("malop", lop.getMALOP());
            lopData.put("tenlop", lop.getTENLOP());

            response.put("success", true);
            response.put("lop", lopData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Thêm lớp mới (Modal)
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addLopApi(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maLop = requestData.get("malop");
            String tenLop = requestData.get("tenlop");

            // Kiểm tra mã lớp đã tồn tại chưa
            if (lopService.isLopExists(maLop)) {
                response.put("success", false);
                response.put("message", "Mã lớp đã tồn tại");
                return ResponseEntity.ok(response);
            }

            LOP lop = new LOP();
            lop.setMALOP(maLop);
            lop.setTENLOP(tenLop);

            boolean success = lopService.addLop(lop);
            if (success) {
                response.put("success", true);
                response.put("message", "Thêm lớp thành công");
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
     * API: Cập nhật lớp (Modal)
     */
    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateLop(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isAdmin()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            LOP lop = new LOP();
            lop.setMALOP(requestData.get("malop")); // Không thay đổi mã lớp khi update
            lop.setTENLOP(requestData.get("tenlop"));

            boolean success = lopService.updateLop(lop);
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật lớp thành công");
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
     * Hiển thị form thêm lớp (Legacy - giữ lại cho tương thích)
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("lop", new LOP());
        model.addAttribute("user", sessionManager.getCurrentUser());
        return "admin/lophoc/form";
    }

    /**
     * Xử lý thêm lớp (Legacy)
     */
    @PostMapping("/add")
    public String addLop(@ModelAttribute LOP lop, RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            // Kiểm tra mã lớp đã tồn tại chưa
            if (lopService.isLopExists(lop.getMALOP())) {
                redirectAttributes.addFlashAttribute("error", "Mã lớp đã tồn tại");
                return "redirect:/admin/lophoc";
            }

            boolean success = lopService.addLop(lop);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Thêm lớp thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
        }

        return "redirect:/admin/lophoc";
    }

    /**
     * Hiển thị form sửa lớp (Legacy)
     */
    @GetMapping("/edit/{maLop}")
    public String showEditForm(@PathVariable String maLop, Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                model.addAttribute("error", "Không tìm thấy lớp học");
                return "redirect:/admin/lophoc";
            }

            model.addAttribute("lop", lop);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/lophoc/form";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "redirect:/admin/lophoc";
        }
    }

    /**
     * Xử lý cập nhật lớp (Legacy)
     */
    @PostMapping("/edit")
    public String updateLop(@ModelAttribute LOP lop, RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            boolean success = lopService.updateLop(lop);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật lớp thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
        }

        return "redirect:/admin/lophoc";
    }

    /**
     * Xóa lớp
     */
    @PostMapping("/delete/{maLop}")
    public String deleteLop(@PathVariable String maLop, RedirectAttributes redirectAttributes) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            boolean success = lopService.deleteLop(maLop);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Xóa lớp thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.GENERAL_ERROR_MSG);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/lophoc";
    }

    /**
     * Xem chi tiết lớp (Legacy)
     */
    @GetMapping("/view/{maLop}")
    public String viewLop(@PathVariable String maLop, Model model) {
        if (!sessionManager.isAdmin()) {
            return "redirect:/admin/dashboard";
        }

        try {
            LOP lop = lopService.getLopById(maLop);
            if (lop == null) {
                model.addAttribute("error", "Không tìm thấy lớp học");
                return "redirect:/admin/lophoc";
            }

            model.addAttribute("lop", lop);
            model.addAttribute("user", sessionManager.getCurrentUser());
            return "admin/lophoc/view";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "redirect:/admin/lophoc";
        }
    }
} 