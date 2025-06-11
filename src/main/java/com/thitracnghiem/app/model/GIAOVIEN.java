package com.thitracnghiem.app.model;

public class GIAOVIEN {
    private String MAGV;        // nChar(8) Primary Key
    private String HO;          // nvarchar(40)
    private String TEN;         // nvarchar(10)
    private String SODTLL;      // nChar(15) Số điện thoại liên lạc
    private String DIACHI;      // nvarchar(50)

    // Constructors
    public GIAOVIEN() {}

    public GIAOVIEN(String MAGV, String HO, String TEN) {
        this.MAGV = MAGV;
        this.HO = HO;
        this.TEN = TEN;
    }

    // Getters and Setters
    public String getMAGV() {
        return MAGV;
    }

    public void setMAGV(String MAGV) {
        this.MAGV = MAGV;
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

    public String getSODTLL() {
        return SODTLL;
    }

    public void setSODTLL(String SODTLL) {
        this.SODTLL = SODTLL;
    }

    public String getDIACHI() {
        return DIACHI;
    }

    public void setDIACHI(String DIACHI) {
        this.DIACHI = DIACHI;
    }

    // Helper method
    public String getFullName() {
        return (HO != null ? HO : "") + " " + (TEN != null ? TEN : "");
    }

    @Override
    public String toString() {
        return "GIAOVIEN{" +
                "MAGV='" + MAGV + '\'' +
                ", HO='" + HO + '\'' +
                ", TEN='" + TEN + '\'' +
                ", SODTLL='" + SODTLL + '\'' +
                ", DIACHI='" + DIACHI + '\'' +
                '}';
    }
}