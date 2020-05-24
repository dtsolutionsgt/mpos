package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_PRODMENU {

    @Element(required=false) public int CODIGO_MENU;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String NOTA;

    public clsBeP_PRODMENU() {
    }

    public clsBeP_PRODMENU(int CODIGO_MENU,
                           int EMPRESA,
                           int CODIGO_PRODUCTO,
                           String NOMBRE,
                           String NOTA
    ) {

        this.CODIGO_MENU=CODIGO_MENU;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.NOMBRE=NOMBRE;
        this.NOTA=NOTA;

    }

    public int getCODIGO_MENU() {
        return CODIGO_MENU;
    }
    public void setCODIGO_MENU(int value) {
        CODIGO_MENU=value;
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
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getNOTA() {
        return NOTA;
    }
    public void setNOTA(String value) {
        NOTA=value;
    }
    
}