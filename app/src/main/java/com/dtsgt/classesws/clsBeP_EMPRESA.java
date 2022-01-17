package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_EMPRESA {

    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int COL_IMP;
    @Element(required=false) public String LOGO;
    @Element(required=false) public String RAZON_SOCIAL;
    @Element(required=false) public String IDENTIFICACION_TRIBUTARIA;
    @Element(required=false) public String TELEFONO;
    @Element(required=false) public String COD_PAIS;
    @Element(required=false) public String NOMBRE_CONTACTO;
    @Element(required=false) public String APELLIDO_CONTACTO;
    @Element(required=false) public String DIRECCION;
    @Element(required=false) public String CORREO;
    @Element(required=false) public String CODIGO_ACTIVACION;
    @Element(required=false) public int COD_CANT_EMP;
    @Element(required=false) public int CANTIDAD_PUNTOS_VENTA;
    @Element(required=false) public String CLAVE;
    @Element(required=false) public boolean ACTIVO;

    public clsBeP_EMPRESA() {
    }

    public clsBeP_EMPRESA(int EMPRESA,String NOMBRE,int COL_IMP,String LOGO,
                          String RAZON_SOCIAL,String IDENTIFICACION_TRIBUTARIA,String TELEFONO,String COD_PAIS,
                          String NOMBRE_CONTACTO,String APELLIDO_CONTACTO,String DIRECCION,String CORREO,
                          String CODIGO_ACTIVACION,int COD_CANT_EMP,int CANTIDAD_PUNTOS_VENTA, String CLAVE) {

        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.COL_IMP=COL_IMP;
        this.LOGO=LOGO;
        this.RAZON_SOCIAL=RAZON_SOCIAL;
        this.IDENTIFICACION_TRIBUTARIA=IDENTIFICACION_TRIBUTARIA;
        this.TELEFONO=TELEFONO;
        this.COD_PAIS=COD_PAIS;
        this.NOMBRE_CONTACTO=NOMBRE_CONTACTO;
        this.APELLIDO_CONTACTO=APELLIDO_CONTACTO;
        this.DIRECCION=DIRECCION;
        this.CORREO=CORREO;
        this.CODIGO_ACTIVACION=CODIGO_ACTIVACION;
        this.COD_CANT_EMP=COD_CANT_EMP;
        this.CANTIDAD_PUNTOS_VENTA=CANTIDAD_PUNTOS_VENTA;
        this.CLAVE=CLAVE;

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
    public int getCOL_IMP() {
        return COL_IMP;
    }
    public void setCOL_IMP(int value) {
        COL_IMP=value;
    }
    public String getLOGO() {
        return LOGO;
    }
    public void setLOGO(String value) {
        LOGO=value;
    }
    public String getRAZON_SOCIAL() {
        return RAZON_SOCIAL;
    }
    public void setRAZON_SOCIAL(String value) {
        RAZON_SOCIAL=value;
    }
    public String getIDENTIFICACION_TRIBUTARIA() {
        return IDENTIFICACION_TRIBUTARIA;
    }
    public void setIDENTIFICACION_TRIBUTARIA(String value) {
        IDENTIFICACION_TRIBUTARIA=value;
    }
    public String getTELEFONO() {
        return TELEFONO;
    }
    public void setTELEFONO(String value) {
        TELEFONO=value;
    }
    public String getCOD_PAIS() {
        return COD_PAIS;
    }
    public void setCOD_PAIS(String value) {
        COD_PAIS=value;
    }
    public String getNOMBRE_CONTACTO() {
        return NOMBRE_CONTACTO;
    }
    public void setNOMBRE_CONTACTO(String value) {
        NOMBRE_CONTACTO=value;
    }
    public String getAPELLIDO_CONTACTO() {
        return APELLIDO_CONTACTO;
    }
    public void setAPELLIDO_CONTACTO(String value) {
        APELLIDO_CONTACTO=value;
    }
    public String getDIRECCION() {
        return DIRECCION;
    }
    public void setDIRECCION(String value) {
        DIRECCION=value;
    }
    public String getCORREO() {
        return CORREO;
    }
    public void setCORREO(String value) {
        CORREO=value;
    }
    public String getCODIGO_ACTIVACION() {
        return CODIGO_ACTIVACION;
    }
    public void setCODIGO_ACTIVACION(String value) {
        CODIGO_ACTIVACION=value;
    }
    public int getCOD_CANT_EMP() {
        return COD_CANT_EMP;
    }
    public void setCOD_CANT_EMP(int value) {
        COD_CANT_EMP=value;
    }
    public int getCANTIDAD_PUNTOS_VENTA() {
        return CANTIDAD_PUNTOS_VENTA;
    }
    public void setCANTIDAD_PUNTOS_VENTA(int value) {
        CANTIDAD_PUNTOS_VENTA=value;
    }
    public String getCLAVE() {
        return CLAVE;
    }
    public void setCLAVE(String value) {
        CLAVE=value;
    }

    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

