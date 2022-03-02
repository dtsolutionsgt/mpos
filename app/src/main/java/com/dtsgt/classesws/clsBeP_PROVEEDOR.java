package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_PROVEEDOR {

    @Element(required=false) public int CODIGO_PROVEEDOR;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String CORREO;
    @Element(required=false) public Boolean ACTIVO;


    public clsBeP_PROVEEDOR() {
    }

    public clsBeP_PROVEEDOR(int CODIGO_PROVEEDOR,String CODIGO,String EMPRESA,String NOMBRE,
                            Boolean ACTIVO, String Correo) {

        this.CODIGO_PROVEEDOR=CODIGO_PROVEEDOR;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;
        this.CORREO=CORREO;

    }


    public int getCODIGO_PROVEEDOR() {
        return CODIGO_PROVEEDOR;
    }
    public void setCODIGO_PROVEEDOR(int value) {
        CODIGO_PROVEEDOR=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

