package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODOPCLIST {

    @Element(required=false) public int ID;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String PRODUCTO;
    @Element(required=false) public double CANT;
    @Element(required=false) public int IDRECETA;


    public clsBeP_PRODOPCLIST() {
    }

    public clsBeP_PRODOPCLIST(int ID,String EMPRESA,String PRODUCTO,double CANT,
                              int IDRECETA) {

        this.ID=ID;
        this.EMPRESA=EMPRESA;
        this.PRODUCTO=PRODUCTO;
        this.CANT=CANT;
        this.IDRECETA=IDRECETA;

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
    public String getPRODUCTO() {
        return PRODUCTO;
    }
    public void setPRODUCTO(String value) {
        PRODUCTO=value;
    }
    public double getCANT() {
        return CANT;
    }
    public void setCANT(double value) {
        CANT=value;
    }
    public int getIDRECETA() {
        return IDRECETA;
    }
    public void setIDRECETA(int value) {
        IDRECETA=value;
    }

}

