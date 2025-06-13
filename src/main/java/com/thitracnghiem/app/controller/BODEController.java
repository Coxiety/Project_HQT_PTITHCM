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
import com.thitracnghiem.app.model.BODE;
import com.thitracnghiem.app.model.MONHOC;
import com.thitracnghiem.app.service.BODEService;
import com.thitracnghiem.app.service.MonHocService;

@Controller
@RequestMapping("/teacher/cauhoi")
public class BODEController {

    @Autowired
    private BODEService bodeService;
    
    @Autowired
    private MonHocService monHocService;
    
    @Autowired
    private SessionManager sessionManager;

    /**
     * Hiển thị trang quản lý câu hỏi
     */
    @GetMapping
    public String listCauHoi(Model model) {
        // Kiểm tra quyền teacher
        if (!sessionManager.isTeacher()) {
            return "redirect:/teacher/dashboard";
        }

        try {
            String maGV = sessionManager.getCurrentUser().getUserId();
            
            // Lấy danh sách tất cả môn học
            List<MONHOC> allMonHoc = monHocService.getAllMonHoc();
            
            // Load tất cả câu hỏi của giáo viên, nhóm theo môn học
            Map<String, List<BODE>> cauHoiByMonHoc = new HashMap<>();
            Map<String, Integer> cauHoiCount = new HashMap<>();
            int totalCauHoi = 0; // Tính tổng số câu hỏi
            
            for (MONHOC mh : allMonHoc) {
                List<BODE> cauHoiList = bodeService.getCauHoiByGiaoVienAndMonHoc(maGV, mh.getMAMH());
                cauHoiByMonHoc.put(mh.getMAMH(), cauHoiList);
                cauHoiCount.put(mh.getMAMH(), cauHoiList.size());
                totalCauHoi += cauHoiList.size();
            }
            
            // Thêm dữ liệu vào model
            model.addAttribute("allMonHoc", allMonHoc);
            model.addAttribute("cauHoiByMonHoc", cauHoiByMonHoc);
            model.addAttribute("cauHoiCount", cauHoiCount);
            model.addAttribute("totalCauHoi", totalCauHoi);
            model.addAttribute("user", sessionManager.getCurrentUser());
            
            return "teacher/cauhoi/list";
        } catch (Exception e) {
            model.addAttribute("error", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return "teacher/cauhoi/list";
        }
    }

    /**
     * API: Lấy danh sách câu hỏi theo môn học
     */
    @GetMapping("/api/monhoc/{maMH}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCauHoiByMonHoc(@PathVariable String maMH) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maGV = sessionManager.getCurrentUser().getUserId();
            List<BODE> cauHoiList = bodeService.getCauHoiByGiaoVienAndMonHoc(maGV, maMH);
            
            response.put("success", true);
            response.put("data", cauHoiList);
            response.put("message", "Tải thành công " + cauHoiList.size() + " câu hỏi");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tải câu hỏi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Thêm câu hỏi mới
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCauHoi(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maGV = sessionManager.getCurrentUser().getUserId();
            
            // Manual mapping từ request data sang BODE object
            BODE bode = new BODE();
            bode.setMAMH((String) requestData.get("mamh"));
            bode.setTRINHDO((String) requestData.get("trinhdo"));
            bode.setNOIDUNG((String) requestData.get("noidung"));
            bode.setA((String) requestData.get("a"));
            bode.setB((String) requestData.get("b"));
            bode.setC((String) requestData.get("c"));
            bode.setD((String) requestData.get("d"));
            bode.setDAP_AN((String) requestData.get("dap_an"));
            bode.setMAGV(maGV); // Đảm bảo câu hỏi thuộc về giáo viên hiện tại
            
            boolean success = bodeService.addCauHoi(bode);
            if (success) {
                response.put("success", true);
                response.put("message", "Thêm câu hỏi thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể thêm câu hỏi");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi thêm câu hỏi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Cập nhật câu hỏi
     */
    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCauHoi(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maGV = sessionManager.getCurrentUser().getUserId();
            
            // Manual mapping từ request data sang BODE object
            BODE bode = new BODE();
            bode.setCAUHOI(Integer.valueOf(requestData.get("cauhoi").toString()));
            bode.setMAMH((String) requestData.get("mamh"));
            bode.setTRINHDO((String) requestData.get("trinhdo"));
            bode.setNOIDUNG((String) requestData.get("noidung"));
            bode.setA((String) requestData.get("a"));
            bode.setB((String) requestData.get("b"));
            bode.setC((String) requestData.get("c"));
            bode.setD((String) requestData.get("d"));
            bode.setDAP_AN((String) requestData.get("dap_an"));
            bode.setMAGV(maGV); // Đảm bảo chỉ cập nhật câu hỏi của mình
            
            boolean success = bodeService.updateCauHoi(bode);
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật câu hỏi thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật câu hỏi");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi cập nhật câu hỏi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Xóa câu hỏi
     */
    @PostMapping("/api/delete/{cauHoi}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCauHoi(@PathVariable Integer cauHoi) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String maGV = sessionManager.getCurrentUser().getUserId();
            
            boolean success = bodeService.deleteCauHoi(cauHoi, maGV);
            if (success) {
                response.put("success", true);
                response.put("message", "Xóa câu hỏi thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa câu hỏi");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xóa câu hỏi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy thông tin câu hỏi theo ID
     */
    @GetMapping("/api/detail/{cauHoi}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCauHoiDetail(@PathVariable Integer cauHoi) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            BODE bode = bodeService.getCauHoiById(cauHoi);
            if (bode != null) {
                // Kiểm tra xem câu hỏi có thuộc về giáo viên hiện tại không
                String maGV = sessionManager.getCurrentUser().getUserId();
                System.out.println("🔍 Debug - Current teacher: '" + maGV + "', Question owner: '" + bode.getMAGV() + "'");
                System.out.println("🔍 Debug - Length check: " + maGV.length() + " vs " + (bode.getMAGV() != null ? bode.getMAGV().length() : "null"));
                
                if (maGV.trim().equals(bode.getMAGV() != null ? bode.getMAGV().trim() : "")) {
                    response.put("success", true);
                    response.put("data", bode);
                } else {
                    response.put("success", false);
                    response.put("message", "Bạn không có quyền xem câu hỏi này");
                }
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy câu hỏi");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi tải thông tin câu hỏi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 