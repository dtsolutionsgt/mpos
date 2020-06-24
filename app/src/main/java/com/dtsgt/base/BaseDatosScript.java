package com.dtsgt.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.mpos.R;

public class BaseDatosScript {

    private Context vcontext;

    public BaseDatosScript(Context context) {
        vcontext=context;
    }

    public int scriptDatabase(SQLiteDatabase database) {
        String vSQL;

        try {

            vSQL="CREATE TABLE [PARAMS] ("+
                    "ID integer NOT NULL primary key,"+
                    "EMPID     INTEGER  NOT NULL,"+
                    "DBVER     INTEGER  NOT NULL,"+
                    "PARAM1    INTEGER  NOT NULL,"+  //
                    "PARAM2    INTEGER  NOT NULL,"+
                    "PRN       TEXT     NOT NULL,"+
                    "PRNPARAM  TEXT     NOT NULL,"+
                    "PRNSERIE  TEXT     NOT NULL,"+
                    "LIC       TEXT     NOT NULL,"+
                    "LICPARAM  TEXT     NOT NULL,"+
                    "URL       TEXT     NOT NULL,"+
                    "SUCURSAL  INTEGER     NOT NULL,"+
                    "RUTA      INTEGER     NOT NULL);";
            database.execSQL(vSQL);

            vSQL="CREATE TABLE [FINDIA] ("+
                    "ID integer NOT NULL primary key,"+
                    "COREL INTEGER  NOT NULL,"+
                    "VAL1  INTEGER  NOT NULL,"+  // Día del cierre
                    "VAL2  INTEGER  NOT NULL,"+	 // Comunicacion
                    "VAL3  INTEGER  NOT NULL,"+	 // Impresión Depósito
                    "VAL4  INTEGER  NOT NULL,"+	 // Depósito
                    "VAL5  INTEGER  NOT NULL,"+	 // Devolución bodega y canastas
                    "VAL6  INTEGER  NOT NULL,"+  // Generación Cierre Z
                    "VAL7  INTEGER  NOT NULL,"+  // Impresión de Cierre Z
                    "VAL8  REAL  NOT NULL);";    // GrandTotal
            database.execSQL(vSQL);

            vSQL="CREATE TABLE [LIC_CLIENTE] ("+
                    "ID      TEXT NOT NULL primary key,"+
                    "LTIPO   TEXT NOT NULL,"+
                    "IDKEY   TEXT NOT NULL,"+
                    "NOMBRE  TEXT NOT NULL,"+
                    "BINKEY  TEXT NOT NULL);";
            database.execSQL(vSQL);

            vSQL="CREATE TABLE [P_PARAMEXT] ("+
                    "ID integer NOT NULL primary key,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "VALOR  TEXT, " +
                    "RUTA INT);";
            database.execSQL(vSQL);


            if (scriptTablasD(database)==0) {return 0;}

            if (scriptTablasP(database)==0) {return 0;}

            if (scriptTablasT(database)==0) {return 0;}

            return 1;
        } catch (SQLiteException e) {
            msgbox(e.getMessage());
            return 0;
        }

    }

    private int scriptTablasP(SQLiteDatabase database) {
        String sql;

        try {

            sql="CREATE TABLE [P_EMPRESA] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "COL_IMP INTEGER NOT NULL,"+
                    "LOGO TEXT NOT NULL,"+
                    "RAZON_SOCIAL TEXT NOT NULL,"+
                    "IDENTIFICACION_TRIBUTARIA TEXT NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "COD_PAIS TEXT NOT NULL,"+
                    "NOMBRE_CONTACTO TEXT NOT NULL,"+
                    "APELLIDO_CONTACTO TEXT NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "CORREO TEXT NOT NULL,"+
                    "CODIGO_ACTIVACION TEXT NOT NULL,"+
                    "COD_CANT_EMP INTEGER NOT NULL,"+
                    "CANTIDAD_PUNTOS_VENTA INTEGER NOT NULL,"+
                    "CLAVE TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_BANCO] ("+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[CUENTA] TEXT NOT NULL,"+
                    "[EMPRESA] INTEGER NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "[CODIGO_BANCO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_BANCO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_BANCO_idx1 ON P_BANCO(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_CLIENTE] ("+
                    "CODIGO_CLIENTE INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "BLOQUEADO INTEGER NOT NULL,"+
                    "NIVELPRECIO INTEGER NOT NULL,"+
                    "MEDIAPAGO INTEGER NOT NULL,"+
                    "LIMITECREDITO REAL NOT NULL,"+
                    "DIACREDITO INTEGER NOT NULL,"+
                    "DESCUENTO INTEGER NOT NULL,"+
                    "BONIFICACION INTEGER NOT NULL,"+
                    "ULTVISITA INTEGER NOT NULL,"+
                    "IMPSPEC REAL NOT NULL,"+
                    "NIT TEXT NOT NULL,"+
                    "EMAIL TEXT NOT NULL,"+
                    "ESERVICE TEXT NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "COORX REAL NOT NULL,"+
                    "COORY REAL NOT NULL,"+
                    "BODEGA TEXT NOT NULL,"+
                    "COD_PAIS TEXT NOT NULL,"+
                    "CODBARRA TEXT NOT NULL,"+
                    "PERCEPCION REAL NOT NULL,"+
                    "TIPO_CONTRIBUYENTE TEXT NOT NULL,"+
                    "IMAGEN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CLIENTE])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_CLIENTE_idx1 ON P_CLIENTE(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_IMPUESTO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[VALOR]  REAL NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "[CODIGO_IMPUESTO] INTEGER NOT NULL,"+
                    "PRIMARY  KEY ([CODIGO_IMPUESTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_NIVELPRECIO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 202005201 Esta tabla se había quitado del Script pero es necesaria para hacer los pagos de caja
            sql="CREATE TABLE [P_CONCEPTOPAGO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_LINEA] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "MARCA TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "CODIGO_LINEA INTEGER NOT NULL,"+
                    "IMAGEN TEXT,"+
                    "PRIMARY KEY ([CODIGO_LINEA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_LINEA_idx1 ON P_LINEA(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODCOMBO] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "PRODUCTO TEXT NOT NULL,"+
                    "TIPO TEXT NOT NULL,"+
                    "CANTMIN REAL NOT NULL,"+
                    "CANTTOT REAL NOT NULL,"+
                    "CODIGO_COMBO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_COMBO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODPRECIO] ("+
                    "CODIGO_PRECIO INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "NIVEL INTEGER NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "UNIDADMEDIDA TEXT NOT NULL"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_PRODPRECIO_idx1 ON P_PRODPRECIO(CODIGO_PRODUCTO)";
            database.execSQL(sql);
            sql="CREATE INDEX P_PRODPRECIO_idx2 ON P_PRODPRECIO(NIVEL)";
            database.execSQL(sql);

            //#CKFK 20200516 Modifiqué la llave primaria de la tabla P_PRODPRECIO
            // por un código único "PRIMARY KEY ([RESOL],[SERIE],[CORELINI])"+
            sql="CREATE UNIQUE INDEX IX_P_PRODPRECIO "+
                    " on P_PRODPRECIO ([CODIGO_PRODUCTO],[EMPRESA],[UNIDADMEDIDA],[NIVEL])";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODUCTO] ("+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "CODIGO_TIPO TEXT NOT NULL,"+
                    "LINEA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "MARCA TEXT NOT NULL,"+
                    "CODBARRA TEXT NOT NULL,"+
                    "DESCCORTA TEXT NOT NULL,"+
                    "DESCLARGA TEXT NOT NULL,"+
                    "COSTO REAL NOT NULL,"+
                    "FACTORCONV REAL NOT NULL,"+
                    "UNIDBAS TEXT NOT NULL,"+
                    "UNIDMED TEXT NOT NULL,"+
                    "UNIMEDFACT REAL NOT NULL,"+
                    "UNIGRA TEXT NOT NULL,"+
                    "UNIGRAFACT REAL NOT NULL,"+
                    "DESCUENTO INTEGER NOT NULL,"+
                    "BONIFICACION INTEGER NOT NULL,"+
                    "IMP1 REAL NOT NULL,"+
                    "IMP2 REAL NOT NULL,"+
                    "IMP3 REAL NOT NULL,"+
                    "VENCOMP TEXT NOT NULL,"+
                    "DEVOL INTEGER NOT NULL,"+
                    "OFRECER INTEGER NOT NULL,"+
                    "RENTAB INTEGER NOT NULL,"+
                    "DESCMAX INTEGER NOT NULL,"+
                    "IVA TEXT NOT NULL,"+
                    "CODBARRA2 TEXT NOT NULL,"+
                    "CBCONV INTEGER NOT NULL,"+
                    "BODEGA TEXT NOT NULL,"+
                    "SUBBODEGA TEXT NOT NULL,"+
                    "PESO_PROMEDIO REAL NOT NULL,"+
                    "MODIF_PRECIO INTEGER NOT NULL,"+
                    "VIDEO TEXT NOT NULL,"+
                    "VENTA_POR_PESO INTEGER NOT NULL,"+
                    "ES_PROD_BARRA INTEGER NOT NULL,"+
                    "UNID_INV TEXT NOT NULL,"+
                    "VENTA_POR_PAQUETE INTEGER NOT NULL,"+
                    "VENTA_POR_FACTOR_CONV INTEGER NOT NULL,"+
                    "ES_SERIALIZADO INTEGER NOT NULL,"+
                    "PARAM_CADUCIDAD INTEGER NOT NULL,"+
                    "PRODUCTO_PADRE INTEGER NOT NULL,"+
                    "FACTOR_PADRE REAL NOT NULL,"+
                    "TIENE_INV INTEGER NOT NULL,"+
                    "TIENE_VINETA_O_TUBO INTEGER NOT NULL,"+
                    "PRECIO_VINETA_O_TUBO REAL NOT NULL,"+
                    "ES_VENDIBLE INTEGER NOT NULL,"+
                    "UNIGRASAP REAL NOT NULL,"+
                    "UM_SALIDA TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "IMAGEN TEXT NOT NULL,"+
                    "TIEMPO_PREPARACION REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_PRODUCTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_PRODUCTO_idx1 ON P_PRODUCTO(LINEA)";
            database.execSQL(sql);
            sql="CREATE INDEX P_PRODUCTO_idx2 ON P_PRODUCTO(CODBARRA)";
            database.execSQL(sql);
            sql="CREATE INDEX P_PRODUCTO_idx3 ON P_PRODUCTO(DESCCORTA)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_DESCUENTO] ("+
                    "[CLIENTE] TEXT NOT NULL,"+
                    "[CTIPO] INTEGER NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[PTIPO] INTEGER NOT NULL,"+
                    "[TIPORUTA] INTEGER NOT NULL,"+
                    "[RANGOINI] REAL NOT NULL,"+
                    "[RANGOFIN] REAL NOT NULL,"+
                    "[DESCTIPO] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[GLOBDESC] TEXT NOT NULL,"+
                    "[PORCANT] TEXT NOT NULL,"+
                    "[FECHAINI] INTEGER NOT NULL,"+
                    "[FECHAFIN] INTEGER NOT NULL,"+
                    "[CODDESC] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "[CODIGO_DESCUENTO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_DESCUENTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_DESCUENTO_idx1 ON P_DESCUENTO(CLIENTE)";
            database.execSQL(sql);
            sql="CREATE INDEX P_DESCUENTO_idx2 ON P_DESCUENTO(CTIPO)";
            database.execSQL(sql);
            sql="CREATE INDEX P_DESCUENTO_idx3 ON P_DESCUENTO(FECHAINI)";
            database.execSQL(sql);
            sql="CREATE INDEX P_DESCUENTO_idx4 ON P_DESCUENTO(FECHAFIN)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_SUCURSAL] ("+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_NIVEL_PRECIO INTEGER NOT NULL,"+
                    "DESCRIPCION TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "CORREO TEXT NOT NULL,"+
                    "NIT TEXT NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "FEL_CODIGO_ESTABLECIMIENTO  TEXT NOT NULL,"+
                    "FEL_USUARIO_FIRMA TEXT NOT NULL,"+
                    "FEL_LLAVE_CERTIFICACION TEXT NOT NULL,"+
                    "FEL_LLAVE_FIRMA TEXT NOT NULL,"+
                    "FEL_AFILIACION_IVA TEXT NOT NULL,"+
                    "FEL_USUARIO_CERTIFICACION TEXT NOT NULL,"+
                    "CODIGO_POSTAL TEXT NOT NULL,"+
                    "CODIGO_ESCENARIO_ISR INTEGER NOT NULL,"+
                    "CODIGO_ESCENARIO_IVA INTEGER NOT NULL,"+
                    "CODIGO_MUNICIPIO TEXT NOT NULL,"+
                    "CODIGO_PROVEEDOR INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_SUCURSAL])"+
                    ");";
             database.execSQL(sql);

            sql="CREATE TABLE [P_STOCK] ("+
                    "[CODIGO] INT NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[CANTM] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[PLIBRA] REAL NOT NULL,"+
                    "[LOTE] TEXT NOT NULL,"+
                    "[DOCUMENTO] TEXT NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[ANULADO] INTEGER NOT NULL,"+
                    "[CENTRO] TEXT NOT NULL,"+
                    "[STATUS] TEXT NOT NULL,"+
                    "[ENVIADO] INTEGER NOT NULL,"+
                    "[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
                    "[COREL_D_MOV] TEXT NOT NULL,"+
                    "[UNIDADMEDIDA] TEXT  NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[LOTE],[DOCUMENTO],[STATUS],[UNIDADMEDIDA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_MEDIAPAGO] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "NIVEL INTEGER NOT NULL,"+
                    "PORCOBRO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_MEDIAPAGO_idx1 ON P_MEDIAPAGO(NIVEL)";
            database.execSQL(sql);
            sql="CREATE INDEX P_MEDIAPAGO_idx2 ON P_MEDIAPAGO(PORCOBRO)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_RUTA] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "SUCURSAL TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CODIGO_RUTA INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_RUTA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_COREL] ("+
                    "CODIGO_COREL INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "RESOL TEXT NOT NULL,"+
                    "SERIE TEXT NOT NULL,"+
                    "CORELINI INTEGER NOT NULL,"+
                    "CORELFIN INTEGER NOT NULL,"+
                    "CORELULT INTEGER NOT NULL,"+
                    "FECHARES INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "ACTIVA INTEGER NOT NULL,"+
                    "HANDHELD TEXT,"+
                    "FECHAVIG INTEGER NOT NULL,"+
                    "RESGUARDO INTEGER NOT NULL,"+
                    "VALOR1 INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_COREL])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 20200516 Modifiqué la llave primaria de la tabla P_COREL
            // por un código único "PRIMARY KEY ([RESOL],[SERIE],[CORELINI])"+
            sql="CREATE UNIQUE INDEX IX_P_COREL "+
                " on P_COREL ([RESOL],[SERIE],[CORELINI])";
            database.execSQL(sql);

            sql="CREATE TABLE [P_ARCHIVOCONF] ("+
                    "CODIGO_ARCHIVOCONF INTEGER NOT NULL,"+
                    "[RUTA] TEXT NOT NULL,"+
                    "[TIPO_HH] TEXT NOT NULL,"+
                    "[IDIOMA] TEXT  NOT NULL,"+
                    "[TIPO_IMPRESORA] TEXT  NOT NULL,"+
                    "[SERIAL_HH] TEXT  NOT NULL,"+
                    "[MODIF_PESO] TEXT NOT NULL,"+
                    "[PUERTO_IMPRESION] TEXT NOT NULL,"+
                    "[LBS_O_KGS] TEXT NOT NULL,"+
                    "[NOTA_CREDITO] INTEGER  NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ARCHIVOCONF])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_ENCABEZADO_REPORTESHH] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[TEXTO] TEXT NOT NULL,"+
                    "[SUCURSAL] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_ENCABEZADO_REPORTESHH_idx1 ON P_ENCABEZADO_REPORTESHH(SUCURSAL)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_BONIF] ("+
                    "[CODDESC] INTEGER NOT NULL,"+
                    "[CLIENTE] TEXT NOT NULL,"+
                    "[CTIPO] INTEGER NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[PTIPO] INTEGER NOT NULL,"+
                    "[TIPORUTA] INTEGER NOT NULL,"+
                    "[TIPOBON] TEXT NOT NULL,"+
                    "[RANGOINI] REAL NOT NULL,"+
                    "[RANGOFIN] REAL NOT NULL,"+
                    "[TIPOLISTA] INTEGER NOT NULL,"+
                    "[TIPOCANT] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[LISTA] TEXT NOT NULL,"+
                    "[CANTEXACT] TEXT NOT NULL,"+
                    "[GLOBBON] TEXT NOT NULL,"+
                    "[PORCANT] TEXT NOT NULL,"+
                    "[FECHAINI] INTEGER NOT NULL,"+
                    "[FECHAFIN] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[EMP] TEXT NOT NULL,"+
                    "[UMPRODUCTO] TEXT NOT NULL," +
                    "[UMBONIFICACION] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODDESC])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_FACTORCONV] ("+
                    "[CODIGO_FACTORCONV] INTEGER NOT NULL,"+
                    "[PRODUCTO] INTEGER NOT NULL,"+
                    "[UNIDADSUPERIOR] TEXT NOT NULL,"+
                    "[FACTORCONVERSION] REAL NOT NULL,"+
                    "[UNIDADMINIMA] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_FACTORCONV])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 20200517 Cambié la llave compuesta por una llave simple y creé un indice unico
            sql="CREATE UNIQUE INDEX IX_P_FACTORCONV "+
                    " on P_FACTORCONV ([PRODUCTO],[UNIDADSUPERIOR],[UNIDADMINIMA])";
            database.execSQL(sql);

            sql="CREATE TABLE [P_MONEDA] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "SYMBOLO TEXT NOT NULL,"+
                    "CAMBIO REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            //'#EJC20200523-COMBOMENUFIX'
            sql="CREATE TABLE [P_PRODMENU] ("+
                    "CODIGO_MENU INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MENU])"+
                    ");";
            database.execSQL(sql);

            //'#EJC20200523-COMBOMENUFIX'
            sql="CREATE TABLE [P_PRODMENUOPC] ("+
                    "CODIGO_MENU_OPCION INTEGER NOT NULL,"+
                    "CODIGO_MENU INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "ORDEN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MENU_OPCION])"+
                    ");";
            database.execSQL(sql);

            //'#EJC20200523-COMBOMENUFIX'
            sql="CREATE TABLE [P_PRODMENUOPC_DET] ("+
                    "CODIGO_MENUOPC_DET INTEGER NOT NULL,"+
                    "CODIGO_MENU_OPCION INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MENUOPC_DET])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PROVEEDOR] ("+
                    "CODIGO_PROVEEDOR INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_PROVEEDOR])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [VENDEDORES] ("+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CLAVE TEXT NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
                    "NIVEL INTEGER NOT NULL,"+
                    "NIVELPRECIO REAL NOT NULL,"+
                    "BODEGA TEXT NOT NULL,"+
                    "SUBBODEGA TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "IMAGEN TEXT,"+
                    "FECHA_INICIO_LABORES INTEGER,"+
                    "FECHA_FIN_LABORES INTEGER, "+
                    "PRIMARY KEY ([CODIGO],[RUTA])"+
                    ");";
            database.execSQL(sql);

            //#EJC20200524: CODIGO_CAJACIERRE Change to Text
            sql="CREATE TABLE [P_CAJACIERRE] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "SUCURSAL INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "COREL INTEGER NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "VENDEDOR INTEGER NOT NULL,"+
                    "CODPAGO INTEGER NOT NULL,"+
                    "FONDOCAJA REAL NOT NULL,"+
                    "MONTOINI REAL NOT NULL,"+
                    "MONTOFIN REAL NOT NULL,"+
                    "MONTODIF REAL NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "CODIGO_CAJACIERRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CAJACIERRE])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE UNIQUE INDEX IX_P_CAJACIERRE "+
                    " on P_CAJACIERRE ([EMPRESA],[SUCURSAL],[RUTA],[COREL],[CODPAGO])";
            database.execSQL(sql);

            //#CKFK 20200516 Modifiqué la llave primaria de la tabla P_CAJACIERRE
            // por un correlativo único "PRIMARY KEY ([EMRESA],[SUCURSAL],[RUTA],[COREL],[CODPAGO])"+
            sql="CREATE TABLE [P_CAJAPAGOS] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "SUCURSAL INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "COREL INTEGER NOT NULL,"+
                    "ITEM INTEGER NOT NULL,"+
                    "ANULADO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TIPO INTEGER NOT NULL,"+
                    "PROVEEDOR INTEGER NOT NULL,"+
                    "MONTO REAL NOT NULL,"+
                    "NODOCUMENTO TEXT NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "OBSERVACION TEXT NOT NULL,"+
                    "VENDEDOR INTEGER NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "CODIGO_CAJAPAGOS TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CAJAPAGOS])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 20200516 Modifiqué la llave primaria de la tabla P_CAJAPAGOS
            // por un correlativo único "PRIMARY KEY ([EMPRESA],[SUCURSAL],[RUTA],[COREL],[ITEM])"+
            sql="CREATE UNIQUE INDEX IX_P_CAJAPAGOS "+
                    " on P_CAJAPAGOS ([EMPRESA],[SUCURSAL],[RUTA],[COREL],[ITEM])";
            database.execSQL(sql);

            sql="CREATE TABLE [P_CAJAREPORTE] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "SUCURSAL INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "COREL INTEGER NOT NULL,"+
                    "LINEA INTEGER NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "CODIGO_CAJAREPORTE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CAJAREPORTE])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 20200516 Modifiqué la llave primaria de la tabla P_CAJAREPORTE
            // por un correlativo único "PRIMARY KEY ([EMRESA],[SUCURSAL],[RUTA],[COREL],[LINEA])"
            sql="CREATE UNIQUE INDEX IX_P_CAJAREPORTE "+
                    " on P_CAJAREPORTE ([EMPRESA],[SUCURSAL],[RUTA],[COREL],[LINEA])";
            database.execSQL(sql);

            sql="CREATE TABLE [FPRINT] ("+
                    "EMPRESA TEXT NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "DEDO INTEGER NOT NULL,"+
                    "IMAGE BLOB,"+
                    "PRIMARY KEY ([EMPRESA],[CODIGO],[DEDO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_USGRUPO] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CUENTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_USGRUPOOPC] ("+
                    "GRUPO INTEGER NOT NULL,"+
                    "OPCION INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GRUPO],[OPCION])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_USOPCION] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "MENUGROUP TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_MOTIVO_AJUSTE]("+
                    "CODIGO_MOTIVO_AJUSTE INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT,"+
                    "ACTIVO BOOLEAN NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MOTIVO_AJUSTE])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_DEPARTAMENTO] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "CODIGO_AREA TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_MUNICIPIO] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "CODIGO_DEPARTAMENTO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            return 1;

        } catch (SQLiteException e) {
            msgbox(e.getMessage());
            return 0;
        }
    }

    private int scriptTablasD(SQLiteDatabase database) {
        String sql;

        try {

            sql="CREATE TABLE [D_FACTURA] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "ANULADO BOOLEAN NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "VENDEDOR INTEGER NOT NULL,"+
                    "CLIENTE INTEGER NOT NULL,"+
                    "KILOMETRAJE REAL NOT NULL,"+
                    "FECHAENTR INTEGER NOT NULL,"+
                    "FACTLINK TEXT NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "DESMONTO REAL NOT NULL,"+
                    "IMPMONTO REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "BANDERA TEXT NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "CALCOBJ BOOLEAN NOT NULL,"+
                    "SERIE TEXT NOT NULL,"+
                    "CORELATIVO INTEGER NOT NULL,"+
                    "IMPRES INTEGER NOT NULL,"+
                    "ADD1 TEXT NOT NULL,"+
                    "ADD2 TEXT NOT NULL,"+
                    "ADD3 TEXT NOT NULL,"+
                    "DEPOS BOOLEAN NOT NULL,"+
                    "PEDCOREL TEXT NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "ASIGNACION TEXT NOT NULL,"+
                    "SUPERVISOR TEXT NOT NULL,"+
                    "AYUDANTE TEXT NOT NULL,"+
                    "VEHICULO TEXT NOT NULL,"+
                    "CODIGOLIQUIDACION INTEGER NOT NULL,"+
                    "RAZON_ANULACION TEXT NOT NULL,"+
                    "FEELSERIE TEXT NOT NULL,"+
                    "FEELNUMERO TEXT NOT NULL,"+
                    "FEELUUID TEXT NOT NULL,"+
                    "FEELFECHAPROCESADO INTEGER NOT NULL,"+
                    "FEELCONTINGENCIA TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [D_FACTURAD] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "UMPESO TEXT NOT NULL,"+
                    "ANULADO BOOLEAN NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "IMP REAL NOT NULL,"+
                    "DES REAL NOT NULL,"+
                    "DESMON REAL NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "PRECIODOC REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "VAL1 REAL NOT NULL,"+
                    "VAL2 TEXT NOT NULL,"+
                    "UMVENTA TEXT NOT NULL,"+
                    "FACTOR REAL NOT NULL,"+
                    "UMSTOCK TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL],[PRODUCTO],[VAL2])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [D_FACTURAP] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "ITEM INTEGER NOT NULL,"+
                    "ANULADO BOOLEAN NOT NULL,"+
                    "CODPAGO INTEGER NOT NULL,"+
                    "TIPO TEXT NOT NULL,"+
                    "VALOR REAL NOT NULL,"+
                    "DESC1 TEXT NOT NULL,"+
                    "DESC2 TEXT NOT NULL,"+
                    "DESC3 TEXT NOT NULL,"+
                    "DEPOS BOOLEAN NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL],[ITEM])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [D_FACTURAF] ("+
                    "[COREL] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[NIT] TEXT NOT NULL,"+
                    "[DIRECCION] TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [D_FACTURAS] ("+
                    "COREL TEXT NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "PRODUCTO TEXT NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "UMSTOCK TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL],[ID],[PRODUCTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [D_BONIF] ("+
                    "[COREL] TEXT NOT NULL,"+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[ANULADO] BOOLEAN NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[RUTA] TEXT NOT NULL,"+
                    "[CLIENTE] TEXT NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[VENPED] TEXT NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[COSTO] REAL NOT NULL,"+
                    "[TOTAL] REAL NOT NULL,"+
                    "[STATCOM] TEXT NOT NULL,"+
                    "[UMVENTA] TEXT NOT NULL,"+
                    "[UMSTOCK] TEXT NOT NULL,"+
                    "[UMPESO] TEXT NOT NULL,"+
                    "[FACTOR] REAL NOT NULL,"+
                    "PRIMARY KEY ([COREL],[ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [D_FACT_LOG] ("+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[SERIE] TEXT NOT NULL,"+
                    "[COREL] INTEGER NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[RUTA] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ITEM])"+
                    ");";
            database.execSQL(sql);


            // ****************

            sql="CREATE TABLE [D_MOV] ("+
                    "[COREL] TEXT NOT NULL,"+
                    "[RUTA] INTEGER NOT NULL,"+
                    "[ANULADO] INTEGER NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[USUARIO] INTEGER NOT NULL,"+
                    "[REFERENCIA] TEXT NOT NULL,"+
                    "[STATCOM] TEXT NOT NULL,"+
                    "[IMPRES] INTEGER NOT NULL,"+
                    "[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
                    "[CODIGO_PROVEEDOR] INTEGER NOT NULL,"+
                    "[TOTAL] REAL NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX D_MOV_idx1 ON D_MOV(ANULADO)";
            database.execSQL(sql);
            sql="CREATE INDEX D_MOV_idx2 ON D_MOV(TIPO)";
            database.execSQL(sql);
            sql="CREATE INDEX D_MOV_idx3 ON D_MOV(STATCOM)";
            database.execSQL(sql);

            sql="CREATE TABLE [D_MOVD] ("+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "CANTM REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "PESOM REAL NOT NULL,"+
                    "LOTE TEXT NOT NULL,"+
                    "CODIGOLIQUIDACION INTEGER NOT NULL,"+
                    "UNIDADMEDIDA TEXT NOT NULL,"+
                    "CORELDET INTEGER NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "MOTIVO_AJUSTE INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CORELDET])"+
                    ");";
            database.execSQL(sql);

            //#CKFK 20200526 Modifiqué la llave primaria de la tabla D_MOVD
            // por un código único "PRIMARY KEY ([COREL],[PRODUCTO],[LOTE])"+


            // JP 20200615 - UIDX no aplica por razon de ajustes de inventario donde ouede ingresar mismo codigo con diferente motivo de ajuste
            //sql="CREATE UNIQUE INDEX IX_D_MOVD on D_MOVD ([COREL],[PRODUCTO],[LOTE])";
            //database.execSQL(sql);

            sql="CREATE TABLE [D_DEPOS] ("+
                    "[COREL] TEXT NOT NULL,"+
                    "[EMPRESA] INTEGER NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[RUTA] INTEGER NOT NULL,"+
                    "[BANCO] INTEGER NOT NULL,"+
                    "[CUENTA] TEXT NOT NULL,"+
                    "[REFERENCIA] TEXT NOT NULL,"+
                    "[TOTAL] REAL NOT NULL,"+
                    "[TOTEFEC] REAL NOT NULL,"+
                    "[TOTCHEQ] REAL NOT NULL,"+
                    "[NUMCHEQ] INTEGER NOT NULL,"+
                    "[IMPRES] INTEGER NOT NULL,"+
                    "[STATCOM] TEXT NOT NULL,"+
                    "[ANULADO] BOOLEAN NOT NULL,"+
                    "[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX D_DEPOS_idx1 ON D_DEPOS(STATCOM)";
            database.execSQL(sql);
            sql="CREATE INDEX D_DEPOS_idx2 ON D_DEPOS(ANULADO)";
            database.execSQL(sql);

            sql="CREATE TABLE [D_DEPOSD] ("+
                    "[COREL_DET] INTEGER NOT NULL,"+
                    "[COREL] TEXT NOT NULL,"+
                    "[DOCCOREL] TEXT NOT NULL,"+
                    "[TIPODOC] TEXT NOT NULL,"+
                    "[CODPAGO] INTEGER NOT NULL,"+
                    "[CHEQUE] BOOLEAN NOT NULL,"+
                    "[MONTO] REAL NOT NULL,"+
                    "[BANCO]   NOT NULL,"+
                    "[NUMERO] TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL_DET])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE UNIQUE INDEX IX_D_DEPOSD "+
                    " on D_DEPOSD ([COREL],[DOCCOREL],[CODPAGO])";
            database.execSQL(sql);

            sql="CREATE TABLE [D_DEPOSB] ("+
                    "[COREL_DET] INTEGER NOT NULL,"+
                    "[COREL] TEXT NOT NULL,"+
                    "[DENOMINACION] REAL NOT NULL,"+
                    "[CANTIDAD] INTEGER NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[MONEDA] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL_DET])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE UNIQUE INDEX IX_D_DEPOSB "+
                    " on D_DEPOSB ([COREL],[DENOMINACION],[TIPO],[MONEDA])";
            database.execSQL(sql);

            /*

            sql="CREATE TABLE [D_PEDIDO] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[KILOMETRAJE] REAL NOT NULL,"+
					"[FECHAENTR] INTEGER NOT NULL,"+
					"[DIRENTREGA] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[DESMONTO] REAL NOT NULL,"+
					"[IMPMONTO] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[BANDERA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[CALCOBJ] TEXT NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[ADD1] TEXT NOT NULL,"+
					"[ADD2] TEXT NOT NULL,"+
					"[ADD3] TEXT NOT NULL,"+
					"[STATPROC] TEXT NOT NULL,"+
					"[RECHAZADO] INTEGER NOT NULL,"+
					"[RAZON_RECHAZADO] TEXT NOT NULL,"+
					"[INFORMADO] INTEGER NOT NULL,"+
					"[SUCURSAL] TEXT NOT NULL,"+
					"[ID_DESPACHO] INTEGER NOT NULL,"+
					"[ID_FACTURACION] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_PEDIDO_idx1 ON D_PEDIDO(COREL)";
			database.execSQL(sql);
			sql="CREATE INDEX D_PEDIDO_idx2 ON D_PEDIDO(ANULADO)";
			database.execSQL(sql);
			sql="CREATE INDEX D_PEDIDO_idx3 ON D_PEDIDO(FECHA)";
			database.execSQL(sql);
			sql="CREATE INDEX D_PEDIDO_idx4 ON D_PEDIDO(STATCOM)";
			database.execSQL(sql);
			sql="CREATE INDEX D_PEDIDO_idx5 ON D_PEDIDO(CALCOBJ)";
			database.execSQL(sql);

			sql="CREATE TABLE [D_PEDIDOD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"[IMP] REAL NOT NULL,"+
					"[DES] REAL NOT NULL,"+
					"[DESMON] REAL NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[PRECIODOC] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[VAL1] REAL NOT NULL,"+
					"[VAL2] TEXT NOT NULL,"+
					"[CANTPROC] REAL NOT NULL,"+
					"[UMVENTA] TEXT NOT NULL,"+
					"[FACTOR] REAL NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_PEDIDOD_idx1 ON D_PEDIDOD(COREL)";
			database.execSQL(sql);
			sql="CREATE INDEX D_PEDIDOD_idx2 ON D_PEDIDOD(ANULADO)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_CxC] ("+
					"[COREL] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[TIPO] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[SUPERVISOR] TEXT NOT NULL,"+
					"[AYUDANTE] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[ESTADO] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_CxC_idx1 ON D_CxC(ANULADO)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_CxCD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ITEM] INTEGER NOT NULL,"+
					"[CODIGO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[CODDEV] TEXT NOT NULL,"+
					"[ESTADO] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"[PRECLISTA] REAL NOT NULL,"+
					"[REF] TEXT NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[FECHA_CAD] INTEGER NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[UMVENTA] TEXT NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+
					"[FACTOR] REAL NOT NULL,"+
					"PRIMARY KEY ([COREL],[ITEM])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_CxCD_idx1 ON D_CxCD(COREL)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_DEVOL] ("+
					"[COREL] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[TIPO] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_DEVOL_idx1 ON D_DEVOL(ANULADO)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_DEVOLD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ITEM] INTEGER NOT NULL,"+
					"[CODIGO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[CODDEV] TEXT NOT NULL,"+
					"[ESTADO] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"[PRECLISTA] REAL NOT NULL,"+
					"[REF] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[ITEM])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_CLINUEVO] ("+
					"[CODIGO] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[NOMBRE] TEXT NOT NULL,"+
					"[NEGOCIO] TEXT NOT NULL,"+
					"[DIRECCION] TEXT NOT NULL,"+
					"[TELEFONO] TEXT NOT NULL,"+
					"[NIT] TEXT NOT NULL,"+
					"[TIPONEG] TEXT NOT NULL,"+
					"[NIVPRECIO] INTEGER NOT NULL,"+
					"[DIA1] TEXT NOT NULL,"+
					"[DIA2] TEXT NOT NULL,"+
					"[DIA3] TEXT NOT NULL,"+
					"[DIA4] TEXT NOT NULL,"+
					"[DIA5] TEXT NOT NULL,"+
					"[DIA6] TEXT NOT NULL,"+
					"[DIA7] TEXT NOT NULL,"+
					"[ORDVIS] INTEGER NOT NULL,"+
					"[BAND1] TEXT NOT NULL,"+
					"[BAND2] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([CODIGO],[RUTA])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_CLINUEVO_idx1 ON D_CLINUEVO(STATCOM)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_CLINUEVO_APR] ("+
					"[CODIGO] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[FECHANAC] INTEGER NOT NULL,"+
					"[CUI] TEXT NOT NULL,"+
					"[DepID] TEXT NOT NULL,"+
					"[MuniID] TEXT NOT NULL,"+
					"[Religion] TEXT NOT NULL,"+
					"[Etnico] TEXT NOT NULL,"+
					"[Escolaridad] TEXT NOT NULL,"+
					"[Estado] TEXT NOT NULL,"+
					"[Genero] TEXT NOT NULL,"+
					"[Hijos] INTEGER NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([CODIGO],[RUTA])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_DEPOS] ("+
					"[COREL] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[BANCO] TEXT NOT NULL,"+
					"[CUENTA] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[TOTEFEC] REAL NOT NULL,"+
					"[TOTCHEQ] REAL NOT NULL,"+
					"[NUMCHEQ] INTEGER NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_DEPOS_idx1 ON D_DEPOS(STATCOM)";
			database.execSQL(sql);
			sql="CREATE INDEX D_DEPOS_idx2 ON D_DEPOS(ANULADO)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_DEPOSD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[DOCCOREL] TEXT NOT NULL,"+
					"[ITEM] INTEGER NOT NULL,"+
					"[TIPODOC] TEXT NOT NULL,"+
					"[CODPAGO] INTEGER NOT NULL,"+
					"[CHEQUE] TEXT NOT NULL,"+
					"[MONTO] REAL NOT NULL,"+
					"[BANCO] TEXT NOT NULL,"+
					"[NUMERO] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[DOCCOREL],[ITEM])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_DEPOSB] ("+
					"[COREL] TEXT NOT NULL,"+
					"[DENOMINACION] REAL NOT NULL,"+
					"[CANTIDAD] INTEGER NOT NULL,"+
					"[TIPO] TEXT NOT NULL,"+
					"[MONEDA] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL],[DENOMINACION],[TIPO],[MONEDA])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_FACTURAD_LOTES] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[CANTIDAD] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+
					"[UMVENTA] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[LOTE])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_FACTURA_STOCK] ("+
					"[COREL] TEXT NOT NULL,"+
					"[CODIGO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[CANTM] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[plibra] REAL NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[DOCUMENTO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] INTEGER NOT NULL,"+
					"[CENTRO] TEXT NOT NULL,"+
					"[STATUS] TEXT NOT NULL,"+
					"[ENVIADO] INTEGER NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[COREL_D_MOV] TEXT NOT NULL,"+
					"[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL,"+
					"PRIMARY KEY ([COREL],[CODIGO],[LOTE],[DOCUMENTO],[STATUS],[UNIDADMEDIDA])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_FACTURA_STOCK_idx1 ON D_FACTURA_STOCK(COREL)";
			database.execSQL(sql);

			sql="CREATE TABLE [D_FACTURA_BARRA] ("+
					"[RUTA] TEXT NOT NULL,"+
					"[BARRA] TEXT NOT NULL,"+
					"[CODIGO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[COREL] TEXT NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[DOCUMENTO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] INTEGER NOT NULL,"+
					"[CENTRO] TEXT NOT NULL,"+
					"[STATUS] TEXT NOT NULL,"+
					"[ENVIADO] INTEGER NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[COREL_D_MOV] TEXT NOT NULL,"+
					"[UNIDADMEDIDA] TEXT NOT NULL,"+
					"[DOC_ENTREGA] TEXT NOT NULL,"+
					"PRIMARY KEY ([RUTA],[BARRA],[DOCUMENTO],[STATUS])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_FACTURA_BARRA_idx1 ON D_FACTURA_BARRA(COREL)";
			database.execSQL(sql);




			sql="CREATE TABLE [D_MOVDB] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[BARRA] TEXT NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[BARRA])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_MOVDPALLET] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[BARRAPALLET] TEXT NOT NULL,"+
					"[BARRAPRODUCTO] TEXT NOT NULL,"+
					"[LOTEPRODUCTO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[BARRAPALLET],[BARRAPRODUCTO])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_MOVDCAN] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[CANTM] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[PESOM] REAL NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[BARRA] TEXT NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[PASEANTE] INTEGER NOT NULL,"+
					"[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_REL_PROD_BON] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[BONIFICADO] TEXT NOT NULL,"+
					"[CANTIDAD] REAL NOT NULL,"+
					"[CONSECUTIVO] INTEGER NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[BONIFICADO])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_CLICOORD] ("+
					"[CODIGO]  TEXT NOT NULL,"+
					"[STAMP]   REAL NOT NULL,"+
					"[COORX]   REAL NOT NULL,"+
					"[COORY]   REAL NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([CODIGO],[STAMP])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_REPFINDIA] ("+
					"[RUTA]  TEXT NOT NULL,"+
					"[LINEA] INTEGER NOT NULL,"+
					"[TEXTO] TEXT NOT NULL,"+
					"PRIMARY KEY ([RUTA],[LINEA])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_SOLICINV] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[USUARIO] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_SOLICINV_idx1 ON D_SOLICINV(STATCOM)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_SOLICINVD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[COSTO] REAL NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[UM] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO])"+
					");";
			database.execSQL(sql);


			sql="CREATE TABLE [D_NOTACRED] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[TOTAL]  REAL NOT NULL,"+
					"[FACTURA] TEXT NOT NULL,"+
					"[SERIE] TEXT NOT NULL,"+
					"[CORELATIVO] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[RESOLNC] TEXT NOT NULL,"+
					"[SERIEFACT] TEXT NOT NULL,"+
					"[CORELFACT] INTEGER NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_NOTACREDD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[PRECIO_ORIG] REAL NOT NULL,"+
					"[PRECIO_ACT] REAL NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[POR_PRESO] TEXT NOT NULL,"+
					"[UMVENTA] TEXT NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+
					"[FACTOR] REAL NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_NOTACREDD_idx1 ON D_NOTACREDD(COREL)";
			database.execSQL(sql);


			sql="CREATE TABLE [D_STOCKB_DEV] ("+
					"[BARRA] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CODIGO] TEXT NOT NULL,"+
					"[COREL] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"PRIMARY KEY ([BARRA],[RUTA],[VENDEDOR],[CODIGO],[COREL])"+
					");";
			database.execSQL(sql);

             */

            return 1;
        } catch (SQLiteException e) {
            msgbox(e.getMessage());
            return 0;
        }
    }

    private int scriptTablasT(SQLiteDatabase database) {
        String sql;

        try {

            sql="CREATE TABLE [T_VENTA] ("+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[UM] TEXT NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[UMSTOCK] TEXT NOT NULL,"+
                    "[FACTOR] REAL NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[IMP] REAL NOT NULL,"+
                    "[DES] REAL NOT NULL,"+
                    "[DESMON] REAL NOT NULL,"+
                    "[TOTAL] REAL NOT NULL,"+
                    "[PRECIODOC] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[VAL1] REAL NOT NULL,"+
                    "[VAL2] TEXT NOT NULL,"+
                    "[VAL3] REAL NOT NULL,"+
                    "[VAL4] TEXT NOT NULL,"+
                    "[PERCEP] REAL NOT NULL,"+
                    "PRIMARY KEY ([PRODUCTO],[UM],[EMPRESA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX T_VENTA_idx1 ON T_VENTA(PRODUCTO)";
            database.execSQL(sql);


            sql="CREATE TABLE [T_DESC] ("+
                    "[ID] INTEGER NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[PTIPO] INTEGER NOT NULL,"+
                    "[RANGOINI] REAL NOT NULL,"+
                    "[RANGOFIN] REAL NOT NULL,"+
                    "[DESCTIPO] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[GLOBDESC] TEXT NOT NULL,"+
                    "[PORCANT] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX T_DESC_idx1 ON T_DESC(PRODUCTO)";
            database.execSQL(sql);
            sql="CREATE INDEX T_DESC_idx2 ON T_DESC(PTIPO)";
            database.execSQL(sql);
            sql="CREATE INDEX T_DESC_idx3 ON T_DESC(RANGOINI)";
            database.execSQL(sql);
            sql="CREATE INDEX T_DESC_idx4 ON T_DESC(RANGOFIN)";
            database.execSQL(sql);

            sql="CREATE TABLE [T_BONIF] ("+
                    "[ID] INTEGER NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[PTIPO] INTEGER NOT NULL,"+
                    "[TIPOBON] TEXT NOT NULL,"+
                    "[RANGOINI] REAL NOT NULL,"+
                    "[RANGOFIN] REAL NOT NULL,"+
                    "[TIPOLISTA] INTEGER NOT NULL,"+
                    "[TIPOCANT] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[LISTA] TEXT NOT NULL,"+
                    "[CANTEXACT] TEXT NOT NULL,"+
                    "[GLOBBON] TEXT NOT NULL,"+
                    "[PORCANT] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[EMP] TEXT NOT NULL,"+
                    "[UMPRODUCTO] TEXT NOT NULL,"+
                    "[UMBONIFICACION] TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [T_CxCD] ("+
                    "[Item] INTEGER NOT NULL,"+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[CODDEV] TEXT NOT NULL,"+
                    "[TOTAL] REAL NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[PRECLISTA] REAL NOT NULL,"+
                    "[REF] TEXT NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[FECHA_CAD] INTEGER NOT NULL,"+
                    "[LOTE] TEXT NOT NULL,"+
                    "[UMVENTA] TEXT NOT NULL,"+
                    "[UMSTOCK] TEXT NOT NULL,"+
                    "[UMPESO] TEXT NOT NULL,"+
                    "[FACTOR] REAL NOT NULL,"+
                    "[POR_PESO] TEXT NOT NULL,"+
                    "[TIENE_LOTE] INTEGER,"+
                    "PRIMARY KEY ([Item])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX T_CxCD_idx1 ON T_CxCD(CODIGO)";
            database.execSQL(sql);
            sql="CREATE INDEX T_CxCD_idx2 ON T_CxCD(CANT)";
            database.execSQL(sql);


            sql="CREATE TABLE [T_DEVOL] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[CANTM] REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [T_PAGO] ("+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[CODPAGO] INTEGER NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[DESC1] TEXT NOT NULL,"+
                    "[DESC2] TEXT NOT NULL,"+
                    "[DESC3] TEXT NOT NULL,"+
                    "PRIMARY KEY ([ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_DEPOSB] ("+
                    "[DENOMINACION] REAL NOT NULL,"+
                    "[CANTIDAD] INTEGER NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[MONEDA] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([DENOMINACION],[TIPO],[MONEDA])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [T_PAGOD] ("+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[DOCUMENTO] TEXT NOT NULL,"+
                    "[TIPODOC] TEXT NOT NULL,"+
                    "[MONTO] REAL NOT NULL,"+
                    "[PAGO] REAL NOT NULL,"+
                    "PRIMARY KEY ([ITEM])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [T_LOTES] ("+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[LOTE] TEXT NOT NULL,"+
                    "[CANTIDAD] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "PRIMARY KEY ([PRODUCTO],[LOTE])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_BONITEM] ("+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[PRODID] TEXT NOT NULL,"+
                    "[BONIID] TEXT NOT NULL,"+
                    "[CANT] REAL NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[COSTO] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[UMVENTA] TEXT NOT NULL,"+
                    "[UMSTOCK] TEXT NOT NULL,"+
                    "[UMPESO] TEXT NOT NULL,"+
                    "[FACTOR] REAL NOT NULL,"+
                    "[POR_PESO] TEXT NOT NULL,"+
                    "PRIMARY KEY ([ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_BARRA] ("+
                    "[BARRA] TEXT NOT NULL,"+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[PESOORIG] REAL NOT NULL,"+
                    "[CANTIDAD] REAL NOT NULL,"+
                    "PRIMARY KEY ([BARRA],[CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_BARRA_BONIF] ("+
                    "[BARRA] TEXT NOT NULL,"+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[PESO] REAL NOT NULL,"+
                    "[PESOORIG] REAL NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "PRIMARY KEY ([BARRA],[CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_BONIFFALT] ("+
                    "[PRODID]   TEXT NOT NULL,"+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[CANT]     REAL NOT NULL,"+
                    "PRIMARY KEY ([PRODID],[PRODUCTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_PRODMENU] ("+
                    "ID     INTEGER NOT NULL,"+
                    "IDSESS INTEGER NOT NULL,"+
                    "IDITEM INTEGER NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "DESCRIP TEXT NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "BANDERA INTEGER NOT NULL,"+
                    "IDLISTA INTEGER NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID],[IDSESS],[IDITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_COMBO] ("+
                    "CODIGO_MENU INTEGER NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "UNID INTEGER NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "IDSELECCION INTEGER NOT NULL,"+
                    "ORDEN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MENU],[IDCOMBO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_MOVD] ("+
                    "CORELDET INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "CANTM REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "PESOM REAL NOT NULL,"+
                    "LOTE TEXT NOT NULL,"+
                    "CODIGOLIQUIDACION INTEGER NOT NULL,"+
                    "UNIDADMEDIDA TEXT NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "RAZON INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CORELDET])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [T_MOVR] ("+
                    "CORELDET INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "CANTM REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "PESOM REAL NOT NULL,"+
                    "LOTE TEXT NOT NULL,"+
                    "CODIGOLIQUIDACION INTEGER NOT NULL,"+
                    "UNIDADMEDIDA TEXT NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "RAZON INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CORELDET])"+
                    ");";
            database.execSQL(sql);

            return 1;

        } catch (SQLiteException e) {
            msgbox(e.getMessage());return 0;
        }
    }

    public int scriptData(SQLiteDatabase db) {

        try {
            db.execSQL("INSERT INTO Params VALUES (0,0,0,0,0, '','','','',''," +
                    "'http://192.168.1.137/wsMPos/wsandr.asmx','','');");
            db.execSQL("INSERT INTO FinDia VALUES (0,0, 0,0,0,0, 0,0,0,0);");

            //db.execSQL("INSERT INTO P_EMPRESA VALUES (" +
            //        "'2','Nombre Empresa','','',0,'','','',  '',0,0,'GUA',0,0,0,36,  0,0,0,0,0,0,0,0,  0,0,'','','',0);");
            //db.execSQL("INSERT INTO P_SUCURSAL VALUES ('1','1','Nombre Negocio','Nombre Negocio','', '','','',1);");
            //db.execSQL("INSERT INTO VENDEDORES VALUES ('1','Gerente','1','1', 3,1,'','',1);");

            //datosIniciales(db);

            return 1;
        } catch (Exception e) {
            msgbox(e.getMessage());return 0;
        }
    }

    private void datosIniciales(SQLiteDatabase db) {

        db.execSQL("INSERT INTO P_MEDIAPAGO VALUES ('1','EFECTIVO','S',1,'N');");
        db.execSQL("INSERT INTO P_MEDIAPAGO VALUES ('2','CHEQUE','N',2,'N');");
        db.execSQL("INSERT INTO P_MEDIAPAGO VALUES ('5','TARJETA CREDITO','S',4,'N');");
        db.execSQL("INSERT INTO P_MEDIAPAGO VALUES ('6','DOLLAR','N',1,'N');");

        db.execSQL("INSERT INTO P_PARAMEXT VALUES (1,'GPS Margen ( Metros )','50',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (2,'Stock Interfaz','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (3,'Modalidad','TOL',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (4,'Solicitud de inventario','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (5,'Pantalla aceptar carga','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (6,'Boton Inventario ( Comunicacion )','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (7,'Boton Precios ( Comunicacion )','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (8,'Boton Recarga ( Comunicacion )','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (9,'Cantidad de decimales en calculos','2',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (10,'Cantidad de decimales impresion','2',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (11,'Cantidad de decimales para cantidad','2',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (12,'Simbolo de moneda','Q',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (13,'Seleccionar ayudante y vehículo','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (14,'Envio parcial','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (15,'Ordenar producto por nombre','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (16,'Formato factura','TOL',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (17,'Pregunta si es correcta la impresion de la factura','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (18,'Restringir venta por GPS margen','P',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (19,'Margen de error de GPS','10',0)");

        db.execSQL("INSERT INTO P_PARAMEXT VALUES (100,'Configuración centralizada','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (101,'Imprimir orden para cosina','N',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (102,'Lista con imagenes','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (103,'Pos modalidad','',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (104,'Imprimir factura','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (105,'FEL','',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (106,'Mostrar foto de cliente para biometrico','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (107,'Cierre diario','S',0)");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (108,'Días anulación permitida','3',0)");

        db.execSQL("INSERT INTO P_usgrupo VALUES (1,'Cajero','S')");
        db.execSQL("INSERT INTO P_usgrupo VALUES (2,'Supervisor','S')");
        db.execSQL("INSERT INTO P_usgrupo VALUES (3,'Gerente','S')");
        //db.execSQL("INSERT INTO P_usgrupo VALUES (4,'Mesero','S')");
        //db.execSQL("INSERT INTO P_usgrupo VALUES (5,'Vendedor','S')");

        db.execSQL("INSERT INTO P_usopcion VALUES ( 1,'Venta','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 2,'Caja','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 3,'Reimpresion','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 4,'Inventario','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 5,'Comunicacion','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 6,'Utilerias','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 7,'Mantenimientos','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 8,'Reportes','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES ( 9,'Anulacion','Ingreso')");
        db.execSQL("INSERT INTO P_usopcion VALUES (10,'Mantenimientos','Botón agregar')");
        db.execSQL("INSERT INTO P_usopcion VALUES (11,'Mantenimientos','completos')");
        db.execSQL("INSERT INTO P_usopcion VALUES (12,'Mantenimientos','Clientes,Productos')");
        db.execSQL("INSERT INTO P_usopcion VALUES (13,'Mantenimientos','Botón guardar')");

        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',1)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',2)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',3)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',4)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',5)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',7)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('1',12)");

        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',1)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',2)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',3)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',4)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',5)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',6)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',7)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',8)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',9)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',10)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',11)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',12)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('2',13)");

        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',1)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',2)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',3)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',4)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',5)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',6)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',7)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',8)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',9)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',10)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',11)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',12)");
        db.execSQL("INSERT INTO P_usgrupoopc VALUES ('3',13)");

        db.execSQL("INSERT INTO P_CONCEPTOPAGO VALUES(1,'SALVAVIDAS',1)");
        db.execSQL("INSERT INTO P_CONCEPTOPAGO VALUES(2,'ELECTRICIDAD',1)");
        db.execSQL("INSERT INTO P_CONCEPTOPAGO VALUES(3,'AGUA',1)");
        db.execSQL("INSERT INTO P_CONCEPTOPAGO VALUES(4,'CAJA CHICA',1)");
    }

    private void msgbox(String msg) {
        ExDialog dialog = new ExDialog(vcontext);
        dialog.setMessage(msg);

        dialog.setIcon(R.drawable.ic_error);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Yes button pressed",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }

}