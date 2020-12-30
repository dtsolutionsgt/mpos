package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_CAJA_IMPRESORA {

    @Element(required=false) public int CODIGO_CAJA_IMPRESORA;
    @Element(required=false) public int CODIGO_CAJA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_IMPRESORA;


    public clsBeP_CAJA_IMPRESORA() {
    }

    public clsBeP_CAJA_IMPRESORA(int CODIGO_CAJA_IMPRESORA,int CODIGO_CAJA,int CODIGO_SUCURSAL,int EMPRESA,
                                 int CODIGO_IMPRESORA) {

        this.CODIGO_CAJA_IMPRESORA=CODIGO_CAJA_IMPRESORA;
        this.CODIGO_CAJA=CODIGO_CAJA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.EMPRESA=EMPRESA;
        this.CODIGO_IMPRESORA=CODIGO_IMPRESORA;

    }


    public int getCODIGO_CAJA_IMPRESORA() {
        return CODIGO_CAJA_IMPRESORA;
    }
    public void setCODIGO_CAJA_IMPRESORA(int value) {
        CODIGO_CAJA_IMPRESORA=value;
    }
    public int getCODIGO_CAJA() {
        return CODIGO_CAJA;
    }
    public void setCODIGO_CAJA(int value) {
        CODIGO_CAJA=value;
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

