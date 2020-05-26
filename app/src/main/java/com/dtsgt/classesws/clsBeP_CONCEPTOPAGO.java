package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_CONCEPTOPAGO {

    @Element(required=false) public int CODIGO=0;
    @Element(required=false) public int EMPRESA=0;
    @Element(required=false) public String NOMBRE="";
    @Element(required=false) public boolean ACTIVO=false;


    public clsBeP_CONCEPTOPAGO() {
    }

    public clsBeP_CONCEPTOPAGO(int CODIGO,int EMPRESA,String NOMBRE,boolean ACTIVO
    ) {

        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
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


