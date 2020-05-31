package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeVENDEDORES {

    @Element(required=false) public int CODIGO_VENDEDOR=0;
    @Element(required=false) public String CODIGO="";
    @Element(required=false) public int EMPRESA=0;
    @Element(required=false) public int RUTA=0;
    @Element(required=false) public String NOMBRE="";
    @Element(required=false) public String CLAVE="";
    @Element(required=false) public int NIVEL=0;
    @Element(required=false) public double NIVELPRECIO=0;
    @Element(required=false) public String BODEGA="";
    @Element(required=false) public String SUBBODEGA="";
    @Element(required=false) public String IMAGEN="";
    @Element(required=false) public boolean ACTIVO=false;
    @Element(required=false) public long FECHA_INICIO_LABORES=0;
    @Element(required=false) public long FECHA_FIN_LABORES=0;


    public clsBeVENDEDORES() {
    }

    public clsBeVENDEDORES(int CODIGO_VENDEDOR,String CODIGO,int EMPRESA,int RUTA,
                           String NOMBRE,String CLAVE,int NIVEL,double NIVELPRECIO,
                           String BODEGA,String SUBBODEGA,String IMAGEN,boolean ACTIVO,
                           long FECHA_INICIO_LABORES,long FECHA_FIN_LABORES) {

        this.CODIGO_VENDEDOR=CODIGO_VENDEDOR;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.RUTA=RUTA;
        this.NOMBRE=NOMBRE;
        this.CLAVE=CLAVE;
        this.NIVEL=NIVEL;
        this.NIVELPRECIO=NIVELPRECIO;
        this.BODEGA=BODEGA;
        this.SUBBODEGA=SUBBODEGA;
        this.IMAGEN=IMAGEN;
        this.ACTIVO=ACTIVO;
        this.FECHA_INICIO_LABORES=FECHA_INICIO_LABORES;
        this.FECHA_FIN_LABORES=FECHA_FIN_LABORES;

    }


    public int getCODIGO_VENDEDOR() {
        return CODIGO_VENDEDOR;
    }
    public void setCODIGO_VENDEDOR(int value) {
        CODIGO_VENDEDOR=value;
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
    public int getRUTA() {
        return RUTA;
    }
    public void setRUTA(int value) {
        RUTA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getCLAVE() {
        return CLAVE;
    }
    public void setCLAVE(String value) {
        CLAVE=value;
    }
    public int getNIVEL() {
        return NIVEL;
    }
    public void setNIVEL(int value) {
        NIVEL=value;
    }
    public double getNIVELPRECIO() {
        return NIVELPRECIO;
    }
    public void setNIVELPRECIO(double value) {
        NIVELPRECIO=value;
    }
    public String getBODEGA() {
        return BODEGA;
    }
    public void setBODEGA(String value) {
        BODEGA=value;
    }
    public String getSUBBODEGA() {
        return SUBBODEGA;
    }
    public void setSUBBODEGA(String value) {
        SUBBODEGA=value;
    }
    public String getIMAGEN() {
        return IMAGEN;
    }
    public void setIMAGEN(String value) {
        IMAGEN=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }
    public long getFECHA_INICIO_LABORES() {
        return FECHA_INICIO_LABORES;
    }
    public void setFECHA_INICIO_LABORES(long value) {
        FECHA_INICIO_LABORES=value;
    }
    public long getFECHA_FIN_LABORES() {
        return FECHA_FIN_LABORES;
    }
    public void setFECHA_FIN_LABORES(long value) {
        FECHA_FIN_LABORES=value;
    }

}

