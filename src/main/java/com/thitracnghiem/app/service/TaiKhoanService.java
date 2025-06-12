package com.thitracnghiem.app.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.thitracnghiem.app.config.SessionManager;

@Service
public class TaiKhoanService {

    @Autowired
    private SessionManager sessionManager;

    /**
     * DTO cho thông tin tài khoản SQL Server
     */
    public static class SqlAccount {
        private String username;
        private String role;
        private String fullName;
        private String accountType; // "TEACHER" hoặc "STUDENT"
        private boolean isActive;
        private String createDate;

        // Constructors
        public SqlAccount() {}

        public SqlAccount(String username, String role, String fullName, String accountType, boolean isActive, String createDate) {
            this.username = username;
            this.role = role;
            this.fullName = fullName;
            this.accountType = accountType;
            this.isActive = isActive;
            this.createDate = createDate;
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        public String getCreateDate() { return createDate; }
        public void setCreateDate(String createDate) { this.createDate = createDate; }
    }

    /**
     * Lấy danh sách tài khoản giáo viên (có TeacherRole)
     */
    public List<SqlAccount> getTeacherAccounts() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT DISTINCT 
                    u.name as username,
                    'TeacherRole' as role,
                    ISNULL(gv.HO + ' ' + gv.TEN, 'Không có thông tin') as fullName,
                    'TEACHER' as accountType,
                    CASE WHEN l.is_disabled = 0 THEN 1 ELSE 0 END as isActive,
                    FORMAT(l.create_date, 'dd/MM/yyyy') as createDate
                FROM sys.database_principals u
                INNER JOIN sys.database_role_members rm ON u.principal_id = rm.member_principal_id
                INNER JOIN sys.database_principals r ON rm.role_principal_id = r.principal_id
                LEFT JOIN sys.sql_logins l ON u.name = l.name
                LEFT JOIN GIAOVIEN gv ON u.name = gv.MAGV
                WHERE r.name = 'TeacherRole'
                    AND u.type = 'S'
                ORDER BY u.name
                """;

            return jdbcTemplate.query(sql, new SqlAccountRowMapper());
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách tài khoản giáo viên: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách tài khoản sinh viên (có StudentRole)
     */
    public List<SqlAccount> getStudentAccounts() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT DISTINCT 
                    u.name as username,
                    'StudentRole' as role,
                    ISNULL(sv.HO + ' ' + sv.TEN, 'Không có thông tin') as fullName,
                    'STUDENT' as accountType,
                    CASE WHEN l.is_disabled = 0 THEN 1 ELSE 0 END as isActive,
                    FORMAT(l.create_date, 'dd/MM/yyyy') as createDate
                FROM sys.database_principals u
                INNER JOIN sys.database_role_members rm ON u.principal_id = rm.member_principal_id
                INNER JOIN sys.database_principals r ON rm.role_principal_id = r.principal_id
                LEFT JOIN sys.sql_logins l ON u.name = l.name
                LEFT JOIN SINHVIEN sv ON u.name = sv.MASV
                WHERE r.name = 'StudentRole'
                    AND u.type = 'S'
                ORDER BY u.name
                """;

            return jdbcTemplate.query(sql, new SqlAccountRowMapper());
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách tài khoản sinh viên: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lấy tất cả tài khoản SQL Server có role
     */
    public List<SqlAccount> getAllAccounts() {
        List<SqlAccount> allAccounts = new ArrayList<>();
        allAccounts.addAll(getTeacherAccounts());
        allAccounts.addAll(getStudentAccounts());
        return allAccounts;
    }

    /**
     * Tạo tài khoản SQL Server cho giáo viên
     */
    public boolean createTeacherAccount(String username, String password) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // 1. Tạo SQL login
            String createLoginSql = "CREATE LOGIN [" + username + "] WITH PASSWORD = '" + password + "', " +
                                   "CHECK_EXPIRATION = OFF, CHECK_POLICY = OFF";
            jdbcTemplate.execute(createLoginSql);
            
            // 2. Tạo database user
            String createUserSql = "CREATE USER [" + username + "] FOR LOGIN [" + username + "]";
            jdbcTemplate.execute(createUserSql);
            
            // 3. Tạo TeacherRole nếu chưa có
            String createRoleSql = "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'TeacherRole' AND type = 'R') " +
                                  "CREATE ROLE [TeacherRole]";
            jdbcTemplate.execute(createRoleSql);
            
            // 4. Gán quyền cho TeacherRole
            String[] permissions = {
                "ALTER ROLE [db_datareader] ADD MEMBER [" + username + "]",
                "ALTER ROLE [db_datawriter] ADD MEMBER [" + username + "]", 
                "ALTER ROLE [TeacherRole] ADD MEMBER [" + username + "]"
            };
            
            for (String permission : permissions) {
                jdbcTemplate.execute(permission);
            }
            
            System.out.println("✅ Tạo tài khoản giáo viên thành công: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo tài khoản giáo viên " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Tạo tài khoản SQL Server cho sinh viên
     */
    public boolean createStudentAccount(String username, String password) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // 1. Tạo SQL login
            String createLoginSql = "CREATE LOGIN [" + username + "] WITH PASSWORD = '" + password + "', " +
                                   "CHECK_EXPIRATION = OFF, CHECK_POLICY = OFF";
            jdbcTemplate.execute(createLoginSql);
            
            // 2. Tạo database user
            String createUserSql = "CREATE USER [" + username + "] FOR LOGIN [" + username + "]";
            jdbcTemplate.execute(createUserSql);
            
            // 3. Tạo StudentRole nếu chưa có
            String createRoleSql = "IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'StudentRole' AND type = 'R') " +
                                  "CREATE ROLE [StudentRole]";
            jdbcTemplate.execute(createRoleSql);
            
            // 4. Gán quyền cho StudentRole (chỉ đọc)
            String[] permissions = {
                "ALTER ROLE [db_datareader] ADD MEMBER [" + username + "]",
                "ALTER ROLE [StudentRole] ADD MEMBER [" + username + "]"
            };
            
            for (String permission : permissions) {
                jdbcTemplate.execute(permission);
            }
            
            System.out.println("✅ Tạo tài khoản sinh viên thành công: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo tài khoản sinh viên " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa tài khoản SQL Server
     */
    public boolean deleteAccount(String username) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // 1. Xóa database user
            try {
                String dropUserSql = "DROP USER IF EXISTS [" + username + "]";
                jdbcTemplate.execute(dropUserSql);
            } catch (Exception e) {
                System.err.println("⚠️ Không thể xóa user: " + username);
            }
            
            // 2. Xóa SQL login
            String dropLoginSql = "DROP LOGIN IF EXISTS [" + username + "]";
            jdbcTemplate.execute(dropLoginSql);
            
            System.out.println("✅ Xóa tài khoản thành công: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi xóa tài khoản " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Reset password cho tài khoản
     */
    public boolean resetPassword(String username, String newPassword) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String alterLoginSql = "ALTER LOGIN [" + username + "] WITH PASSWORD = '" + newPassword + "'";
            jdbcTemplate.execute(alterLoginSql);
            
            System.out.println("✅ Reset password thành công cho: " + username);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi reset password cho " + username + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * RowMapper cho SqlAccount
     */
    private static class SqlAccountRowMapper implements RowMapper<SqlAccount> {
        @Override
        public SqlAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            SqlAccount account = new SqlAccount();
            account.setUsername(rs.getString("username"));
            account.setRole(rs.getString("role"));
            account.setFullName(rs.getString("fullName"));
            account.setAccountType(rs.getString("accountType"));
            account.setActive(rs.getBoolean("isActive"));
            account.setCreateDate(rs.getString("createDate"));
            return account;
        }
    }
}
