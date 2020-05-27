package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_PARAMEXT {

    @Element(required=false) public int ID=0;
    @Element(required=false) public String IdRuta="";
    @Element(required=false) public String Nombre="";
    @Element(required=false) public String Valor="";
    @Element(required=false) public String EMPRESA="";


    public clsBeP_PARAMEXT() {
    }

    public clsBeP_PARAMEXT(int ID,String IdRuta,String Nombre,String Valor,
                           String EMPRESA) {

        this.ID=ID;
        this.IdRuta=IdRuta;
        this.Nombre=Nombre;
        this.Valor=Valor;
        this.EMPRESA=EMPRESA;

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

}


