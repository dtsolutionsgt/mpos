package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODMENU {

    @Element(required=false) public String CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public int ITEM;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int IDOPCION;
    @Element(required=false) public int CANT;
    @Element(required=false) public int ORDEN;
    @Element(required=false) public int BANDERA;
    @Element(required=false) public String NOTA;


    public clsBeP_PRODMENU() {
    }

    public clsBeP_PRODMENU(String CODIGO,String EMPRESA,int ITEM,String NOMBRE,
                           int IDOPCION,int CANT,int ORDEN,int BANDERA,
                           String NOTA) {

        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.ITEM=ITEM;
        this.NOMBRE=NOMBRE;
        this.IDOPCION=IDOPCION;
        this.CANT=CANT;
        this.ORDEN=ORDEN;
        this.BANDERA=BANDERA;
        this.NOTA=NOTA;

    }


    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public int getITEM() {
        return ITEM;
    }
    public void setITEM(int value) {
        ITEM=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getIDOPCION() {
        return IDOPCION;
    }
    public void setIDOPCION(int value) {
        IDOPCION=value;
    }
    public int getCANT() {
        return CANT;
    }
    public void setCANT(int value) {
        CANT=value;
    }
    public int getORDEN() {
        return ORDEN;
    }
    public void setORDEN(int value) {
        ORDEN=value;
    }
    public int getBANDERA() {
        return BANDERA;
    }
    public void setBANDERA(int value) {
        BANDERA=value;
    }
    public String getNOTA() {
        return NOTA;
    }
    public void setNOTA(String value) {
        NOTA=value;
    }

}

