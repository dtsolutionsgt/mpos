package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_REGLA_COSTO {

    @Element(required=false) public int CODIGO_EMPRESA;
    @Element(required=false) public int CODIGO_TIPO;
    @Element(required=false) public int DIAS;


    public clsBeP_REGLA_COSTO() {
    }

    public clsBeP_REGLA_COSTO(int CODIGO_EMPRESA,int CODIGO_TIPO,int DIAS) {

        this.CODIGO_EMPRESA=CODIGO_EMPRESA;
        this.CODIGO_TIPO=CODIGO_TIPO;
        this.DIAS=DIAS;

    }


    public int getCODIGO_EMPRESA() {
        return CODIGO_EMPRESA;
    }
    public void setCODIGO_EMPRESA(int value) {
        CODIGO_EMPRESA=value;
    }
    public int getCODIGO_TIPO() {
        return CODIGO_TIPO;
    }
    public void setCODIGO_TIPO(int value) {
        CODIGO_TIPO=value;
    }
    public int getDIAS() {
        return DIAS;
    }
    public void setDIAS(int value) {
        DIAS=value;
    }

}

