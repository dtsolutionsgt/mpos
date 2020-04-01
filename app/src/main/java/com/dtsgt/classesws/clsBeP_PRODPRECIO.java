package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODPRECIO {

    @Element(required=false) public String CODIGO;
    @Element(required=false) public int NIVEL;
    @Element(required=false) public double PRECIO;
    @Element(required=false) public String UNIDADMEDIDA;
    @Element(required=false) public String EMPRESA;


    public clsBeP_PRODPRECIO() {
    }

    public clsBeP_PRODPRECIO(String CODIGO,int NIVEL,double PRECIO,String UNIDADMEDIDA,
                             String EMPRESA) {

        this.CODIGO=CODIGO;
        this.NIVEL=NIVEL;
        this.PRECIO=PRECIO;
        this.UNIDADMEDIDA=UNIDADMEDIDA;
        this.EMPRESA=EMPRESA;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
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
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

}

