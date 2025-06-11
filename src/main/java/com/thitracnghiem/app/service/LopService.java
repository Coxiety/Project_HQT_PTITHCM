package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.model.LOP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class LopService {

    @Autowired
    private SessionManager sessionManager;

    private RowMapper<LOP> lopRowMapper = new RowMapper<LOP>() {
        @Override
        public LOP mapRow(ResultSet rs, int rowNum) throws SQLException {
            LOP lop = new LOP();
            lop.setMALOP(rs.getString("MALOP"));
            lop.setTENLOP(rs.getString("TENLOP"));
            return lop;
        }
    };

    /**
     * Lấy danh sách tất cả lớp
     */
    public List<LOP> getAllLop() {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MALOP, TENLOP FROM LOP ORDER BY MALOP";
        return jdbcTemplate.query(sql, lopRowMapper);
    }

    /**
     * Lấy thông tin lớp theo mã
     */
    public LOP getLopById(String maLop) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT MALOP, TENLOP FROM LOP WHERE MALOP = ?";
        try {
            return jdbcTemplate.queryForObject(sql, lopRowMapper, maLop);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Thêm lớp mới
     */
    public boolean addLop(LOP lop) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "INSERT INTO LOP (MALOP, TENLOP) VALUES (?, ?)";
            
            int result = jdbcTemplate.update(sql, lop.getMALOP(), lop.getTENLOP());
            return result > 0;
        } catch (Exception e) {
            System.err.println("Lỗi thêm lớp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin lớp
     */
    public boolean updateLop(LOP lop) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "UPDATE LOP SET TENLOP = ? WHERE MALOP = ?";
            
            int result = jdbcTemplate.update(sql, lop.getTENLOP(), lop.getMALOP());
            return result > 0;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật lớp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa lớp (kiểm tra ràng buộc với sinh viên)
     */
    public boolean deleteLop(String maLop) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Kiểm tra xem có sinh viên nào trong lớp không
            String checkSql = "SELECT COUNT(*) FROM SINHVIEN WHERE MALOP = ?";
            int studentCount = jdbcTemplate.queryForObject(checkSql, Integer.class, maLop);
            
            if (studentCount > 0) {
                throw new RuntimeException("Không thể xóa lớp vì còn có " + studentCount + " sinh viên trong lớp");
            }
            
            String sql = "DELETE FROM LOP WHERE MALOP = ?";
            int result = jdbcTemplate.update(sql, maLop);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Kiểm tra mã lớp đã tồn tại chưa
     */
    public boolean isLopExists(String maLop) {
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
        String sql = "SELECT COUNT(*) FROM LOP WHERE MALOP = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, maLop);
        return count > 0;
    }
} 