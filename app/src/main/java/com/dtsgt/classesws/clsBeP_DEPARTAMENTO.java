package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_DEPARTAMENTO {

    @Element(required=false) public String CODIGO="";
    @Element(required=false) public String CODIGO_AREA="";
    @Element(required=false) public String NOMBRE="";


    public clsBeP_DEPARTAMENTO() {
    }

    public clsBeP_DEPARTAMENTO(String CODIGO,String CODIGO_AREA,String NOMBRE) {

        this.CODIGO=CODIGO;
        this.CODIGO_AREA=CODIGO_AREA;
        this.NOMBRE=NOMBRE;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getCODIGO_AREA() {
        return CODIGO_AREA;
    }
    public void setCODIGO_AREA(String value) {
        CODIGO_AREA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}

