package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_DESCUENTO {

    @Element(required=false) public String CLIENTE;
    @Element(required=false) public int CTIPO;
    @Element(required=false) public String PRODUCTO;
    @Element(required=false) public int PTIPO;
    @Element(required=false) public int TIPORUTA;
    @Element(required=false) public double RANGOINI;
    @Element(required=false) public double RANGOFIN;
    @Element(required=false) public String DESCTIPO;
    @Element(required=false) public double VALOR;
    @Element(required=false) public String GLOBDESC;
    @Element(required=false) public String PORCANT;
    @Element(required=false) public int FECHAINI;
    @Element(required=false) public int FECHAFIN;
    @Element(required=false) public int CODDESC;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String EMP;
    @Element(required=false) public int ACTIVO;
    @Element(required=false) public int CODIGO_DESCUENTO;


    public clsBeP_DESCUENTO() {
    }

    public clsBeP_DESCUENTO(String CLIENTE,int CTIPO,String PRODUCTO,int PTIPO,
                            int TIPORUTA,double RANGOINI,double RANGOFIN,String DESCTIPO,
                            double VALOR,String GLOBDESC,String PORCANT,int FECHAINI,
                            int FECHAFIN,int CODDESC,String NOMBRE,String EMP,
                            int ACTIVO,int CODIGO_DESCUENTO) {

        this.CLIENTE=CLIENTE;
        this.CTIPO=CTIPO;
        this.PRODUCTO=PRODUCTO;
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
        this.EMP=EMP;
        this.ACTIVO=ACTIVO;
        this.CODIGO_DESCUENTO=CODIGO_DESCUENTO;

    }


    public String getCLIENTE() {
        return CLIENTE;
    }
    public void setCLIENTE(String value) {
        CLIENTE=value;
    }
    public int getCTIPO() {
        return CTIPO;
    }
    public void setCTIPO(int value) {
        CTIPO=value;
    }
    public String getPRODUCTO() {
        return PRODUCTO;
    }
    public void setPRODUCTO(String value) {
        PRODUCTO=value;
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
    public int getFECHAINI() {
        return FECHAINI;
    }
    public void setFECHAINI(int value) {
        FECHAINI=value;
    }
    public int getFECHAFIN() {
        return FECHAFIN;
    }
    public void setFECHAFIN(int value) {
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
    public String getEMP() {
        return EMP;
    }
    public void setEMP(String value) {
        EMP=value;
    }
    public int getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(int value) {
        ACTIVO=value;
    }
    public int getCODIGO_DESCUENTO() {
        return CODIGO_DESCUENTO;
    }
    public void setCODIGO_DESCUENTO(int value) {
        CODIGO_DESCUENTO=value;
    }
}

