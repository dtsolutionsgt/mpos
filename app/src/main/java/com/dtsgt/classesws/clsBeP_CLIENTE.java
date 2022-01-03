package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_CLIENTE {

    @Element(required=false) public long CODIGO_CLIENTE;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean BLOQUEADO;
    @Element(required=false) public int NIVELPRECIO;
    @Element(required=false) public int MEDIAPAGO;
    @Element(required=false) public double LIMITECREDITO;
    @Element(required=false) public int DIACREDITO;
    @Element(required=false) public boolean DESCUENTO;
    @Element(required=false) public boolean BONIFICACION;
    @Element(required=false) public int ULTVISITA;
    @Element(required=false) public double IMPSPEC;
    @Element(required=false) public String NIT;
    @Element(required=false) public String EMAIL;
    @Element(required=false) public String ESERVICE;
    @Element(required=false) public String TELEFONO;
    @Element(required=false) public String DIRECCION;
    @Element(required=false) public double COORX;
    @Element(required=false) public double COORY;
    @Element(required=false) public String BODEGA;
    @Element(required=false) public String COD_PAIS;
    @Element(required=false) public String CODBARRA;
    @Element(required=false) public double PERCEPCION;
    @Element(required=false) public String TIPO_CONTRIBUYENTE;
    @Element(required=false) public String IMAGEN;


    public clsBeP_CLIENTE() {
    }

    public clsBeP_CLIENTE(int CODIGO_CLIENTE,String CODIGO,String EMPRESA,String NOMBRE,
                          boolean BLOQUEADO,int NIVELPRECIO,int MEDIAPAGO,double LIMITECREDITO,
                          int DIACREDITO,boolean DESCUENTO,boolean BONIFICACION,int ULTVISITA,
                          double IMPSPEC,String NIT,String EMAIL,String ESERVICE,
                          String TELEFONO,String DIRECCION,double COORX,double COORY,
                          String BODEGA,String COD_PAIS,String CODBARRA,double PERCEPCION,
                          String TIPO_CONTRIBUYENTE,String IMAGEN) {

        this.CODIGO_CLIENTE=CODIGO_CLIENTE;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.NOMBRE=NOMBRE;
        this.BLOQUEADO=BLOQUEADO;
        this.NIVELPRECIO=NIVELPRECIO;
        this.MEDIAPAGO=MEDIAPAGO;
        this.LIMITECREDITO=LIMITECREDITO;
        this.DIACREDITO=DIACREDITO;
        this.DESCUENTO=DESCUENTO;
        this.BONIFICACION=BONIFICACION;
        this.ULTVISITA=ULTVISITA;
        this.IMPSPEC=IMPSPEC;
        this.NIT=NIT;
        this.EMAIL=EMAIL;
        this.ESERVICE=ESERVICE;
        this.TELEFONO=TELEFONO;
        this.DIRECCION=DIRECCION;
        this.COORX=COORX;
        this.COORY=COORY;
        this.BODEGA=BODEGA;
        this.COD_PAIS=COD_PAIS;
        this.CODBARRA=CODBARRA;
        this.PERCEPCION=PERCEPCION;
        this.TIPO_CONTRIBUYENTE=TIPO_CONTRIBUYENTE;
        this.IMAGEN=IMAGEN;

    }


    public long getCODIGO_CLIENTE() {
        return CODIGO_CLIENTE;
    }
    public void setCODIGO_CLIENTE(long value) {
        CODIGO_CLIENTE=value;
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
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public boolean getBLOQUEADO() {
        return BLOQUEADO;
    }
    public void setBLOQUEADO(boolean value) {
        BLOQUEADO=value;
    }
    public int getNIVELPRECIO() {
        return NIVELPRECIO;
    }
    public void setNIVELPRECIO(int value) {
        NIVELPRECIO=value;
    }
    public int getMEDIAPAGO() {
        return MEDIAPAGO;
    }
    public void setMEDIAPAGO(int value) {
        MEDIAPAGO=value;
    }
    public double getLIMITECREDITO() {
        return LIMITECREDITO;
    }
    public void setLIMITECREDITO(double value) {
        LIMITECREDITO=value;
    }
    public int getDIACREDITO() {
        return DIACREDITO;
    }
    public void setDIACREDITO(int value) {
        DIACREDITO=value;
    }
    public boolean getDESCUENTO() {
        return DESCUENTO;
    }
    public void setDESCUENTO(boolean value) {
        DESCUENTO=value;
    }
    public boolean getBONIFICACION() {
        return BONIFICACION;
    }
    public void setBONIFICACION(boolean value) {
        BONIFICACION=value;
    }
    public int getULTVISITA() {
        return ULTVISITA;
    }
    public void setULTVISITA(int value) {
        ULTVISITA=value;
    }
    public double getIMPSPEC() {
        return IMPSPEC;
    }
    public void setIMPSPEC(double value) {
        IMPSPEC=value;
    }
    public String getNIT() {
        return NIT;
    }
    public void setNIT(String value) {
        NIT=value;
    }
    public String getEMAIL() {
        return EMAIL;
    }
    public void setEMAIL(String value) {
        EMAIL=value;
    }
    public String getESERVICE() {
        return ESERVICE;
    }
    public void setESERVICE(String value) {
        ESERVICE=value;
    }
    public String getTELEFONO() {
        return TELEFONO;
    }
    public void setTELEFONO(String value) {
        TELEFONO=value;
    }
    public String getDIRECCION() {
        return DIRECCION;
    }
    public void setDIRECCION(String value) {
        DIRECCION=value;
    }
    public double getCOORX() {
        return COORX;
    }
    public void setCOORX(double value) {
        COORX=value;
    }
    public double getCOORY() {
        return COORY;
    }
    public void setCOORY(double value) {
        COORY=value;
    }
    public String getBODEGA() {
        return BODEGA;
    }
    public void setBODEGA(String value) {
        BODEGA=value;
    }
    public String getCOD_PAIS() {
        return COD_PAIS;
    }
    public void setCOD_PAIS(String value) {
        COD_PAIS=value;
    }
    public String getCODBARRA() {
        return CODBARRA;
    }
    public void setCODBARRA(String value) {
        CODBARRA=value;
    }
    public double getPERCEPCION() {
        return PERCEPCION;
    }
    public void setPERCEPCION(double value) {
        PERCEPCION=value;
    }
    public String getTIPO_CONTRIBUYENTE() {
        return TIPO_CONTRIBUYENTE;
    }
    public void setTIPO_CONTRIBUYENTE(String value) {
        TIPO_CONTRIBUYENTE=value;
    }
    public String getIMAGEN() {
        return IMAGEN;
    }
    public void setIMAGEN(String value) {
        IMAGEN=value;
    }

}

