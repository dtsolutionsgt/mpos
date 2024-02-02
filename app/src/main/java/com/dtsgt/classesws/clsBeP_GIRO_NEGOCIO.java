package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_GIRO_NEGOCIO {

    @Element(required=false) public int CODIGO_GIRO_NEGOCIO;
    @Element(required=false) public String COD_PAIS;
    @Element(required=false) public int CODIGO;
    @Element(required=false) public String DESCRIPCION;


    public clsBeP_GIRO_NEGOCIO() {
    }

    public clsBeP_GIRO_NEGOCIO(int CODIGO_GIRO_NEGOCIO,String COD_PAIS,int CODIGO,String DESCRIPCION
    ) {

        this.CODIGO_GIRO_NEGOCIO=CODIGO_GIRO_NEGOCIO;
        this.COD_PAIS=COD_PAIS;
        this.CODIGO=CODIGO;
        this.DESCRIPCION=DESCRIPCION;

    }


    public int getCODIGO_GIRO_NEGOCIO() {
        return CODIGO_GIRO_NEGOCIO;
    }
    public void setCODIGO_GIRO_NEGOCIO(int value) {
        CODIGO_GIRO_NEGOCIO=value;
    }
    public String getCOD_PAIS() {
        return COD_PAIS;
    }
    public void setCOD_PAIS(String value) {
        COD_PAIS=value;
    }
    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
        CODIGO=value;
    }
    public String getDESCRIPCION() {
        return DESCRIPCION;
    }
    public void setDESCRIPCION(String value) {
        DESCRIPCION=value;
    }

}

