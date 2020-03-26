package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_FACTORCONV {

    @Element(required=false) public String PRODUCTO;
    @Element(required=false) public String UNIDADSUPERIOR;
    @Element(required=false) public double FACTORCONVERSION;
    @Element(required=false) public String UNIDADMINIMA;
    @Element(required=false) public String EMPRESA;


    public clsBeP_FACTORCONV() {
    }

    public clsBeP_FACTORCONV(String PRODUCTO,String UNIDADSUPERIOR,double FACTORCONVERSION,String UNIDADMINIMA,
                             String EMPRESA) {

        this.PRODUCTO=PRODUCTO;
        this.UNIDADSUPERIOR=UNIDADSUPERIOR;
        this.FACTORCONVERSION=FACTORCONVERSION;
        this.UNIDADMINIMA=UNIDADMINIMA;
        this.EMPRESA=EMPRESA;

    }


    public String getPRODUCTO() {
        return PRODUCTO;
    }
    public void setPRODUCTO(String value) {
        PRODUCTO=value;
    }
    public String getUNIDADSUPERIOR() {
        return UNIDADSUPERIOR;
    }
    public void setUNIDADSUPERIOR(String value) {
        UNIDADSUPERIOR=value;
    }
    public double getFACTORCONVERSION() {
        return FACTORCONVERSION;
    }
    public void setFACTORCONVERSION(double value) {
        FACTORCONVERSION=value;
    }
    public String getUNIDADMINIMA() {
        return UNIDADMINIMA;
    }
    public void setUNIDADMINIMA(String value) {
        UNIDADMINIMA=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

}

