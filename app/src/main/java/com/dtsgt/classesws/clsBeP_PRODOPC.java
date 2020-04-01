package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODOPC {

    @Element(required=false) public int ID;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int ACTIVO;


    public clsBeP_PRODOPC() {
    }

    public clsBeP_PRODOPC(int ID,String EMPRESA,String NOMBRE,int ACTIVO
    ) {

        this.ID=ID;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getID() {
        return ID;
    }
    public void setID(int value) {
        ID=value;
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
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }

}

