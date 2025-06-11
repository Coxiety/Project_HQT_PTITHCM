package com.thitracnghiem.app.model;

import java.util.Date;

public class SINHVIEN {
    private String MASV;        // nChar(8) Primary Key
    private String HO;          // nvarchar(40)
    private String TEN;         // nvarchar(10)
    private Date NGAYSINH;      // Date
    private String DIACHI;      // nvarchar(100)
    private String MALOP;       // nchar(8) Foreign key
    
    // Constructors
    public SINHVIEN() {}
    
    public SINHVIEN(String MASV, String HO, String TEN) {
        this.MASV = MASV;
        this.HO = HO;
        this.TEN = TEN;
    }
    
    // Getters and Setters
    public String getMASV() {
        return MASV;
    }
    
    public void setMASV(String MASV) {
        this.MASV = MASV;
    }
    
    public String getHO() {
        return HO;
    }
    
    public void setHO(String HO) {
        this.HO = HO;
    }
    
    public String getTEN() {
        return TEN;
    }
    
    public void setTEN(String TEN) {
        this.TEN = TEN;
    }
    
    public Date getNGAYSINH() {
        return NGAYSINH;
    }
    
    public void setNGAYSINH(Date NGAYSINH) {
        this.NGAYSINH = NGAYSINH;
    }
    
    public String getDIACHI() {
        return DIACHI;
    }
    
    public void setDIACHI(String DIACHI) {
        this.DIACHI = DIACHI;
    }
    
    public String getMALOP() {
        return MALOP;
    }
    
    public void setMALOP(String MALOP) {
        this.MALOP = MALOP;
    }
    
    // Helper method
    public String getFullName() {
        return (HO != null ? HO : "") + " " + (TEN != null ? TEN : "");
    }
    
    @Override
    public String toString() {
        return "SINHVIEN{" +
                "MASV='" + MASV + '\'' +
                ", HO='" + HO + '\'' +
                ", TEN='" + TEN + '\'' +
                ", NGAYSINH=" + NGAYSINH +
                ", DIACHI='" + DIACHI + '\'' +
                ", MALOP='" + MALOP + '\'' +
                '}';
    }
} 