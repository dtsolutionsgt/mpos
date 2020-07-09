package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_FRASE {

    @Element(required=false) public int CODIGO_FRASE;
    @Element(required=false) public String TEXTO;


    public clsBeP_FRASE() {
    }

    public clsBeP_FRASE(int CODIGO_FRASE,String TEXTO) {

        this.CODIGO_FRASE=CODIGO_FRASE;
        this.TEXTO=TEXTO;

    }


    public int getCODIGO_FRASE() {
        return CODIGO_FRASE;
    }
    public void setCODIGO_FRASE(int value) {
        CODIGO_FRASE=value;
    }
    public String getTEXTO() {
        return TEXTO;
    }
    public void setTEXTO(String value) {
        TEXTO=value;
    }

}

