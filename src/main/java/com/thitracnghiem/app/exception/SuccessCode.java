package com.thitracnghiem.app.exception;

/**
 * Centralized Success Codes and Messages
 * Quản lý tập trung các mã thành công và thông báo thành công
 */
public class SuccessCode {
    
    // ==============================================
    // GENERAL SUCCESS - THÀNH CÔNG CHUNG
    // ==============================================
    public static final String GENERAL_SUCCESS = "SUC_001";
    public static final String GENERAL_SUCCESS_MSG = "Thao tác thành công";
    
    public static final String DATA_LOADED = "SUC_002";
    public static final String DATA_LOADED_MSG = "Tải dữ liệu thành công";
    
    public static final String OPERATION_COMPLETED = "SUC_003";
    public static final String OPERATION_COMPLETED_MSG = "Hoàn tất thao tác";
    
    // ==============================================
    // AUTHENTICATION SUCCESS - THÀNH CÔNG XÁC THỰC
    // ==============================================
    public static final String AUTH_LOGIN_SUCCESS = "AUTH_SUC_001";
    public static final String AUTH_LOGIN_SUCCESS_MSG = "Đăng nhập thành công";
    
    public static final String AUTH_LOGOUT_SUCCESS = "AUTH_SUC_002";
    public static final String AUTH_LOGOUT_SUCCESS_MSG = "Đăng xuất thành công";
    
    public static final String AUTH_ROLE_ASSIGNED = "AUTH_SUC_003";
    public static final String AUTH_ROLE_ASSIGNED_MSG = "Gán quyền thành công";
    
    // ==============================================
    // GIAO VIEN SUCCESS - THÀNH CÔNG GIÁO VIÊN
    // ==============================================
    public static final String GV_CREATE_SUCCESS = "GV_SUC_001";
    public static final String GV_CREATE_SUCCESS_MSG = "Thêm giáo viên thành công";
    
    public static final String GV_UPDATE_SUCCESS = "GV_SUC_002";
    public static final String GV_UPDATE_SUCCESS_MSG = "Cập nhật giáo viên thành công";
    
    public static final String GV_DELETE_SUCCESS = "GV_SUC_003";
    public static final String GV_DELETE_SUCCESS_MSG = "Xóa giáo viên thành công";
    
    public static final String GV_LOGIN_CREATED = "GV_SUC_004";
    public static final String GV_LOGIN_CREATED_MSG = "Tạo tài khoản đăng nhập cho giáo viên thành công";
    
    public static final String GV_ROLE_ASSIGNED = "GV_SUC_005";
    public static final String GV_ROLE_ASSIGNED_MSG = "Gán quyền giáo viên thành công";
    
    public static final String GV_LOGIN_DELETED = "GV_SUC_006";
    public static final String GV_LOGIN_DELETED_MSG = "Xóa tài khoản đăng nhập của giáo viên thành công";
    
    public static final String GV_LIST_LOADED = "GV_SUC_007";
    public static final String GV_LIST_LOADED_MSG = "Tải danh sách giáo viên thành công";
    
    public static final String GV_DETAILS_LOADED = "GV_SUC_008";
    public static final String GV_DETAILS_LOADED_MSG = "Tải thông tin chi tiết giáo viên thành công";
    
    // ==============================================
    // SINH VIEN SUCCESS - THÀNH CÔNG SINH VIÊN
    // ==============================================
    public static final String SV_CREATE_SUCCESS = "SV_SUC_001";
    public static final String SV_CREATE_SUCCESS_MSG = "Thêm sinh viên thành công";
    
    public static final String SV_UPDATE_SUCCESS = "SV_SUC_002";
    public static final String SV_UPDATE_SUCCESS_MSG = "Cập nhật sinh viên thành công";
    
    public static final String SV_DELETE_SUCCESS = "SV_SUC_003";
    public static final String SV_DELETE_SUCCESS_MSG = "Xóa sinh viên thành công";
    
    // ==============================================
    // MON HOC SUCCESS - THÀNH CÔNG MÔN HỌC
    // ==============================================
    public static final String MH_CREATE_SUCCESS = "MH_SUC_001";
    public static final String MH_CREATE_SUCCESS_MSG = "Thêm môn học thành công";
    
    public static final String MH_UPDATE_SUCCESS = "MH_SUC_002";
    public static final String MH_UPDATE_SUCCESS_MSG = "Cập nhật môn học thành công";
    
    public static final String MH_DELETE_SUCCESS = "MH_SUC_003";
    public static final String MH_DELETE_SUCCESS_MSG = "Xóa môn học thành công";
    
    public static final String MH_LIST_LOADED = "MH_SUC_004";
    public static final String MH_LIST_LOADED_MSG = "Tải danh sách môn học thành công";
    
    public static final String MH_DETAILS_LOADED = "MH_SUC_005";
    public static final String MH_DETAILS_LOADED_MSG = "Tải thông tin chi tiết môn học thành công";
    
    // ==============================================
    // DATABASE SUCCESS - THÀNH CÔNG DATABASE
    // ==============================================
    public static final String DB_CONNECTION_SUCCESS = "DB_SUC_001";
    public static final String DB_CONNECTION_SUCCESS_MSG = "Kết nối cơ sở dữ liệu thành công";
    
    public static final String DB_QUERY_SUCCESS = "DB_SUC_002";
    public static final String DB_QUERY_SUCCESS_MSG = "Thực hiện truy vấn thành công";
    
    public static final String DB_BACKUP_SUCCESS = "DB_SUC_003";
    public static final String DB_BACKUP_SUCCESS_MSG = "Sao lưu dữ liệu thành công";
    
    // ==============================================
    // EXAM SUCCESS - THÀNH CÔNG THI CỬ
    // ==============================================
    public static final String EXAM_CREATE_SUCCESS = "EXAM_SUC_001";
    public static final String EXAM_CREATE_SUCCESS_MSG = "Tạo đề thi thành công";
    
    public static final String EXAM_SUBMIT_SUCCESS = "EXAM_SUC_002";
    public static final String EXAM_SUBMIT_SUCCESS_MSG = "Nộp bài thi thành công";
    
    public static final String EXAM_GRADE_SUCCESS = "EXAM_SUC_003";
    public static final String EXAM_GRADE_SUCCESS_MSG = "Chấm điểm thành công";
    
    // ==============================================
    // QUESTION SUCCESS - THÀNH CÔNG CÂU HỎI
    // ==============================================
    public static final String QUESTION_CREATE_SUCCESS = "QUES_SUC_001";
    public static final String QUESTION_CREATE_SUCCESS_MSG = "Thêm câu hỏi thành công";
    
    public static final String QUESTION_UPDATE_SUCCESS = "QUES_SUC_002";
    public static final String QUESTION_UPDATE_SUCCESS_MSG = "Cập nhật câu hỏi thành công";
    
    public static final String QUESTION_DELETE_SUCCESS = "QUES_SUC_003";
    public static final String QUESTION_DELETE_SUCCESS_MSG = "Xóa câu hỏi thành công";
    
    // ==============================================
    // UTILITY METHODS - PHƯƠNG THỨC HỖ TRỢ
    // ==============================================
    
    /**
     * Tạo thông báo thành công chi tiết với tham số
     */
    public static String formatSuccess(String baseMessage, Object... params) {
        return String.format(baseMessage, params);
    }
    
    /**
     * Tạo thông báo thành công với code và message
     */
    public static String createFullSuccess(String successCode, String successMessage) {
        return String.format("[%s] %s", successCode, successMessage);
    }
    
    /**
     * Tạo thông báo thành công với code, message và chi tiết
     */
    public static String createFullSuccess(String successCode, String successMessage, String details) {
        return String.format("[%s] %s\nChi tiết: %s", successCode, successMessage, details);
    }
    
    /**
     * Tạo thông báo thành công với số lượng
     */
    public static String createCountSuccess(String baseMessage, int count) {
        return formatSuccess(baseMessage + " (%d record)", count);
    }
    
    /**
     * Tạo thông báo thành công cho thao tác xóa an toàn
     */
    public static String createSafeDeleteSuccess(String entityType, String entityId) {
        return formatSuccess("Xóa %s %s thành công (không có dữ liệu liên quan)", entityType, entityId);
    }
} 