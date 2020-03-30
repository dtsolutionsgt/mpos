package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_MEDIAPAGO {

    @Element(required=false) public int CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean ACTIVO;
    @Element(required=false) public int NIVEL;
    @Element(required=false) public String PORCOBRO;


    public clsBeP_MEDIAPAGO() {
    }

    public clsBeP_MEDIAPAGO(int CODIGO,String EMPRESA,String NOMBRE,boolean ACTIVO,
                            int NIVEL,String PORCOBRO) {

        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;
        this.NIVEL=NIVEL;
        this.PORCOBRO=PORCOBRO;

    }


    public int getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(int value) {
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
    public int getNIVEL() {
        return NIVEL;
    }
    public void setNIVEL(int value) {
        NIVEL=value;
    }
    public String getPORCOBRO() {
        return PORCOBRO;
    }
    public void setPORCOBRO(String value) {
        PORCOBRO=value;
    }

}

