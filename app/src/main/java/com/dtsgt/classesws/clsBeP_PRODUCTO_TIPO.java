package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODUCTO_TIPO {

    @Element(required=false) public String CODIGO_TIPO_PRODUCTO;
    @Element(required=false) public String NOMBRE;
    @Element(required=false) public boolean UTILIZA_STOCK;

    public clsBeP_PRODUCTO_TIPO() {
    }

    public clsBeP_PRODUCTO_TIPO(String CODIGO_TIPO_PRODUCTO,
                                String NOMBRE, boolean UTILIZA_STOCK) {

        this.CODIGO_TIPO_PRODUCTO = CODIGO_TIPO_PRODUCTO;
        this.NOMBRE=NOMBRE;
        this.UTILIZA_STOCK = UTILIZA_STOCK;

    }

    public String getCODIGO_TIPO_PRODUCTO() {
        return CODIGO_TIPO_PRODUCTO;
    }
    public void setCODIGO_TIPO_PRODUCTO(String value) {
        CODIGO_TIPO_PRODUCTO =value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public boolean getUTILIZA_STOCK() {
        return UTILIZA_STOCK;
    }
    public void setUTILIZA_STOCK(boolean value) {
        UTILIZA_STOCK =value;
    }

}

