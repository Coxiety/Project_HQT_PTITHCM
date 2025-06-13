package com.thitracnghiem.app.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.BODE;
import com.thitracnghiem.app.model.GIAOVIEN_DANGKY;
import com.thitracnghiem.app.model.LOP;
import com.thitracnghiem.app.model.MONHOC;
import com.thitracnghiem.app.service.BODEService;
import com.thitracnghiem.app.service.GiaoVienDangKyService;
import com.thitracnghiem.app.service.GiaoVienService;
import com.thitracnghiem.app.service.LopService;
import com.thitracnghiem.app.service.MonHocService;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private GiaoVienDangKyService dangKyService;

    @Autowired
    private GiaoVienService giaoVienService;

    @Autowired
    private LopService lopService;

    @Autowired
    private MonHocService monHocService;

    @Autowired
    private BODEService bodeService;



    /**
     * Trang đăng ký thi
     */
    @GetMapping("/dangkythi")
    public String dangKyThi(Model model) {
        if (!sessionManager.isTeacher()) {
            return "redirect:/login";
        }

        model.addAttribute("user", sessionManager.getCurrentUser());
        return "teacher/dangkythi/list";
    }

    /**
     * API: Lấy danh sách tất cả lịch thi (để hiển thị cho giáo viên xem tránh trùng)
     */
    @GetMapping("/api/dangkythi/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllDangKyThi() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
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
     * API: Lấy danh sách lớp
     */
    @GetMapping("/api/lop")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllLop() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
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
        
        if (!sessionManager.isTeacher()) {
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
     * API: Đăng ký thi mới
     */
    @PostMapping("/api/dangkythi")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dangKyThiMoi(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Lấy thông tin giáo viên hiện tại
            String currentTeacherId = sessionManager.getCurrentUser().getUserId();
            
            // Parse và validate dữ liệu
            String malop = requestData.get("malop");
            String mamh = requestData.get("mamh");
            String trinhdo = requestData.get("trinhdo");
            Short lan = Short.parseShort(requestData.get("lan"));
            Short socauthi = Short.parseShort(requestData.get("socauthi"));
            Short thoigian = Short.parseShort(requestData.get("thoigian"));
            
            // Parse ngày thi
            String ngayThiStr = requestData.get("ngaythi");
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date ngayThi = formatter.parse(ngayThiStr);
            
            // Validation 1: Kiểm tra trùng lịch thi
            String scheduleError = validateScheduleConflict(ngayThi, thoigian, null);
            if (scheduleError != null) {
                response.put("success", false);
                response.put("message", scheduleError);
                return ResponseEntity.ok(response);
            }
            
            // Validation 2: Kiểm tra đủ câu hỏi theo trình độ
            String questionError = validateQuestionAvailability(mamh, trinhdo, socauthi);
            if (questionError != null) {
                response.put("success", false);
                response.put("message", questionError);
                return ResponseEntity.ok(response);
            }
            
            // Tạo đối tượng GIAOVIEN_DANGKY từ dữ liệu form
            GIAOVIEN_DANGKY dangKy = new GIAOVIEN_DANGKY();
            dangKy.setMAGV(currentTeacherId); // MAGV chính là người đang đăng ký
            dangKy.setMALOP(malop);
            dangKy.setMAMH(mamh);
            dangKy.setTRINHDO(trinhdo);
            dangKy.setLAN(lan);
            dangKy.setSOCAUTHI(socauthi);
            dangKy.setTHOIGIAN(thoigian);
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
     * API: Sửa đăng ký thi
     */
    @PutMapping("/api/dangkythi/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> suaDangKyThi(@PathVariable String id, @RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String currentTeacherId = sessionManager.getCurrentUser().getUserId();
            
            // Lấy thông tin đăng ký hiện tại
            GIAOVIEN_DANGKY existingDangKy = dangKyService.getDangKyThiById(id);
            if (existingDangKy == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đăng ký thi");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra quyền sở hữu
            if (!existingDangKy.getMAGV().equals(currentTeacherId)) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền sửa đăng ký này");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra trạng thái thời gian
            String status = getExamStatus(existingDangKy.getNGAYTHI(), existingDangKy.getTHOIGIAN());
            if (!status.equals("Chưa bắt đầu")) {
                response.put("success", false);
                response.put("message", "Không thể sửa đăng ký thi đã " + status.toLowerCase());
                return ResponseEntity.ok(response);
            }
            
            // Parse dữ liệu cập nhật
            String trinhdo = requestData.get("trinhdo");
            Short socauthi = Short.parseShort(requestData.get("socauthi"));
            Short thoigian = Short.parseShort(requestData.get("thoigian"));
            
            // Parse ngày thi
            String ngayThiStr = requestData.get("ngaythi");
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date ngayThi = formatter.parse(ngayThiStr);
            
            // Validation 1: Kiểm tra trùng lịch thi (loại trừ chính đăng ký này)
            String scheduleError = validateScheduleConflict(ngayThi, thoigian, id);
            if (scheduleError != null) {
                response.put("success", false);
                response.put("message", scheduleError);
                return ResponseEntity.ok(response);
            }
            
            // Validation 2: Kiểm tra đủ câu hỏi theo trình độ
            String questionError = validateQuestionAvailability(existingDangKy.getMAMH(), trinhdo, socauthi);
            if (questionError != null) {
                response.put("success", false);
                response.put("message", questionError);
                return ResponseEntity.ok(response);
            }
            
            // Cập nhật thông tin (chỉ các trường được phép sửa)
            existingDangKy.setTRINHDO(trinhdo);
            existingDangKy.setSOCAUTHI(socauthi);
            existingDangKy.setTHOIGIAN(thoigian);
            existingDangKy.setNGAYTHI(ngayThi);
            
            // Cập nhật vào database
            boolean success = dangKyService.updateDangKyThi(existingDangKy);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật đăng ký thi thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật đăng ký thi. Vui lòng thử lại.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Xóa đăng ký thi
     */
    @DeleteMapping("/api/dangkythi/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaDangKyThi(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            String currentTeacherId = sessionManager.getCurrentUser().getUserId();
            
            // Lấy thông tin đăng ký hiện tại
            GIAOVIEN_DANGKY existingDangKy = dangKyService.getDangKyThiById(id);
            if (existingDangKy == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đăng ký thi");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra quyền sở hữu
            if (!existingDangKy.getMAGV().equals(currentTeacherId)) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền xóa đăng ký này");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra trạng thái thời gian
            String status = getExamStatus(existingDangKy.getNGAYTHI(), existingDangKy.getTHOIGIAN());
            if (!status.equals("Chưa bắt đầu")) {
                response.put("success", false);
                response.put("message", "Không thể xóa đăng ký thi đã " + status.toLowerCase());
                return ResponseEntity.ok(response);
            }
            
            // Xóa khỏi database
            boolean success = dangKyService.deleteDangKyThi(id);
            
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
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy thông tin chi tiết một đăng ký thi
     */
    @GetMapping("/api/dangkythi/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDangKyThiDetail(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            GIAOVIEN_DANGKY dangKy = dangKyService.getDangKyThiById(id);
            if (dangKy == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đăng ký thi");
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("dangky", dangKy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Helper method: Xác định trạng thái của kỳ thi
     */
    private String getExamStatus(Date ngayThi, int thoiGianThi) {
        Date now = new Date();
        
        // Thời gian kết thúc = thời gian bắt đầu + thời gian thi (phút)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayThi);
        calendar.add(Calendar.MINUTE, thoiGianThi);
        Date ngayKetThuc = calendar.getTime();
        
        if (now.before(ngayThi)) {
            return "Chưa bắt đầu";
        } else if (now.after(ngayThi) && now.before(ngayKetThuc)) {
            return "Đang thi";
        } else {
            return "Đã kết thúc";
        }
    }

    /**
     * Kiểm tra trùng lịch thi và khoảng cách thời gian
     */
    private String validateScheduleConflict(Date ngayThiBatDau, int thoiGianThi, String excludeId) {
        try {
            List<GIAOVIEN_DANGKY> allSchedules = dangKyService.getAllDangKyThi();
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ngayThiBatDau);
            calendar.add(Calendar.MINUTE, thoiGianThi);
            Date ngayThiKetThuc = calendar.getTime();
            
            for (GIAOVIEN_DANGKY schedule : allSchedules) {
                // Skip nếu là chính schedule đang sửa
                String scheduleId = GiaoVienDangKyService.createCompositeId(
                    schedule.getMAGV(), schedule.getMALOP(), schedule.getMAMH(), schedule.getLAN());
                if (excludeId != null && excludeId.equals(scheduleId)) {
                    continue;
                }
                
                Date existingStart = schedule.getNGAYTHI();
                Calendar existingCal = Calendar.getInstance();
                existingCal.setTime(existingStart);
                existingCal.add(Calendar.MINUTE, schedule.getTHOIGIAN());
                Date existingEnd = existingCal.getTime();
                
                // Kiểm tra trùng lịch
                boolean isOverlapping = (ngayThiBatDau.before(existingEnd) && ngayThiKetThuc.after(existingStart));
                
                // Kiểm tra khoảng cách 30 phút
                long diffInMinutes;
                boolean tooClose = false;
                
                if (ngayThiBatDau.after(existingEnd)) {
                    // Lịch mới sau lịch cũ
                    diffInMinutes = (ngayThiBatDau.getTime() - existingEnd.getTime()) / (60 * 1000);
                    tooClose = diffInMinutes < 30;
                } else if (ngayThiKetThuc.before(existingStart)) {
                    // Lịch mới trước lịch cũ
                    diffInMinutes = (existingStart.getTime() - ngayThiKetThuc.getTime()) / (60 * 1000);
                    tooClose = diffInMinutes < 30;
                }
                
                if (isOverlapping || tooClose) {
                    // Lấy thông tin môn học để hiển thị
                    String tenMonHoc = getSubjectNameById(schedule.getMAMH());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    
                    if (isOverlapping) {
                        return String.format("Trùng lịch thi với môn %s (%s), kết thúc lúc %s", 
                            tenMonHoc, schedule.getMAMH(), formatter.format(existingEnd));
                    } else {
                        return String.format("Lịch thi quá gần với môn %s (%s), phải cách ít nhất 30 phút. Môn này kết thúc lúc %s", 
                            tenMonHoc, schedule.getMAMH(), formatter.format(existingEnd));
                    }
                }
            }
            
            return null; // Không có xung đột
        } catch (Exception e) {
            return "Lỗi khi kiểm tra lịch thi: " + e.getMessage();
        }
    }

    /**
     * Kiểm tra đủ câu hỏi theo trình độ
     */
    private String validateQuestionAvailability(String mamh, String trinhdo, int socau) {
        try {
            // Lấy tất cả câu hỏi của môn học
            List<BODE> allQuestions = bodeService.getCauHoiByMonHoc(mamh);
            
            // Đếm số câu theo từng trình độ
            long cauA = allQuestions.stream().filter(q -> "A".equals(q.getTRINHDO())).count();
            long cauB = allQuestions.stream().filter(q -> "B".equals(q.getTRINHDO())).count();
            long cauC = allQuestions.stream().filter(q -> "C".equals(q.getTRINHDO())).count();
            
            String tenMonHoc = getSubjectNameById(mamh);
            
            if ("C".equals(trinhdo)) {
                // Trình độ C: cần 100% câu trình độ C
                if (cauC < socau) {
                    int thieuCau = socau - (int)cauC;
                    return String.format("Môn %s còn thiếu %d câu trình độ C. Cần %d câu, hiện có %d câu.", 
                        tenMonHoc, thieuCau, socau, cauC);
                }
            } else if ("B".equals(trinhdo)) {
                // Trình độ B: cần ít nhất 70% câu trình độ B
                int cauBCanThiet = (int) Math.ceil(socau * 0.7);
                int cauCCoThe = socau - cauBCanThiet;
                
                if (cauB < cauBCanThiet) {
                    int thieuCauB = cauBCanThiet - (int)cauB;
                    return String.format("Môn %s còn thiếu %d câu trình độ B. Cần ít nhất %d câu trình độ B, hiện có %d câu.", 
                        tenMonHoc, thieuCauB, cauBCanThiet, cauB);
                }
                if (cauB + cauC < socau) {
                    int thieuTongCau = socau - (int)(cauB + cauC);
                    return String.format("Môn %s còn thiếu %d câu hỏi. Cần %d câu (ít nhất %d câu B + tối đa %d câu C), hiện có %d câu B và %d câu C.", 
                        tenMonHoc, thieuTongCau, socau, cauBCanThiet, cauCCoThe, cauB, cauC);
                }
            } else if ("A".equals(trinhdo)) {
                // Trình độ A: cần ít nhất 70% câu trình độ A
                int cauACanThiet = (int) Math.ceil(socau * 0.7);
                int cauKhacCoThe = socau - cauACanThiet;
                
                if (cauA < cauACanThiet) {
                    int thieuCauA = cauACanThiet - (int)cauA;
                    return String.format("Môn %s còn thiếu %d câu trình độ A. Cần ít nhất %d câu trình độ A, hiện có %d câu.", 
                        tenMonHoc, thieuCauA, cauACanThiet, cauA);
                }
                if (cauA + cauB + cauC < socau) {
                    int thieuTongCau = socau - (int)(cauA + cauB + cauC);
                    return String.format("Môn %s còn thiếu %d câu hỏi. Cần %d câu (ít nhất %d câu A + tối đa %d câu B/C), hiện có %d câu A, %d câu B, %d câu C.", 
                        tenMonHoc, thieuTongCau, socau, cauACanThiet, cauKhacCoThe, cauA, cauB, cauC);
                }
            }
            
            return null; // Đủ câu hỏi
        } catch (Exception e) {
            return "Lỗi khi kiểm tra câu hỏi: " + e.getMessage();
        }
    }

    /**
     * Helper method: Lấy tên môn học theo mã
     */
    private String getSubjectNameById(String mamh) {
        try {
            List<MONHOC> subjects = monHocService.getAllMonHoc();
            return subjects.stream()
                .filter(s -> s.getMAMH().equals(mamh))
                .map(MONHOC::getTENMH)
                .findFirst()
                .orElse("Không xác định");
        } catch (Exception e) {
            return "Không xác định";
        }
    }
}