package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_LINEA_ESTACION {

    @Element(required=false) public int CODIGO_LINEA_ESTACION;
    @Element(required=false) public int CODIGO_LINEA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_ESTACION;


    public clsBeP_LINEA_ESTACION() {
    }

    public clsBeP_LINEA_ESTACION(int CODIGO_LINEA_ESTACION,int CODIGO_LINEA,int CODIGO_SUCURSAL,int EMPRESA,
                                 int CODIGO_ESTACION) {

        this.CODIGO_LINEA_ESTACION=CODIGO_LINEA_ESTACION;
        this.CODIGO_LINEA=CODIGO_LINEA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.EMPRESA=EMPRESA;
        this.CODIGO_ESTACION=CODIGO_ESTACION;

    }


    public int getCODIGO_LINEA_ESTACION() {
        return CODIGO_LINEA_ESTACION;
    }
    public void setCODIGO_LINEA_ESTACION(int value) {
        CODIGO_LINEA_ESTACION=value;
    }
    public int getCODIGO_LINEA() {
        return CODIGO_LINEA;
    }
    public void setCODIGO_LINEA(int value) {
        CODIGO_LINEA=value;
    }
    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_ESTACION() {
        return CODIGO_ESTACION;
    }
    public void setCODIGO_ESTACION(int value) {
        CODIGO_ESTACION=value;
    }

}

