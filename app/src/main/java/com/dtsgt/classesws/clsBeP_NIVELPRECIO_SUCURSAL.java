package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_NIVELPRECIO_SUCURSAL {

    @Element(required=false) public int CODIGO_NIVEL_SUCURSAL;
    @Element(required=false) public int CODIGO_EMPRESA;
    @Element(required=false) public int CODIGO_SUCURSAL;
    @Element(required=false) public int CODIGO_NIVEL_PRECIO;
    @Element(required=false) public int USUARIO_AGREGO;
    @Element(required=false) public String FECHA_AGREGADO;
    @Element(required=false) public boolean ACTIVO;


    public clsBeP_NIVELPRECIO_SUCURSAL() {
    }

    public clsBeP_NIVELPRECIO_SUCURSAL(int CODIGO_NIVEL_SUCURSAL,int CODIGO_EMPRESA,int CODIGO_SUCURSAL,int CODIGO_NIVEL_PRECIO,
                                       int USUARIO_AGREGO,String FECHA_AGREGADO,boolean ACTIVO) {

        this.CODIGO_NIVEL_SUCURSAL=CODIGO_NIVEL_SUCURSAL;
        this.CODIGO_EMPRESA=CODIGO_EMPRESA;
        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO_NIVEL_PRECIO=CODIGO_NIVEL_PRECIO;
        this.USUARIO_AGREGO=USUARIO_AGREGO;
        this.FECHA_AGREGADO=FECHA_AGREGADO;
        this.ACTIVO=ACTIVO;

    }


    public int getCODIGO_NIVEL_SUCURSAL() {
        return CODIGO_NIVEL_SUCURSAL;
    }
    public void setCODIGO_NIVEL_SUCURSAL(int value) {
        CODIGO_NIVEL_SUCURSAL=value;
    }
    public int getCODIGO_EMPRESA() {
        return CODIGO_EMPRESA;
    }
    public void setCODIGO_EMPRESA(int value) {
        CODIGO_EMPRESA=value;
    }
    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
    }
    public int getCODIGO_NIVEL_PRECIO() {
        return CODIGO_NIVEL_PRECIO;
    }
    public void setCODIGO_NIVEL_PRECIO(int value) {
        CODIGO_NIVEL_PRECIO=value;
    }
    public int getUSUARIO_AGREGO() {
        return USUARIO_AGREGO;
    }
    public void setUSUARIO_AGREGO(int value) {
        USUARIO_AGREGO=value;
    }
    public String getFECHA_AGREGADO() {
        return FECHA_AGREGADO;
    }
    public void setFECHA_AGREGADO(String value) {
        FECHA_AGREGADO=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }

}

