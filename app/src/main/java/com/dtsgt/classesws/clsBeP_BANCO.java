package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_BANCO {

    @Element(required=false) public String CODIGO;
    @Element(required=false) public String TIPO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String CUENTA;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public int Activo;


    public clsBeP_BANCO() {
    }

    public clsBeP_BANCO(String CODIGO,String TIPO,String NOMBRE,String CUENTA,
                        String EMPRESA,int Activo) {

        this.CODIGO=CODIGO;
        this.TIPO=TIPO;
        this.NOMBRE=NOMBRE;
        this.CUENTA=CUENTA;
        this.EMPRESA=EMPRESA;
        this.Activo=Activo;

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
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public int getActivo() {
        return Activo;
    }
    public void setActivo(int value) {
        Activo=value;
    }

}

