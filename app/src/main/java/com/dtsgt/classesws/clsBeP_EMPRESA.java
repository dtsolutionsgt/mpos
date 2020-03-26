package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_EMPRESA {

    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int COL_IMP;

    public clsBeP_EMPRESA() {
    }

    public clsBeP_EMPRESA(String EMPRESA,String NOMBRE,int COL_IMP) {

        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.COL_IMP=COL_IMP;

    }


    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getCOL_IMP() {
        return COL_IMP;
    }
    public void setCOL_IMP(int value) {
        COL_IMP=value;
    }

}

