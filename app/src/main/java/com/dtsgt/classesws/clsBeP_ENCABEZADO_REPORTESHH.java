package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_ENCABEZADO_REPORTESHH {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String TEXTO;
    @Element(required=false) public String SUCURSAL;
    @Element(required=false) public String EMPRESA;


    public clsBeP_ENCABEZADO_REPORTESHH() {
    }

    public clsBeP_ENCABEZADO_REPORTESHH(int CODIGO,String TEXTO,String SUCURSAL,String EMPRESA
    ) {

        this.CODIGO=CODIGO;
        this.TEXTO=TEXTO;
        this.SUCURSAL=SUCURSAL;
        this.EMPRESA=EMPRESA;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public String getTEXTO() {
        return TEXTO;
    }
    public void setTEXTO(String value) {
        TEXTO=value;
    }
    public String getSUCURSAL() {
        return SUCURSAL;
    }
    public void setSUCURSAL(String value) {
        SUCURSAL=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

}

