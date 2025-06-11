package com.thitracnghiem.app.model;

public class BODE {
    private String MAMH;        // Char(5) Foreign Key
    private Integer CAUHOI;     // Int, tự động Primary key
    private String TRINHDO;     // char(1) 'A', 'B', 'C'
    private String NOIDUNG;     // Nvarchar(200)
    private String A;           // nvarchar(50)
    private String B;           // nvarchar(50)
    private String C;           // nvarchar(50)
    private String D;           // nvarchar(50)
    private String DAP_AN;      // nchar(1) IN ('A', 'B', 'C', 'D')
    private String MAGV;        // nChar(8) Foreign Key
    
    // Constructors
    public BODE() {}
    
    public BODE(String MAMH, String NOIDUNG, String MAGV) {
        this.MAMH = MAMH;
        this.NOIDUNG = NOIDUNG;
        this.MAGV = MAGV;
    }
    
    // Getters and Setters
    public String getMAMH() {
        return MAMH;
    }
    
    public void setMAMH(String MAMH) {
        this.MAMH = MAMH;
    }
    
    public Integer getCAUHOI() {
        return CAUHOI;
    }
    
    public void setCAUHOI(Integer CAUHOI) {
        this.CAUHOI = CAUHOI;
    }
    
    public String getTRINHDO() {
        return TRINHDO;
    }
    
    public void setTRINHDO(String TRINHDO) {
        this.TRINHDO = TRINHDO;
    }
    
    public String getNOIDUNG() {
        return NOIDUNG;
    }
    
    public void setNOIDUNG(String NOIDUNG) {
        this.NOIDUNG = NOIDUNG;
    }
    
    public String getA() {
        return A;
    }
    
    public void setA(String A) {
        this.A = A;
    }
    
    public String getB() {
        return B;
    }
    
    public void setB(String B) {
        this.B = B;
    }
    
    public String getC() {
        return C;
    }
    
    public void setC(String C) {
        this.C = C;
    }
    
    public String getD() {
        return D;
    }
    
    public void setD(String D) {
        this.D = D;
    }
    
    public String getDAP_AN() {
        return DAP_AN;
    }
    
    public void setDAP_AN(String DAP_AN) {
        this.DAP_AN = DAP_AN;
    }
    
    public String getMAGV() {
        return MAGV;
    }
    
    public void setMAGV(String MAGV) {
        this.MAGV = MAGV;
    }
    
    @Override
    public String toString() {
        return "BODE{" +
                "MAMH='" + MAMH + '\'' +
                ", CAUHOI=" + CAUHOI +
                ", TRINHDO='" + TRINHDO + '\'' +
                ", NOIDUNG='" + NOIDUNG + '\'' +
                ", A='" + A + '\'' +
                ", B='" + B + '\'' +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                ", DAP_AN='" + DAP_AN + '\'' +
                ", MAGV='" + MAGV + '\'' +
                '}';
    }
} 