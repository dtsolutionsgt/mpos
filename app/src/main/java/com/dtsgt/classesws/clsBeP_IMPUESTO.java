package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_IMPUESTO {

    @Element(required=false) public int CODIGO_IMPUESTO;
    @Element(required=false) public int CODIGO;
    @Element(required=false) public double VALOR;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public String EMPRESA;

    public clsBeP_IMPUESTO() {
    }

    public clsBeP_IMPUESTO(int CODIGO_IMPUESTO,int CODIGO,double VALOR,int ACTIVO,String EMPRESA
    ) {

        this.CODIGO_IMPUESTO=CODIGO_IMPUESTO;
        this.CODIGO=CODIGO;
        this.VALOR=VALOR;
        this.ACTIVO=ACTIVO;
        this.EMPRESA=EMPRESA;

    }

    public int getCODIGO_IMPUESTO() {
        return CODIGO_IMPUESTO;
    }
    public void setCODIGO_IMPUESTO(int value) {
        CODIGO_IMPUESTO=value;
    }
    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public double getVALOR() {
        return VALOR;
    }
    public void setVALOR(double value) {
        VALOR=value;
    }
    public int getActivo() {
        return ACTIVO;
    }
    public void setActivo(int value) {
        ACTIVO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

}

