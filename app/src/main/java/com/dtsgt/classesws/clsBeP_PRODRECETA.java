package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODRECETA {

    @Element(required=false) public int CODIGO_RECETA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public int CODIGO_ARTICULO;
    @Element(required=false) public double CANT;
    @Element(required=false) public String UM;


    public clsBeP_PRODRECETA() {
    }

    public clsBeP_PRODRECETA(int CODIGO_RECETA,int EMPRESA,int CODIGO_PRODUCTO,int CODIGO_ARTICULO,
                             double CANT,String UM) {

        this.CODIGO_RECETA=CODIGO_RECETA;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.CODIGO_ARTICULO=CODIGO_ARTICULO;
        this.CANT=CANT;
        this.UM=UM;

    }


    public int getCODIGO_RECETA() {
        return CODIGO_RECETA;
    }
    public void setCODIGO_RECETA(int value) {
        CODIGO_RECETA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_PRODUCTO() {
        return CODIGO_PRODUCTO;
    }
    public void setCODIGO_PRODUCTO(int value) {
        CODIGO_PRODUCTO=value;
    }
    public int getCODIGO_ARTICULO() {
        return CODIGO_ARTICULO;
    }
    public void setCODIGO_ARTICULO(int value) {
        CODIGO_ARTICULO=value;
    }
    public double getCANT() {
        return CANT;
    }
    public void setCANT(double value) {
        CANT=value;
    }
    public String getUM() {
        return UM;
    }
    public void setUM(String value) {
        UM=value;
    }

}

