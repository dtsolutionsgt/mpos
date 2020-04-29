package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_SUCURSAL {

    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_NIVEL_PRECIO;
    @Element(required=false) public String DESCRIPCION;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String DIRECCION;
    @Element(required=false) public String TELEFONO;
    @Element(required=false) public String NIT;
    @Element(required=false) public String TEXTO;
    @Element(required=false) public boolean ACTIVO;


    public clsBeP_SUCURSAL() {
    }

    public clsBeP_SUCURSAL(int CODIGO_SUCURSAL,String CODIGO,int EMPRESA,int CODIGO_NIVEL_PRECIO,
                           String DESCRIPCION,String NOMBRE,String DIRECCION,String TELEFONO,
                           String NIT,String TEXTO,boolean ACTIVO) {

        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_NIVEL_PRECIO=CODIGO_NIVEL_PRECIO;
        this.DESCRIPCION=DESCRIPCION;
        this.NOMBRE=NOMBRE;
        this.DIRECCION=DIRECCION;
        this.TELEFONO=TELEFONO;
        this.NIT=NIT;
        this.TEXTO=TEXTO;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_NIVEL_PRECIO() {
        return CODIGO_NIVEL_PRECIO;
    }
    public void setCODIGO_NIVEL_PRECIO(int value) {
        CODIGO_NIVEL_PRECIO=value;
    }
    public String getDESCRIPCION() {
        return DESCRIPCION;
    }
    public void setDESCRIPCION(String value) {
        DESCRIPCION=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getDIRECCION() {
        return DIRECCION;
    }
    public void setDIRECCION(String value) {
        DIRECCION=value;
    }
    public String getTELEFONO() {
        return TELEFONO;
    }
    public void setTELEFONO(String value) {
        TELEFONO=value;
    }
    public String getNIT() {
        return NIT;
    }
    public void setNIT(String value) {
        NIT=value;
    }
    public String getTEXTO() {
        return TEXTO;
    }
    public void setTEXTO(String value) {
        TEXTO=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

