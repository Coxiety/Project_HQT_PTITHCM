package com.thitracnghiem.app.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.thitracnghiem.app.config.DatabaseConfig;
import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.model.UserCredentials;
import com.thitracnghiem.app.model.UserInfo;

@Service
public class AuthService {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Autowired
    private SessionManager sessionManager;

    /**
     * Xác thực người dùng và thiết lập phiên làm việc
     * @param credentials Thông tin đăng nhập
     * @return Thông tin người dùng nếu đăng nhập thành công, null nếu thất bại
     */
    public UserInfo authenticate(UserCredentials credentials) {
        try {
            // Tạo DataSource với thông tin đăng nhập của người dùng
            DataSource userDataSource = databaseConfig.createUserDataSource(
                    credentials.getUserId(), credentials.getPassword());
            
            // Kiểm tra kết nối
            Connection connection = userDataSource.getConnection();
            
            // Nếu kết nối thành công, tạo UserInfo
            UserInfo userInfo = new UserInfo();
            
            // Tạo JdbcTemplate từ DataSource
            JdbcTemplate jdbcTemplate = new JdbcTemplate(userDataSource);
            
            // Lấy thông tin vai trò và userId trực tiếp từ username
            String username = credentials.getUserId();
            String role = "";
            String userId = username;
            
            // Xác định role dựa trên username
            if ("PGV".equals(username)) {
                role = "PGV";
            } 
            else 
            {
                // Kiểm tra trong bảng GIAOVIEN
                try {
                    String checkTeacherSql = "SELECT COUNT(*) FROM GIAOVIEN WHERE MAGV = ?";
                    int teacherCount = jdbcTemplate.queryForObject(checkTeacherSql, Integer.class, username);
                    if (teacherCount > 0) {
                        role = "Giangvien";
                    }
                } catch (Exception e) {
                    // Nếu lỗi khi truy vấn GIAOVIEN, thử SINHVIEN
                }
                
                // Nếu không phải giáo viên, kiểm tra sinh viên
                if (role.isEmpty()) {
                    try {
                        String checkStudentSql = "SELECT COUNT(*) FROM SINHVIEN WHERE MASV = ?";
                        int studentCount = jdbcTemplate.queryForObject(checkStudentSql, Integer.class, username);
                        if (studentCount > 0) {
                            role = "Sinhvien";
                        }
                    } catch (Exception e) {
                        // Nếu lỗi, để role = Unknown
                    }
                }
                
                // Nếu vẫn chưa tìm thấy role
                if (role.isEmpty()) {
                    role = "Unknown";
                }
            }
            
            // Lấy thông tin tên người dùng
            String fullName = "";
            if ("PGV".equals(role)) {
                fullName = "Quản trị viên";
            } else if ("Giangvien".equals(role)) {
                String sql = "SELECT HO + ' ' + TEN AS fullName FROM GIAOVIEN WHERE MAGV = ?";
                fullName = jdbcTemplate.queryForObject(sql, String.class, userId);
            } else if ("Sinhvien".equals(role)) {
                String sql = "SELECT HO + ' ' + TEN AS fullName FROM SINHVIEN WHERE MASV = ?";
                fullName = jdbcTemplate.queryForObject(sql, String.class, userId);
            }
            
            // Thiết lập thông tin người dùng
            userInfo.setUserId(userId);
            userInfo.setRole(role);
            userInfo.setFullName(fullName);
            
            // Đóng kết nối
            connection.close();
            
            // Thiết lập phiên làm việc
            sessionManager.setCurrentUser(userInfo);
            sessionManager.setJdbcTemplate(userDataSource);
            
            return userInfo;
        } catch (SQLException e) {
            // Đăng nhập thất bại
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Đăng xuất người dùng
     */
    public void logout() {
        sessionManager.logout();
    }
} 