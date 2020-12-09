package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_IMPRESORA {

    @Element(required=false) public int CODIGO_IMPRESORA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String NUMERO_SERIE;
    @Element(required=false) public int CODIGO_MARCA;
    @Element(required=false) public int CODIGO_MODELO;
    @Element(required=false) public String TIPO_IMPRESORA;
    @Element(required=false) public String MAC;
    @Element(required=false) public String IP;
    @Element(required=false) public String FECHA_AGR;
    @Element(required=false) public int IMPRESIONES;
    @Element(required=false) public boolean ACTIVO;


    public clsBeP_IMPRESORA() {
    }

    public clsBeP_IMPRESORA(int CODIGO_IMPRESORA,int EMPRESA,int CODIGO_SUCURSAL,String NOMBRE,
                            String NUMERO_SERIE,int CODIGO_MARCA,int CODIGO_MODELO,String TIPO_IMPRESORA,
                            String MAC,String IP,String FECHA_AGR,int IMPRESIONES,
                            boolean ACTIVO) {

        this.CODIGO_IMPRESORA=CODIGO_IMPRESORA;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.NOMBRE=NOMBRE;
        this.NUMERO_SERIE=NUMERO_SERIE;
        this.CODIGO_MARCA=CODIGO_MARCA;
        this.CODIGO_MODELO=CODIGO_MODELO;
        this.TIPO_IMPRESORA=TIPO_IMPRESORA;
        this.MAC=MAC;
        this.IP=IP;
        this.FECHA_AGR=FECHA_AGR;
        this.IMPRESIONES=IMPRESIONES;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_IMPRESORA() {
        return CODIGO_IMPRESORA;
    }
    public void setCODIGO_IMPRESORA(int value) {
        CODIGO_IMPRESORA=value;
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
    public String getNUMERO_SERIE() {
        return NUMERO_SERIE;
    }
    public void setNUMERO_SERIE(String value) {
        NUMERO_SERIE=value;
    }
    public int getCODIGO_MARCA() {
        return CODIGO_MARCA;
    }
    public void setCODIGO_MARCA(int value) {
        CODIGO_MARCA=value;
    }
    public int getCODIGO_MODELO() {
        return CODIGO_MODELO;
    }
    public void setCODIGO_MODELO(int value) {
        CODIGO_MODELO=value;
    }
    public String getTIPO_IMPRESORA() {
        return TIPO_IMPRESORA;
    }
    public void setTIPO_IMPRESORA(String value) {
        TIPO_IMPRESORA=value;
    }
    public String getMAC() {
        return MAC;
    }
    public void setMAC(String value) {
        MAC=value;
    }
    public String getIP() {
        return IP;
    }
    public void setIP(String value) {
        IP=value;
    }
    public String getFECHA_AGR() {
        return FECHA_AGR;
    }
    public void setFECHA_AGR(String value) {
        FECHA_AGR=value;
    }
    public int getIMPRESIONES() {
        return IMPRESIONES;
    }
    public void setIMPRESIONES(int value) {
        IMPRESIONES=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

