package com.thitracnghiem.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
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
import com.thitracnghiem.app.model.MONHOC;
import com.thitracnghiem.app.service.MonHocService;

@Controller
@RequestMapping("/admin/monhoc")
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    @Autowired
    private SessionManager sessionManager;

    /**
     * Hiển thị trang quản lý môn học
     */
    @GetMapping
    public String showMonHocManagement(Model model) {
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                return "redirect:/auth/login";
            }

            List<MONHOC> monHocList = monHocService.getAllMonHoc();
            model.addAttribute("monHocList", monHocList);
            model.addAttribute("newMonHoc", new MONHOC());
            
            return "admin/monhoc/list";
            
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.USER_GENERAL_ERROR);
            return "admin/monhoc/list";
        }
    }

    // ==============================================
    // TEST ENDPOINT - FOR DEBUGGING
    // ==============================================
    
    /**
     * Test endpoint to verify JSON mapping
     */
    @PostMapping("/api/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testMapping(@RequestBody MONHOC monHoc) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("=== TEST ENDPOINT ===");
        System.out.println("Received MONHOC: " + monHoc);
        System.out.println("MAMH: '" + monHoc.getMAMH() + "'");
        System.out.println("TENMH: '" + monHoc.getTENMH() + "'");
        
        response.put("success", true);
        response.put("received_mamh", monHoc.getMAMH());
        response.put("received_tenmh", monHoc.getTENMH());
        response.put("mamh_null", monHoc.getMAMH() == null);
        response.put("tenmh_null", monHoc.getTENMH() == null);
        return ResponseEntity.ok(response);
    }

    // ==============================================
    // REST API ENDPOINTS - CHO AJAX CALLS
    // ==============================================

    /**
     * REST API: Lấy danh sách tất cả môn học
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonHocList() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                response.put("success", false);
                response.put("message", ErrorCode.USER_ACCESS_DENIED);
                return ResponseEntity.status(403).body(response);
            }

            List<MONHOC> monHocList = monHocService.getAllMonHoc();
            response.put("success", true);
            response.put("data", monHocList);
            response.put("message", SuccessCode.MH_LIST_LOADED_MSG);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_GENERAL_ERROR);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * REST API: Lấy thông tin môn học theo mã
     */
    @GetMapping("/api/{maMH}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonHocById(@PathVariable String maMH) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                response.put("success", false);
                response.put("message", ErrorCode.USER_ACCESS_DENIED);
                return ResponseEntity.status(403).body(response);
            }

            MONHOC monHoc = monHocService.getMonHocById(maMH);
            if (monHoc != null) {
                response.put("success", true);
                response.put("data", monHoc);
                response.put("message", SuccessCode.MH_DETAILS_LOADED_MSG);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.USER_MH_NOT_FOUND);
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_GENERAL_ERROR);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * REST API: Thêm môn học mới
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addMonHoc(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Debug logging  
            System.out.println("DEBUG - Controller received request:");
            System.out.println("Request data: " + requestData);
            
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                response.put("success", false);
                response.put("message", ErrorCode.USER_ACCESS_DENIED);
                return ResponseEntity.status(403).body(response);
            }

            // Tạo object MONHOC từ Map
            MONHOC monHoc = new MONHOC();
            monHoc.setMAMH(requestData.get("MAMH"));
            monHoc.setTENMH(requestData.get("TENMH"));
            
            System.out.println("DEBUG - Created MONHOC object:");
            System.out.println("MAMH: '" + monHoc.getMAMH() + "'");
            System.out.println("TENMH: '" + monHoc.getTENMH() + "'");

            boolean success = monHocService.addMonHoc(monHoc);
            if (success) {
                response.put("success", true);
                response.put("message", SuccessCode.MH_CREATE_SUCCESS_MSG);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.USER_GENERAL_ERROR);
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (DuplicateKeyException e) {
            response.put("success", false);
            if (e.getMessage().contains("MH_002")) {
                response.put("message", ErrorCode.MH_DUPLICATE_ID_MSG);
            } else if (e.getMessage().contains("MH_003")) {
                response.put("message", ErrorCode.MH_DUPLICATE_NAME_MSG);
            } else {
                response.put("message", "Dữ liệu đã tồn tại");
            }
            return ResponseEntity.status(400).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", ErrorCode.MH_REQUIRED_FIELDS_MSG);
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_GENERAL_ERROR);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * REST API: Cập nhật thông tin môn học
     */
    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMonHoc(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                response.put("success", false);
                response.put("message", ErrorCode.USER_ACCESS_DENIED);
                return ResponseEntity.status(403).body(response);
            }

            // Tạo object MONHOC từ Map
            MONHOC monHoc = new MONHOC();
            monHoc.setMAMH(requestData.get("MAMH"));
            monHoc.setTENMH(requestData.get("TENMH"));

            boolean success = monHocService.updateMonHoc(monHoc);
            if (success) {
                response.put("success", true);
                response.put("message", SuccessCode.MH_UPDATE_SUCCESS_MSG);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.USER_GENERAL_ERROR);
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (DuplicateKeyException e) {
            response.put("success", false);
            response.put("message", ErrorCode.MH_DUPLICATE_NAME_MSG);
            return ResponseEntity.status(400).body(response);
        } catch (EmptyResultDataAccessException e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_MH_NOT_FOUND);
            return ResponseEntity.status(404).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", ErrorCode.MH_REQUIRED_FIELDS_MSG);
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_GENERAL_ERROR);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * REST API: Xóa môn học
     */
    @PostMapping("/api/delete/{maMH}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteMonHoc(@PathVariable String maMH) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                response.put("success", false);
                response.put("message", ErrorCode.USER_ACCESS_DENIED);
                return ResponseEntity.status(403).body(response);
            }

            boolean success = monHocService.deleteMonHoc(maMH);
            if (success) {
                response.put("success", true);
                response.put("message", SuccessCode.MH_DELETE_SUCCESS_MSG);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", ErrorCode.USER_GENERAL_ERROR);
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (DataIntegrityViolationException e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_MH_CANNOT_DELETE);
            response.put("details", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (EmptyResultDataAccessException e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_MH_NOT_FOUND);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", ErrorCode.USER_GENERAL_ERROR);
            return ResponseEntity.status(500).body(response);
        }
    }

    // ==============================================
    // FORM-BASED ENDPOINTS - CHO LEGACY SUPPORT
    // ==============================================

    /**
     * Form-based: Thêm môn học mới
     */
    @PostMapping("/add")
    public String addMonHocForm(@ModelAttribute MONHOC monHoc, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_ACCESS_DENIED);
                return "redirect:/auth/login";
            }

            boolean success = monHocService.addMonHoc(monHoc);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.MH_CREATE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
            }
            
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("MH_002")) {
                redirectAttributes.addFlashAttribute("error", ErrorCode.MH_DUPLICATE_ID_MSG);
            } else if (e.getMessage().contains("MH_003")) {
                redirectAttributes.addFlashAttribute("error", ErrorCode.MH_DUPLICATE_NAME_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu đã tồn tại");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.MH_REQUIRED_FIELDS_MSG);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
        }
        
        return "redirect:/admin/monhoc";
    }

    /**
     * Form-based: Cập nhật thông tin môn học
     */
    @PostMapping("/update")
    public String updateMonHocForm(@ModelAttribute MONHOC monHoc, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_ACCESS_DENIED);
                return "redirect:/auth/login";
            }

            boolean success = monHocService.updateMonHoc(monHoc);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.MH_UPDATE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
            }
            
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.MH_DUPLICATE_NAME_MSG);
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_MH_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.MH_REQUIRED_FIELDS_MSG);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
        }
        
        return "redirect:/admin/monhoc";
    }

    /**
     * Form-based: Xóa môn học
     */
    @PostMapping("/delete/{maMH}")
    public String deleteMonHocForm(@PathVariable String maMH, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra quyền admin
            if (!sessionManager.isAdmin()) {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_ACCESS_DENIED);
                return "redirect:/auth/login";
            }

            boolean success = monHocService.deleteMonHoc(maMH);
            if (success) {
                redirectAttributes.addFlashAttribute("success", SuccessCode.MH_DELETE_SUCCESS_MSG);
            } else {
                redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
            }
            
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_MH_CANNOT_DELETE);
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_MH_NOT_FOUND);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", ErrorCode.USER_GENERAL_ERROR);
        }
        
        return "redirect:/admin/monhoc";
    }
} 