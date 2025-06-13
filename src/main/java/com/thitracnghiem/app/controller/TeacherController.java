package com.thitracnghiem.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.BODE;
import com.thitracnghiem.app.model.GIAOVIEN_DANGKY;
import com.thitracnghiem.app.model.LOP;
import com.thitracnghiem.app.model.MONHOC;
import com.thitracnghiem.app.service.BODEService;
import com.thitracnghiem.app.service.BangDiemService;
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

    @Autowired
    private BangDiemService bangDiemService;



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
     * Trang bảng điểm
     */
    @GetMapping("/bangdiem")
    public String bangDiem(Model model) {
        if (!sessionManager.isTeacher()) {
            return "redirect:/login";
        }

        model.addAttribute("user", sessionManager.getCurrentUser());
        return "teacher/bangdiem/list";
    }

    /**
     * API: Lấy danh sách lớp có thi (cho bảng điểm)
     */
    @GetMapping("/api/bangdiem/lop")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLopCoBangDiem() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Lấy tất cả lớp học từ hệ thống
            List<LOP> tatCaLop = lopService.getAllLop();
            System.out.println("DEBUG: Tổng số lớp trong hệ thống: " + tatCaLop.size());
            
            // Lấy danh sách lớp có bài thi thực từ database
            List<String> maLopCoBaiThi = bangDiemService.getLopCoBaiThi();
            System.out.println("DEBUG: Số lớp có bài thi: " + maLopCoBaiThi.size());
            
            // Tạo response với thông tin đầy đủ
            List<Map<String, Object>> danhSachLopResult = new ArrayList<>();
            int lopCoBaiThiCount = 0;
            
            for (LOP lop : tatCaLop) {
                Map<String, Object> lopInfo = new HashMap<>();
                lopInfo.put("MALOP", lop.getMALOP());
                lopInfo.put("TENLOP", lop.getTENLOP());
                
                // Kiểm tra lớp có bài thi không
                boolean coBaiThi = maLopCoBaiThi.contains(lop.getMALOP());
                lopInfo.put("coBaiThi", coBaiThi);
                
                if (coBaiThi) {
                    lopCoBaiThiCount++;
                    // Đếm số môn thi của lớp này
                    List<Map<String, Object>> monThiList = bangDiemService.getMonThiCuaLop(lop.getMALOP());
                    lopInfo.put("soMonThi", monThiList.size());
                } else {
                    lopInfo.put("soMonThi", 0);
                }
                
                danhSachLopResult.add(lopInfo);
                System.out.println("DEBUG: Lớp " + lop.getMALOP() + " - " + lop.getTENLOP() + 
                                 " (Có bài thi: " + coBaiThi + ")");
            }
            
            response.put("success", true);
            response.put("lops", danhSachLopResult);
            response.put("totalLops", tatCaLop.size());
            response.put("lopCoBaiThi", lopCoBaiThiCount);
            response.put("lopChuaCoBaiThi", tatCaLop.size() - lopCoBaiThiCount);
            
            System.out.println("DEBUG: Trả về " + danhSachLopResult.size() + " lớp (" + 
                             lopCoBaiThiCount + " có bài thi, " + 
                             (tatCaLop.size() - lopCoBaiThiCount) + " chưa có bài thi)");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in getLopCoBangDiem: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy danh sách môn thi của một lớp
     */
    @GetMapping("/api/bangdiem/lop/{malop}/monthi")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonThiCuaLop(@PathVariable String malop) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Lấy danh sách môn thi thực từ bảng BANGDIEM
            List<Map<String, Object>> danhSachMonThi = bangDiemService.getMonThiCuaLop(malop);
            
            System.out.println("DEBUG: Lớp " + malop + " có " + danhSachMonThi.size() + " môn thi");
            for (Map<String, Object> monThi : danhSachMonThi) {
                System.out.println("DEBUG: Môn thi - " + monThi.get("MAMH") + 
                                 ", Lần " + monThi.get("LAN") + 
                                 ", Trình độ " + monThi.get("TRINHDO"));
            }
            
            response.put("success", true);
            response.put("monthi", danhSachMonThi);
            response.put("malop", malop);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in getMonThiCuaLop: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * API: Lấy bảng điểm chi tiết của một lớp - môn thi
     */
    @GetMapping("/api/bangdiem/chitiet")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBangDiemChiTiet(
            @RequestParam String malop, 
            @RequestParam String mamh, 
            @RequestParam String lan) {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            // Parse lan parameter
            Short lanThi = Short.parseShort(lan);
            
            // Lấy bảng điểm chi tiết thực từ database
            List<Map<String, Object>> bangDiem = bangDiemService.getBangDiemChiTiet(malop, mamh, lanThi);
            
            System.out.println("DEBUG: Bảng điểm lớp " + malop + ", môn " + mamh + 
                             ", lần " + lan + " có " + bangDiem.size() + " sinh viên");
            
            response.put("success", true);
            response.put("bangdiem", bangDiem);
            response.put("malop", malop);
            response.put("mamh", mamh);
            response.put("lan", lan);
            
            // Thêm thông tin thống kê
            if (!bangDiem.isEmpty()) {
                Double diemTB = bangDiemService.getDiemTrungBinh(malop, mamh, lanThi);
                long soDat = bangDiem.stream()
                    .filter(bd -> (Double) bd.get("diem") >= 5.0)
                    .count();
                
                response.put("diemTrungBinh", Math.round(diemTB * 100.0) / 100.0);
                response.put("soDat", soDat);
                response.put("soKhongDat", bangDiem.size() - soDat);
                response.put("tiLeDat", Math.round((soDat * 100.0 / bangDiem.size()) * 100.0) / 100.0);
            }
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Tham số 'lan' không hợp lệ");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in getBangDiemChiTiet: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
            return ResponseEntity.ok(response);
        }
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
            
            // Thêm thông tin về khả năng edit/delete cho mỗi đăng ký
            List<Map<String, Object>> enrichedList = new ArrayList<>();
            String currentTeacherId = sessionManager.getCurrentUser().getUserId();
            
            for (GIAOVIEN_DANGKY dangKy : danhSachDangKy) {
                Map<String, Object> item = new HashMap<>();
                item.put("magv", dangKy.getMAGV());
                item.put("malop", dangKy.getMALOP());
                item.put("mamh", dangKy.getMAMH());
                item.put("trinhdo", dangKy.getTRINHDO());
                item.put("ngaythi", dangKy.getNGAYTHI());
                item.put("lan", dangKy.getLAN());
                item.put("socauthi", dangKy.getSOCAUTHI());
                item.put("thoigian", dangKy.getTHOIGIAN());
                
                // Kiểm tra quyền sở hữu
                boolean isMyRegistration = dangKy.getMAGV() != null && 
                                         dangKy.getMAGV().trim().equals(currentTeacherId.trim());
                
                // Kiểm tra khả năng edit/delete
                boolean canEditDelete = false;
                String editDeleteReason = "";
                
                if (isMyRegistration) {
                    String status = getExamStatus(dangKy.getNGAYTHI(), dangKy.getTHOIGIAN());
                    boolean hasSinhVienThi = bangDiemService.isDangKyThiDaCoSinhVienThi(
                        dangKy.getMALOP(), dangKy.getMAMH(), dangKy.getLAN());
                    
                    canEditDelete = status.equals("Chưa bắt đầu") || !hasSinhVienThi;
                    
                    if (!canEditDelete) {
                        editDeleteReason = "Đã có sinh viên tham gia thi";
                    }
                } else {
                    editDeleteReason = "Không có quyền";
                }
                
                item.put("isMyRegistration", isMyRegistration);
                item.put("canEditDelete", canEditDelete);
                item.put("editDeleteReason", editDeleteReason);
                
                enrichedList.add(item);
            }
            
            response.put("success", true);
            response.put("registrations", enrichedList);
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

            // Kiểm tra logic mới: cho phép edit nếu chưa bắt đầu HOẶC chưa có sinh viên nào thi
            String status = getExamStatus(existingDangKy.getNGAYTHI(), existingDangKy.getTHOIGIAN());
            boolean canEdit = status.equals("Chưa bắt đầu") || 
                             !bangDiemService.isDangKyThiDaCoSinhVienThi(
                                 existingDangKy.getMALOP(), 
                                 existingDangKy.getMAMH(), 
                                 existingDangKy.getLAN());
            
            if (!canEdit) {
                response.put("success", false);
                response.put("message", "Không thể sửa đăng ký thi vì đã có sinh viên tham gia thi");
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

            // Kiểm tra logic mới: cho phép delete nếu chưa bắt đầu HOẶC chưa có sinh viên nào thi
            String status = getExamStatus(existingDangKy.getNGAYTHI(), existingDangKy.getTHOIGIAN());
            boolean canDelete = status.equals("Chưa bắt đầu") || 
                               !bangDiemService.isDangKyThiDaCoSinhVienThi(
                                   existingDangKy.getMALOP(), 
                                   existingDangKy.getMAMH(), 
                                   existingDangKy.getLAN());
            
            if (!canDelete) {
                response.put("success", false);
                response.put("message", "Không thể hủy đăng ký thi vì đã có sinh viên tham gia thi");
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
     * API: Lấy thông tin user hiện tại
     */
    @GetMapping("/api/currentuser")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        
        if (!sessionManager.isTeacher()) {
            response.put("success", false);
            response.put("message", ErrorCode.ACCESS_DENIED_MSG);
            return ResponseEntity.ok(response);
        }

        try {
            var user = sessionManager.getCurrentUser();
            response.put("success", true);
            response.put("userId", user.getUserId());
            response.put("role", user.getRole());
            response.put("fullName", user.getFullName());
            
            System.out.println("DEBUG: Current user info - ID: " + user.getUserId() + 
                             ", Role: " + user.getRole() + 
                             ", Name: " + user.getFullName());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in getCurrentUser: " + e.getMessage());
            response.put("success", false);
            response.put("message", ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage());
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