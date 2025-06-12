package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.GIAOVIEN_DANGKY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class GiaoVienDangKyService {

    @Autowired
    private SessionManager sessionManager;

    private RowMapper<GIAOVIEN_DANGKY> dangKyRowMapper = new RowMapper<GIAOVIEN_DANGKY>() {
        @Override
        public GIAOVIEN_DANGKY mapRow(ResultSet rs, int rowNum) throws SQLException {
            GIAOVIEN_DANGKY dk = new GIAOVIEN_DANGKY();
            dk.setMAGV(rs.getString("MAGV"));
            dk.setMALOP(rs.getString("MALOP"));
            dk.setMAMH(rs.getString("MAMH"));
            dk.setTRINHDO(rs.getString("TRINHDO"));
            dk.setNGAYTHI(rs.getTimestamp("NGAYTHI"));
            dk.setLAN(rs.getShort("LAN"));
            dk.setSOCAUTHI(rs.getShort("SOCAUTHI"));
            dk.setTHOIGIAN(rs.getShort("THOIGIAN"));
            return dk;
        }
    };

    /**
     * Lấy danh sách tất cả đăng ký thi
     */
    public List<GIAOVIEN_DANGKY> getAllDangKyThi() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAGV, MALOP, MAMH, TRINHDO, NGAYTHI, LAN, SOCAUTHI, THOIGIAN " +
                        "FROM GIAOVIEN_DANGKY ORDER BY NGAYTHI DESC";
            List<GIAOVIEN_DANGKY> result = jdbcTemplate.query(sql, dangKyRowMapper);
            System.out.println("✅ Lấy danh sách đăng ký thi thành công - Tìm thấy " + result.size() + " đăng ký");
            return result;
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách đăng ký thi: " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Thêm đăng ký thi mới
     */
    public boolean addDangKyThi(GIAOVIEN_DANGKY dangKy) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Kiểm tra đăng ký đã tồn tại chưa (cùng MAGV, MALOP, MAMH, LAN)
            String checkSql = "SELECT COUNT(*) FROM GIAOVIEN_DANGKY WHERE MAGV = ? AND MALOP = ? AND MAMH = ? AND LAN = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, 
                dangKy.getMAGV(), dangKy.getMALOP(), dangKy.getMAMH(), dangKy.getLAN());
                
            if (count > 0) {
                throw new RuntimeException("Đăng ký thi đã tồn tại cho giáo viên này với lớp, môn học và lần thi tương tự");
            }
            
            // Thêm đăng ký mới
            String sql = "INSERT INTO GIAOVIEN_DANGKY (MAGV, MALOP, MAMH, TRINHDO, NGAYTHI, LAN, SOCAUTHI, THOIGIAN) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(sql,
                dangKy.getMAGV(),
                dangKy.getMALOP(),
                dangKy.getMAMH(),
                dangKy.getTRINHDO(),
                dangKy.getNGAYTHI(),
                dangKy.getLAN(),
                dangKy.getSOCAUTHI(),
                dangKy.getTHOIGIAN()
            );
            
            if (result > 0) {
                System.out.println("✅ Thêm đăng ký thi thành công: " + dangKy.getMAGV() + " - " + dangKy.getMAMH());
                return true;
            }
            
            return false;
        } catch (RuntimeException e) {
            // Re-throw business logic errors
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm đăng ký thi: " + e.getMessage());
            throw new RuntimeException(ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage(), e);
        }
    }

    /**
     * Xóa đăng ký thi
     */
    public boolean deleteDangKyThi(String magv, String malop, String mamh, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = "DELETE FROM GIAOVIEN_DANGKY WHERE MAGV = ? AND MALOP = ? AND MAMH = ? AND LAN = ?";
            int result = jdbcTemplate.update(sql, magv, malop, mamh, lan);
            
            if (result > 0) {
                System.out.println("✅ Xóa đăng ký thi thành công: " + magv + " - " + mamh);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("❌ Lỗi xóa đăng ký thi: " + e.getMessage());
            throw new RuntimeException(ErrorCode.GENERAL_ERROR_MSG + ": " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra đăng ký thi đã tồn tại chưa
     */
    public boolean isDangKyExists(String magv, String malop, String mamh, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT COUNT(*) FROM GIAOVIEN_DANGKY WHERE MAGV = ? AND MALOP = ? AND MAMH = ? AND LAN = ?";
            int count = jdbcTemplate.queryForObject(sql, Integer.class, magv, malop, mamh, lan);
            return count > 0;
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra đăng ký thi: " + e.getMessage());
            return false;
        }
    }
}