package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_MODIFICADOR {

    @Element(required=false) public int CODIGO_MODIF;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_GRUPO;
    @Element(required=false) public String NOMBRE;


    public clsBeP_MODIFICADOR() {
    }

    public clsBeP_MODIFICADOR(int CODIGO_MODIF,int EMPRESA,int CODIGO_GRUPO,String NOMBRE
    ) {

        this.CODIGO_MODIF=CODIGO_MODIF;
        this.EMPRESA=EMPRESA;
        this.CODIGO_GRUPO=CODIGO_GRUPO;
        this.NOMBRE=NOMBRE;

    }


    public int getCODIGO_MODIF() {
        return CODIGO_MODIF;
    }
    public void setCODIGO_MODIF(int value) {
        CODIGO_MODIF=value;
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
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}


