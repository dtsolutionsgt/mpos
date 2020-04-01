package com.dtsgt.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dtsgt.mpos.R;

public class BaseDatosScript {
	
	private Context vcontext;
	
	public BaseDatosScript(Context context) {
		vcontext=context;
	}
	
	public int scriptDatabase(SQLiteDatabase database) {
		String vSQL;

		try {

			vSQL="CREATE TABLE [Params] ("+
					"ID integer NOT NULL primary key,"+
					"EmpID     INTEGER  NOT NULL,"+
					"dbver     INTEGER  NOT NULL,"+
					"param1    INTEGER  NOT NULL,"+
					"param2    INTEGER  NOT NULL,"+
					"prn       TEXT     NOT NULL,"+
					"prnparam  TEXT     NOT NULL,"+
					"prnserie  TEXT     NOT NULL,"+
                    "lic       TEXT     NOT NULL,"+
                    "licparam  TEXT     NOT NULL,"+
					"url       TEXT     NOT NULL,"+
                    "sucursal  TEXT     NOT NULL,"+
					"ruta      TEXT     NOT NULL);";
			database.execSQL(vSQL);

			vSQL="CREATE TABLE [FinDia] ("+
					"ID integer NOT NULL primary key,"+
					"Corel INTEGER  NOT NULL,"+
					"val1  INTEGER  NOT NULL,"+  // Día del cierre
					"val2  INTEGER  NOT NULL,"+	 // Comunicacion
					"val3  INTEGER  NOT NULL,"+	 // Impresión Depósito
					"val4  INTEGER  NOT NULL,"+	 // Depósito
					"val5  INTEGER  NOT NULL,"+	 // Devolución bodega y canastas
					"val6  INTEGER  NOT NULL,"+  // Generación Cierre Z
					"val7  INTEGER  NOT NULL,"+  // Impresión de Cierre Z
					"val8  REAL  NOT NULL);";    // GrandTotal
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
					"Nombre TEXT NOT NULL,"+
					"Valor  TEXT);"; 
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

            sql="CREATE TABLE [P_empresa] ("+
                    "EMPRESA TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "COL_IMP INTEGER NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_BANCO] ("+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[CUENTA] TEXT NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[activo] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[TIPO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_BANCO_idx1 ON P_BANCO(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_cliente] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "BLOQUEADO TEXT NOT NULL,"+
                    "NIVELPRECIO INTEGER NOT NULL,"+
                    "MEDIAPAGO INTEGER NOT NULL,"+
                    "LIMITECREDITO REAL NOT NULL,"+
                    "DIACREDITO INTEGER NOT NULL,"+
                    "DESCUENTO TEXT NOT NULL,"+
                    "BONIFICACION TEXT NOT NULL,"+
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
                    "CODIGO_CLIENTE INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CLIENTE])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_CLIENTE_idx1 ON P_CLIENTE(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_IMPUESTO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[VALOR]  REAL NOT NULL,"+
                    "[activo] INTEGER NOT NULL,"+
                    "PRIMARY  KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_NIVELPRECIO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_CONCEPTOPAGO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_linea] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "MARCA TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "CODIGO_LINEA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE INDEX P_LINEA_idx1 ON P_LINEA(NOMBRE)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_prodcombo] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "PRODUCTO TEXT NOT NULL,"+
                    "TIPO TEXT NOT NULL,"+
                    "CANTMIN REAL NOT NULL,"+
                    "CANTTOT REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[PRODUCTO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_PRODPRECIO] ("+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[NIVEL] INTEGER NOT NULL,"+
                    "[PRECIO] REAL NOT NULL,"+
                    "[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[NIVEL],[UNIDADMEDIDA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_PRODPRECIO_idx1 ON P_PRODPRECIO(CODIGO)";
            database.execSQL(sql);
            sql="CREATE INDEX P_PRODPRECIO_idx2 ON P_PRODPRECIO(NIVEL)";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODUCTO] ("+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[LINEA] TEXT NOT NULL,"+
                    "[SUBLINEA] TEXT NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[MARCA] TEXT NOT NULL,"+
                    "[CODBARRA] TEXT NOT NULL,"+
                    "[DESCCORTA] TEXT NOT NULL,"+
                    "[DESCLARGA] TEXT NOT NULL,"+
                    "[COSTO] REAL NOT NULL,"+
                    "[FACTORCONV] REAL NOT NULL,"+
                    "[UNIDBAS] TEXT NOT NULL,"+
                    "[UNIDMED] TEXT NOT NULL,"+
                    "[UNIMEDFACT] REAL NOT NULL,"+
                    "[UNIGRA] TEXT NOT NULL,"+
                    "[UNIGRAFACT] REAL NOT NULL,"+
                    "[DESCUENTO] TEXT NOT NULL,"+
                    "[BONIFICACION] TEXT NOT NULL,"+
                    "[IMP1] REAL NOT NULL,"+
                    "[IMP2] REAL NOT NULL,"+
                    "[IMP3] REAL NOT NULL,"+
                    "[VENCOMP] TEXT NOT NULL,"+
                    "[DEVOL] TEXT NOT NULL,"+
                    "[OFRECER] TEXT NOT NULL,"+
                    "[RENTAB] TEXT NOT NULL,"+
                    "[DESCMAX] TEXT NOT NULL,"+
                    "[PESO_PROMEDIO] REAL NOT NULL,"+
                    "[MODIF_PRECIO] INTEGER NOT NULL,"+
                    "[IMAGEN] TEXT NOT NULL,"+
                    "[VIDEO] TEXT NOT NULL,"+
                    "[VENTA_POR_PESO] INTEGER NOT NULL,"+
                    "[ES_PROD_BARRA] INTEGER NOT NULL,"+
                    "[UNID_INV] TEXT NOT NULL,"+
                    "[VENTA_POR_PAQUETE] INTEGER NOT NULL,"+
                    "[VENTA_POR_FACTOR_CONV] INTEGER NOT NULL,"+
                    "[ES_SERIALIZADO] INTEGER NOT NULL,"+
                    "[PARAM_CADUCIDAD] INTEGER NOT NULL,"+
                    "[PRODUCTO_PADRE] TEXT NOT NULL,"+
                    "[FACTOR_PADRE] REAL NOT NULL,"+
                    "[TIENE_INV] INTEGER NOT NULL,"+
                    "[TIENE_VINETA_O_TUBO] INTEGER NOT NULL,"+
                    "[PRECIO_VINETA_O_TUBO] REAL NOT NULL,"+
                    "[ES_VENDIBLE] INTEGER NOT NULL,"+
                    "[UNIGRASAP] REAL NOT NULL,"+
                    "[UM_SALIDA] TEXT NOT NULL,"+
                    "[activo] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
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
                    "PRIMARY KEY ([CLIENTE],[CTIPO],[PRODUCTO],[PTIPO],[TIPORUTA],[RANGOINI])"+
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
                    "[CODIGO] TEXT NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[DESCRIPCION] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[DIRECCION] TEXT NOT NULL,"+
                    "[TELEFONO] TEXT NOT NULL,"+
                    "[NIT] TEXT NOT NULL,"+
                    "[TEXTO] TEXT NOT NULL,"+
                    "[ACTIVO] INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_STOCK] ("+
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
                    "[UNIDADMEDIDA] TEXT  NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[LOTE],[DOCUMENTO],[STATUS],[UNIDADMEDIDA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_NIVELMEDIAPAGO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[DESCRIPCION] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_MEDIAPAGO] ("+
                    "[CODIGO] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] TEXT NOT NULL,"+
                    "[NIVEL] INTEGER NOT NULL,"+
                    "[PORCOBRO] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX P_MEDIAPAGO_idx1 ON P_MEDIAPAGO(NIVEL)";
            database.execSQL(sql);
            sql="CREATE INDEX P_MEDIAPAGO_idx2 ON P_MEDIAPAGO(PORCOBRO)";
            database.execSQL(sql);


            sql="CREATE TABLE [P_RUTA] ("+
                    "[CODIGO] TEXT NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[ACTIVO] TEXT NOT NULL,"+
                    "[SUCURSAL] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_corel] ("+
                    "RESOL TEXT NOT NULL,"+
                    "SERIE TEXT NOT NULL,"+
                    "CORELINI INTEGER NOT NULL,"+
                    "CORELFIN INTEGER NOT NULL,"+
                    "CORELULT INTEGER NOT NULL,"+
                    "FECHARES INTEGER NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
                    "FECHAVIG INTEGER NOT NULL,"+
                    "RESGUARDO INTEGER NOT NULL,"+
                    "VALOR1 INTEGER NOT NULL,"+
                    "PRIMARY KEY ([RESOL],[SERIE],[CORELINI])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_ARCHIVOCONF] ("+
                    "[RUTA] TEXT NOT NULL,"+
                    "[TIPO_HH] TEXT NOT NULL,"+
                    "[IDIOMA] TEXT  NOT NULL,"+
                    "[TIPO_IMPRESORA] TEXT  NOT NULL,"+
                    "[SERIAL_HH] TEXT  NOT NULL,"+
                    "[MODIF_PESO] TEXT NOT NULL,"+
                    "[PUERTO_IMPRESION] TEXT NOT NULL,"+
                    "[LBS_O_KGS] TEXT NOT NULL,"+
                    "[NOTA_CREDITO] INTEGER  NOT NULL,"+
                    "PRIMARY KEY ([RUTA])"+
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
                    "[CODDESC] INTEGER NOT NULL,"+
                    "[NOMBRE] TEXT NOT NULL,"+
                    "[EMP] TEXT NOT NULL,"+
                    "[UMPRODUCTO] TEXT NOT NULL," +
                    "[UMBONIFICACION] TEXT NOT NULL,"+
                    "PRIMARY KEY ([CLIENTE],[CTIPO],[PRODUCTO],[PTIPO],[TIPORUTA],[TIPOBON],[RANGOINI])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [P_FACTORCONV] ("+
                    "[PRODUCTO] TEXT NOT NULL,"+
                    "[UNIDADSUPERIOR] TEXT NOT NULL,"+
                    "[FACTORCONVERSION] REAL NOT NULL,"+
                    "[UNIDADMINIMA] TEXT NOT NULL,"+
                    "PRIMARY KEY ([PRODUCTO],[UNIDADSUPERIOR],[UNIDADMINIMA])"+
                    ");";
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

            sql="CREATE TABLE [P_PROVEEDOR] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);


            sql="CREATE TABLE [VENDEDORES] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CLAVE TEXT NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
                    "NIVEL INTEGER NOT NULL,"+
                    "NIVELPRECIO REAL NOT NULL,"+
                    "BODEGA TEXT NOT NULL,"+
                    "SUBBODEGA TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[RUTA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODMENU] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "ITEM INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "IDOPCION INTEGER NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "ORDEN INTEGER NOT NULL,"+
                    "BANDERA INTEGER NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO],[ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODOPC] ("+
                    "ID TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_PRODOPCLIST] ("+
                    "ID TEXT NOT NULL,"+
                    "PRODUCTO TEXT NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "IDRECETA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID],[PRODUCTO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_cajacierre] ("+
                    "SUCURSAL TEXT NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
                    "COREL INTEGER NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "VENDEDOR TEXT NOT NULL,"+
                    "CODPAGO INTEGER NOT NULL,"+
                    "FONDOCAJA REAL NOT NULL,"+
                    "MONTOINI REAL NOT NULL,"+
                    "MONTOFIN REAL NOT NULL,"+
                    "MONTODIF REAL NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "PRIMARY KEY ([SUCURSAL],[RUTA],[COREL],[CODPAGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_cajapagos] ("+
                    "EMPRESA TEXT NOT NULL,"+
                    "SUCURSAL TEXT NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
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
                    "VENDEDOR TEXT NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[SUCURSAL],[RUTA],[COREL],[ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_cajareporte] ("+
                    "SUCURSAL TEXT NOT NULL,"+
                    "RUTA TEXT NOT NULL,"+
                    "COREL INTEGER NOT NULL,"+
                    "LINEA INTEGER NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "PRIMARY KEY ([SUCURSAL],[RUTA],[COREL],[LINEA])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [FPRINT] ("+
                    "EMPRESA TEXT NOT NULL,"+
                    "CODIGO TEXT NOT NULL,"+
                    "DEDO INTEGER NOT NULL,"+
                    "IMAGE BLOB,"+
                    "PRIMARY KEY ([EMPRESA],[CODIGO],[DEDO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_usgrupo] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CUENTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_usgrupoopc] ("+
                    "GRUPO TEXT NOT NULL,"+
                    "OPCION INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GRUPO],[OPCION])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE TABLE [P_usopcion] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "MENUGROUP TEXT NOT NULL,"+
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

			
			sql="CREATE TABLE [D_MERPROPIO] ("+
					"[CLIENTE] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"PRIMARY KEY ([CLIENTE],[PRODUCTO],[FECHA])"+
					");";
			database.execSQL(sql);
			

			sql="CREATE TABLE [D_MERCOMP] ("+
					"[CLIENTE] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PRECIO] REAL NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[DESCUENTO] REAL NOT NULL,"+
					"[DIAVISITA] INTEGER NOT NULL,"+
					"[FRECUENCIA] INTEGER NOT NULL,"+
					"PRIMARY KEY ([CLIENTE],[PRODUCTO],[FECHA])"+
					");";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_MEREQUIPO] ("+
					"[CLIENTE] TEXT NOT NULL,"+
					"[SERIAL] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[TIPO] INTEGER NOT NULL,"+
					"[ESTADO] INTEGER NOT NULL,"+
					"[CODBARRA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([CLIENTE],[SERIAL],[FECHA])"+
					");";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_MERFALTA] ("+
					"[CLIENTE] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"PRIMARY KEY ([CLIENTE],[PRODUCTO],[FECHA])"+
					");";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_MERPREGUNTA] ("+
					"[CLIENTE] TEXT NOT NULL,"+
					"[CODIGO] INTEGER NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[RESP] INTEGER NOT NULL,"+
					"[STATCOM] INTEGER NOT NULL,"+
					"[FOTO] TEXT NOT NULL,"+
					"[GRABACION] TEXT NOT NULL,"+
					"[STATCOMG] INTEGER NOT NULL,"+
					"PRIMARY KEY ([CLIENTE],[CODIGO],[FECHA])"+
					");";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_COBRO] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[KILOMETRAJE] REAL NOT NULL,"+
					"[CORELC] TEXT NOT NULL,"+
					"[BANDERA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[DEPOS] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[CALCOBJ] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[SERIE] TEXT NOT NULL,"+
					"[CORELATIVO] INTEGER NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_COBRO_idx1 ON D_COBRO(ANULADO)";
			database.execSQL(sql);
			sql="CREATE INDEX D_COBRO_idx2 ON D_COBRO(CLIENTE)";
			database.execSQL(sql);
			sql="CREATE INDEX D_COBRO_idx3 ON D_COBRO(STATCOM)";
			database.execSQL(sql);
			sql="CREATE INDEX D_COBRO_idx4 ON D_COBRO(CALCOBJ)";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_COBROD] ("+
					"[COREL] TEXT NOT NULL,"+
					"[DOCUMENTO] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[TIPODOC] TEXT NOT NULL,"+
					"[MONTO] REAL NOT NULL,"+
					"[PAGO] REAL NOT NULL,"+
					"[CONTRASENA] TEXT NOT NULL,"+
					"[ID_TRANSACCION] INTEGER NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[ASIGNACION] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[DOCUMENTO])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_COBROD_idx1 ON D_COBROD(ANULADO)";
			database.execSQL(sql);
			

			sql="CREATE TABLE [D_COBROD_SR] ("+
					"[COREL] TEXT NOT NULL,"+
					"[DOCUMENTO] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[TIPODOC] TEXT NOT NULL,"+
					"[MONTO] REAL NOT NULL,"+
					"[PAGO] REAL NOT NULL,"+
					"[CONTRASENA] TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL],[DOCUMENTO])"+
					");";
			database.execSQL(sql);


            sql="CREATE TABLE [D_COBROP] ("+
                    "[COREL] TEXT NOT NULL,"+
                    "[ITEM] INTEGER NOT NULL,"+
                    "[ANULADO] TEXT NOT NULL,"+
                    "[EMPRESA] TEXT NOT NULL,"+
                    "[CODPAGO] INTEGER NOT NULL,"+
                    "[TIPO] TEXT NOT NULL,"+
                    "[VALOR] REAL NOT NULL,"+
                    "[DESC1] TEXT NOT NULL,"+
                    "[DESC2] TEXT NOT NULL,"+
                    "[DESC3] TEXT NOT NULL,"+
                    "[DEPOS] TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL],[ITEM])"+
                    ");";
            database.execSQL(sql);

            sql="CREATE INDEX D_COBROP_idx1 ON D_COBROP(ANULADO)";
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

			sql="CREATE TABLE [D_FACT_LOG] ("+
					"[ITEM] INTEGER NOT NULL,"+
					"[SERIE] TEXT NOT NULL,"+
					"[COREL] INTEGER NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"PRIMARY KEY ([ITEM])"+
					");";
			database.execSQL(sql);
      

			sql="CREATE TABLE [D_FACTURA] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[KILOMETRAJE] REAL NOT NULL,"+
					"[FECHAENTR] INTEGER NOT NULL,"+
					"[FACTLINK] TEXT NOT NULL,"+
					"[TOTAL] REAL NOT NULL,"+
					"[DESMONTO] REAL NOT NULL,"+
					"[IMPMONTO] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[BANDERA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[CALCOBJ] TEXT NOT NULL,"+
					"[SERIE] TEXT NOT NULL,"+
					"[CORELATIVO] INTEGER NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[ADD1] TEXT NOT NULL,"+
					"[ADD2] TEXT NOT NULL,"+
					"[ADD3] TEXT NOT NULL,"+
					"[DEPOS] TEXT NOT NULL,"+
					"[PEDCOREL] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[ASIGNACION] TEXT NOT NULL,"+
					"[SUPERVISOR] TEXT NOT NULL,"+
					"[AYUDANTE] TEXT NOT NULL,"+
					"[VEHICULO] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[RAZON_ANULACION] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL])"+
					");";
			database.execSQL(sql);

			sql="CREATE INDEX D_FACTURA_idx1 ON D_FACTURA(ANULADO)";
			database.execSQL(sql);
			sql="CREATE INDEX D_FACTURA_idx2 ON D_FACTURA(FECHA)";
			database.execSQL(sql);
			

			sql="CREATE TABLE [D_FACTURAD] ("+
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
					"[UMVENTA] TEXT NOT NULL,"+
					"[FACTOR] REAL NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+										
					"PRIMARY KEY ([COREL],[PRODUCTO],[UMPESO])"+
					");";
			database.execSQL(sql);
			

			sql="CREATE TABLE [D_FACTURAP] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ITEM] INTEGER NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[EMPRESA] TEXT NOT NULL,"+
					"[CODPAGO] INTEGER NOT NULL,"+
					"[TIPO] TEXT NOT NULL,"+
					"[VALOR] REAL NOT NULL,"+
					"[DESC1] TEXT NOT NULL,"+
					"[DESC2] TEXT NOT NULL,"+
					"[DESC3] TEXT NOT NULL,"+
					"[DEPOS] TEXT NOT NULL,"+
					"PRIMARY KEY ([COREL],[ITEM])"+
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


			sql="CREATE TABLE [D_MOV] ("+
					"[COREL] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[TIPO] TEXT NOT NULL,"+
					"[USUARIO] TEXT NOT NULL,"+
					"[REFERENCIA] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[IMPRES] INTEGER NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
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
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[CANTM] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[PESOM] REAL NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[CODIGOLIQUIDACION] INTEGER NOT NULL,"+
					"[UNIDADMEDIDA] TEXT DEFAULT 'UN' NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[LOTE],[UNIDADMEDIDA])"+
					");";
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

			sql="CREATE TABLE [D_ATENCION] ("+
					"[RUTA] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[HORALLEG] TEXT NOT NULL,"+
					"[HORASAL] TEXT NOT NULL,"+
					"[TIEMPO] INTEGER NOT NULL,"+
					"[VENDEDOR] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[DIAACT] INTEGER NOT NULL,"+
					"[DIA] INTEGER NOT NULL,"+
					"[DIAFLAG] TEXT NOT NULL,"+
					"[SECUENCIA] INTEGER NOT NULL,"+
					"[SECUENACT] INTEGER NOT NULL,"+
					"[CODATEN] TEXT NOT NULL,"+
					"[KILOMET] REAL NOT NULL,"+
					"[VALORVENTA] REAL NOT NULL,"+
					"[VALORNEXT] REAL NOT NULL,"+
					"[CLIPORDIA] INTEGER NOT NULL,"+
					"[CODOPER] TEXT NOT NULL,"+
					"[COREL] TEXT NOT NULL,"+
					"[SCANNED] TEXT NOT NULL,"+
					"[STATCOM] TEXT NOT NULL,"+
					"[LLEGO_COMPETENCIA_ANTES] INTEGER NOT NULL,"+
					"[CoorX] REAL NOT NULL,"+
					"[CoorY] REAL NOT NULL,"+
					"[CliCoorX] REAL NOT NULL,"+
					"[CliCoorY] REAL NOT NULL,"+
					"[Dist] REAL NOT NULL,"+
					"PRIMARY KEY ([RUTA],[FECHA],[HORALLEG])"+
					");";
			database.execSQL(sql);

			
			sql="CREATE TABLE [D_BONIF] ("+
					"[COREL] TEXT NOT NULL,"+
					"[ITEM] INTEGER NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
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


			sql="CREATE TABLE [D_BONIF_BARRA] ("+
					"[COREL] TEXT NOT NULL," +
					"[BARRA] TEXT NOT NULL," +
					"[PESO] REAL NOT NULL," +
					"[PRODUCTO] TEXT NOT NULL," +
					"[UMVENTA] TEXT NOT NULL," +
					"[UMSTOCK] TEXT NOT NULL," +
					"[UMPESO] TEXT NOT NULL," +
					"[FACTOR] REAL NOT NULL," +
					"PRIMARY KEY ([COREL],[BARRA])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_BONIF_LOTES] ("+
					"[COREL] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[LOTE] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"[PESO] REAL NOT NULL,"+
					"[UMVENTA] TEXT NOT NULL,"+
					"[UMSTOCK] TEXT NOT NULL,"+
					"[UMPESO] TEXT NOT NULL,"+
					"[FACTOR] REAL NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO],[LOTE])"+
					");";
			database.execSQL(sql);

			sql="CREATE TABLE [D_BONIF_STOCK] ("+
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

			sql="CREATE INDEX D_BONIF_STOCK_idx1 ON D_BONIF_STOCK(COREL)";
			database.execSQL(sql);
			

			sql="CREATE TABLE [D_BONIFFALT] ("+
					"[COREL] TEXT NOT NULL,"+
					"[FECHA] INTEGER NOT NULL,"+
					"[ANULADO] TEXT NOT NULL,"+
					"[RUTA] TEXT NOT NULL,"+
					"[CLIENTE] TEXT NOT NULL,"+
					"[PRODUCTO] TEXT NOT NULL,"+
					"[CANT] REAL NOT NULL,"+
					"PRIMARY KEY ([COREL],[PRODUCTO])"+
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

            sql= "CREATE TABLE [D_RATING]("+
                    "[IDRATING] INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "[RUTA] TEXT NOT NULL,"+
                    "[VENDEDOR] TEXT NOT NULL,"+
                    "[RATING] REAL NOT NULL,"+
                    "[COMENTARIO] TEXT NOT NULL,"+
                    "[IDTRANSERROR] INTEGER NOT NULL,"+
                    "[FECHA] INTEGER NOT NULL,"+
                    "[STATCOM] TEXT NOT NULL"+
                    ");";
            database.execSQL(sql);

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
					"[CODIGO] TEXT NOT NULL,"+
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

            db.execSQL("INSERT INTO P_EMPRESA VALUES (" +
                    "'2','Nombre Empresa','','',0,'','','',  '',0,0,'GUA',0,0,0,36,  0,0,0,0,0,0,0,0,  0,0,'','','',0);");
            db.execSQL("INSERT INTO P_SUCURSAL VALUES ('1','1','Nombre Negocio','Nombre Negocio','', '','','',1);");
            db.execSQL("INSERT INTO VENDEDORES VALUES ('1','Gerente','1','1', 3,1,'','',1);");

           datosIniciales(db);

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

        db.execSQL("INSERT INTO P_PARAMEXT VALUES (1,'GPS Margen ( Metros )','50')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (2,'Stock Interfaz','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (3,'Modalidad','TOL')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (4,'Solicitud de inventario','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (5,'Pantalla aceptar carga','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (6,'Boton Inventario ( Comunicacion )','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (7,'Boton Precios ( Comunicacion )','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (8,'Boton Recarga ( Comunicacion )','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (9,'Cantidad de decimales en calculos','2')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (10,'Cantidad de decimales impresion','2')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (11,'Cantidad de decimales para cantidad','2')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (12,'Simbolo de moneda','Q')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (13,'Seleccionar ayudante y vehículo','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (14,'Envio parcial','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (15,'Ordenar producto por nombre','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (16,'Formato factura','TOL')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (17,'Pregunta si es correcta la impresion de la factura','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (18,'Restringir venta por GPS margen','P')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (19,'Margen de error de GPS','10')");

        db.execSQL("INSERT INTO P_PARAMEXT VALUES (100,'Configuración centralizada','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (101,'Imprimir orden para cosina','N')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (102,'Lista con imagenes','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (103,'Pos modalidad','')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (104,'Imprimir factura','S')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (105,'FEL','')");
        db.execSQL("INSERT INTO P_PARAMEXT VALUES (106,'Mostrar foto de cliente para biometrico','S')");

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

    }
	
	private void msgbox(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(vcontext);
    	
		dialog.setTitle(R.string.app_name);
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