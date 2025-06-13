package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.exception.SuccessCode;
import com.thitracnghiem.app.model.GIAOVIEN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GiaoVienService {

    @Autowired
    private SessionManager sessionManager;

    private RowMapper<GIAOVIEN> giaoVienRowMapper = new RowMapper<GIAOVIEN>() {
        @Override
        public GIAOVIEN mapRow(ResultSet rs, int rowNum) throws SQLException {
            GIAOVIEN gv = new GIAOVIEN();
            gv.setMAGV(rs.getString("MAGV") != null ? rs.getString("MAGV").trim() : null);
            gv.setHO(rs.getString("HO") != null ? rs.getString("HO").trim() : null);
            gv.setTEN(rs.getString("TEN") != null ? rs.getString("TEN").trim() : null);
            gv.setSODTLL(rs.getString("SODTLL") != null ? rs.getString("SODTLL").trim() : null);
            gv.setDIACHI(rs.getString("DIACHI") != null ? rs.getString("DIACHI").trim() : null);
            return gv;
        }
    };

    /**
     * Lấy danh sách tất cả giáo viên
     */
    public List<GIAOVIEN> getAllGiaoVien() {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MAGV, HO, TEN, SODTLL, DIACHI FROM GIAOVIEN ORDER BY MAGV";
        return jdbcTemplate.query(sql, giaoVienRowMapper);
    }

    /**
     * Lấy thông tin giáo viên theo mã
     */
    public GIAOVIEN getGiaoVienById(String maGV) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MAGV, HO, TEN, SODTLL, DIACHI FROM GIAOVIEN WHERE MAGV = ?";
        try {
            return jdbcTemplate.queryForObject(sql, giaoVienRowMapper, maGV);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Thêm giáo viên mới (chỉ tạo record trong bảng GIAOVIEN)
     * Tài khoản SQL Server sẽ được tạo riêng thông qua module Quản lý tài khoản
     */
    public boolean addGiaoVien(GIAOVIEN giaoVien) {
        try {
            // Validation: Kiểm tra MAGV format và độ dài
            String magv = giaoVien.getMAGV();
            if (magv == null || magv.trim().isEmpty()) {
                throw new RuntimeException("Mã giáo viên không được để trống");
            }
            
            // Trim và uppercase MAGV
            magv = magv.trim().toUpperCase();
            giaoVien.setMAGV(magv);
            
            // Kiểm tra độ dài (phải đúng 8 ký tự cho NCHAR(8))
            if (magv.length() != 8) {
                throw new RuntimeException("Mã giáo viên phải có đúng 8 ký tự");
            }
            
            // Kiểm tra format: 2 chữ cái + 6 chữ số (đúng 8 ký tự)
            if (!magv.matches("^[A-Z]{2}[0-9]{6}$")) {
                throw new RuntimeException("Mã giáo viên phải có format: 2 chữ cái + 6 chữ số (VD: GV000001)");
            }
            
            // Kiểm tra không có khoảng trắng
            if (magv.contains(" ")) {
                throw new RuntimeException("Mã giáo viên không được chứa khoảng trắng");
            }
            
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Backup trước khi thay đổi
            createBackup("ADD_GIAOVIEN", giaoVien.getMAGV());
            
            // Thêm vào bảng GIAOVIEN
            String sql = "INSERT INTO GIAOVIEN (MAGV, HO, TEN, SODTLL, DIACHI) VALUES (?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(sql,
                giaoVien.getMAGV(),
                giaoVien.getHO(),
                giaoVien.getTEN(),
                giaoVien.getSODTLL(),
                giaoVien.getDIACHI()
            );
            
            if (result > 0) {
                System.out.println("✅ Thêm giáo viên thành công: " + giaoVien.getMAGV() + 
                                 " - " + giaoVien.getHO() + " " + giaoVien.getTEN());
                System.out.println("ℹ️ Lưu ý: Tài khoản SQL Server cần được tạo riêng thông qua module 'Quản lý tài khoản'");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm giáo viên: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin giáo viên (không thay đổi SQL login)
     */
    public boolean updateGiaoVien(GIAOVIEN giaoVien) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Backup trước khi thay đổi
            createBackup("UPDATE_GIAOVIEN", giaoVien.getMAGV());
            
            String sql = "UPDATE GIAOVIEN SET HO = ?, TEN = ?, SODTLL = ?, DIACHI = ? WHERE MAGV = ?";
            
            int result = jdbcTemplate.update(sql,
                giaoVien.getHO(),
                giaoVien.getTEN(),
                giaoVien.getSODTLL(),
                giaoVien.getDIACHI(),
                giaoVien.getMAGV()
            );
            
            return result > 0;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật giáo viên: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa giáo viên + Xóa SQL login
     */
    public boolean deleteGiaoVien(String maGV) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            System.out.println("🔍 Kiểm tra điều kiện xóa giáo viên: " + maGV);
            
            // 1. KIỂM TRA CÁC THAM CHIẾU TRƯỚC KHI XÓA (Business Logic)
            StringBuilder errorMessage = new StringBuilder();
            boolean hasReferences = false;
            
            // Kiểm tra GIAOVIEN_DANGKY
            try {
                String checkDangKy = "SELECT COUNT(*) FROM GIAOVIEN_DANGKY WHERE MAGV = ?";
                int dangKyCount = jdbcTemplate.queryForObject(checkDangKy, Integer.class, maGV);
                if (dangKyCount > 0) {
                    errorMessage.append("- ").append(dangKyCount).append(" đăng ký thi\n");
                    hasReferences = true;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra GIAOVIEN_DANGKY: " + e.getMessage());
            }
            
            // Kiểm tra BODE
            try {
                String checkBoDe = "SELECT COUNT(*) FROM BODE WHERE MAGV = ?";
                int bodeCount = jdbcTemplate.queryForObject(checkBoDe, Integer.class, maGV);
                if (bodeCount > 0) {
                    errorMessage.append("- ").append(bodeCount).append(" bộ đề thi\n");
                    hasReferences = true;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra BODE: " + e.getMessage());
            }
            
            // Kiểm tra CAUHOI
            try {
                String checkCauHoi = "SELECT COUNT(*) FROM CAUHOI WHERE MAGV = ?";
                int cauhoiCount = jdbcTemplate.queryForObject(checkCauHoi, Integer.class, maGV);
                if (cauhoiCount > 0) {
                    errorMessage.append("- ").append(cauhoiCount).append(" câu hỏi\n");
                    hasReferences = true;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra CAUHOI: " + e.getMessage());
            }
            
            // 2. NẾU CÓ THAM CHIẾU → TỪ CHỐI XÓA (Business Rule)
            if (hasReferences) {
                String errorDetails = errorMessage.toString().trim();
                String fullErrorMessage = ErrorCode.createDeleteError(maGV, errorDetails);
                
                // Log chi tiết cho developer
                System.err.println(fullErrorMessage);
                
                // Throw exception với message đơn giản cho user
                throw new RuntimeException(ErrorCode.USER_GV_CANNOT_DELETE);
            }
            
            // 3. NẾU KHÔNG CÓ THAM CHIẾU → CHO PHÉP XÓA
            System.out.println("✅ Giáo viên " + maGV + " không có dữ liệu liên quan, có thể xóa an toàn");
            
            // Backup trước khi thay đổi
            createBackup("DELETE_GIAOVIEN", maGV);
            
            // Xóa từ bảng GIAOVIEN
            String deleteGiaoVien = "DELETE FROM GIAOVIEN WHERE MAGV = ?";
            int result = jdbcTemplate.update(deleteGiaoVien, maGV);
            
            if (result > 0) {
                // Xóa SQL Server login
                boolean loginDeleted = deleteSqlLogin(maGV);
                
                if (loginDeleted) {
                    System.out.println(SuccessCode.createSafeDeleteSuccess("giáo viên", maGV));
                } else {
                    System.err.println(ErrorCode.createFullError(
                        ErrorCode.GV_DELETE_LOGIN_FAILED, 
                        ErrorCode.GV_DELETE_LOGIN_FAILED_MSG, 
                        "Mã giáo viên: " + maGV));
                }
                
                return true;
            } else {
                // Log chi tiết cho developer
                System.err.println(ErrorCode.createFullError(
                    ErrorCode.GV_NOT_FOUND, 
                    ErrorCode.GV_NOT_FOUND_MSG, 
                    "Mã giáo viên: " + maGV));
                
                // Throw user-friendly message
                throw new RuntimeException(ErrorCode.USER_GV_NOT_FOUND);
            }
            
        } catch (RuntimeException e) {
            // Re-throw RuntimeException đã được format (business logic errors)
            throw e;
        } catch (Exception e) {
            // Log chi tiết cho developer (unexpected errors)
            String errorMessage = ErrorCode.createFullError(
                ErrorCode.GENERAL_ERROR, 
                ErrorCode.GENERAL_ERROR_MSG, 
                e.getMessage());
            System.err.println(errorMessage);
            
            // Re-throw user-friendly message
            throw new RuntimeException(ErrorCode.USER_GENERAL_ERROR);
        }
    }

    /**
     * Tạo SQL Server login cho giáo viên
     */
    private boolean createSqlLogin(String username, String password) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            System.out.println("🔄 Bắt đầu tạo SQL login cho: " + username + " với password: " + password);
            
            // Kiểm tra permissions trước
            try {
                String currentUser = jdbcTemplate.queryForObject("SELECT SYSTEM_USER", String.class);
                System.out.println("👤 Current user: " + currentUser);
                
                String currentDatabase = jdbcTemplate.queryForObject("SELECT DB_NAME()", String.class);
                System.out.println("🗄️ Current database: " + currentDatabase);
                
                // Test có quyền tạo login không
                String hasPermission = jdbcTemplate.queryForObject(
                    "SELECT HAS_PERMS_BY_NAME(NULL, NULL, 'ALTER ANY LOGIN')", String.class);
                System.out.println("🔑 Has CREATE LOGIN permission: " + hasPermission);
                
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra permissions: " + e.getMessage());
            }
            
            // Kiểm tra login đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM sys.sql_logins WHERE name = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            
            if (count > 0) {
                System.out.println("⚠️ SQL login đã tồn tại: " + username);
                return true; // Đã tồn tại thì coi như thành công
            }
            
            // Tạo login mới - đơn giản hóa SQL
            String createLoginSql = "CREATE LOGIN [" + username + "] WITH PASSWORD = '" + password + "', " +
                                   "CHECK_EXPIRATION = OFF, " +
                                   "CHECK_POLICY = OFF";
            
            System.out.println("🔄 Executing SQL: " + createLoginSql);
            jdbcTemplate.execute(createLoginSql);
            System.out.println("✅ Tạo login thành công");
            
            // Tạo user trong database hiện tại
            String createUserSql = "CREATE USER [" + username + "] FOR LOGIN [" + username + "]";
            System.out.println("🔄 Executing SQL: " + createUserSql);
            jdbcTemplate.execute(createUserSql);
            System.out.println("✅ Tạo user thành công");
            
            System.out.println("✅ Tạo SQL login hoàn tất: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo SQL login cho " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gán TeacherRole cho user
     */
    private boolean assignTeacherRole(String username) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            System.out.println("🔄 Bắt đầu gán quyền giáo viên cho: " + username);
            
            // Kiểm tra user đã tồn tại chưa
            String checkUserSql = "SELECT COUNT(*) FROM sys.database_principals WHERE name = ?";
            int userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, username);
            
            if (userCount == 0) {
                System.err.println("❌ User " + username + " không tồn tại trong database");
                return false;
            }
            
            // Gán database roles thay vì server role
            try {
                String assignDbRoleSql1 = "ALTER ROLE [db_datareader] ADD MEMBER [" + username + "]";
                jdbcTemplate.execute(assignDbRoleSql1);
                System.out.println("✅ Gán db_datareader thành công");
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi gán db_datareader: " + e.getMessage());
            }
            
            try {
                String assignDbRoleSql2 = "ALTER ROLE [db_datawriter] ADD MEMBER [" + username + "]";
                jdbcTemplate.execute(assignDbRoleSql2);
                System.out.println("✅ Gán db_datawriter thành công");
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi gán db_datawriter: " + e.getMessage());
            }
            
            // Tạo và gán database role TeacherRole (nếu cần)
            try {
                // Tạo role TeacherRole nếu chưa có
                String createRoleSql = "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'TeacherRole' AND type = 'R') " +
                                      "CREATE ROLE [TeacherRole]";
                jdbcTemplate.execute(createRoleSql);
                System.out.println("✅ Đảm bảo role TeacherRole tồn tại");
                
                // Gán user vào TeacherRole
                String assignTeacherRoleSql = "ALTER ROLE [TeacherRole] ADD MEMBER [" + username + "]";
                jdbcTemplate.execute(assignTeacherRoleSql);
                System.out.println("✅ Gán TeacherRole (database role) thành công");
                
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi gán TeacherRole: " + e.getMessage());
            }
            
            System.out.println("✅ Hoàn tất gán quyền giáo viên cho: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi gán quyền giáo viên cho " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa SQL Server login
     */
    private boolean deleteSqlLogin(String username) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Kiểm tra login có tồn tại không
            String checkSql = "SELECT COUNT(*) FROM sys.sql_logins WHERE name = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            
            if (count == 0) {
                System.out.println("⚠️ SQL login không tồn tại: " + username);
                return true; // Không tồn tại thì coi như đã xóa thành công
            }
            
            // Xóa user khỏi database trước
            try {
                String dropUserSql = "DROP USER [" + username + "]";
                jdbcTemplate.execute(dropUserSql);
            } catch (Exception e) {
                System.err.println("⚠️ Không thể xóa user (có thể không tồn tại): " + username);
            }
            
            // Xóa login
            String dropLoginSql = "DROP LOGIN [" + username + "]";
            jdbcTemplate.execute(dropLoginSql);
            
            System.out.println("✅ Xóa SQL login thành công: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi xóa SQL login cho " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách khoa
     */
    public List<String> getAllKhoa() {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MAKHOA FROM KHOA ORDER BY MAKHOA";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * Tạo backup đơn giản - ghi log thay đổi
     */
    private void createBackup(String action, String maGV) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Tạo bảng backup log nếu chưa có
            String createLogTable = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BACKUP_LOG') " +
                                   "CREATE TABLE BACKUP_LOG (" +
                                   "ID INT IDENTITY(1,1) PRIMARY KEY, " +
                                   "ACTION_TYPE NVARCHAR(50), " +
                                   "TABLE_NAME NVARCHAR(50), " +
                                   "RECORD_ID NVARCHAR(50), " +
                                   "OLD_DATA NVARCHAR(MAX), " +
                                   "ACTION_TIME DATETIME DEFAULT GETDATE(), " +
                                   "USER_NAME NVARCHAR(50)" +
                                   ")";
            jdbcTemplate.execute(createLogTable);
            
            // Lưu dữ liệu cũ (nếu là UPDATE hoặc DELETE)
            String oldData = "";
            if ("UPDATE_GIAOVIEN".equals(action) || "DELETE_GIAOVIEN".equals(action)) {
                try {
                    String getOldData = "SELECT MAGV + '|' + HO + '|' + TEN + '|' + " +
                                       "ISNULL(SODTLL,'') + '|' + ISNULL(DIACHI,'') AS old_data " +
                                       "FROM GIAOVIEN WHERE MAGV = ?";
                    oldData = jdbcTemplate.queryForObject(getOldData, String.class, maGV);
                } catch (Exception e) {
                    oldData = "Không lấy được dữ liệu cũ";
                }
            }
            
            // Ghi log backup
            String insertLog = "INSERT INTO BACKUP_LOG (ACTION_TYPE, TABLE_NAME, RECORD_ID, OLD_DATA, USER_NAME) " +
                              "VALUES (?, 'GIAOVIEN', ?, ?, ?)";
            String currentUser = "ADMIN"; // Tạm thời hard-code
            try {
                currentUser = sessionManager.getCurrentUser().getUserId();
            } catch (Exception e) {
                // Nếu không lấy được user hiện tại thì dùng default
            }
            jdbcTemplate.update(insertLog, action, maGV, oldData, currentUser);
            
            System.out.println("✅ Backup log created: " + action + " for " + maGV);
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi tạo backup: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách giáo viên chưa có SQL login
     */
    public List<GIAOVIEN> getGiaoVienChuaCoLogin() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = """
                SELECT gv.MAGV, gv.HO, gv.TEN, gv.SODTLL, gv.DIACHI 
                FROM GIAOVIEN gv 
                WHERE gv.MAGV NOT IN (
                    SELECT name FROM sys.sql_logins WHERE name = gv.MAGV
                )
                ORDER BY gv.MAGV
                """;
            
            return jdbcTemplate.query(sql, giaoVienRowMapper);
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách giáo viên chưa có login: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Tạo SQL login cho giáo viên với password là số điện thoại
     */
    public boolean taoSqlLoginVoiPasswordTest(String magv) {
        try {
            // Kiểm tra giáo viên có tồn tại không
            GIAOVIEN gv = getGiaoVienById(magv);
            if (gv == null) {
                System.err.println("❌ Giáo viên không tồn tại: " + magv);
                return false;
            }

            // Lấy số điện thoại làm password
            String password = gv.getSODTLL();
            if (password == null || password.trim().isEmpty()) {
                System.err.println("❌ Giáo viên " + magv + " không có số điện thoại");
                return false;
            }
            
            // Loại bỏ khoảng trắng và ký tự đặc biệt
            password = password.replaceAll("[^0-9]", "");
            if (password.length() < 6) {
                System.err.println("❌ Số điện thoại không hợp lệ cho giáo viên " + magv + ": " + password);
                return false;
            }

            // Tạo SQL login với password là số điện thoại
            boolean loginCreated = createSqlLogin(magv, password);
            
            if (loginCreated) {
                // Gán TeacherRole
                boolean roleAssigned = assignTeacherRole(magv);
                
                if (roleAssigned) {
                    System.out.println("✅ Tạo tài khoản thành công cho giáo viên: " + magv + " với password: " + password);
                    return true;
                } else {
                    System.err.println("⚠️ Gán role thất bại cho giáo viên: " + magv);
                    return true; // Vẫn coi như thành công vì đã tạo login
                }
            } else {
                System.err.println("❌ Tạo tài khoản thất bại cho: " + magv);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Lỗi tạo SQL login cho giáo viên " + magv + ": " + e.getMessage());
            return false;
        }
    }
} 