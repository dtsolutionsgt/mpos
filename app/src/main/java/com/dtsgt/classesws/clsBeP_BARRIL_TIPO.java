package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_BARRIL_TIPO {

    @Element(required=false) public int CODIGO_TIPO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String DESCRIPCION;
    @Element(required=false) public double CAPACIDAD;
    @Element(required=false) public double MERMAMIN;
    @Element(required=false) public double MERMAMAX;
    @Element(required=false) public int ACTIVO;


    public clsBeP_BARRIL_TIPO() {
    }

    public clsBeP_BARRIL_TIPO(int CODIGO_TIPO,int EMPRESA,String DESCRIPCION,double CAPACIDAD,
                              double MERMAMIN,double MERMAMAX,int ACTIVO) {

        this.CODIGO_TIPO=CODIGO_TIPO;
        this.EMPRESA=EMPRESA;
        this.DESCRIPCION=DESCRIPCION;
        this.CAPACIDAD=CAPACIDAD;
        this.MERMAMIN=MERMAMIN;
        this.MERMAMAX=MERMAMAX;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_TIPO() {
        return CODIGO_TIPO;
    }
    public void setCODIGO_TIPO(int value) {
        CODIGO_TIPO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public String getDESCRIPCION() {
        return DESCRIPCION;
    }
    public void setDESCRIPCION(String value) {
        DESCRIPCION=value;
    }
    public double getCAPACIDAD() {
        return CAPACIDAD;
    }
    public void setCAPACIDAD(double value) {
        CAPACIDAD=value;
    }
    public double getMERMAMIN() {
        return MERMAMIN;
    }
    public void setMERMAMIN(double value) {
        MERMAMIN=value;
    }
    public double getMERMAMAX() {
        return MERMAMAX;
    }
    public void setMERMAMAX(double value) {
        MERMAMAX=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }

}

