package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_TIPONEG {

    @Element(required=false) public int CODIGO_TIPO_NEGOCIO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String DESCRIPCION;
    @Element(required=false) public boolean ACTIVO;
    @Element(required=false) public String Fec_agr;
    @Element(required=false) public int User_agr;
    @Element(required=false) public String Fec_mod;
    @Element(required=false) public int User_mod;


    public clsBeP_TIPONEG() {
    }

    public clsBeP_TIPONEG(int CODIGO_TIPO_NEGOCIO,int EMPRESA,String DESCRIPCION,boolean ACTIVO,
                          String Fec_agr,int User_agr,String Fec_mod,int User_mod
    ) {

        this.CODIGO_TIPO_NEGOCIO=CODIGO_TIPO_NEGOCIO;
        this.EMPRESA=EMPRESA;
        this.DESCRIPCION=DESCRIPCION;
        this.ACTIVO=ACTIVO;
        this.Fec_agr=Fec_agr;
        this.User_agr=User_agr;
        this.Fec_mod=Fec_mod;
        this.User_mod=User_mod;

    }


    public int getCODIGO_TIPO_NEGOCIO() {
        return CODIGO_TIPO_NEGOCIO;
    }
    public void setCODIGO_TIPO_NEGOCIO(int value) {
        CODIGO_TIPO_NEGOCIO=value;
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
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }
    public String getFec_agr() {
        return Fec_agr;
    }
    public void setFec_agr(String value) {
        Fec_agr=value;
    }
    public int getUser_agr() {
        return User_agr;
    }
    public void setUser_agr(int value) {
        User_agr=value;
    }
    public String getFec_mod() {
        return Fec_mod;
    }
    public void setFec_mod(String value) {
        Fec_mod=value;
    }
    public int getUser_mod() {
        return User_mod;
    }
    public void setUser_mod(int value) {
        User_mod=value;
    }

}

