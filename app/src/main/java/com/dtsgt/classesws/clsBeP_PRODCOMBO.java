package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODCOMBO {

    @Element(required=false) public String CODIGO;
    @Element(required=false) public String PRODUCTO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String TIPO;
    @Element(required=false) public double CANTMIN;
    @Element(required=false) public double CANTTOT;
    @Element(required=false) public int CODIGO_COMBO;

    public clsBeP_PRODCOMBO() {
    }

    public clsBeP_PRODCOMBO(String CODIGO,String PRODUCTO,String EMPRESA,String TIPO,
                            double CANTMIN,double CANTTOT, int CODIGO_COMBO) {

        this.CODIGO=CODIGO;
        this.PRODUCTO=PRODUCTO;
        this.EMPRESA=EMPRESA;
        this.TIPO=TIPO;
        this.CANTMIN=CANTMIN;
        this.CANTTOT=CANTTOT;
        this.CODIGO_COMBO=CODIGO_COMBO;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getPRODUCTO() {
        return PRODUCTO;
    }
    public void setPRODUCTO(String value) {
        PRODUCTO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public String getTIPO() {
        return TIPO;
    }
    public void setTIPO(String value) {
        TIPO=value;
    }
    public double getCANTMIN() {
        return CANTMIN;
    }
    public void setCANTMIN(double value) {
        CANTMIN=value;
    }
    public double getCANTTOT() {
        return CANTTOT;
    }
    public void setCANTTOT(double value) {
        CANTTOT=value;
    }
    public int getCODIGO_COMBO() {
        return CODIGO_COMBO;
    }
    public void setCODIGO_COMBO(int value) {
        CODIGO_COMBO=value;
    }

}

