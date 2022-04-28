package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_ALMACEN {

    @Element(required=false) public int CODIGO_ALMACEN;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public boolean ACTIVO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean ES_PRINCIPAL;


    public clsBeP_ALMACEN() {
    }

    public clsBeP_ALMACEN(int CODIGO_ALMACEN,int EMPRESA,int CODIGO_SUCURSAL,boolean ACTIVO,
                          String NOMBRE,boolean ES_PRINCIPAL) {

        this.CODIGO_ALMACEN=CODIGO_ALMACEN;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.ACTIVO=ACTIVO;
        this.NOMBRE=NOMBRE;
        this.ES_PRINCIPAL=ES_PRINCIPAL;

    }


    public int getCODIGO_ALMACEN() {
        return CODIGO_ALMACEN;
    }
    public void setCODIGO_ALMACEN(int value) {
        CODIGO_ALMACEN=value;
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
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public boolean getES_PRINCIPAL() {
        return ES_PRINCIPAL;
    }
    public void setES_PRINCIPAL(boolean value) {
        ES_PRINCIPAL=value;
    }

}

