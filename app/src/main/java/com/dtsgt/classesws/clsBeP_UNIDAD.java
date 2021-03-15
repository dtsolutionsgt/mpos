package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_UNIDAD {

    @Element(required=false) public String CODIGO_UNIDAD;
    @Element(required=false) public String NOMBRE;


    public clsBeP_UNIDAD() {
    }

    public clsBeP_UNIDAD(String CODIGO_UNIDAD,String NOMBRE) {

        this.CODIGO_UNIDAD=CODIGO_UNIDAD;
        this.NOMBRE=NOMBRE;

    }


    public String getCODIGO_UNIDAD() {
        return CODIGO_UNIDAD;
    }
    public void setCODIGO_UNIDAD(String value) {
        CODIGO_UNIDAD=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}

