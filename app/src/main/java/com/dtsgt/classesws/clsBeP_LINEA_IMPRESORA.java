package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_LINEA_IMPRESORA {

    @Element(required=false) public int CODIGO_LINEA_IMPRESORA;
    @Element(required=false) public int CODIGO_LINEA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_IMPRESORA;


    public clsBeP_LINEA_IMPRESORA() {
    }

    public clsBeP_LINEA_IMPRESORA(int CODIGO_LINEA_IMPRESORA,int CODIGO_LINEA,int CODIGO_SUCURSAL,int EMPRESA,
                                  int CODIGO_IMPRESORA) {

        this.CODIGO_LINEA_IMPRESORA=CODIGO_LINEA_IMPRESORA;
        this.CODIGO_LINEA=CODIGO_LINEA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.EMPRESA=EMPRESA;
        this.CODIGO_IMPRESORA=CODIGO_IMPRESORA;

    }


    public int getCODIGO_LINEA_IMPRESORA() {
        return CODIGO_LINEA_IMPRESORA;
    }
    public void setCODIGO_LINEA_IMPRESORA(int value) {
        CODIGO_LINEA_IMPRESORA=value;
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
    public int getCODIGO_IMPRESORA() {
        return CODIGO_IMPRESORA;
    }
    public void setCODIGO_IMPRESORA(int value) {
        CODIGO_IMPRESORA=value;
    }

}

