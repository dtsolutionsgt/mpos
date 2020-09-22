package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_RES_GRUPO {

    @Element(required=false) public int CODIGO_GRUPO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String TELEFONO;


    public clsBeP_RES_GRUPO() {
    }

    public clsBeP_RES_GRUPO(int CODIGO_GRUPO,int EMPRESA,int CODIGO_SUCURSAL,String NOMBRE,
                            String TELEFONO) {

        this.CODIGO_GRUPO=CODIGO_GRUPO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.NOMBRE=NOMBRE;
        this.TELEFONO=TELEFONO;

    }


    public int getCODIGO_GRUPO() {
        return CODIGO_GRUPO;
    }
    public void setCODIGO_GRUPO(int value) {
        CODIGO_GRUPO=value;
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
    public String getTELEFONO() {
        return TELEFONO;
    }
    public void setTELEFONO(String value) {
        TELEFONO=value;
    }

}

