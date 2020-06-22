package com.dtsgt.classesws;

import org.simpleframework.xml.Element;

public class clsBeP_SUCURSAL {

    @Element(required=false) public int CODIGO_SUCURSAL=0;
    @Element(required=false) public String CODIGO="";
    @Element(required=false) public int EMPRESA=0;
    @Element(required=false) public int CODIGO_NIVEL_PRECIO=0;
    @Element(required=false) public String DESCRIPCION="";
    @Element(required=false) public String NOMBRE="";
    @Element(required=false) public String DIRECCION="";
    @Element(required=false) public String TELEFONO="";
    @Element(required=false) public String CORREO="";
    @Element(required=false) public String NIT="";
    @Element(required=false) public String TEXTO="";
    @Element(required=false) public boolean ACTIVO=false;
    @Element(required=false) public String FEL_CODIGO_ESTABLECIMIENTO="";
    @Element(required=false) public String FEL_USUARIO_FIRMA="";
    @Element(required=false) public String FEL_USUARIO_CERTIFICACION ="";
    @Element(required=false) public String FEL_LLAVE_FIRMA="";
    @Element(required=false) public String FEL_LLAVE_CERTIFICACION="";
    @Element(required=false) public String FEL_AFILIACION_IVA="";
    @Element(required=false) public String CODIGO_POSTAL="";
    @Element(required=false) public int CODIGO_ESCENARIO_ISR=0;
    @Element(required=false) public int CODIGO_ESCENARIO_IVA=0;
    @Element(required=false) public String CODIGO_MUNICIPIO="";
    @Element(required=false) public int CODIGO_PROVEEDOR=0;


    public clsBeP_SUCURSAL() {
    }

    public clsBeP_SUCURSAL(int CODIGO_SUCURSAL,String CODIGO,int EMPRESA,int CODIGO_NIVEL_PRECIO,
                           String DESCRIPCION,String NOMBRE,String DIRECCION,String TELEFONO,
                           String CORREO,String NIT,String TEXTO,boolean ACTIVO,
                           String FEL_CODIGO_ESTABLECIMIENTO,String FEL_USUARIO_CERTIFICACION,String FEL_LLAVE_FIRMA,
                           String FEL_LLAVE_CERTIFICACION,String FEL_AFILIACION_IVA,String FEL_USUARIO_FIRMA,
                           String CODIGO_POSTAL,int CODIGO_ESCENARIO_ISR,int CODIGO_ESCENARIO_IVA,String CODIGO_MUNICIPIO,
                           int CODIGO_PROVEEDOR) {

        this.CODIGO_SUCURSAL=CODIGO_SUCURSAL;
        this.CODIGO=CODIGO;
        this.EMPRESA=EMPRESA;
        this.CODIGO_NIVEL_PRECIO=CODIGO_NIVEL_PRECIO;
        this.DESCRIPCION=DESCRIPCION;
        this.NOMBRE=NOMBRE;
        this.DIRECCION=DIRECCION;
        this.TELEFONO=TELEFONO;
        this.CORREO=CORREO;
        this.NIT=NIT;
        this.TEXTO=TEXTO;
        this.ACTIVO=ACTIVO;
        this.FEL_CODIGO_ESTABLECIMIENTO=FEL_CODIGO_ESTABLECIMIENTO;
        this.FEL_USUARIO_CERTIFICACION =FEL_USUARIO_CERTIFICACION;
        this.FEL_USUARIO_FIRMA =FEL_USUARIO_FIRMA;
        this.FEL_LLAVE_FIRMA=FEL_LLAVE_FIRMA;
        this.FEL_LLAVE_CERTIFICACION=FEL_LLAVE_CERTIFICACION;
        this.FEL_AFILIACION_IVA=FEL_AFILIACION_IVA;
        this.CODIGO_POSTAL=CODIGO_POSTAL;
        this.CODIGO_ESCENARIO_ISR=CODIGO_ESCENARIO_ISR;
        this.CODIGO_ESCENARIO_IVA=CODIGO_ESCENARIO_IVA;
        this.CODIGO_MUNICIPIO=CODIGO_MUNICIPIO;
        this.CODIGO_PROVEEDOR=CODIGO_PROVEEDOR;

    }


    public int getCODIGO_SUCURSAL() {
        return CODIGO_SUCURSAL;
    }
    public void setCODIGO_SUCURSAL(int value) {
        CODIGO_SUCURSAL=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public int getCODIGO_NIVEL_PRECIO() {
        return CODIGO_NIVEL_PRECIO;
    }
    public void setCODIGO_NIVEL_PRECIO(int value) {
        CODIGO_NIVEL_PRECIO=value;
    }
    public String getDESCRIPCION() {
        return DESCRIPCION;
    }
    public void setDESCRIPCION(String value) {
        DESCRIPCION=value;
    }
    public String getNOMBRE() {
        return NOMBRE;
    }
    public void setNOMBRE(String value) {
        NOMBRE=value;
    }
    public String getDIRECCION() {
        return DIRECCION;
    }
    public void setDIRECCION(String value) {
        DIRECCION=value;
    }
    public String getTELEFONO() {
        return TELEFONO;
    }
    public void setTELEFONO(String value) {
        TELEFONO=value;
    }
    public String getCORREO() {
        return CORREO;
    }
    public void setCORREO(String value) {
        CORREO=value;
    }
    public String getNIT() {
        return NIT;
    }
    public void setNIT(String value) {
        NIT=value;
    }
    public String getTEXTO() {
        return TEXTO;
    }
    public void setTEXTO(String value) {
        TEXTO=value;
    }
    public boolean getACTIVO() {
        return ACTIVO;
    }
    public void setACTIVO(boolean value) {
        ACTIVO=value;
    }
    public String getFEL_CODIGO_ESTABLECIMIENTO() {
        return FEL_CODIGO_ESTABLECIMIENTO;
    }
    public void setFEL_CODIGO_ESTABLECIMIENTO(String value) {
        FEL_CODIGO_ESTABLECIMIENTO=value;
    }
    public String getFEL_USUARIO_CERTIFICACION() {
        return FEL_USUARIO_CERTIFICACION;
    }
    public void setFEL_USUARIO_CERTIFICACION(String value) {
        FEL_USUARIO_CERTIFICACION =value;
    }
    public String getFEL_USUARIO_FIRMA() {
        return FEL_USUARIO_FIRMA;
    }
    public void setFEL_USUARIO_FIRMA(String value) {
        FEL_USUARIO_FIRMA =value;
    }
    public String getFEL_LLAVE_FIRMA() {
        return FEL_LLAVE_FIRMA;
    }
    public void setFEL_LLAVE_FIRMA(String value) {
        FEL_LLAVE_FIRMA=value;
    }
    public String getFEL_LLAVE_CERTIFICACION() {
        return FEL_LLAVE_CERTIFICACION;
    }
    public void setFEL_LLAVE_CERTIFICACION(String value) {
        FEL_LLAVE_CERTIFICACION=value;
    }
    public String getFEL_AFILIACION_IVA() {
        return FEL_AFILIACION_IVA;
    }
    public void setFEL_AFILIACION_IVA(String value) {
        FEL_AFILIACION_IVA=value;
    }
    public String getCODIGO_POSTAL() {
        return CODIGO_POSTAL;
    }
    public void setCODIGO_POSTAL(String value) {
        CODIGO_POSTAL=value;
    }
    public int getCODIGO_ESCENARIO_ISR() {
        return CODIGO_ESCENARIO_ISR;
    }
    public void setCODIGO_ESCENARIO_ISR(int value) {
        CODIGO_ESCENARIO_ISR=value;
    }
    public int getCODIGO_ESCENARIO_IVA() {
        return CODIGO_ESCENARIO_IVA;
    }
    public void setCODIGO_ESCENARIO_IVA(int value) {
        CODIGO_ESCENARIO_IVA=value;
    }
    public String getCODIGO_MUNICIPIO() {
        return CODIGO_MUNICIPIO;
    }
    public void setCODIGO_MUNICIPIO(String value) {
        CODIGO_MUNICIPIO=value;
    }
    public int getCODIGO_PROVEEDOR() {
        return CODIGO_PROVEEDOR;
    }
    public void setCODIGO_PROVEEDOR(int value) {
        CODIGO_PROVEEDOR=value;
    }

}

