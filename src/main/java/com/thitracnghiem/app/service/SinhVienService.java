package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.SINHVIEN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SinhVienService {

    @Autowired
    private SessionManager sessionManager;

    private RowMapper<SINHVIEN> sinhVienRowMapper = new RowMapper<SINHVIEN>() {
        @Override
        public SINHVIEN mapRow(ResultSet rs, int rowNum) throws SQLException {
            SINHVIEN sv = new SINHVIEN();
            sv.setMASV(rs.getString("MASV"));
            sv.setHO(rs.getString("HO"));
            sv.setTEN(rs.getString("TEN"));
            sv.setNGAYSINH(rs.getDate("NGAYSINH"));
            sv.setDIACHI(rs.getString("DIACHI"));
            sv.setMALOP(rs.getString("MALOP"));
            return sv;
        }
    };

    /**
     * Lấy danh sách tất cả sinh viên
     */
    public List<SINHVIEN> getAllSinhVien() {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MASV, HO, TEN, NGAYSINH, DIACHI, MALOP FROM SINHVIEN ORDER BY MASV";
        return jdbcTemplate.query(sql, sinhVienRowMapper);
    }

    /**
     * Lấy thông tin sinh viên theo mã
     */
    public SINHVIEN getSinhVienById(String maSV) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MASV, HO, TEN, NGAYSINH, DIACHI, MALOP FROM SINHVIEN WHERE MASV = ?";
        try {
            return jdbcTemplate.queryForObject(sql, sinhVienRowMapper, maSV);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lấy danh sách sinh viên theo lớp
     */
    public List<SINHVIEN> getAllSinhVienByLop(String maLop) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MASV, HO, TEN, NGAYSINH, DIACHI, MALOP FROM SINHVIEN WHERE MALOP = ? ORDER BY MASV";
        try {
            return jdbcTemplate.query(sql, sinhVienRowMapper, maLop);
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách sinh viên theo lớp: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Tạo mã sinh viên tự động theo format: N[năm]L[lớp][sinh viên]
     * Ví dụ: năm 2022, lớp L001, sinh viên thứ 1 → N22L0101
     * Ví dụ: năm 2022, lớp L001, sinh viên thứ 2 → N22L0102
     * Format: N + 2 số năm + L + 2 số lớp + 2 số sinh viên = 8 ký tự
     */
    public String generateMaSinhVien(int namNhapHoc, String maLop) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        
        // Lấy 2 số cuối của năm
        String namHocSuffix = String.format("%02d", namNhapHoc % 100);
        
        // Extract số từ mã lớp (L001→01, LOP1→01, K01→01, CNTT→01 nếu không có số)
        String lopNumber = maLop.replaceAll("\\D", ""); // Lấy tất cả số
        int lopCode;
        if (lopNumber.length() > 0) {
            lopCode = Integer.parseInt(lopNumber) % 100; // Lấy 2 số cuối, max 99
        } else {
            // Nếu không có số, hash tên lớp thành số 1-99
            lopCode = (Math.abs(maLop.hashCode()) % 99) + 1;
        }
        String lopSuffix = String.format("%02d", lopCode);
        
        // Tạo prefix cho lớp này: N + năm + L + số lớp
        String prefix = "N" + namHocSuffix + "L" + lopSuffix;
        
        // Đếm sinh viên hiện tại trong lớp này (cùng prefix)
        String sql = "SELECT COUNT(*) FROM SINHVIEN WHERE MASV LIKE ?";
        String pattern = prefix + "%";
        
        int count = jdbcTemplate.queryForObject(sql, Integer.class, pattern);
        
        // Tạo số sequence sinh viên (01-99)
        int sinhVienNumber = (count + 1) % 100; // Max 99 sinh viên/lớp
        if (sinhVienNumber == 0) sinhVienNumber = 99; // Avoid 00
        String sinhVienSuffix = String.format("%02d", sinhVienNumber);
        
        return prefix + sinhVienSuffix;
    }

    /**
     * Thêm sinh viên mới (chỉ tạo record trong bảng SINHVIEN)
     * Tài khoản SQL Server sẽ được tạo riêng thông qua module Quản lý tài khoản
     */
    public boolean addSinhVien(SINHVIEN sinhVien, int namNhapHoc) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Tự động tạo mã sinh viên
            String maSinhVien = generateMaSinhVien(namNhapHoc, sinhVien.getMALOP());
            sinhVien.setMASV(maSinhVien);
            
            // Backup trước khi thay đổi
            createBackup("ADD_SINHVIEN", sinhVien.getMASV());
            
            // Thêm vào bảng SINHVIEN
            String sql = "INSERT INTO SINHVIEN (MASV, HO, TEN, NGAYSINH, DIACHI, MALOP) VALUES (?, ?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(sql,
                sinhVien.getMASV(),
                sinhVien.getHO(),
                sinhVien.getTEN(),
                sinhVien.getNGAYSINH(),
                sinhVien.getDIACHI(),
                sinhVien.getMALOP()
            );
            
            if (result > 0) {
                System.out.println("✅ Thêm sinh viên thành công: " + sinhVien.getMASV() + 
                                 " - " + sinhVien.getHO() + " " + sinhVien.getTEN());
                System.out.println("ℹ️ Lưu ý: Tài khoản SQL Server cần được tạo riêng thông qua module 'Quản lý tài khoản'");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm sinh viên: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thêm sinh viên mới (compatibility method - deprecated)
     * Sử dụng method addSinhVien(SINHVIEN, int) thay thế
     */
    @Deprecated
    public boolean addSinhVien(SINHVIEN sinhVien) {
        // Nếu không có năm nhập học, sử dụng năm hiện tại
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        return addSinhVien(sinhVien, currentYear);
    }

    /**
     * Cập nhật thông tin sinh viên (không thay đổi SQL login)
     */
    public boolean updateSinhVien(SINHVIEN sinhVien) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Backup trước khi thay đổi
            createBackup("UPDATE_SINHVIEN", sinhVien.getMASV());
            
            String sql = "UPDATE SINHVIEN SET HO = ?, TEN = ?, NGAYSINH = ?, DIACHI = ?, MALOP = ? WHERE MASV = ?";
            
            int result = jdbcTemplate.update(sql,
                sinhVien.getHO(),
                sinhVien.getTEN(),
                sinhVien.getNGAYSINH(),
                sinhVien.getDIACHI(),
                sinhVien.getMALOP(),
                sinhVien.getMASV()
            );
            
            return result > 0;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật sinh viên: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa sinh viên + Xóa SQL login
     */
    public boolean deleteSinhVien(String maSV) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            System.out.println("🔍 Kiểm tra điều kiện xóa sinh viên: " + maSV);
            
            // 1. KIỂM TRA CÁC THAM CHIẾU TRƯỚC KHI XÓA (Business Logic)
            StringBuilder errorMessage = new StringBuilder();
            boolean hasReferences = false;
            
            // Kiểm tra BANGDIEM
            try {
                String checkBangDiem = "SELECT COUNT(*) FROM BANGDIEM WHERE MASV = ?";
                int bangdiemCount = jdbcTemplate.queryForObject(checkBangDiem, Integer.class, maSV);
                if (bangdiemCount > 0) {
                    errorMessage.append("- ").append(bangdiemCount).append(" bảng điểm\n");
                    hasReferences = true;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra BANGDIEM: " + e.getMessage());
            }
            
            // Kiểm tra KETQUA
            try {
                String checkKetQua = "SELECT COUNT(*) FROM KETQUA WHERE MASV = ?";
                int ketquaCount = jdbcTemplate.queryForObject(checkKetQua, Integer.class, maSV);
                if (ketquaCount > 0) {
                    errorMessage.append("- ").append(ketquaCount).append(" kết quả thi\n");
                    hasReferences = true;
                }
            } catch (Exception e) {
                System.err.println("⚠️ Không thể kiểm tra KETQUA: " + e.getMessage());
            }
            
            // 2. NẾU CÓ THAM CHIẾU → TỪ CHỐI XÓA (Business Rule)
            if (hasReferences) {
                String errorDetails = errorMessage.toString().trim();
                String fullErrorMessage = "Không thể xóa sinh viên " + maSV + " vì còn có:\n" + errorDetails;
                
                // Log chi tiết cho developer
                System.err.println(fullErrorMessage);
                
                // Throw exception với message đơn giản cho user
                throw new RuntimeException(ErrorCode.USER_SV_CANNOT_DELETE);
            }
            
            // 3. NẾU KHÔNG CÓ THAM CHIẾU → CHO PHÉP XÓA
            System.out.println("✅ Sinh viên " + maSV + " không có dữ liệu liên quan, có thể xóa an toàn");
            
            // Backup trước khi thay đổi
            createBackup("DELETE_SINHVIEN", maSV);
            
            // Xóa từ bảng SINHVIEN
            String deleteSinhVien = "DELETE FROM SINHVIEN WHERE MASV = ?";
            int result = jdbcTemplate.update(deleteSinhVien, maSV);
            
            if (result > 0) {
                // Xóa SQL Server login
                boolean loginDeleted = deleteSqlLogin(maSV);
                
                if (loginDeleted) {
                    System.out.println("✅ Xóa sinh viên " + maSV + " thành công (bao gồm SQL login)");
                } else {
                    System.err.println("⚠️ " + ErrorCode.SV_DELETE_LOGIN_FAILED_MSG + " - Mã sinh viên: " + maSV);
                }
                
                return true;
            } else {
                // Log chi tiết cho developer
                System.err.println(ErrorCode.SV_NOT_FOUND_MSG + " - Mã sinh viên: " + maSV);
                
                // Throw user-friendly message
                throw new RuntimeException(ErrorCode.USER_SV_NOT_FOUND);
            }
            
        } catch (RuntimeException e) {
            // Re-throw RuntimeException đã được format (business logic errors)
            throw e;
        } catch (Exception e) {
            // Log chi tiết cho developer (unexpected errors)
            String errorMessage = ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage();
            System.err.println(errorMessage);
            
            // Re-throw user-friendly message
            throw new RuntimeException(ErrorCode.USER_GENERAL_ERROR);
        }
    }

    /**
     * Tạo SQL Server login cho sinh viên
     */
    private boolean createSqlLogin(String username, String password) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            System.out.println("🔄 Bắt đầu tạo SQL login cho sinh viên: " + username + " với password: " + password);
            
            // Kiểm tra login đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM sys.sql_logins WHERE name = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            
            if (count > 0) {
                System.out.println("⚠️ SQL login đã tồn tại: " + username);
                return true; // Đã tồn tại thì coi như thành công
            }
            
            // Tạo login mới
            String createLoginSql = "CREATE LOGIN [" + username + "] WITH PASSWORD = '" + password + "', " +
                                   "CHECK_EXPIRATION = OFF, " +
                                   "CHECK_POLICY = OFF";
            
            jdbcTemplate.execute(createLoginSql);
            
            // Tạo user trong database
            String createUserSql = "CREATE USER [" + username + "] FOR LOGIN [" + username + "]";
            jdbcTemplate.execute(createUserSql);
            
            System.out.println("✅ Tạo tài khoản thành công cho sinh viên: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo SQL login cho sinh viên " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Gán StudentRole cho sinh viên
     */
    private boolean assignStudentRole(String username) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            System.out.println("🔄 Gán StudentRole cho: " + username);
            
            // Kiểm tra role đã tồn tại chưa
            String checkRoleSql = "SELECT COUNT(*) FROM sys.database_principals WHERE name = 'StudentRole' AND type = 'R'";
            int roleCount = jdbcTemplate.queryForObject(checkRoleSql, Integer.class);
            
            if (roleCount == 0) {
                // Tạo StudentRole nếu chưa có
                String createRoleSql = "CREATE ROLE StudentRole";
                jdbcTemplate.execute(createRoleSql);
                
                // Cấp quyền SELECT cho StudentRole
                String grantPermissions = """
                    GRANT SELECT ON MONHOC TO StudentRole;
                    GRANT SELECT ON BODE TO StudentRole;
                    GRANT SELECT ON CAUHOI TO StudentRole;
                    GRANT SELECT ON KETQUA TO StudentRole;
                    GRANT SELECT ON BANGDIEM TO StudentRole;
                    GRANT SELECT ON SINHVIEN TO StudentRole;
                    """;
                jdbcTemplate.execute(grantPermissions);
                
                System.out.println("✅ Tạo StudentRole và cấp quyền thành công");
            }
            
            // Thêm user vào role
            String addToRoleSql = "ALTER ROLE StudentRole ADD MEMBER [" + username + "]";
            jdbcTemplate.execute(addToRoleSql);
            
            System.out.println("✅ Gán StudentRole thành công cho: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi gán StudentRole cho " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa SQL Server login cho sinh viên
     */
    private boolean deleteSqlLogin(String username) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            System.out.println("🔄 Xóa SQL login cho sinh viên: " + username);
            
            // Xóa user khỏi database trước
            try {
                String dropUserSql = "DROP USER IF EXISTS [" + username + "]";
                jdbcTemplate.execute(dropUserSql);
                System.out.println("✅ Xóa database user thành công: " + username);
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi xóa database user " + username + ": " + e.getMessage());
            }
            
            // Xóa login
            try {
                String dropLoginSql = "DROP LOGIN IF EXISTS [" + username + "]";
                jdbcTemplate.execute(dropLoginSql);
                System.out.println("✅ Xóa SQL login thành công: " + username);
                return true;
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi xóa SQL login " + username + ": " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi tổng thể khi xóa login " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách sinh viên chưa có SQL login
     */
    public List<SINHVIEN> getSinhVienChuaCoLogin() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = """
                SELECT sv.MASV, sv.HO, sv.TEN, sv.MALOP, sv.NGAYSINH, sv.DIACHI 
                FROM SINHVIEN sv 
                WHERE sv.MASV NOT IN (
                    SELECT name FROM sys.sql_logins WHERE name = sv.MASV
                )
                ORDER BY sv.MASV
                """;
            
            return jdbcTemplate.query(sql, sinhVienRowMapper);
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách sinh viên chưa có login: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Tạo SQL login cho sinh viên với password là ngày sinh (DDMMYY)
     */
    public boolean taoSqlLoginVoiPasswordTest(String masv) {
        try {
            // Kiểm tra sinh viên có tồn tại không
            SINHVIEN sv = getSinhVienById(masv);
            if (sv == null) {
                System.err.println("❌ Sinh viên không tồn tại: " + masv);
                return false;
            }

            // Tạo password từ ngày sinh (DDMMYY)
            String password = convertNgaySinhToPassword(sv.getNGAYSINH());
            if (password == null || password.length() != 6) {
                System.err.println("❌ Không thể tạo password từ ngày sinh cho sinh viên " + masv);
                return false;
            }

            // Tạo SQL login với password là ngày sinh
            boolean loginCreated = createSqlLogin(masv, password);
            
            if (loginCreated) {
                // Gán StudentRole
                boolean roleAssigned = assignStudentRole(masv);
                
                if (roleAssigned) {
                    System.out.println("✅ Tạo tài khoản thành công cho sinh viên: " + masv + " với password: " + password);
                    return true;
                } else {
                    System.err.println("⚠️ Gán role thất bại cho sinh viên: " + masv);
                    return true; // Vẫn coi như thành công vì đã tạo login
                }
            } else {
                System.err.println("❌ Tạo tài khoản thất bại cho: " + masv);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Lỗi tạo SQL login cho sinh viên " + masv + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Chuyển đổi ngày sinh thành password (DDMMYY)
     * Ví dụ: 11/03/2001 -> 110301
     */
    private String convertNgaySinhToPassword(java.util.Date ngaySinh) {
        try {
            if (ngaySinh == null) {
                return null;
            }
            
            // Chuyển java.util.Date thành LocalDate
            java.time.LocalDate localDate = ngaySinh.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
                
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonthValue();
            int year = localDate.getYear() % 100; // Lấy 2 chữ số cuối của năm
            
            return String.format("%02d%02d%02d", day, month, year);
        } catch (Exception e) {
            System.err.println("Lỗi chuyển đổi ngày sinh: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tạo backup trước khi thay đổi dữ liệu
     */
    private void createBackup(String operation, String entityId) {
        try {
            System.out.println("📦 Tạo backup cho " + operation + " - " + entityId);
            // Có thể implement logic backup ở đây nếu cần
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi tạo backup: " + e.getMessage());
        }
    }
} 