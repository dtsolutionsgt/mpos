package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_USGRUPO {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String CUENTA;


    public clsBeP_USGRUPO() {
    }

    public clsBeP_USGRUPO(int CODIGO,String NOMBRE,String CUENTA) {

        this.CODIGO=CODIGO;
        this.NOMBRE=NOMBRE;
        this.CUENTA=CUENTA;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getCUENTA() {
        return CUENTA;
    }
    public void setCUENTA(String value) {
        CUENTA=value;
    }

}

