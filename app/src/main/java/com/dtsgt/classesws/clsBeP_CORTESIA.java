package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_CORTESIA {

    @Element(required=false) public int CODIGO_CORTESIA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int CODIGO_VENDEDOR;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public String CLAVE;


    public clsBeP_CORTESIA() {
    }

    public clsBeP_CORTESIA(int CODIGO_CORTESIA,int EMPRESA,String NOMBRE,int CODIGO_VENDEDOR,
                           int ACTIVO,String CLAVE) {

        this.CODIGO_CORTESIA=CODIGO_CORTESIA;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.CODIGO_VENDEDOR=CODIGO_VENDEDOR;
        this.ACTIVO=ACTIVO;
        this.CLAVE=CLAVE;

    }


    public int getCODIGO_CORTESIA() {
        return CODIGO_CORTESIA;
    }
    public void setCODIGO_CORTESIA(int value) {
        CODIGO_CORTESIA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getCODIGO_VENDEDOR() {
        return CODIGO_VENDEDOR;
    }
    public void setCODIGO_VENDEDOR(int value) {
        CODIGO_VENDEDOR=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }
    public String getCLAVE() {
        return CLAVE;
    }
    public void setCLAVE(String value) {
        CLAVE=value;
    }

}

