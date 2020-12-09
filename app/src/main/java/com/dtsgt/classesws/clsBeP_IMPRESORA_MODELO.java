package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_IMPRESORA_MODELO {

    @Element(required=false) public int CODIGO_IMPRESORA_MODELO;
    @Element(required=false) public int CODIGO_IMPRESORA_MARCA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean ACTIVO;


    public clsBeP_IMPRESORA_MODELO() {
    }

    public clsBeP_IMPRESORA_MODELO(int CODIGO_IMPRESORA_MODELO,int CODIGO_IMPRESORA_MARCA,String NOMBRE,boolean ACTIVO
    ) {

        this.CODIGO_IMPRESORA_MODELO=CODIGO_IMPRESORA_MODELO;
        this.CODIGO_IMPRESORA_MARCA=CODIGO_IMPRESORA_MARCA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_IMPRESORA_MODELO() {
        return CODIGO_IMPRESORA_MODELO;
    }
    public void setCODIGO_IMPRESORA_MODELO(int value) {
        CODIGO_IMPRESORA_MODELO=value;
    }
    public int getCODIGO_IMPRESORA_MARCA() {
        return CODIGO_IMPRESORA_MARCA;
    }
    public void setCODIGO_IMPRESORA_MARCA(int value) {
        CODIGO_IMPRESORA_MARCA=value;
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

