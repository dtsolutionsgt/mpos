package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_BONIF {

    @Element(required=false) public String CLIENTE;
    @Element(required=false) public int CTIPO;
    @Element(required=false) public String PRODUCTO;
    @Element(required=false) public int PTIPO;
    @Element(required=false) public int TIPORUTA;
    @Element(required=false) public String TIPOBON;
    @Element(required=false) public double RANGOINI;
    @Element(required=false) public double RANGOFIN;
    @Element(required=false) public int TIPOLISTA;
    @Element(required=false) public String TIPOCANT;
    @Element(required=false) public double VALOR;
    @Element(required=false) public String LISTA;
    @Element(required=false) public String CANTEXACT;
    @Element(required=false) public String GLOBBON;
    @Element(required=false) public String PORCANT;
    @Element(required=false) public Long FECHAINI;
    @Element(required=false) public Long FECHAFIN;
    @Element(required=false) public int CODDESC;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public String EMP;
    @Element(required=false) public String UMPRODUCTO;
    @Element(required=false) public String UMBONIFICACION;


    public clsBeP_BONIF() {
    }

    public clsBeP_BONIF(String CLIENTE,int CTIPO,String PRODUCTO,int PTIPO,
                        int TIPORUTA,String TIPOBON,double RANGOINI,double RANGOFIN,
                        int TIPOLISTA,String TIPOCANT,double VALOR,String LISTA,
                        String CANTEXACT,String GLOBBON,String PORCANT,Long FECHAINI,
                        Long FECHAFIN,int CODDESC,String NOMBRE,String EMP,
                        String UMPRODUCTO,String UMBONIFICACION) {

        this.CLIENTE=CLIENTE;
        this.CTIPO=CTIPO;
        this.PRODUCTO=PRODUCTO;
        this.PTIPO=PTIPO;
        this.TIPORUTA=TIPORUTA;
        this.TIPOBON=TIPOBON;
        this.RANGOINI=RANGOINI;
        this.RANGOFIN=RANGOFIN;
        this.TIPOLISTA=TIPOLISTA;
        this.TIPOCANT=TIPOCANT;
        this.VALOR=VALOR;
        this.LISTA=LISTA;
        this.CANTEXACT=CANTEXACT;
        this.GLOBBON=GLOBBON;
        this.PORCANT=PORCANT;
        this.FECHAINI=FECHAINI;
        this.FECHAFIN=FECHAFIN;
        this.CODDESC=CODDESC;
        this.NOMBRE=NOMBRE;
        this.EMP=EMP;
        this.UMPRODUCTO=UMPRODUCTO;
        this.UMBONIFICACION=UMBONIFICACION;

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
    public String getTIPOBON() {
        return TIPOBON;
    }
    public void setTIPOBON(String value) {
        TIPOBON=value;
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
    public int getTIPOLISTA() {
        return TIPOLISTA;
    }
    public void setTIPOLISTA(int value) {
        TIPOLISTA=value;
    }
    public String getTIPOCANT() {
        return TIPOCANT;
    }
    public void setTIPOCANT(String value) {
        TIPOCANT=value;
    }
    public double getVALOR() {
        return VALOR;
    }
    public void setVALOR(double value) {
        VALOR=value;
    }
    public String getLISTA() {
        return LISTA;
    }
    public void setLISTA(String value) {
        LISTA=value;
    }
    public String getCANTEXACT() {
        return CANTEXACT;
    }
    public void setCANTEXACT(String value) {
        CANTEXACT=value;
    }
    public String getGLOBBON() {
        return GLOBBON;
    }
    public void setGLOBBON(String value) {
        GLOBBON=value;
    }
    public String getPORCANT() {
        return PORCANT;
    }
    public void setPORCANT(String value) {
        PORCANT=value;
    }
    public Long getFECHAINI() {
        return FECHAINI;
    }
    public void setFECHAINI(Long value) {
        FECHAINI=value;
    }
    public Long getFECHAFIN() {
        return FECHAFIN;
    }
    public void setFECHAFIN(Long value) {
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
    public String getUMPRODUCTO() {
        return UMPRODUCTO;
    }
    public void setUMPRODUCTO(String value) {
        UMPRODUCTO=value;
    }
    public String getUMBONIFICACION() {
        return UMBONIFICACION;
    }
    public void setUMBONIFICACION(String value) {
        UMBONIFICACION=value;
    }

}

