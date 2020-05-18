package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_PRODUCTO {

    @Element(required=false) public int CODIGO_PRODUCTO;
    @Element(required=false) public String CODIGO;
    @Element(required=false) public String CODIGO_TIPO;
    @Element(required=false) public int LINEA;
    @Element(required=false) public int EMPRESA;
    @Element(required=false) public String MARCA;
    @Element(required=false) public String CODBARRA;
    @Element(required=false) public String DESCCORTA;
    @Element(required=false) public String DESCLARGA;
    @Element(required=false) public double COSTO;
    @Element(required=false) public double FACTORCONV;
    @Element(required=false) public String UNIDBAS;
    @Element(required=false) public String UNIDMED;
    @Element(required=false) public double UNIMEDFACT;
    @Element(required=false) public String UNIGRA;
    @Element(required=false) public double UNIGRAFACT;
    @Element(required=false) public String DESCUENTO;
    @Element(required=false) public String BONIFICACION;
    @Element(required=false) public double IMP1;
    @Element(required=false) public double IMP2;
    @Element(required=false) public double IMP3;
    @Element(required=false) public String VENCOMP;
    @Element(required=false) public String DEVOL;
    @Element(required=false) public String OFRECER;
    @Element(required=false) public String RENTAB;
    @Element(required=false) public String DESCMAX;
    @Element(required=false) public String IVA;
    @Element(required=false) public String CODBARRA2;
    @Element(required=false) public int CBCONV;
    @Element(required=false) public String BODEGA;
    @Element(required=false) public String SUBBODEGA;
    @Element(required=false) public double PESO_PROMEDIO;
    @Element(required=false) public boolean MODIF_PRECIO;
    @Element(required=false) public String IMAGEN;
    @Element(required=false) public String VIDEO;
    @Element(required=false) public boolean VENTA_POR_PESO;
    @Element(required=false) public boolean ES_PROD_BARRA;
    @Element(required=false) public String UNID_INV;
    @Element(required=false) public boolean VENTA_POR_PAQUETE;
    @Element(required=false) public boolean VENTA_POR_FACTOR_CONV;
    @Element(required=false) public boolean ES_SERIALIZADO;
    @Element(required=false) public int PARAM_CADUCIDAD;
    @Element(required=false) public String PRODUCTO_PADRE;
    @Element(required=false) public double FACTOR_PADRE;
    @Element(required=false) public boolean TIENE_INV;
    @Element(required=false) public boolean TIENE_VINETA_O_TUBO;
    @Element(required=false) public double PRECIO_VINETA_O_TUBO;
    @Element(required=false) public boolean ES_VENDIBLE;
    @Element(required=false) public double UNIGRASAP;
    @Element(required=false) public String UM_SALIDA;
    @Element(required=false) public int Activo;


    public clsBeP_PRODUCTO() {
    }

    public clsBeP_PRODUCTO(int CODIGO_PRODUCTO,String CODIGO,String CODIGO_TIPO,int LINEA,
                           String SUBLINEA,int EMPRESA,String MARCA,String CODBARRA,
                           String DESCCORTA,String DESCLARGA,double COSTO,double FACTORCONV,
                           String UNIDBAS,String UNIDMED,double UNIMEDFACT,String UNIGRA,
                           double UNIGRAFACT,String DESCUENTO,String BONIFICACION,double IMP1,
                           double IMP2,double IMP3,String VENCOMP,String DEVOL,
                           String OFRECER,String RENTAB,String DESCMAX,String IVA,
                           String CODBARRA2,int CBCONV,String BODEGA,String SUBBODEGA,
                           double PESO_PROMEDIO,boolean MODIF_PRECIO,String IMAGEN,String VIDEO,
                           boolean VENTA_POR_PESO,boolean ES_PROD_BARRA,String UNID_INV,boolean VENTA_POR_PAQUETE,
                           boolean VENTA_POR_FACTOR_CONV,boolean ES_SERIALIZADO,int PARAM_CADUCIDAD,String PRODUCTO_PADRE,
                           double FACTOR_PADRE,boolean TIENE_INV,boolean TIENE_VINETA_O_TUBO,double PRECIO_VINETA_O_TUBO,
                           boolean ES_VENDIBLE,double UNIGRASAP,String UM_SALIDA,int Activo
    ) {

        this.CODIGO_PRODUCTO=CODIGO_PRODUCTO;
        this.CODIGO=CODIGO;
        this.CODIGO_TIPO=CODIGO_TIPO;
        this.LINEA=LINEA;
        this.EMPRESA=EMPRESA;
        this.MARCA=MARCA;
        this.CODBARRA=CODBARRA;
        this.DESCCORTA=DESCCORTA;
        this.DESCLARGA=DESCLARGA;
        this.COSTO=COSTO;
        this.FACTORCONV=FACTORCONV;
        this.UNIDBAS=UNIDBAS;
        this.UNIDMED=UNIDMED;
        this.UNIMEDFACT=UNIMEDFACT;
        this.UNIGRA=UNIGRA;
        this.UNIGRAFACT=UNIGRAFACT;
        this.DESCUENTO=DESCUENTO;
        this.BONIFICACION=BONIFICACION;
        this.IMP1=IMP1;
        this.IMP2=IMP2;
        this.IMP3=IMP3;
        this.VENCOMP=VENCOMP;
        this.DEVOL=DEVOL;
        this.OFRECER=OFRECER;
        this.RENTAB=RENTAB;
        this.DESCMAX=DESCMAX;
        this.IVA=IVA;
        this.CODBARRA2=CODBARRA2;
        this.CBCONV=CBCONV;
        this.BODEGA=BODEGA;
        this.SUBBODEGA=SUBBODEGA;
        this.PESO_PROMEDIO=PESO_PROMEDIO;
        this.MODIF_PRECIO=MODIF_PRECIO;
        this.IMAGEN=IMAGEN;
        this.VIDEO=VIDEO;
        this.VENTA_POR_PESO=VENTA_POR_PESO;
        this.ES_PROD_BARRA=ES_PROD_BARRA;
        this.UNID_INV=UNID_INV;
        this.VENTA_POR_PAQUETE=VENTA_POR_PAQUETE;
        this.VENTA_POR_FACTOR_CONV=VENTA_POR_FACTOR_CONV;
        this.ES_SERIALIZADO=ES_SERIALIZADO;
        this.PARAM_CADUCIDAD=PARAM_CADUCIDAD;
        this.PRODUCTO_PADRE=PRODUCTO_PADRE;
        this.FACTOR_PADRE=FACTOR_PADRE;
        this.TIENE_INV=TIENE_INV;
        this.TIENE_VINETA_O_TUBO=TIENE_VINETA_O_TUBO;
        this.PRECIO_VINETA_O_TUBO=PRECIO_VINETA_O_TUBO;
        this.ES_VENDIBLE=ES_VENDIBLE;
        this.UNIGRASAP=UNIGRASAP;
        this.UM_SALIDA=UM_SALIDA;
        this.Activo=Activo;

    }


    public int getCODIGO_PRODUCTO() {
        return CODIGO_PRODUCTO;
    }
    public void setCODIGO_PRODUCTO(int value) {
        CODIGO_PRODUCTO=value;
    }
    public String getCODIGO() {
        return CODIGO;
    }
    public void setCODIGO(String value) {
        CODIGO=value;
    }
    public String getCODIGO_TIPO() {
        return CODIGO_TIPO;
    }
    public void setTIPO(String value) {
        CODIGO_TIPO=value;
    }
    public int getLINEA() {
        return LINEA;
    }
    public void setLINEA(int value) {
        LINEA=value;
    }
    public int getEMPRESA() {
        return EMPRESA;
    }
    public void setEMPRESA(int value) {
        EMPRESA=value;
    }
    public String getMARCA() {
        return MARCA;
    }
    public void setMARCA(String value) {
        MARCA=value;
    }
    public String getCODBARRA() {
        return CODBARRA;
    }
    public void setCODBARRA(String value) {
        CODBARRA=value;
    }
    public String getDESCCORTA() {
        return DESCCORTA;
    }
    public void setDESCCORTA(String value) {
        DESCCORTA=value;
    }
    public String getDESCLARGA() {
        return DESCLARGA;
    }
    public void setDESCLARGA(String value) {
        DESCLARGA=value;
    }
    public double getCOSTO() {
        return COSTO;
    }
    public void setCOSTO(double value) {
        COSTO=value;
    }
    public double getFACTORCONV() {
        return FACTORCONV;
    }
    public void setFACTORCONV(double value) {
        FACTORCONV=value;
    }
    public String getUNIDBAS() {
        return UNIDBAS;
    }
    public void setUNIDBAS(String value) {
        UNIDBAS=value;
    }
    public String getUNIDMED() {
        return UNIDMED;
    }
    public void setUNIDMED(String value) {
        UNIDMED=value;
    }
    public double getUNIMEDFACT() {
        return UNIMEDFACT;
    }
    public void setUNIMEDFACT(double value) {
        UNIMEDFACT=value;
    }
    public String getUNIGRA() {
        return UNIGRA;
    }
    public void setUNIGRA(String value) {
        UNIGRA=value;
    }
    public double getUNIGRAFACT() {
        return UNIGRAFACT;
    }
    public void setUNIGRAFACT(double value) {
        UNIGRAFACT=value;
    }
    public String getDESCUENTO() {
        return DESCUENTO;
    }
    public void setDESCUENTO(String value) {
        DESCUENTO=value;
    }
    public String getBONIFICACION() {
        return BONIFICACION;
    }
    public void setBONIFICACION(String value) {
        BONIFICACION=value;
    }
    public double getIMP1() {
        return IMP1;
    }
    public void setIMP1(double value) {
        IMP1=value;
    }
    public double getIMP2() {
        return IMP2;
    }
    public void setIMP2(double value) {
        IMP2=value;
    }
    public double getIMP3() {
        return IMP3;
    }
    public void setIMP3(double value) {
        IMP3=value;
    }
    public String getVENCOMP() {
        return VENCOMP;
    }
    public void setVENCOMP(String value) {
        VENCOMP=value;
    }
    public String getDEVOL() {
        return DEVOL;
    }
    public void setDEVOL(String value) {
        DEVOL=value;
    }
    public String getOFRECER() {
        return OFRECER;
    }
    public void setOFRECER(String value) {
        OFRECER=value;
    }
    public String getRENTAB() {
        return RENTAB;
    }
    public void setRENTAB(String value) {
        RENTAB=value;
    }
    public String getDESCMAX() {
        return DESCMAX;
    }
    public void setDESCMAX(String value) {
        DESCMAX=value;
    }
    public String getIVA() {
        return IVA;
    }
    public void setIVA(String value) {
        IVA=value;
    }
    public String getCODBARRA2() {
        return CODBARRA2;
    }
    public void setCODBARRA2(String value) {
        CODBARRA2=value;
    }
    public int getCBCONV() {
        return CBCONV;
    }
    public void setCBCONV(int value) {
        CBCONV=value;
    }
    public String getBODEGA() {
        return BODEGA;
    }
    public void setBODEGA(String value) {
        BODEGA=value;
    }
    public String getSUBBODEGA() {
        return SUBBODEGA;
    }
    public void setSUBBODEGA(String value) {
        SUBBODEGA=value;
    }
    public double getPESO_PROMEDIO() {
        return PESO_PROMEDIO;
    }
    public void setPESO_PROMEDIO(double value) {
        PESO_PROMEDIO=value;
    }
    public boolean getMODIF_PRECIO() {
        return MODIF_PRECIO;
    }
    public void setMODIF_PRECIO(boolean value) {
        MODIF_PRECIO=value;
    }
    public String getIMAGEN() {
        return IMAGEN;
    }
    public void setIMAGEN(String value) {
        IMAGEN=value;
    }
    public String getVIDEO() {
        return VIDEO;
    }
    public void setVIDEO(String value) {
        VIDEO=value;
    }
    public boolean getVENTA_POR_PESO() {
        return VENTA_POR_PESO;
    }
    public void setVENTA_POR_PESO(boolean value) {
        VENTA_POR_PESO=value;
    }
    public boolean getES_PROD_BARRA() {
        return ES_PROD_BARRA;
    }
    public void setES_PROD_BARRA(boolean value) {
        ES_PROD_BARRA=value;
    }
    public String getUNID_INV() {
        return UNID_INV;
    }
    public void setUNID_INV(String value) {
        UNID_INV=value;
    }
    public boolean getVENTA_POR_PAQUETE() {
        return VENTA_POR_PAQUETE;
    }
    public void setVENTA_POR_PAQUETE(boolean value) {
        VENTA_POR_PAQUETE=value;
    }
    public boolean getVENTA_POR_FACTOR_CONV() {
        return VENTA_POR_FACTOR_CONV;
    }
    public void setVENTA_POR_FACTOR_CONV(boolean value) {
        VENTA_POR_FACTOR_CONV=value;
    }
    public boolean getES_SERIALIZADO() {
        return ES_SERIALIZADO;
    }
    public void setES_SERIALIZADO(boolean value) {
        ES_SERIALIZADO=value;
    }
    public int getPARAM_CADUCIDAD() {
        return PARAM_CADUCIDAD;
    }
    public void setPARAM_CADUCIDAD(int value) {
        PARAM_CADUCIDAD=value;
    }
    public String getPRODUCTO_PADRE() {
        return PRODUCTO_PADRE;
    }
    public void setPRODUCTO_PADRE(String value) {
        PRODUCTO_PADRE=value;
    }
    public double getFACTOR_PADRE() {
        return FACTOR_PADRE;
    }
    public void setFACTOR_PADRE(double value) {
        FACTOR_PADRE=value;
    }
    public boolean getTIENE_INV() {
        return TIENE_INV;
    }
    public void setTIENE_INV(boolean value) {
        TIENE_INV=value;
    }
    public boolean getTIENE_VINETA_O_TUBO() {
        return TIENE_VINETA_O_TUBO;
    }
    public void setTIENE_VINETA_O_TUBO(boolean value) {
        TIENE_VINETA_O_TUBO=value;
    }
    public double getPRECIO_VINETA_O_TUBO() {
        return PRECIO_VINETA_O_TUBO;
    }
    public void setPRECIO_VINETA_O_TUBO(double value) {
        PRECIO_VINETA_O_TUBO=value;
    }
    public boolean getES_VENDIBLE() {
        return ES_VENDIBLE;
    }
    public void setES_VENDIBLE(boolean value) {
        ES_VENDIBLE=value;
    }
    public double getUNIGRASAP() {
        return UNIGRASAP;
    }
    public void setUNIGRASAP(double value) {
        UNIGRASAP=value;
    }
    public String getUM_SALIDA() {
        return UM_SALIDA;
    }
    public void setUM_SALIDA(String value) {
        UM_SALIDA=value;
    }
    public int getActivo() {
        return Activo;
    }
    public void setActivo(int value) {
        Activo=value;
    }

}

