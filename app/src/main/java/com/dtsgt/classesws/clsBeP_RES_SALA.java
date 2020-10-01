package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_RES_SALA {

    @Element(required=false) public int CODIGO_SALA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean ACTIVO;
    @Element(required=false) public double ESCALA;
    @Element(required=false) public double TAM_LETRA;


    public clsBeP_RES_SALA() {
    }

    public clsBeP_RES_SALA(int CODIGO_SALA,int EMPRESA,int CODIGO_SUCURSAL,String NOMBRE,
                           boolean ACTIVO,double ESCALA,double TAM_LETRA) {

        this.CODIGO_SALA=CODIGO_SALA;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;
        this.ESCALA=ESCALA;
        this.TAM_LETRA=TAM_LETRA;

    }


    public int getCODIGO_SALA() {
        return CODIGO_SALA;
    }
    public void setCODIGO_SALA(int value) {
        CODIGO_SALA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
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
    public double getESCALA() {
        return ESCALA;
    }
    public void setESCALA(double value) {
        ESCALA=value;
    }
    public double getTAM_LETRA() {
        return TAM_LETRA;
    }
    public void setTAM_LETRA(double value) {
        TAM_LETRA=value;
    }

}


