package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_VENDEDOR_SUCURSAL {

    @Element(required=false) public int CODIGO_VENDEDOR_SUCURSAL;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int CODIGO_VENDEDOR;
    @Element(required=false) public int Fec_agr;
    @Element(required=false) public int User_agr;
    @Element(required=false) public int Fec_mod;
    @Element(required=false) public int User_mod;


    public clsBeP_VENDEDOR_SUCURSAL() {
    }

    public clsBeP_VENDEDOR_SUCURSAL(int CODIGO_VENDEDOR_SUCURSAL,int EMPRESA,int CODIGO_SUCURSAL,int CODIGO_VENDEDOR,
                                    int Fec_agr,int User_agr,int Fec_mod,int User_mod
    ) {

        this.CODIGO_VENDEDOR_SUCURSAL=CODIGO_VENDEDOR_SUCURSAL;
        this.EMPRESA=EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO_VENDEDOR=CODIGO_VENDEDOR;
        this.Fec_agr=Fec_agr;
        this.User_agr=User_agr;
        this.Fec_mod=Fec_mod;
        this.User_mod=User_mod;

    }


    public int getCODIGO_VENDEDOR_SUCURSAL() {
        return CODIGO_VENDEDOR_SUCURSAL;
    }
    public void setCODIGO_VENDEDOR_SUCURSAL(int value) {
        CODIGO_VENDEDOR_SUCURSAL=value;
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
    public int getCODIGO_VENDEDOR() {
        return CODIGO_VENDEDOR;
    }
    public void setCODIGO_VENDEDOR(int value) {
        CODIGO_VENDEDOR=value;
    }
    public int getFec_agr() {
        return Fec_agr;
    }
    public void setFec_agr(int value) {
        Fec_agr=value;
    }
    public int getUser_agr() {
        return User_agr;
    }
    public void setUser_agr(int value) {
        User_agr=value;
    }
    public int getFec_mod() {
        return Fec_mod;
    }
    public void setFec_mod(int value) {
        Fec_mod=value;
    }
    public int getUser_mod() {
        return User_mod;
    }
    public void setUser_mod(int value) {
        User_mod=value;
    }

}

