package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_COREL {

    @Element(required=false) public String RESOL;
    @Element(required=false) public String SERIE;
    @Element(required=false) public int CORELINI;
    @Element(required=false) public int CORELFIN;
    @Element(required=false) public int CORELULT;
    @Element(required=false) public Long FECHARES;
    @Element(required=false) public int RUTA;
    @Element(required=false) public String EMPRESA;
    @Element(required=false) public boolean ACTIVA;
    @Element(required=false) public String HANDHELD;
    @Element(required=false) public Long FECHAVIG;
    @Element(required=false) public int RESGUARDO;
    @Element(required=false) public int VALOR1;
    @Element(required=false) public int CODIGO_COREL;


    public clsBeP_COREL() {
    }

    public clsBeP_COREL(String RESOL,String SERIE,int CORELINI,int CORELFIN,
                        int CORELULT,Long FECHARES,int RUTA,String EMPRESA,
                        boolean ACTIVA,String HANDHELD,Long FECHAVIG,int RESGUARDO,
                        int VALOR1,int CODIGO_COREL) {

        this.RESOL=RESOL;
        this.SERIE=SERIE;
        this.CORELINI=CORELINI;
        this.CORELFIN=CORELFIN;
        this.CORELULT=CORELULT;
        this.FECHARES=FECHARES;
        this.RUTA=RUTA;
        this.EMPRESA=EMPRESA;
        this.ACTIVA=ACTIVA;
        this.HANDHELD=HANDHELD;
        this.FECHAVIG=FECHAVIG;
        this.RESGUARDO=RESGUARDO;
        this.VALOR1=VALOR1;
        this.CODIGO_COREL=CODIGO_COREL;

    }


    public String getRESOL() {
        return RESOL;
    }
    public void setRESOL(String value) {
        RESOL=value;
    }
    public String getSERIE() {
        return SERIE;
    }
    public void setSERIE(String value) {
        SERIE=value;
    }
    public int getCORELINI() {
        return CORELINI;
    }
    public void setCORELINI(int value) {
        CORELINI=value;
    }
    public int getCORELFIN() {
        return CORELFIN;
    }
    public void setCORELFIN(int value) {
        CORELFIN=value;
    }
    public int getCORELULT() {
        return CORELULT;
    }
    public void setCORELULT(int value) {
        CORELULT=value;
    }
    public Long getFECHARES() {
        return FECHARES;
    }
    public void setFECHARES(Long value) {
        FECHARES=value;
    }
    public int getRUTA() {
        return RUTA;
    }
    public void setRUTA(int value) {
        RUTA=value;
    }
    public String getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(String value) {
        EMPRESA=value;
    }
    public boolean getACTIVA() {
        return ACTIVA;
    }
    public void setACTIVA(boolean value) {
        ACTIVA=value;
    }
    public String getHANDHELD() {
        return HANDHELD;
    }
    public void setHANDHELD(String value) {
        HANDHELD=value;
    }
    public Long getFECHAVIG() {
        return FECHAVIG;
    }
    public void setFECHAVIG(Long value) {
        FECHAVIG=value;
    }
    public int getRESGUARDO() {
        return RESGUARDO;
    }
    public void setRESGUARDO(int value) {
        RESGUARDO=value;
    }
    public int getVALOR1() {
        return VALOR1;
    }
    public void setVALOR1(int value) {
        VALOR1=value;
    }
    public int getCODIGO_COREL() {
        return CODIGO_COREL;
    }
    public void setCODIGO_COREL(int value) {
        CODIGO_COREL=value;
    }

}

