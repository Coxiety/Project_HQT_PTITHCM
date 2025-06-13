package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.BODE;
import com.thitracnghiem.app.model.MONHOC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class BODEService {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MonHocService monHocService;

    private RowMapper<BODE> bodeRowMapper = new RowMapper<BODE>() {
        @Override
        public BODE mapRow(ResultSet rs, int rowNum) throws SQLException {
            BODE bode = new BODE();
            bode.setMAMH(rs.getString("MAMH"));
            bode.setCAUHOI(rs.getInt("CAUHOI"));
            bode.setTRINHDO(rs.getString("TRINHDO"));
            bode.setNOIDUNG(rs.getString("NOIDUNG"));
            bode.setA(rs.getString("A"));
            bode.setB(rs.getString("B"));
            bode.setC(rs.getString("C"));
            bode.setD(rs.getString("D"));
            bode.setDAP_AN(rs.getString("DAP_AN"));
            bode.setMAGV(rs.getString("MAGV"));
            return bode;
        }
    };

    /**
     * Lấy tất cả câu hỏi theo môn học
     */
    public List<BODE> getCauHoiByMonHoc(String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, CAUHOI, TRINHDO, NOIDUNG, A, B, C, D, DAP_AN, MAGV FROM BODE WHERE MAMH = ? ORDER BY CAUHOI";
            List<BODE> result = jdbcTemplate.query(sql, bodeRowMapper, maMH);
            System.out.println("📚 Tìm thấy " + result.size() + " câu hỏi cho môn " + maMH);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy câu hỏi theo môn học: " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Lấy tất cả câu hỏi của giáo viên hiện tại
     */
    public List<BODE> getCauHoiByGiaoVien(String maGV) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, CAUHOI, TRINHDO, NOIDUNG, A, B, C, D, DAP_AN, MAGV FROM BODE WHERE MAGV = ? ORDER BY MAMH, CAUHOI";
            List<BODE> result = jdbcTemplate.query(sql, bodeRowMapper, maGV);
            System.out.println("👨‍🏫 Giáo viên " + maGV + " có " + result.size() + " câu hỏi");
            return result;
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy câu hỏi theo giáo viên: " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Lấy câu hỏi của giáo viên theo môn học
     */
    public List<BODE> getCauHoiByGiaoVienAndMonHoc(String maGV, String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, CAUHOI, TRINHDO, NOIDUNG, A, B, C, D, DAP_AN, MAGV FROM BODE WHERE MAGV = ? AND MAMH = ? ORDER BY CAUHOI";
            List<BODE> result = jdbcTemplate.query(sql, bodeRowMapper, maGV, maMH);
            System.out.println("📝 Giáo viên " + maGV + " có " + result.size() + " câu hỏi cho môn " + maMH);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy câu hỏi theo giáo viên và môn học: " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Thêm câu hỏi mới
     */
    public boolean addCauHoi(BODE bode) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Validate dữ liệu
            if (bode.getMAMH() == null || bode.getNOIDUNG() == null || bode.getDAP_AN() == null) {
                throw new RuntimeException("Thông tin câu hỏi không đầy đủ");
            }
            
            // Validate không trùng lặp đáp án
            String[] answers = {
                bode.getA() != null ? bode.getA().trim().toLowerCase() : "",
                bode.getB() != null ? bode.getB().trim().toLowerCase() : "",
                bode.getC() != null ? bode.getC().trim().toLowerCase() : "",
                bode.getD() != null ? bode.getD().trim().toLowerCase() : ""
            };
            
            java.util.Set<String> uniqueAnswers = new java.util.HashSet<>();
            for (String answer : answers) {
                if (!answer.isEmpty() && !uniqueAnswers.add(answer)) {
                    throw new RuntimeException("Các đáp án A, B, C, D không được trùng lặp nhau");
                }
            }
            
            // Validate không trùng nội dung câu hỏi trong cùng môn học
            String checkDuplicateSql = "SELECT COUNT(*) FROM BODE WHERE MAMH = ? AND TRIM(LOWER(NOIDUNG)) = TRIM(LOWER(?))";
            int duplicateCount = jdbcTemplate.queryForObject(checkDuplicateSql, Integer.class, 
                bode.getMAMH(), bode.getNOIDUNG());
            
            if (duplicateCount > 0) {
                throw new RuntimeException("Đã tồn tại câu hỏi có nội dung tương tự trong môn học này");
            }
            
            String sql = "INSERT INTO BODE (MAMH, TRINHDO, NOIDUNG, A, B, C, D, DAP_AN, MAGV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            int result = jdbcTemplate.update(sql,
                bode.getMAMH(),
                bode.getTRINHDO(),
                bode.getNOIDUNG(),
                bode.getA(),
                bode.getB(),
                bode.getC(),
                bode.getD(),
                bode.getDAP_AN(),
                bode.getMAGV()
            );
            
            if (result > 0) {
                System.out.println("✅ Thêm câu hỏi thành công cho môn " + bode.getMAMH());
                return true;
            } else {
                throw new RuntimeException("Không thể thêm câu hỏi");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm câu hỏi: " + e.getMessage());
            throw new RuntimeException("Lỗi thêm câu hỏi: " + e.getMessage());
        }
    }

    /**
     * Cập nhật câu hỏi
     */
    public boolean updateCauHoi(BODE bode) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Validate không trùng lặp đáp án
            String[] answers = {
                bode.getA() != null ? bode.getA().trim().toLowerCase() : "",
                bode.getB() != null ? bode.getB().trim().toLowerCase() : "",
                bode.getC() != null ? bode.getC().trim().toLowerCase() : "",
                bode.getD() != null ? bode.getD().trim().toLowerCase() : ""
            };
            
            java.util.Set<String> uniqueAnswers = new java.util.HashSet<>();
            for (String answer : answers) {
                if (!answer.isEmpty() && !uniqueAnswers.add(answer)) {
                    throw new RuntimeException("Các đáp án A, B, C, D không được trùng lặp nhau");
                }
            }
            
            // Validate không trùng nội dung câu hỏi trong cùng môn học (loại trừ câu hỏi hiện tại)
            String checkDuplicateSql = "SELECT COUNT(*) FROM BODE WHERE MAMH = ? AND TRIM(LOWER(NOIDUNG)) = TRIM(LOWER(?)) AND CAUHOI != ?";
            int duplicateCount = jdbcTemplate.queryForObject(checkDuplicateSql, Integer.class, 
                bode.getMAMH(), bode.getNOIDUNG(), bode.getCAUHOI());
            
            if (duplicateCount > 0) {
                throw new RuntimeException("Đã tồn tại câu hỏi có nội dung tương tự trong môn học này");
            }
            
            String sql = "UPDATE BODE SET TRINHDO = ?, NOIDUNG = ?, A = ?, B = ?, C = ?, D = ?, DAP_AN = ? WHERE CAUHOI = ? AND MAGV = ?";
            
            int result = jdbcTemplate.update(sql,
                bode.getTRINHDO(),
                bode.getNOIDUNG(),
                bode.getA(),
                bode.getB(),
                bode.getC(),
                bode.getD(),
                bode.getDAP_AN(),
                bode.getCAUHOI(),
                bode.getMAGV()
            );
            
            if (result > 0) {
                System.out.println("✅ Cập nhật câu hỏi " + bode.getCAUHOI() + " thành công");
                return true;
            } else {
                throw new RuntimeException("Không thể cập nhật câu hỏi hoặc bạn không có quyền");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật câu hỏi: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật câu hỏi: " + e.getMessage());
        }
    }

    /**
     * Xóa câu hỏi
     */
    public boolean deleteCauHoi(Integer cauHoi, String maGV) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = "DELETE FROM BODE WHERE CAUHOI = ? AND MAGV = ?";
            int result = jdbcTemplate.update(sql, cauHoi, maGV);
            
            if (result > 0) {
                System.out.println("✅ Xóa câu hỏi " + cauHoi + " thành công");
                return true;
            } else {
                throw new RuntimeException("Không thể xóa câu hỏi hoặc bạn không có quyền");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi xóa câu hỏi: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa câu hỏi: " + e.getMessage());
        }
    }

    /**
     * Lấy câu hỏi theo ID
     */
    public BODE getCauHoiById(Integer cauHoi) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, CAUHOI, TRINHDO, NOIDUNG, A, B, C, D, DAP_AN, MAGV FROM BODE WHERE CAUHOI = ?";
            return jdbcTemplate.queryForObject(sql, bodeRowMapper, cauHoi);
        } catch (Exception e) {
            System.err.println("❌ Không tìm thấy câu hỏi " + cauHoi);
            return null;
        }
    }

    /**
     * Đếm số câu hỏi theo môn học
     */
    public int countCauHoiByMonHoc(String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT COUNT(*) FROM BODE WHERE MAMH = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, maMH);
        } catch (Exception e) {
            System.err.println("❌ Lỗi đếm câu hỏi: " + e.getMessage());
            return 0;
        }
    }
} 