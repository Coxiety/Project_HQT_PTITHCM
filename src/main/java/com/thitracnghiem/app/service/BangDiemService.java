package com.thitracnghiem.app.service;

import com.thitracnghiem.app.config.SessionManager;
import com.thitracnghiem.app.exception.ErrorCode;
import com.thitracnghiem.app.model.BANGDIEM;
import com.thitracnghiem.app.model.SINHVIEN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BangDiemService {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SinhVienService sinhVienService;

    private RowMapper<BANGDIEM> bangDiemRowMapper = new RowMapper<BANGDIEM>() {
        @Override
        public BANGDIEM mapRow(ResultSet rs, int rowNum) throws SQLException {
            BANGDIEM bd = new BANGDIEM();
            bd.setMASV(rs.getString("MASV"));
            bd.setMAMH(rs.getString("MAMH"));
            bd.setLAN(rs.getShort("LAN"));
            bd.setNGAYTHI(rs.getTimestamp("NGAYTHI"));
            bd.setDIEM(rs.getFloat("DIEM"));
            return bd;
        }
    };

    /**
     * Lấy bảng điểm chi tiết của một lớp - môn thi - lần thi cụ thể
     */
    public List<Map<String, Object>> getBangDiemChiTiet(String maLop, String maMH, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            // Query để lấy bảng điểm với thông tin sinh viên
            String sql = """
                SELECT 
                    bd.MASV, 
                    bd.DIEM, 
                    bd.NGAYTHI,
                    sv.HO, 
                    sv.TEN,
                    sv.MALOP
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                WHERE sv.MALOP = ? 
                  AND bd.MAMH = ? 
                  AND bd.LAN = ?
                ORDER BY bd.MASV
            """;
            
            List<Map<String, Object>> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("masv", rs.getString("MASV"));
                row.put("tensv", (rs.getString("HO") + " " + rs.getString("TEN")).trim());
                row.put("diem", rs.getFloat("DIEM"));
                row.put("ketqua", rs.getFloat("DIEM") >= 5.0 ? "Đạt" : "Không đạt");
                row.put("ngaythi", rs.getTimestamp("NGAYTHI"));
                return row;
            }, maLop, maMH, lan);
            
            System.out.println("📊 Lấy bảng điểm: " + result.size() + " sinh viên cho lớp " + maLop + 
                             ", môn " + maMH + ", lần " + lan);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy bảng điểm chi tiết: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách lớp có bài thi (có dữ liệu trong BANGDIEM)
     */
    public List<String> getLopCoBaiThi() {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT DISTINCT sv.MALOP
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                ORDER BY sv.MALOP
            """;
            
            List<String> result = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("MALOP"));
            
            System.out.println("🏫 Tìm thấy " + result.size() + " lớp có bài thi");
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy danh sách lớp có bài thi: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách môn thi của một lớp (từ bảng BANGDIEM)
     */
    public List<Map<String, Object>> getMonThiCuaLop(String maLop) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT DISTINCT 
                    bd.MAMH,
                    bd.LAN,
                    MIN(bd.NGAYTHI) as NGAYTHI,
                    gdk.TRINHDO,
                    gdk.THOIGIAN
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                LEFT JOIN GIAOVIEN_DANGKY gdk ON gdk.MALOP = sv.MALOP 
                    AND gdk.MAMH = bd.MAMH 
                    AND gdk.LAN = bd.LAN
                WHERE sv.MALOP = ?
                GROUP BY bd.MAMH, bd.LAN, gdk.TRINHDO, gdk.THOIGIAN
                ORDER BY bd.MAMH, bd.LAN
            """;
            
            List<Map<String, Object>> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("MAMH", rs.getString("MAMH"));
                row.put("LAN", rs.getShort("LAN"));
                row.put("NGAYTHI", rs.getTimestamp("NGAYTHI"));
                row.put("TRINHDO", rs.getString("TRINHDO"));
                row.put("THOIGIAN", rs.getShort("THOIGIAN"));
                return row;
            }, maLop);
            
            System.out.println("📚 Lớp " + maLop + " có " + result.size() + " môn thi");
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy môn thi của lớp: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Kiểm tra xem một lớp có dữ liệu bài thi không
     */
    public boolean isLopCoBaiThi(String maLop) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT COUNT(*) 
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                WHERE sv.MALOP = ?
            """;
            
            int count = jdbcTemplate.queryForObject(sql, Integer.class, maLop);
            return count > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra lớp có bài thi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đếm số sinh viên đã thi của một lớp - môn - lần cụ thể
     */
    public int countSinhVienDaThi(String maLop, String maMH, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT COUNT(*) 
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                WHERE sv.MALOP = ? AND bd.MAMH = ? AND bd.LAN = ?
            """;
            
            return jdbcTemplate.queryForObject(sql, Integer.class, maLop, maMH, lan);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi đếm sinh viên đã thi: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Lấy điểm trung bình của một lớp - môn - lần cụ thể
     */
    public Double getDiemTrungBinh(String maLop, String maMH, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT AVG(CAST(bd.DIEM AS FLOAT)) 
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                WHERE sv.MALOP = ? AND bd.MAMH = ? AND bd.LAN = ?
            """;
            
            return jdbcTemplate.queryForObject(sql, Double.class, maLop, maMH, lan);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi tính điểm trung bình: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Kiểm tra xem đăng ký thi đã có sinh viên nào thi chưa
     * Dùng để quyết định có cho phép edit/delete đăng ký thi không
     */
    public boolean isDangKyThiDaCoSinhVienThi(String maLop, String maMH, Short lan) {
        try {
            JdbcTemplate jdbcTemplate = sessionManager.getJdbcTemplate();
            
            String sql = """
                SELECT COUNT(*) 
                FROM BANGDIEM bd
                INNER JOIN SINHVIEN sv ON bd.MASV = sv.MASV
                WHERE sv.MALOP = ? AND bd.MAMH = ? AND bd.LAN = ?
            """;
            
            int count = jdbcTemplate.queryForObject(sql, Integer.class, maLop, maMH, lan);
            boolean daCoSinhVienThi = count > 0;
            
            System.out.println("🔍 Kiểm tra đăng ký thi: Lớp " + maLop + ", Môn " + maMH + 
                             ", Lần " + lan + " → " + (daCoSinhVienThi ? 
                             "Đã có " + count + " sinh viên thi" : "Chưa có sinh viên nào thi"));
            
            return daCoSinhVienThi;
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra sinh viên đã thi: " + e.getMessage());
            return false; // Default: coi như chưa có ai thi (cho phép edit/delete)
        }
    }
} 