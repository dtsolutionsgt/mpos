package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODCLASIFMODIF {

    @Element(required=false) public int CODIGO_CLASIFICACION;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_GRUPO;


    public clsBeP_PRODCLASIFMODIF() {
    }

    public clsBeP_PRODCLASIFMODIF(int CODIGO_CLASIFICACION,int EMPRESA,int CODIGO_GRUPO) {

        this.CODIGO_CLASIFICACION=CODIGO_CLASIFICACION;
        this.EMPRESA=EMPRESA;
        this.CODIGO_GRUPO=CODIGO_GRUPO;

    }


    public int getCODIGO_CLASIFICACION() {
        return CODIGO_CLASIFICACION;
    }
    public void setCODIGO_CLASIFICACION(int value) {
        CODIGO_CLASIFICACION=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_GRUPO() {
        return CODIGO_GRUPO;
    }
    public void setCODIGO_GRUPO(int value) {
        CODIGO_GRUPO=value;
    }

}


