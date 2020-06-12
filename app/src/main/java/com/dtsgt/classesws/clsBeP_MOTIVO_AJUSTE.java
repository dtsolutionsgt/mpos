package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_MOTIVO_AJUSTE{

    @Element(required=false) public int CODIGO_MOTIVO_AJUSTE=0;
    @Element(required=false) public int EMPRESA=0;
    @Element(required=false) public String NOMBRE="";
    @Element(required=false) public boolean ACTIVO=false;


    public clsBeP_MOTIVO_AJUSTE() {
    }

    public clsBeP_MOTIVO_AJUSTE(int CODIGO_MOTIVO_AJUSTE,int EMPRESA,String NOMBRE,boolean ACTIVO
    ) {

        this.CODIGO_MOTIVO_AJUSTE=CODIGO_MOTIVO_AJUSTE;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_MOTIVO_AJUSTE() {
        return CODIGO_MOTIVO_AJUSTE;
    }
    public void setCODIGO_MOTIVO_AJUSTE(int value) {
        CODIGO_MOTIVO_AJUSTE=value;
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
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

