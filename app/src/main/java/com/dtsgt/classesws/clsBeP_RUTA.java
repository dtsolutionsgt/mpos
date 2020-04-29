package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_RUTA {

    @Element(required=false) public int CODIGO_RUTA;
    @Element(required=false) public int SUCURSAL;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean ACTIVO;


    public clsBeP_RUTA() {
    }

    public clsBeP_RUTA(int CODIGO_RUTA,int SUCURSAL,int EMPRESA,String CODIGO,
                       String NOMBRE,boolean ACTIVO) {

        this.CODIGO_RUTA=CODIGO_RUTA;
        this.SUCURSAL=SUCURSAL;
        this.EMPRESA=EMPRESA;
        this.CODIGO=CODIGO;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_RUTA() {
        return CODIGO_RUTA;
    }
    public void setCODIGO_RUTA(int value) {
        CODIGO_RUTA=value;
    }
    public int getSUCURSAL() {
        return SUCURSAL;
    }
    public void setSUCURSAL(int value) {
        SUCURSAL=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
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

