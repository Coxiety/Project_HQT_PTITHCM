package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.exception.SuccessCode;
import com.thitracnghiem.app.model.MONHOC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class MonHocService {

    @Autowired
    private SessionManager sessionManager;

    private RowMapper<MONHOC> monHocRowMapper = new RowMapper<MONHOC>() {
        @Override
        public MONHOC mapRow(ResultSet rs, int rowNum) throws SQLException {
            MONHOC monHoc = new MONHOC();
            monHoc.setMAMH(rs.getString("MAMH"));
            monHoc.setTENMH(rs.getString("TENMH"));
            return monHoc;
        }
    };

    /**
     * Lấy danh sách tất cả môn học
     */
    public List<MONHOC> getAllMonHoc() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, TENMH FROM MONHOC ORDER BY MAMH";
            List<MONHOC> result = jdbcTemplate.query(sql, monHocRowMapper);
            System.out.println(SuccessCode.MH_LIST_LOADED_MSG + " - Tìm thấy " + result.size() + " môn học");
            return result;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Lấy thông tin môn học theo mã
     */
    public MONHOC getMonHocById(String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT MAMH, TENMH FROM MONHOC WHERE MAMH = ?";
            MONHOC result = jdbcTemplate.queryForObject(sql, monHocRowMapper, maMH);
            System.out.println(SuccessCode.MH_DETAILS_LOADED_MSG + " cho môn học: " + maMH);
            return result;
        } catch (EmptyResultDataAccessException e) {
            System.err.println(ErrorCode.MH_NOT_FOUND_MSG + ": " + maMH);
            return null;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Kiểm tra xem mã môn học đã tồn tại chưa
     */
    public boolean existsByMaMH(String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "SELECT COUNT(*) FROM MONHOC WHERE MAMH = ?";
            int count = jdbcTemplate.queryForObject(sql, Integer.class, maMH);
            return count > 0;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Kiểm tra xem tên môn học đã tồn tại chưa (trừ môn học hiện tại nếu đang update)
     */
    public boolean existsByTenMH(String tenMH, String currentMaMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql;
            int count;
            
            if (currentMaMH != null && !currentMaMH.trim().isEmpty()) {
                // Khi update, bỏ qua môn học hiện tại
                sql = "SELECT COUNT(*) FROM MONHOC WHERE TENMH = ? AND MAMH != ?";
                count = jdbcTemplate.queryForObject(sql, Integer.class, tenMH, currentMaMH);
            } else {
                // Khi thêm mới
                sql = "SELECT COUNT(*) FROM MONHOC WHERE TENMH = ?";
                count = jdbcTemplate.queryForObject(sql, Integer.class, tenMH);
            }
            
            return count > 0;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Thêm môn học mới
     */
    public boolean addMonHoc(MONHOC monHoc) {
        try {
            // Validation
            if (monHoc.getMAMH() == null || monHoc.getMAMH().trim().isEmpty() ||
                monHoc.getTENMH() == null || monHoc.getTENMH().trim().isEmpty()) {
                throw new IllegalArgumentException(ErrorCode.MH_REQUIRED_FIELDS_MSG);
            }

            // Trim data
            monHoc.setMAMH(monHoc.getMAMH().trim());
            monHoc.setTENMH(monHoc.getTENMH().trim());

            // Check duplicate mã môn học
            if (existsByMaMH(monHoc.getMAMH())) {
                throw new DuplicateKeyException(ErrorCode.MH_DUPLICATE_ID_MSG);
            }

            // Check duplicate tên môn học
            if (existsByTenMH(monHoc.getTENMH(), null)) {
                throw new DuplicateKeyException(ErrorCode.MH_DUPLICATE_NAME_MSG);
            }

            // Backup trước khi thay đổi
            createBackup("ADD_MONHOC", monHoc.getMAMH());

            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "INSERT INTO MONHOC (MAMH, TENMH) VALUES (?, ?)";
            
            int result = jdbcTemplate.update(sql, monHoc.getMAMH(), monHoc.getTENMH());
            
            if (result > 0) {
                System.out.println(SuccessCode.MH_CREATE_SUCCESS_MSG + ": " + monHoc.getMAMH() + " - " + monHoc.getTENMH());
                return true;
            }
            return false;
            
        } catch (DuplicateKeyException e) {
            System.err.println("Duplicate key error: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Cập nhật thông tin môn học
     */
    public boolean updateMonHoc(MONHOC monHoc) {
        try {
            // Validation
            if (monHoc.getMAMH() == null || monHoc.getMAMH().trim().isEmpty() ||
                monHoc.getTENMH() == null || monHoc.getTENMH().trim().isEmpty()) {
                throw new IllegalArgumentException(ErrorCode.MH_REQUIRED_FIELDS_MSG);
            }

            // Trim data
            monHoc.setMAMH(monHoc.getMAMH().trim());
            monHoc.setTENMH(monHoc.getTENMH().trim());

            // Check if môn học exists
            if (!existsByMaMH(monHoc.getMAMH())) {
                throw new EmptyResultDataAccessException(ErrorCode.MH_NOT_FOUND_MSG, 1);
            }

            // Check duplicate tên môn học (excluding current)
            if (existsByTenMH(monHoc.getTENMH(), monHoc.getMAMH())) {
                throw new DuplicateKeyException(ErrorCode.MH_DUPLICATE_NAME_MSG);
            }

            // Backup trước khi thay đổi
            createBackup("UPDATE_MONHOC", monHoc.getMAMH());

            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "UPDATE MONHOC SET TENMH = ? WHERE MAMH = ?";
            
            int result = jdbcTemplate.update(sql, monHoc.getTENMH(), monHoc.getMAMH());
            
            if (result > 0) {
                System.out.println(SuccessCode.MH_UPDATE_SUCCESS_MSG + ": " + monHoc.getMAMH() + " - " + monHoc.getTENMH());
                return true;
            }
            return false;
            
        } catch (DuplicateKeyException e) {
            System.err.println("Duplicate key error: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            throw e;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Not found error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Xóa môn học
     */
    public boolean deleteMonHoc(String maMH) {
        try {
            // Check if môn học exists
            if (!existsByMaMH(maMH)) {
                throw new EmptyResultDataAccessException(ErrorCode.MH_NOT_FOUND_MSG, 1);
            }

            // Check for references
            String references = checkReferences(maMH);
            if (!references.isEmpty()) {
                throw new DataIntegrityViolationException(
                    ErrorCode.createDeleteError(maMH, references)
                );
            }

            // Backup trước khi xóa
            createBackup("DELETE_MONHOC", maMH);

            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String sql = "DELETE FROM MONHOC WHERE MAMH = ?";
            
            int result = jdbcTemplate.update(sql, maMH);
            
            if (result > 0) {
                System.out.println(SuccessCode.MH_DELETE_SUCCESS_MSG + ": " + maMH);
                return true;
            }
            return false;
            
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity error: " + e.getMessage());
            throw e;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Not found error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println(ErrorCode.DB_QUERY_FAILED_MSG + ": " + e.getMessage());
            throw new RuntimeException(ErrorCode.DB_QUERY_FAILED_MSG, e);
        }
    }

    /**
     * Kiểm tra các tham chiếu đến môn học
     */
    private String checkReferences(String maMH) {
        StringBuilder references = new StringBuilder();
        JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();

        try {
            // Check BODE table
            String sql1 = "SELECT COUNT(*) FROM BODE WHERE MAMH = ?";
            int bodeCount = jdbcTemplate.queryForObject(sql1, Integer.class, maMH);
            if (bodeCount > 0) {
                references.append("- ").append(bodeCount).append(" câu hỏi trong bộ đề\n");
            }

            // Check GIAOVIEN_DANGKY table
            String sql2 = "SELECT COUNT(*) FROM GIAOVIEN_DANGKY WHERE MAMH = ?";
            int dangkyCount = jdbcTemplate.queryForObject(sql2, Integer.class, maMH);
            if (dangkyCount > 0) {
                references.append("- ").append(dangkyCount).append(" đăng ký thi của giáo viên\n");
            }

            // Check BANGDIEM table
            String sql3 = "SELECT COUNT(*) FROM BANGDIEM WHERE MAMH = ?";
            int diemCount = jdbcTemplate.queryForObject(sql3, Integer.class, maMH);
            if (diemCount > 0) {
                references.append("- ").append(diemCount).append(" bảng điểm của sinh viên\n");
            }

        } catch (Exception e) {
            System.err.println("Error checking references: " + e.getMessage());
        }

        return references.toString();
    }

    /**
     * Tạo backup dữ liệu trước khi thay đổi
     */
    private void createBackup(String operation, String maMH) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String backupNote = String.format("%s_%s_%s", operation, maMH, timestamp);
            
            // Log the backup operation
            System.out.println(SuccessCode.DB_BACKUP_SUCCESS_MSG + " - " + backupNote);
            
        } catch (Exception e) {
            System.err.println("Backup failed: " + e.getMessage());
            // Don't throw exception for backup failure
        }
    }
} 