package com.thitracnghiem.app.model;

import java.util.Date;

public class GIAOVIEN_DANGKY {
    private String MAGV;        // nChar(8) Foreign Key
    private String MALOP;       // nchar(8) Foreign key
    private String MAMH;        // nChar(5) Foreign key
    private String TRINHDO;     // nChar(1) 'A', 'B', 'C'
    private Date NGAYTHI;       // datetime Default: Getdate()
    private Short LAN;          // SmallInt Lần thi >=1 và <=2
    private Short SOCAUTHI;     // SmallInt >=10 và <=100
    private Short THOIGIAN;     // SmallInt >=5' và <=60'
    
    // Constructors
    public GIAOVIEN_DANGKY() {}
    
    public GIAOVIEN_DANGKY(String MAGV, String MALOP, String MAMH, Short LAN) {
        this.MAGV = MAGV;
        this.MALOP = MALOP;
        this.MAMH = MAMH;
        this.LAN = LAN;
    }
    
    // Getters and Setters
    public String getMAGV() {
        return MAGV;
    }
    
    public void setMAGV(String MAGV) {
        this.MAGV = MAGV;
    }
    
    public String getMALOP() {
        return MALOP;
    }
    
    public void setMALOP(String MALOP) {
        this.MALOP = MALOP;
    }
    
    public String getMAMH() {
        return MAMH;
    }
    
    public void setMAMH(String MAMH) {
        this.MAMH = MAMH;
    }
    
    public String getTRINHDO() {
        return TRINHDO;
    }
    
    public void setTRINHDO(String TRINHDO) {
        this.TRINHDO = TRINHDO;
    }
    
    public Date getNGAYTHI() {
        return NGAYTHI;
    }
    
    public void setNGAYTHI(Date NGAYTHI) {
        this.NGAYTHI = NGAYTHI;
    }
    
    public Short getLAN() {
        return LAN;
    }
    
    public void setLAN(Short LAN) {
        this.LAN = LAN;
    }
    
    public Short getSOCAUTHI() {
        return SOCAUTHI;
    }
    
    public void setSOCAUTHI(Short SOCAUTHI) {
        this.SOCAUTHI = SOCAUTHI;
    }
    
    public Short getTHOIGIAN() {
        return THOIGIAN;
    }
    
    public void setTHOIGIAN(Short THOIGIAN) {
        this.THOIGIAN = THOIGIAN;
    }
    
    @Override
    public String toString() {
        return "GIAOVIEN_DANGKY{" +
                "MAGV='" + MAGV + '\'' +
                ", MALOP='" + MALOP + '\'' +
                ", MAMH='" + MAMH + '\'' +
                ", TRINHDO='" + TRINHDO + '\'' +
                ", NGAYTHI=" + NGAYTHI +
                ", LAN=" + LAN +
                ", SOCAUTHI=" + SOCAUTHI +
                ", THOIGIAN=" + THOIGIAN +
                '}';
    }
} 