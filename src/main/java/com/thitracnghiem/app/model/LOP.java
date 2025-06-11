package com.thitracnghiem.app.model;

public class LOP {
    private String MALOP;      // nChar(8) Primary Key
    private String TENLOP;     // nVarchar(40) Unique, not NULL
    
    // Constructors
    public LOP() {}
    
    public LOP(String MALOP, String TENLOP) {
        this.MALOP = MALOP;
        this.TENLOP = TENLOP;
    }
    
    // Getters and Setters
    public String getMALOP() {
        return MALOP;
    }
    
    public void setMALOP(String MALOP) {
        this.MALOP = MALOP;
    }
    
    public String getTENLOP() {
        return TENLOP;
    }
    
    public void setTENLOP(String TENLOP) {
        this.TENLOP = TENLOP;
    }
    
    @Override
    public String toString() {
        return "LOP{" +
                "MALOP='" + MALOP + '\'' +
                ", TENLOP='" + TENLOP + '\'' +
                '}';
    }
} 