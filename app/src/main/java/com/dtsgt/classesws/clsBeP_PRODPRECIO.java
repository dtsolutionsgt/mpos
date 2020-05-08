package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODPRECIO {

    @Element(required=false) public int CODIGO_PRECIO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public int NIVEL;
    @Element(required=false) public double PRECIO;
    @Element(required=false) public String UNIDADMEDIDA;


    public clsBeP_PRODPRECIO() {
    }

    public clsBeP_PRODPRECIO(int CODIGO_PRECIO,int EMPRESA,int CODIGO_PRODUCTO,int NIVEL,
                             double PRECIO,String UNIDADMEDIDA) {

        this.CODIGO_PRECIO=CODIGO_PRECIO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.NIVEL=NIVEL;
        this.PRECIO=PRECIO;
        this.UNIDADMEDIDA=UNIDADMEDIDA;

    }


    public int getCODIGO_PRECIO() {
        return CODIGO_PRECIO;
    }
    public void setCODIGO_PRECIO(int value) {
        CODIGO_PRECIO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_PRODUCTO() {
        return CODIGO_PRODUCTO;
    }
    public void setCODIGO_PRODUCTO(int value) {
        CODIGO_PRODUCTO=value;
    }
    public int getNIVEL() {
        return NIVEL;
    }
    public void setNIVEL(int value) {
        NIVEL=value;
    }
    public double getPRECIO() {
        return PRECIO;
    }
    public void setPRECIO(double value) {
        PRECIO=value;
    }
    public String getUNIDADMEDIDA() {
        return UNIDADMEDIDA;
    }
    public void setUNIDADMEDIDA(String value) {
        UNIDADMEDIDA=value;
    }

}

