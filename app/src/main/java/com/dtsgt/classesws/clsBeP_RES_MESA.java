package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_RES_MESA {

    @Element(required=false) public int CODIGO_MESA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int CODIGO_SALA;
    @Element(required=false) public int CODIGO_GRUPO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public double LARGO;
    @Element(required=false) public double ANCHO;
    @Element(required=false) public double POSX;
    @Element(required=false) public double POSY;
    @Element(required=false) public String CODIGO_QR;


    public clsBeP_RES_MESA() {
    }

    public clsBeP_RES_MESA(int CODIGO_MESA,int EMPRESA,int CODIGO_SUCURSAL,int CODIGO_SALA,
                           int CODIGO_GRUPO,String NOMBRE,double LARGO,double ANCHO,
                           double POSX,double POSY,String CODIGO_QR) {

        this.CODIGO_MESA=CODIGO_MESA;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO_SALA=CODIGO_SALA;
        this.CODIGO_GRUPO=CODIGO_GRUPO;
        this.NOMBRE=NOMBRE;
        this.LARGO=LARGO;
        this.ANCHO=ANCHO;
        this.POSX=POSX;
        this.POSY=POSY;
        this.CODIGO_QR=CODIGO_QR;

    }


    public int getCODIGO_MESA() {
        return CODIGO_MESA;
    }
    public void setCODIGO_MESA(int value) {
        CODIGO_MESA=value;
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
    public int getCODIGO_SALA() {
        return CODIGO_SALA;
    }
    public void setCODIGO_SALA(int value) {
        CODIGO_SALA=value;
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
    public double getLARGO() {
        return LARGO;
    }
    public void setLARGO(double value) {
        LARGO=value;
    }
    public double getANCHO() {
        return ANCHO;
    }
    public void setANCHO(double value) {
        ANCHO=value;
    }
    public double getPOSX() {
        return POSX;
    }
    public void setPOSX(double value) {
        POSX=value;
    }
    public double getPOSY() {
        return POSY;
    }
    public void setPOSY(double value) {
        POSY=value;
    }
    public String getCODIGO_QR() {
        return CODIGO_QR;
    }
    public void setCODIGO_QR(String value) {
        CODIGO_QR=value;
    }

}


