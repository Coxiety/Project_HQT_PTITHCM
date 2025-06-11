package com.thitracnghiem.app.model;

public class MONHOC {
    private String MAMH;       // nChar(5) Primary key
    private String TENMH;      // nVarchar(40) Unique Key, not NULL
    
    // Constructors
    public MONHOC() {}
    
    public MONHOC(String MAMH, String TENMH) {
        this.MAMH = MAMH;
        this.TENMH = TENMH;
    }
    
    // Getters and Setters
    public String getMAMH() {
        return MAMH;
    }
    
    public void setMAMH(String MAMH) {
        this.MAMH = MAMH;
    }
    
    public String getTENMH() {
        return TENMH;
    }
    
    public void setTENMH(String TENMH) {
        this.TENMH = TENMH;
    }
    
    @Override
    public String toString() {
        return "MONHOC{" +
                "MAMH='" + MAMH + '\'' +
                ", TENMH='" + TENMH + '\'' +
                '}';
    }
} 