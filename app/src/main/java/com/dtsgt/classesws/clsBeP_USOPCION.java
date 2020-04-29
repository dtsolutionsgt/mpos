package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_USOPCION {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String MENUGROUP;
    @Element(required=false) public String NOMBRE;


    public clsBeP_USOPCION() {
    }

    public clsBeP_USOPCION(int CODIGO,String MENUGROUP,String NOMBRE) {

        this.CODIGO=CODIGO;
        this.MENUGROUP=MENUGROUP;
        this.NOMBRE=NOMBRE;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public String getMENUGROUP() {
        return MENUGROUP;
    }
    public void setMENUGROUP(String value) {
        MENUGROUP=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}

