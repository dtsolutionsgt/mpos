package com.dtsgt.classesws;
import org.simpleframework.xml.Element;

public class clsBeP_PRODMENUOPC_DET {

    @Element(required=false) public int CODIGO_MENUOPC_DET=0;
    @Element(required=false) public int CODIGO_MENU_OPCION=0;
    @Element(required=false) public int CODIGO_PRODUCTO=0;

    public clsBeP_PRODMENUOPC_DET() {
    }

    public clsBeP_PRODMENUOPC_DET(int CODIGO_MENUOPC_DET,int CODIGO_MENU_OPCION,int CODIGO_PRODUCTO) {

        this.CODIGO_MENUOPC_DET=CODIGO_MENUOPC_DET;
        this.CODIGO_MENU_OPCION=CODIGO_MENU_OPCION;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;

    }

    public int getCODIGO_MENUOPC_DET() {
        return CODIGO_MENUOPC_DET;
    }
    public void setCODIGO_MENUOPC_DET(int value) {
        CODIGO_MENUOPC_DET=value;
    }
    public int getCODIGO_MENU_OPCION() {
        return CODIGO_MENU_OPCION;
    }
    public void setCODIGO_MENU_OPCION(int value) {
        CODIGO_MENU_OPCION=value;
    }
    public int getCODIGO_PRODUCTO() {
        return CODIGO_PRODUCTO;
    }
    public void setCODIGO_PRODUCTO(int value) {
        CODIGO_PRODUCTO=value;
    }

}