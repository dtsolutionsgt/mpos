package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_MONEDA {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public String SYMBOLO;
    @Element(required=false) public double CAMBIO;


    public clsBeP_MONEDA() {
    }

    public clsBeP_MONEDA(int CODIGO,String EMPRESA,String NOMBRE,int ACTIVO,
                         String SYMBOLO,double CAMBIO) {

        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;
        this.SYMBOLO=SYMBOLO;
        this.CAMBIO=CAMBIO;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
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
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }
    public String getSYMBOLO() {
        return SYMBOLO;
    }
    public void setSYMBOLO(String value) {
        SYMBOLO=value;
    }
    public double getCAMBIO() {
        return CAMBIO;
    }
    public void setCAMBIO(double value) {
        CAMBIO=value;
    }

}

