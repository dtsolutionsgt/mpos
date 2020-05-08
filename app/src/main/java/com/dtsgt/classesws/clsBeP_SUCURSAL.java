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
    @Element(required=false) public String PET_PREFIJO;
    @Element(required=false) public String PET_LLAVE;
    @Element(required=false) public String PET_ALIAS_PFX;
    @Element(required=false) public String PET_PFX_LLAVE;
    @Element(required=false) public int CODIGO_ESCENARIO_ISR;
    @Element(required=false) public int CODIGO_ESCENARIO_IVA;
    @Element(required=false) public String CODIGO_MUNICIPIO;


    public clsBeP_SUCURSAL() {
    }

    public clsBeP_SUCURSAL(int CODIGO_SUCURSAL,String CODIGO,int EMPRESA,int CODIGO_NIVEL_PRECIO,
                           String DESCRIPCION,String NOMBRE,String DIRECCION,String TELEFONO,
                           String NIT,String TEXTO,boolean ACTIVO,String PET_PREFIJO,
                           String PET_LLAVE,String PET_ALIAS_PFX,String PET_PFX_LLAVE,int CODIGO_ESCENARIO_ISR,
                           int CODIGO_ESCENARIO_IVA,String CODIGO_MUNICIPIO) {

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
        this.PET_PREFIJO=PET_PREFIJO;
        this.PET_LLAVE=PET_LLAVE;
        this.PET_ALIAS_PFX=PET_ALIAS_PFX;
        this.PET_PFX_LLAVE=PET_PFX_LLAVE;
        this.CODIGO_ESCENARIO_ISR=CODIGO_ESCENARIO_ISR;
        this.CODIGO_ESCENARIO_IVA=CODIGO_ESCENARIO_IVA;
        this.CODIGO_MUNICIPIO=CODIGO_MUNICIPIO;

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
    public String getPET_PREFIJO() {
        return PET_PREFIJO;
    }
    public void setPET_PREFIJO(String value) {
        PET_PREFIJO=value;
    }
    public String getPET_LLAVE() {
        return PET_LLAVE;
    }
    public void setPET_LLAVE(String value) {
        PET_LLAVE=value;
    }
    public String getPET_ALIAS_PFX() {
        return PET_ALIAS_PFX;
    }
    public void setPET_ALIAS_PFX(String value) {
        PET_ALIAS_PFX=value;
    }
    public String getPET_PFX_LLAVE() {
        return PET_PFX_LLAVE;
    }
    public void setPET_PFX_LLAVE(String value) {
        PET_PFX_LLAVE=value;
    }
    public int getCODIGO_ESCENARIO_ISR() {
        return CODIGO_ESCENARIO_ISR;
    }
    public void setCODIGO_ESCENARIO_ISR(int value) {
        CODIGO_ESCENARIO_ISR=value;
    }
    public int getCODIGO_ESCENARIO_IVA() {
        return CODIGO_ESCENARIO_IVA;
    }
    public void setCODIGO_ESCENARIO_IVA(int value) {
        CODIGO_ESCENARIO_IVA=value;
    }
    public String getCODIGO_MUNICIPIO() {
        return CODIGO_MUNICIPIO;
    }
    public void setCODIGO_MUNICIPIO(String value) {
        CODIGO_MUNICIPIO=value;
    }

}

