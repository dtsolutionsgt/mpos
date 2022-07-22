package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_MODIFICADOR_GRUPO {

    @Element(required=false) public int CODIGO_GRUPO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String NOMBRE;


    public clsBeP_MODIFICADOR_GRUPO() {
    }

    public clsBeP_MODIFICADOR_GRUPO(int CODIGO_GRUPO,int EMPRESA,String NOMBRE) {

        this.CODIGO_GRUPO=CODIGO_GRUPO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;

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
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}

