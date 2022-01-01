package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_DESCUENTO {

    @Element(required=false) public int CODIGO_DESCUENTO;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public int CODIGO_CLIENTE;
    @Element(required=false) public int CTIPO;
    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public int PTIPO;
    @Element(required=false) public int TIPORUTA;
    @Element(required=false) public double RANGOINI;
    @Element(required=false) public double RANGOFIN;
    @Element(required=false) public String DESCTIPO;
    @Element(required=false) public double VALOR;
    @Element(required=false) public String GLOBDESC;
    @Element(required=false) public String PORCANT;
    @Element(required=false) public Long FECHAINI;
    @Element(required=false) public Long FECHAFIN;
    @Element(required=false) public int CODDESC;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public int ACTIVO;


    public clsBeP_DESCUENTO() {
    }

    public clsBeP_DESCUENTO(int CODIGO_DESCUENTO,int EMPRESA,int CODIGO_CLIENTE,int CTIPO,
                            int CODIGO_PRODUCTO,int PTIPO,int TIPORUTA,double RANGOINI,
                            double RANGOFIN,String DESCTIPO,double VALOR,String GLOBDESC,
                            String PORCANT,long FECHAINI,long FECHAFIN,int CODDESC,
                            String NOMBRE,int ACTIVO) {

        this.CODIGO_DESCUENTO=CODIGO_DESCUENTO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_CLIENTE=CODIGO_CLIENTE;
        this.CTIPO=CTIPO;
        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.PTIPO=PTIPO;
        this.TIPORUTA=TIPORUTA;
        this.RANGOINI=RANGOINI;
        this.RANGOFIN=RANGOFIN;
        this.DESCTIPO=DESCTIPO;
        this.VALOR=VALOR;
        this.GLOBDESC=GLOBDESC;
        this.PORCANT=PORCANT;
        this.FECHAINI=FECHAINI;
        this.FECHAFIN=FECHAFIN;
        this.CODDESC=CODDESC;
        this.NOMBRE=NOMBRE;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_DESCUENTO() {
        return CODIGO_DESCUENTO;
    }
    public void setCODIGO_DESCUENTO(int value) {
        CODIGO_DESCUENTO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_CLIENTE() {
        return CODIGO_CLIENTE;
    }
    public void setCODIGO_CLIENTE(int value) {
        CODIGO_CLIENTE=value;
    }
    public int getCTIPO() {
        return CTIPO;
    }
    public void setCTIPO(int value) {
        CTIPO=value;
    }
    public int getCODIGO_PRODUCTO() {
        return CODIGO_PRODUCTO;
    }
    public void setCODIGO_PRODUCTO(int value) {
        CODIGO_PRODUCTO=value;
    }
    public int getPTIPO() {
        return PTIPO;
    }
    public void setPTIPO(int value) {
        PTIPO=value;
    }
    public int getTIPORUTA() {
        return TIPORUTA;
    }
    public void setTIPORUTA(int value) {
        TIPORUTA=value;
    }
    public double getRANGOINI() {
        return RANGOINI;
    }
    public void setRANGOINI(double value) {
        RANGOINI=value;
    }
    public double getRANGOFIN() {
        return RANGOFIN;
    }
    public void setRANGOFIN(double value) {
        RANGOFIN=value;
    }
    public String getDESCTIPO() {
        return DESCTIPO;
    }
    public void setDESCTIPO(String value) {
        DESCTIPO=value;
    }
    public double getVALOR() {
        return VALOR;
    }
    public void setVALOR(double value) {
        VALOR=value;
    }
    public String getGLOBDESC() {
        return GLOBDESC;
    }
    public void setGLOBDESC(String value) {
        GLOBDESC=value;
    }
    public String getPORCANT() {
        return PORCANT;
    }
    public void setPORCANT(String value) {
        PORCANT=value;
    }
    public long getFECHAINI() {
        return FECHAINI;
    }
    public void setFECHAINI(long value) {
        FECHAINI=value;
    }
    public long getFECHAFIN() {
        return FECHAFIN;
    }
    public void setFECHAFIN(long value) {
        FECHAFIN=value;
    }
    public int getCODDESC() {
        return CODDESC;
    }
    public void setCODDESC(int value) {
        CODDESC=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }

}

