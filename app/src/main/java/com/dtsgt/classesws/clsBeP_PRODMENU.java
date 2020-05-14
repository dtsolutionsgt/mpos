package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_PRODMENU {

    @Element(required=false) public int CODIGO_MENU;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public int OPCION_LISTA;
    @Element(required=false) public int OPCION_PRODUCTO;
    @Element(required=false) public int ORDEN;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String NOTA;


    public clsBeP_PRODMENU() {
    }

    public clsBeP_PRODMENU(int CODIGO_MENU,int EMPRESA,int CODIGO_PRODUCTO,int OPCION_LISTA,
                           int OPCION_PRODUCTO,int ORDEN,String NOMBRE,String NOTA
    ) {

        this.CODIGO_MENU=CODIGO_MENU;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.OPCION_LISTA=OPCION_LISTA;
        this.OPCION_PRODUCTO=OPCION_PRODUCTO;
        this.ORDEN=ORDEN;
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
    public int getOPCION_LISTA() {
        return OPCION_LISTA;
    }
    public void setOPCION_LISTA(int value) {
        OPCION_LISTA=value;
    }
    public int getOPCION_PRODUCTO() {
        return OPCION_PRODUCTO;
    }
    public void setOPCION_PRODUCTO(int value) {
        OPCION_PRODUCTO=value;
    }
    public int getORDEN() {
        return ORDEN;
    }
    public void setORDEN(int value) {
        ORDEN=value;
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

