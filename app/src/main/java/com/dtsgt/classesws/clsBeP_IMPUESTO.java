package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_IMPUESTO {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public double VALOR;
    @Element(required=false) public int Activo;
    @Element(required=false) public String EMPRESA;


    public clsBeP_IMPUESTO() {
    }

    public clsBeP_IMPUESTO(int CODIGO,double VALOR,int Activo,String EMPRESA
    ) {

        this.CODIGO=CODIGO;
        this.VALOR=VALOR;
        this.Activo=Activo;
        this.EMPRESA=EMPRESA;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public double getVALOR() {
        return VALOR;
    }
    public void setVALOR(double value) {
        VALOR=value;
    }
    public int getActivo() {
        return Activo;
    }
    public void setActivo(int value) {
        Activo=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

}

