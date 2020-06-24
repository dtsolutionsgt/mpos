package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_MUNICIPIO {

    @Element(required=false) public String CODIGO="";
    @Element(required=false) public String CODIGO_DEPARTAMENTO="";
    @Element(required=false) public String NOMBRE="";


    public clsBeP_MUNICIPIO() {
    }

    public clsBeP_MUNICIPIO(String CODIGO,String CODIGO_DEPARTAMENTO,String NOMBRE) {

        this.CODIGO=CODIGO;
        this.CODIGO_DEPARTAMENTO=CODIGO_DEPARTAMENTO;
        this.NOMBRE=NOMBRE;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getCODIGO_DEPARTAMENTO() {
        return CODIGO_DEPARTAMENTO;
    }
    public void setCODIGO_DEPARTAMENTO(String value) {
        CODIGO_DEPARTAMENTO=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }

}


