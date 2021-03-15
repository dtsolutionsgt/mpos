package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_UNIDAD_CONV {

    @Element(required=false) public int CODIGO_CONVERSION;
    @Element(required=false) public String CODIGO_UNIDAD1;
    @Element(required=false) public String CODIGO_UNIDAD2;
    @Element(required=false) public double FACTOR;


    public clsBeP_UNIDAD_CONV() {
    }

    public clsBeP_UNIDAD_CONV(int CODIGO_CONVERSION,String CODIGO_UNIDAD1,String CODIGO_UNIDAD2,double FACTOR
    ) {

        this.CODIGO_CONVERSION=CODIGO_CONVERSION;
        this.CODIGO_UNIDAD1=CODIGO_UNIDAD1;
        this.CODIGO_UNIDAD2=CODIGO_UNIDAD2;
        this.FACTOR=FACTOR;

    }


    public int getCODIGO_CONVERSION() {
        return CODIGO_CONVERSION;
    }
    public void setCODIGO_CONVERSION(int value) {
        CODIGO_CONVERSION=value;
    }
    public String getCODIGO_UNIDAD1() {
        return CODIGO_UNIDAD1;
    }
    public void setCODIGO_UNIDAD1(String value) {
        CODIGO_UNIDAD1=value;
    }
    public String getCODIGO_UNIDAD2() {
        return CODIGO_UNIDAD2;
    }
    public void setCODIGO_UNIDAD2(String value) {
        CODIGO_UNIDAD2=value;
    }
    public double getFACTOR() {
        return FACTOR;
    }
    public void setFACTOR(double value) {
        FACTOR=value;
    }

}

