package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_IMPRESORA_REDIRECCION {

    @Element(required=false) public int CODIGO_REDIR;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_RUTA;
    @Element(required=false) public int CODIGO_IMPRESORA;
    @Element(required=false) public int CODIGO_IMPRESORA_FINAL;


    public clsBeP_IMPRESORA_REDIRECCION() {
    }

    public clsBeP_IMPRESORA_REDIRECCION(int CODIGO_REDIR,int EMPRESA,int CODIGO_RUTA,int CODIGO_IMPRESORA,
                                        int CODIGO_IMPRESORA_FINAL) {

        this.CODIGO_REDIR=CODIGO_REDIR;
        this.EMPRESA=EMPRESA;
        this.CODIGO_RUTA=CODIGO_RUTA;
        this.CODIGO_IMPRESORA=CODIGO_IMPRESORA;
        this.CODIGO_IMPRESORA_FINAL=CODIGO_IMPRESORA_FINAL;

    }


    public int getCODIGO_REDIR() {
        return CODIGO_REDIR;
    }
    public void setCODIGO_REDIR(int value) {
        CODIGO_REDIR=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_RUTA() {
        return CODIGO_RUTA;
    }
    public void setCODIGO_RUTA(int value) {
        CODIGO_RUTA=value;
    }
    public int getCODIGO_IMPRESORA() {
        return CODIGO_IMPRESORA;
    }
    public void setCODIGO_IMPRESORA(int value) {
        CODIGO_IMPRESORA=value;
    }
    public int getCODIGO_IMPRESORA_FINAL() {
        return CODIGO_IMPRESORA_FINAL;
    }
    public void setCODIGO_IMPRESORA_FINAL(int value) {
        CODIGO_IMPRESORA_FINAL=value;
    }

}


