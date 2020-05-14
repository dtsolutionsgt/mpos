package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODMENUOPC {

    @Element(required=false) public int CODIGO_MENU_OPCION;
    @Element(required=false) public int CODIGO_OPCION;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public int CODIGO_RECETA;


    public clsBeP_PRODMENUOPC() {
    }

    public clsBeP_PRODMENUOPC(int CODIGO_MENU_OPCION,int CODIGO_OPCION,int EMPRESA,int CODIGO_PRODUCTO,
                              int CODIGO_RECETA) {

        this.CODIGO_MENU_OPCION=CODIGO_MENU_OPCION;
        this.CODIGO_OPCION=CODIGO_OPCION;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.CODIGO_RECETA=CODIGO_RECETA;

    }


    public int getCODIGO_MENU_OPCION() {
        return CODIGO_MENU_OPCION;
    }
    public void setCODIGO_MENU_OPCION(int value) {
        CODIGO_MENU_OPCION=value;
    }
    public int getCODIGO_OPCION() {
        return CODIGO_OPCION;
    }
    public void setCODIGO_OPCION(int value) {
        CODIGO_OPCION=value;
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
    public int getCODIGO_RECETA() {
        return CODIGO_RECETA;
    }
    public void setCODIGO_RECETA(int value) {
        CODIGO_RECETA=value;
    }

}

