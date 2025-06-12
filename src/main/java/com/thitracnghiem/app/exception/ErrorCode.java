package com.thitracnghiem.app.exception;

/**
 * Centralized Error Codes and Messages
 * Quản lý tập trung các mã lỗi và thông báo lỗi
 */
public class ErrorCode {
    
    // ==============================================
    // GENERAL ERRORS - LỖI CHUNG
    // ==============================================
    public static final String GENERAL_ERROR = "ERR_001";
    public static final String GENERAL_ERROR_MSG = "Đã xảy ra lỗi không xác định";
    
    public static final String ACCESS_DENIED = "ERR_002";
    public static final String ACCESS_DENIED_MSG = "Bạn không có quyền thực hiện thao tác này";
    
    public static final String DATA_NOT_FOUND = "ERR_003";
    public static final String DATA_NOT_FOUND_MSG = "Không tìm thấy dữ liệu";
    
    // ==============================================
    // AUTHENTICATION ERRORS - LỖI XÁC THỰC
    // ==============================================
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_001";
    public static final String AUTH_INVALID_CREDENTIALS_MSG = "Tên đăng nhập hoặc mật khẩu không chính xác";
    
    public static final String AUTH_USER_NOT_FOUND = "AUTH_002";
    public static final String AUTH_USER_NOT_FOUND_MSG = "Không tìm thấy tài khoản người dùng";
    
    public static final String AUTH_ROLE_NOT_ASSIGNED = "AUTH_003";
    public static final String AUTH_ROLE_NOT_ASSIGNED_MSG = "Tài khoản chưa được gán quyền";
    
    // ==============================================
    // GIAO VIEN ERRORS - LỖI GIÁO VIÊN
    // ==============================================
    public static final String GV_NOT_FOUND = "GV_001";
    public static final String GV_NOT_FOUND_MSG = "Không tìm thấy giáo viên";
    
    public static final String GV_DUPLICATE_ID = "GV_002";
    public static final String GV_DUPLICATE_ID_MSG = "Mã giáo viên đã tồn tại";
    
    public static final String GV_REQUIRED_FIELDS = "GV_003";
    public static final String GV_REQUIRED_FIELDS_MSG = "Vui lòng điền đầy đủ thông tin bắt buộc";
    
    public static final String GV_CREATE_LOGIN_FAILED = "GV_004";
    public static final String GV_CREATE_LOGIN_FAILED_MSG = "Không thể tạo tài khoản đăng nhập cho giáo viên";
    
    public static final String GV_ASSIGN_ROLE_FAILED = "GV_005";
    public static final String GV_ASSIGN_ROLE_FAILED_MSG = "Không thể gán quyền cho giáo viên";
    
    public static final String GV_DELETE_LOGIN_FAILED = "GV_006";
    public static final String GV_DELETE_LOGIN_FAILED_MSG = "Không thể xóa tài khoản đăng nhập của giáo viên";
    
    // ==============================================
    // SINH VIEN ERRORS - LỖI SINH VIÊN
    // ==============================================
    public static final String SV_NOT_FOUND = "SV_001";
    public static final String SV_NOT_FOUND_MSG = "Không tìm thấy sinh viên";
    
    public static final String SV_DUPLICATE_ID = "SV_002";
    public static final String SV_DUPLICATE_ID_MSG = "Mã sinh viên đã tồn tại";
    
    public static final String SV_REQUIRED_FIELDS = "SV_003";
    public static final String SV_REQUIRED_FIELDS_MSG = "Vui lòng điền đầy đủ thông tin bắt buộc";
    
    public static final String SV_CREATE_LOGIN_FAILED = "SV_004";
    public static final String SV_CREATE_LOGIN_FAILED_MSG = "Không thể tạo tài khoản đăng nhập cho sinh viên";
    
    public static final String SV_ASSIGN_ROLE_FAILED = "SV_005";
    public static final String SV_ASSIGN_ROLE_FAILED_MSG = "Không thể gán quyền cho sinh viên";
    
    public static final String SV_DELETE_LOGIN_FAILED = "SV_006";
    public static final String SV_DELETE_LOGIN_FAILED_MSG = "Không thể xóa tài khoản đăng nhập của sinh viên";
    
    // ==============================================
    // MON HOC ERRORS - LỖI MÔN HỌC
    // ==============================================
    public static final String MH_NOT_FOUND = "MH_001";
    public static final String MH_NOT_FOUND_MSG = "Không tìm thấy môn học";
    
    public static final String MH_DUPLICATE_ID = "MH_002";
    public static final String MH_DUPLICATE_ID_MSG = "Mã môn học đã tồn tại";
    
    public static final String MH_DUPLICATE_NAME = "MH_003";
    public static final String MH_DUPLICATE_NAME_MSG = "Tên môn học đã tồn tại";
    
    public static final String MH_REQUIRED_FIELDS = "MH_004";
    public static final String MH_REQUIRED_FIELDS_MSG = "Vui lòng điền đầy đủ thông tin bắt buộc";
    
    public static final String MH_CANNOT_DELETE_HAS_REFERENCES = "MH_005";
    public static final String MH_CANNOT_DELETE_HAS_REFERENCES_MSG = "Không thể xóa môn học vì còn có dữ liệu liên quan";
    
    // ==============================================
    // BUSINESS RULE ERRORS - LỖI BUSINESS LOGIC
    // ==============================================
    public static final String GV_CANNOT_DELETE_HAS_REFERENCES = "BIZ_001";
    public static final String GV_CANNOT_DELETE_HAS_REFERENCES_MSG = "Không thể xóa giáo viên vì còn có dữ liệu liên quan";
    
    public static final String GV_CANNOT_DELETE_HAS_DANGKY = "BIZ_002";
    public static final String GV_CANNOT_DELETE_HAS_DANGKY_MSG = "Không thể xóa giáo viên vì còn có đăng ký thi";
    
    public static final String GV_CANNOT_DELETE_HAS_BODE = "BIZ_003";
    public static final String GV_CANNOT_DELETE_HAS_BODE_MSG = "Không thể xóa giáo viên vì còn có bộ đề thi";
    
    public static final String GV_CANNOT_DELETE_HAS_CAUHOI = "BIZ_004";
    public static final String GV_CANNOT_DELETE_HAS_CAUHOI_MSG = "Không thể xóa giáo viên vì còn có câu hỏi";
    
    // ==============================================
    // USER-FRIENDLY MESSAGES - THÔNG BÁO CHO NGƯỜI DÙNG
    // ==============================================
    public static final String USER_GV_CANNOT_DELETE = "Không thể xóa giáo viên vì có dữ liệu liên quan";
    public static final String USER_GV_NOT_FOUND = "Không tìm thấy giáo viên";
    public static final String USER_SV_CANNOT_DELETE = "Không thể xóa sinh viên vì có dữ liệu liên quan";
    public static final String USER_SV_NOT_FOUND = "Không tìm thấy sinh viên";
    public static final String USER_MH_CANNOT_DELETE = "Không thể xóa môn học vì có dữ liệu liên quan";
    public static final String USER_MH_NOT_FOUND = "Không tìm thấy môn học";
    public static final String USER_ACCESS_DENIED = "Bạn không có quyền thực hiện thao tác này";
    public static final String USER_GENERAL_ERROR = "Đã xảy ra lỗi, vui lòng thử lại";
    
    // ==============================================
    // DATABASE ERRORS - LỖI DATABASE
    // ==============================================
    public static final String DB_CONNECTION_FAILED = "DB_001";
    public static final String DB_CONNECTION_FAILED_MSG = "Không thể kết nối đến cơ sở dữ liệu";
    
    public static final String DB_QUERY_FAILED = "DB_002";
    public static final String DB_QUERY_FAILED_MSG = "Lỗi thực hiện truy vấn cơ sở dữ liệu";
    
    public static final String DB_CONSTRAINT_VIOLATION = "DB_003";
    public static final String DB_CONSTRAINT_VIOLATION_MSG = "Vi phạm ràng buộc dữ liệu";
    
    public static final String DB_FOREIGN_KEY_VIOLATION = "DB_004";
    public static final String DB_FOREIGN_KEY_VIOLATION_MSG = "Vi phạm ràng buộc khóa ngoại";
    
    // ==============================================
    // VALIDATION ERRORS - LỖI VALIDATION
    // ==============================================
    public static final String VALIDATE_REQUIRED_FIELD = "VAL_001";
    public static final String VALIDATE_REQUIRED_FIELD_MSG = "Trường bắt buộc không được để trống";
    
    public static final String VALIDATE_INVALID_FORMAT = "VAL_002";
    public static final String VALIDATE_INVALID_FORMAT_MSG = "Định dạng dữ liệu không hợp lệ";
    
    public static final String VALIDATE_MAX_LENGTH = "VAL_003";
    public static final String VALIDATE_MAX_LENGTH_MSG = "Dữ liệu vượt quá độ dài cho phép";
    
    // ==============================================
    // UTILITY METHODS - PHƯƠNG THỨC HỖ TRỢ
    // ==============================================
    
    /**
     * Tạo thông báo lỗi chi tiết với tham số
     */
    public static String formatError(String baseMessage, Object... params) {
        return String.format(baseMessage, params);
    }
    
    /**
     * Tạo thông báo lỗi xóa giáo viên với chi tiết tham chiếu
     */
    public static String createDeleteError(String maGV, String references) {
        return formatError("Không thể xóa giáo viên %s vì còn có dữ liệu liên quan:\n%s\n\n💡 Hướng dẫn: Cần xóa/chuyển những dữ liệu trên trước khi xóa giáo viên.", 
                          maGV, references);
    }
    
    /**
     * Tạo thông báo lỗi với code và message
     */
    public static String createFullError(String errorCode, String errorMessage) {
        return String.format("[%s] %s", errorCode, errorMessage);
    }
    
    /**
     * Tạo thông báo lỗi với code, message và chi tiết
     */
    public static String createFullError(String errorCode, String errorMessage, String details) {
        return String.format("[%s] %s\nChi tiết: %s", errorCode, errorMessage, details);
    }
} 