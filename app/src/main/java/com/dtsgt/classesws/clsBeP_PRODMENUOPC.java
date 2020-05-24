package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODMENUOPC {

    @Element(required=false) public int CODIGO_MENU_OPCION;
    @Element(required=false) public int CODIGO_MENU;
    @Element(required=false) public int CANT;
    @Element(required=false) public int ORDEN;
    @Element(required=false) public String NOMBRE;


    public clsBeP_PRODMENUOPC() {
    }

    public clsBeP_PRODMENUOPC(int CODIGO_MENU_OPCION, int CODIGO_MENU, int CANT, int ORDEN,
                              String NOMBRE) {

        this.CODIGO_MENU_OPCION=CODIGO_MENU_OPCION;
        this.CODIGO_MENU = CODIGO_MENU;
        this.CANT = CANT;
        this.ORDEN = ORDEN;
        this.NOMBRE = NOMBRE;

    }

    public int getCODIGO_MENU_OPCION() {
        return CODIGO_MENU_OPCION;
    }
    public void setCODIGO_MENU_OPCION(int value) {
        CODIGO_MENU_OPCION=value;
    }
    public int getCODIGO_MENU() {
        return CODIGO_MENU;
    }
    public void setCODIGO_MENU(int value) {
        CODIGO_MENU =value;
    }
    public int getCANT() {
        return CANT;
    }
    public void setCANT(int value) {
        CANT =value;
    }
    public int getORDEN() {
        return ORDEN;
    }
    public void setORDEN(int value) {
        ORDEN =value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE =value;
    }

}