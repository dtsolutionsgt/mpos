package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_PARAMEXT {

    @Element(required=false) public int ID=0;
    @Element(required=false) public String IdRuta="";
    @Element(required=false) public String Nombre="";
    @Element(required=false) public String Valor="";
    @Element(required=false) public String EMPRESA="";
    @Element(required=false) public int CODIGO_PARAMEXT=0;
    @Element(required=false) public int IDRUTA=0;

    public clsBeP_PARAMEXT() {
    }

    public clsBeP_PARAMEXT(int ID,String IdRuta,String Nombre,String Valor,
                           String EMPRESA, int CODIGO_PARAMEXT, int IDRUTA) {

        this.ID=ID;
        this.IdRuta=IdRuta;
        this.Nombre=Nombre;
        this.Valor=Valor;
        this.EMPRESA=EMPRESA;
        this.CODIGO_PARAMEXT = CODIGO_PARAMEXT;
        this.IDRUTA = IDRUTA;

    }


    public int getID() {
        return ID;
    }
    public void setID(int value) {
        ID=value;
    }
    public String getIdRuta() {
        return IdRuta;
    }
    public void setIdRuta(String value) {
        IdRuta=value;
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String value) {
        Nombre=value;
    }
    public String getValor() {
        return Valor;
    }
    public void setValor(String value) {
        Valor=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }

    public int getCODIGO_PARAMEXT() {
        return CODIGO_PARAMEXT;
    }
    public void setCODIGO_PARAMEXT(int value) {
        CODIGO_PARAMEXT=value;
    }

    public int getIDRUTA() {
        return IDRUTA;
    }
    public void setIDRUTA(int value) {
        IDRUTA=value;
    }

}


