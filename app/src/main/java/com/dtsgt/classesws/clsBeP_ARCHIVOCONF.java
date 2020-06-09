package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_ARCHIVOCONF {

    @Element(required=false) public String RUTA;
    @Element(required=false) public String TIPO_HH;
    @Element(required=false) public String IDIOMA;
    @Element(required=false) public String TIPO_IMPRESORA;
    @Element(required=false) public String SERIAL_HH;
    @Element(required=false) public String MODIF_PESO;
    @Element(required=false) public String PUERTO_IMPRESION;
    @Element(required=false) public String LBS_O_KGS;
    @Element(required=false) public boolean NOTA_CREDITO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public int CODIGO_ARCHIVOCONF;

    public clsBeP_ARCHIVOCONF() {
    }

    public clsBeP_ARCHIVOCONF(String RUTA,String TIPO_HH,String IDIOMA,String TIPO_IMPRESORA,
                              String SERIAL_HH,String MODIF_PESO,String PUERTO_IMPRESION,String LBS_O_KGS,
                              boolean NOTA_CREDITO,String EMPRESA, int CODIGO_ARCHIVOCONF) {

        this.RUTA=RUTA;
        this.TIPO_HH=TIPO_HH;
        this.IDIOMA=IDIOMA;
        this.TIPO_IMPRESORA=TIPO_IMPRESORA;
        this.SERIAL_HH=SERIAL_HH;
        this.MODIF_PESO=MODIF_PESO;
        this.PUERTO_IMPRESION=PUERTO_IMPRESION;
        this.LBS_O_KGS=LBS_O_KGS;
        this.NOTA_CREDITO=NOTA_CREDITO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_ARCHIVOCONF = CODIGO_ARCHIVOCONF;

    }


    public String getRUTA() {
        return RUTA;
    }
    public void setRUTA(String value) {
        RUTA=value;
    }
    public String getTIPO_HH() {
        return TIPO_HH;
    }
    public void setTIPO_HH(String value) {
        TIPO_HH=value;
    }
    public String getIDIOMA() {
        return IDIOMA;
    }
    public void setIDIOMA(String value) {
        IDIOMA=value;
    }
    public String getTIPO_IMPRESORA() {
        return TIPO_IMPRESORA;
    }
    public void setTIPO_IMPRESORA(String value) {
        TIPO_IMPRESORA=value;
    }
    public String getSERIAL_HH() {
        return SERIAL_HH;
    }
    public void setSERIAL_HH(String value) {
        SERIAL_HH=value;
    }
    public String getMODIF_PESO() {
        return MODIF_PESO;
    }
    public void setMODIF_PESO(String value) {
        MODIF_PESO=value;
    }
    public String getPUERTO_IMPRESION() {
        return PUERTO_IMPRESION;
    }
    public void setPUERTO_IMPRESION(String value) {
        PUERTO_IMPRESION=value;
    }
    public String getLBS_O_KGS() {
        return LBS_O_KGS;
    }
    public void setLBS_O_KGS(String value) {
        LBS_O_KGS=value;
    }
    public boolean getNOTA_CREDITO() {
        return NOTA_CREDITO;
    }
    public void setNOTA_CREDITO(boolean value) {
        NOTA_CREDITO=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public int getCODIGO_ARCHIVOCONF() {
        return CODIGO_ARCHIVOCONF;
    }
    public void setCODIGO_ARCHIVOCONF(int value) {
        CODIGO_ARCHIVOCONF=value;
    }

}

