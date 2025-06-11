package com.thitracnghiem.app.model;

import java.util.Date;

public class BANGDIEM {
    private String MASV;        // nChar(8) Foreign Key
    private String MAMH;        // nChar(5) Foreign Key
    private Short LAN;          // SmallInt Lần thi >=1 và <=2
    private Date NGAYTHI;       // Date Default: GetDate()
    private Float DIEM;         // float Điểm từ 0 đến 10
    
    // Constructors
    public BANGDIEM() {}
    
    public BANGDIEM(String MASV, String MAMH, Short LAN) {
        this.MASV = MASV;
        this.MAMH = MAMH;
        this.LAN = LAN;
    }
    
    // Getters and Setters
    public String getMASV() {
        return MASV;
    }
    
    public void setMASV(String MASV) {
        this.MASV = MASV;
    }
    
    public String getMAMH() {
        return MAMH;
    }
    
    public void setMAMH(String MAMH) {
        this.MAMH = MAMH;
    }
    
    public Short getLAN() {
        return LAN;
    }
    
    public void setLAN(Short LAN) {
        this.LAN = LAN;
    }
    
    public Date getNGAYTHI() {
        return NGAYTHI;
    }
    
    public void setNGAYTHI(Date NGAYTHI) {
        this.NGAYTHI = NGAYTHI;
    }
    
    public Float getDIEM() {
        return DIEM;
    }
    
    public void setDIEM(Float DIEM) {
        this.DIEM = DIEM;
    }
    
    @Override
    public String toString() {
        return "BANGDIEM{" +
                "MASV='" + MASV + '\'' +
                ", MAMH='" + MAMH + '\'' +
                ", LAN=" + LAN +
                ", NGAYTHI=" + NGAYTHI +
                ", DIEM=" + DIEM +
                '}';
    }
} 