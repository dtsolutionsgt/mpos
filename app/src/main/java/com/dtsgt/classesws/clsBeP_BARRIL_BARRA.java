package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_BARRIL_BARRA {

    @Element(required=false) public int CODIGO_BARRA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String BARRA;
    @Element(required=false) public int CODIGO_TIPO;
    @Element(required=false) public String CODIGO_INTERNO;


    public clsBeP_BARRIL_BARRA() {
    }

    public clsBeP_BARRIL_BARRA(int CODIGO_BARRA,int EMPRESA,String BARRA,int CODIGO_TIPO,
                               String CODIGO_INTERNO) {

        this.CODIGO_BARRA=CODIGO_BARRA;
        this.EMPRESA=EMPRESA;
        this.BARRA=BARRA;
        this.CODIGO_TIPO=CODIGO_TIPO;
        this.CODIGO_INTERNO=CODIGO_INTERNO;

    }


    public int getCODIGO_BARRA() {
        return CODIGO_BARRA;
    }
    public void setCODIGO_BARRA(int value) {
        CODIGO_BARRA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public String getBARRA() {
        return BARRA;
    }
    public void setBARRA(String value) {
        BARRA=value;
    }
    public int getCODIGO_TIPO() {
        return CODIGO_TIPO;
    }
    public void setCODIGO_TIPO(int value) {
        CODIGO_TIPO=value;
    }
    public String getCODIGO_INTERNO() {
        return CODIGO_INTERNO;
    }
    public void setCODIGO_INTERNO(String value) {
        CODIGO_INTERNO=value;
    }

}

