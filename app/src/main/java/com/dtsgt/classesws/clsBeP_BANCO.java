package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_BANCO {

    @Element(required=false) public String CODIGO;
    @Element(required=false) public String TIPO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String CUENTA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public int CODIGO_BANCO;

    public clsBeP_BANCO() {
    }

    public clsBeP_BANCO(String CODIGO, String TIPO, String NOMBRE, String CUENTA,
                        int EMPRESA, int ACTIVO, int CODIGO_BANCO) {

        this.CODIGO=CODIGO;
        this.TIPO=TIPO;
        this.NOMBRE=NOMBRE;
        this.CUENTA=CUENTA;
        this.EMPRESA=EMPRESA;
        this.ACTIVO = ACTIVO;
        this.CODIGO_BANCO=CODIGO_BANCO;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getTIPO() {
        return TIPO;
    }
    public void setTIPO(String value) {
        TIPO=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getCUENTA() {
        return CUENTA;
    }
    public void setCUENTA(String value) {
        CUENTA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO =value;
    }
    public int getCODIGO_BANCO() {
        return CODIGO_BANCO;
    }
    public void setCODIGO_BANCO(int value) {
        CODIGO_BANCO=value;
    }

}

