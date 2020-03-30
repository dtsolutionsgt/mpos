package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_NIVELPRECIO {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int ACTIVO;


    public clsBeP_NIVELPRECIO() {
    }

    public clsBeP_NIVELPRECIO(int CODIGO,String EMPRESA,String NOMBRE,int ACTIVO
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
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
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

}

