package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_ESTACION {

    @Element(required=false) public int CODIGO_ESTACION;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int CODIGO_IMPRESORA;


    public clsBeP_ESTACION() {
    }

    public clsBeP_ESTACION(int CODIGO_ESTACION,int EMPRESA,int CODIGO_SUCURSAL,String NOMBRE,
                           int CODIGO_IMPRESORA) {

        this.CODIGO_ESTACION=CODIGO_ESTACION;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.NOMBRE=NOMBRE;
        this.CODIGO_IMPRESORA=CODIGO_IMPRESORA;

    }


    public int getCODIGO_ESTACION() {
        return CODIGO_ESTACION;
    }
    public void setCODIGO_ESTACION(int value) {
        CODIGO_ESTACION=value;
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
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getCODIGO_IMPRESORA() {
        return CODIGO_IMPRESORA;
    }
    public void setCODIGO_IMPRESORA(int value) {
        CODIGO_IMPRESORA=value;
    }

}

