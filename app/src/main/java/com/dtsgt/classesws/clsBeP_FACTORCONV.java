package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_FACTORCONV {

    @Element(required=false) public int PRODUCTO;
    @Element(required=false) public String UNIDADSUPERIOR;
    @Element(required=false) public double FACTORCONVERSION;
    @Element(required=false) public String UNIDADMINIMA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_FACTORCONV;


    public clsBeP_FACTORCONV() {
    }

    public clsBeP_FACTORCONV(int PRODUCTO,String UNIDADSUPERIOR,double FACTORCONVERSION,String UNIDADMINIMA,
                             int EMPRESA, int CODIGO_FACTORCONV) {

        this.PRODUCTO=PRODUCTO;
        this.UNIDADSUPERIOR=UNIDADSUPERIOR;
        this.FACTORCONVERSION=FACTORCONVERSION;
        this.UNIDADMINIMA=UNIDADMINIMA;
        this.EMPRESA=EMPRESA;
        this.CODIGO_FACTORCONV=CODIGO_FACTORCONV;

    }


    public int getPRODUCTO() {
        return PRODUCTO;
    }
    public void setPRODUCTO(int value) {
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
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_FACTORCONV() {
        return CODIGO_FACTORCONV;
    }
    public void setCODIGO_FACTORCONV(int value) {
        CODIGO_FACTORCONV=value;
    }

}

