package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_RES_MESERO {

    @Element(required=false) public int CODIGO_MESERO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int CODIGO_GRUPO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String CLAVE;


    public clsBeP_RES_MESERO() {
    }

    public clsBeP_RES_MESERO(int CODIGO_MESERO,int EMPRESA,int CODIGO_SUCURSAL,int CODIGO_GRUPO,
                             String NOMBRE,String CLAVE) {

        this.CODIGO_MESERO=CODIGO_MESERO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO_GRUPO=CODIGO_GRUPO;
        this.NOMBRE=NOMBRE;
        this.CLAVE=CLAVE;

    }


    public int getCODIGO_MESERO() {
        return CODIGO_MESERO;
    }
    public void setCODIGO_MESERO(int value) {
        CODIGO_MESERO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
    }
    public int getCODIGO_GRUPO() {
        return CODIGO_GRUPO;
    }
    public void setCODIGO_GRUPO(int value) {
        CODIGO_GRUPO=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getCLAVE() {
        return CLAVE;
    }
    public void setCLAVE(String value) {
        CLAVE=value;
    }

}


