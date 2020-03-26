package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_LINEA {

    @Element(required=false) public int CODIGO_LINEA;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String MARCA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public String IMAGEN;


    public clsBeP_LINEA() {
    }

    public clsBeP_LINEA(int CODIGO_LINEA,String CODIGO,String EMPRESA,String MARCA,
                        String NOMBRE,int ACTIVO,String IMAGEN) {

        this.CODIGO_LINEA=CODIGO_LINEA;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.MARCA=MARCA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;
        this.IMAGEN=IMAGEN;

    }


    public int getCODIGO_LINEA() {
        return CODIGO_LINEA;
    }
    public void setCODIGO_LINEA(int value) {
        CODIGO_LINEA=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public String getMARCA() {
        return MARCA;
    }
    public void setMARCA(String value) {
        MARCA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }
    public String getIMAGEN() {
        return IMAGEN;
    }
    public void setIMAGEN(String value) {
        IMAGEN=value;
    }

}

