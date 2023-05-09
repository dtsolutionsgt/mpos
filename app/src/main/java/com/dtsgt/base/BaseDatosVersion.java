package com.dtsgt.base;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.mpos.R;

public class BaseDatosVersion {

	public ArrayList<String> items=new ArrayList<String>();
	
	private Context cont;
	private SQLiteDatabase db;
	private BaseDatos Con;
	private String sql;
    private Cursor dt;
	
	private int aver,pver;
	private boolean flag;
	
	public BaseDatosVersion(Context context,SQLiteDatabase dbase,BaseDatos dbCon) {
		cont=context;
		db=dbase;
		Con=dbCon;
	}

    public void update(){
        update01();
        update02();

        update99();
    }

    private void update02() {

        try {

            sql="CREATE TABLE [D_orden_bitacora] ("+
                    "FECHA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "CANT_ORDENES INTEGER NOT NULL,"+
                    "CANT_RETRASADOS INTEGER NOT NULL,"+
                    "TPPO REAL NOT NULL,"+
                    "EFICIENCIA REAL NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([FECHA],[CODIGO_VENDEDOR])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_res_grupo] ("+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_GRUPO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_res_mesa] ("+
                    "CODIGO_MESA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_SALA INTEGER NOT NULL,"+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "LARGO REAL NOT NULL,"+
                    "ANCHO REAL NOT NULL,"+
                    "POSX REAL NOT NULL,"+
                    "POSY REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MESA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="ALTER TABLE P_res_mesa ADD COLUMN CODIGO_QR TEXT;";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="DROP TABLE P_res_mesero;";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_res_sala] ("+
                    "CODIGO_SALA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "ESCALA REAL NOT NULL,"+
                    "TAM_LETRA REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_SALA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_res_turno] ("+
                    "FECHA INTEGER NOT NULL,"+
                    "VENDEDOR INTEGER NOT NULL,"+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([FECHA],[VENDEDOR])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_res_sesion] ("+
                    "ID TEXT NOT NULL,"+
                    "CODIGO_MESA INTEGER NOT NULL,"+
                    "VENDEDOR INTEGER NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "CANTP INTEGER NOT NULL,"+
                    "CANTC INTEGER NOT NULL,"+
                    "FECHAINI INTEGER NOT NULL,"+
                    "FECHAFIN INTEGER NOT NULL,"+
                    "FECHAULT INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";

            db.execSQL(sql);

            sql="CREATE INDEX P_res_sesion_idx1 ON P_res_sesion(CODIGO_MESA)";db.execSQL(sql);
            sql="CREATE INDEX P_res_sesion_idx2 ON P_res_sesion(ESTADO)";db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [D_fel_bitacora] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_RUTA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TIEMPO_FIRMA REAL NOT NULL,"+
                    "TIEMPO_CERT REAL NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_fel_bitacora_idx1 ON D_fel_bitacora(FECHA)";db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_usuario_asistencia] ("+
                    "CODIGO_ASISTENCIA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "INICIO INTEGER NOT NULL,"+
                    "FIN INTEGER NOT NULL,"+
                    "BANDERA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ASISTENCIA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}


        try {

            Cursor vCursor = db.rawQuery("SELECT * FROM T_ORDEN", null);
            if (vCursor != null) vCursor.moveToLast();
            if (vCursor.getCount()==0) db.execSQL("DROP TABLE T_ORDEN");

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_orden] ("+
                    "ID INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO TEXT NOT NULL,"+
                    "EMPRESA TEXT NOT NULL,"+
                    "UM TEXT NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "UMSTOCK TEXT NOT NULL,"+
                    "FACTOR REAL NOT NULL,"+
                    "PRECIO REAL NOT NULL,"+
                    "IMP REAL NOT NULL,"+
                    "DES REAL NOT NULL,"+
                    "DESMON REAL NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "PRECIODOC REAL NOT NULL,"+
                    "PESO REAL NOT NULL,"+
                    "VAL1 REAL NOT NULL,"+
                    "VAL2 TEXT NOT NULL,"+
                    "VAL3 REAL NOT NULL,"+
                    "VAL4 TEXT NOT NULL,"+
                    "PERCEP REAL NOT NULL,"+
                    "CUENTA INTEGER NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID],[COREL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_orden_idx1 ON T_orden(COREL)";db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_ordencombo] ("+
                    "COREL TEXT NOT NULL,"+
                    "CODIGO_MENU INTEGER NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "UNID INTEGER NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "IDSELECCION INTEGER NOT NULL,"+
                    "ORDEN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL],[CODIGO_MENU],[IDCOMBO])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_orden_nota] ("+
                    "ID INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID],[COREL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_orden_nota_idx1 ON T_orden_nota(COREL)";db.execSQL(sql);
        } catch (Exception e) {}


        try {

            sql="CREATE TABLE [T_FACTURA_FEL] ("+
                    "COREL TEXT NOT NULL,"+
                    "FEELSERIE TEXT NOT NULL,"+
                    "FEELNUMERO TEXT NOT NULL,"+
                    "FEELUUID TEXT NOT NULL,"+
                    "FEELFECHAPROCESADO INTEGER NOT NULL,"+
                    "FEELCONTINGENCIA TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            Cursor vCursor = db.rawQuery("SELECT * FROM T_ordencuenta", null);
            if (vCursor != null) vCursor.moveToLast();
            if (vCursor.getCount()==0) db.execSQL("DROP TABLE T_ordencuenta");

        } catch (Exception e) {
            String ee=e.getMessage();
        }

        try {

            sql="CREATE TABLE [T_ordencuenta] ("+
                    "COREL TEXT NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "CF INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "NIT TEXT NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "CORREO TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL],[ID])"+
                    ");";

            db.execSQL(sql);
            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [D_facturapr] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "ANULADO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "PROPINA REAL NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {
        }


        try {

            sql="CREATE TABLE [P_estacion] ("+
                    "CODIGO_ESTACION INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CODIGO_IMPRESORA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ESTACION])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_linea_impresora] ("+
                    "CODIGO_LINEA_IMPRESORA INTEGER NOT NULL,"+
                    "CODIGO_LINEA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_IMPRESORA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_LINEA_IMPRESORA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_impresora] ("+
                    "CODIGO_IMPRESORA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "NUMERO_SERIE TEXT NOT NULL,"+
                    "CODIGO_MARCA INTEGER NOT NULL,"+
                    "CODIGO_MODELO INTEGER NOT NULL,"+
                    "TIPO_IMPRESORA TEXT NOT NULL,"+
                    "MAC TEXT NOT NULL,"+
                    "IP TEXT NOT NULL,"+
                    "FECHA_AGR INTEGER NOT NULL,"+
                    "IMPRESIONES INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_IMPRESORA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_impresora_marca] ("+
                    "CODIGO_IMPRESORA_MARCA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_IMPRESORA_MARCA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_impresora_modelo] ("+
                    "CODIGO_IMPRESORA_MODELO INTEGER NOT NULL,"+
                    "CODIGO_IMPRESORA_MARCA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_IMPRESORA_MODELO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_caja_impresora] ("+
                    "CODIGO_CAJA_IMPRESORA INTEGER NOT NULL,"+
                    "CODIGO_CAJA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_IMPRESORA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CAJA_IMPRESORA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_comanda] ("+
                    "LINEA INTEGER NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "PRIMARY KEY ([LINEA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_comanda_idx1 ON T_comanda(ID)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_orden_numero] ("+
                    "ID INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_cierre] ("+
                    "ID INTEGER NOT NULL,"+
                    "DIA INTEGER NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_cajahora] ("+
                    "COREL INTEGER NOT NULL,"+
                    "FECHAINI INTEGER NOT NULL,"+
                    "FECHAFIN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

           sql="CREATE TABLE [T_ordencomboad] ("+
                    "ID INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID],[COREL],[IDCOMBO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_ordencombodet] ("+
                    "CODIGO_MENUOPC_DET INTEGER NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "CODIGO_MENU_OPCION INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MENUOPC_DET])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_orden_cor] ("+
                    "ID INTEGER NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_ordencomboprecio] ("+
                    "COREL TEXT NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "PRECORIG REAL NOT NULL,"+
                    "PRECITEMS REAL NOT NULL,"+
                    "PRECDIF REAL NOT NULL,"+
                    "PRECTOTAL REAL NOT NULL,"+
                    "PRIMARY KEY ([COREL],[IDCOMBO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [D_facturar] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "UM TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL],[PRODUCTO])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_facturar_idx1 ON D_facturar(COREL)";
            db.execSQL(sql);

            sql="CREATE INDEX D_facturar_idx2 ON D_facturar(PRODUCTO)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_prodreceta] ("+
                    "CODIGO_RECETA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CODIGO_ARTICULO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "UM TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_RECETA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_prodreceta_idx1 ON P_prodreceta(EMPRESA)";
            db.execSQL(sql);

            sql="CREATE INDEX P_prodreceta_idx2 ON P_prodreceta(CODIGO_PRODUCTO)";
            db.execSQL(sql);

            sql="CREATE INDEX P_prodreceta_idx3 ON P_prodreceta(CODIGO_ARTICULO)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_unidad] ("+
                    "CODIGO_UNIDAD TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_UNIDAD])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_unidad_conv] ("+
                    "CODIGO_CONVERSION INTEGER NOT NULL,"+
                    "CODIGO_UNIDAD1 TEXT NOT NULL,"+
                    "CODIGO_UNIDAD2 TEXT NOT NULL,"+
                    "FACTOR REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CONVERSION])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_unidad_conv_idx1 ON P_unidad_conv(CODIGO_UNIDAD1)";
            db.execSQL(sql);

            sql="CREATE INDEX P_unidad_conv_idx2 ON P_unidad_conv(CODIGO_UNIDAD2)";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [T_factreceta] ("+
                    "ID INTEGER NOT NULL,"+
                    "PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "UM TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }


        try {

            sql="CREATE TABLE [D_facturac] ("+
                    "EMPRESA INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "CODIGO_MENU INTEGER NOT NULL,"+
                    "IDCOMBO INTEGER NOT NULL,"+
                    "UNID INTEGER NOT NULL,"+
                    "CANT INTEGER NOT NULL,"+
                    "IDSELECCION INTEGER NOT NULL,"+
                    "ORDEN INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([EMPRESA],[COREL],[CODIGO_MENU],[IDCOMBO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_cliente_dir] ("+
                    "CODIGO_DIRECCION INTEGER NOT NULL,"+
                    "CODIGO_CLIENTE INTEGER NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "CODIGO_DEPARTAMENTO TEXT NOT NULL,"+
                    "CODIGO_MUNICIPIO TEXT NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "ZONA_ENTREGA INTEGER NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_DIRECCION])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {  }

        try {

            sql="CREATE TABLE [P_cliente_dir_bita] ("+
                    "CODIGO_BITACORA INTEGER NOT NULL,"+
                    "CODIGO_DIRECCION INTEGER NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "CODIGO_CLIENTE INTEGER NOT NULL,"+
                    "DIRECCION TEXT NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "TELEFONO TEXT NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_BITACORA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_cliente_dir_bita_idx1 ON P_cliente_dir_bita(STATCOM)";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {
            sql="ALTER TABLE D_FACTURAPR ADD COLUMN PROPPERC REAL DEFAULT 0;";
            db.execSQL(sql);
        } catch (Exception e) {
        }

        try {
            sql="ALTER TABLE D_FACTURAPR ADD COLUMN PROPEXTRA REAL DEFAULT 0;";
            db.execSQL(sql);
        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [P_nivelprecio_sucursal] ("+
                    "CODIGO_NIVEL_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_NIVEL_PRECIO INTEGER NOT NULL,"+
                    "USUARIO_AGREGO INTEGER NOT NULL,"+
                    "FECHA_AGREGADO INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_NIVEL_SUCURSAL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {
        }

        try {

            sql="CREATE TABLE [D_factura_fel] ("+
                    "COREL TEXT NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NIT TEXT NOT NULL,"+
                    "RAZON_SOCIAL TEXT NOT NULL,"+
                    "NOMBRE_COMERCIAL TEXT NOT NULL,"+
                    "SUCURSAL INTEGER NOT NULL,"+
                    "RUTA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL],[EMPRESA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {

            sql="CREATE TABLE [P_almacen] ("+
                    "CODIGO_ALMACEN INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "ES_PRINCIPAL INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ALMACEN])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_almacen_idx1 ON P_almacen(EMPRESA)";db.execSQL(sql);
            sql="CREATE INDEX P_almacen_idx2 ON P_almacen(CODIGO_SUCURSAL)";db.execSQL(sql);

        } catch (Exception e) { }

        try {

            sql="CREATE TABLE [D_mov_almacen] ("+
                    "COREL TEXT NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "ALMACEN_ORIGEN INTEGER NOT NULL,"+
                    "ALMACEN_DESTINO INTEGER NOT NULL,"+
                    "ANULADO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TIPO TEXT NOT NULL,"+
                    "USUARIO INTEGER NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "STATCOM TEXT NOT NULL,"+
                    "IMPRES INTEGER NOT NULL,"+
                    "CODIGOLIQUIDACION INTEGER NOT NULL,"+
                    "CODIGO_PROVEEDOR INTEGER NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {

            sql="CREATE TABLE [D_movd_almacen] ("+
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
            db.execSQL(sql);

        } catch (Exception e) { }

        try {

            sql="CREATE TABLE [P_stock_almacen] ("+
                "CODIGO_STOCK INTEGER NOT NULL,"+
                "EMPRESA INTEGER NOT NULL,"+
                "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                "CODIGO_ALMACEN INTEGER NOT NULL,"+
                "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                "UNIDADMEDIDA TEXT NOT NULL,"+
                "LOTE TEXT NOT NULL,"+
                "CANT REAL NOT NULL,"+
                "CANTM REAL NOT NULL,"+
                "PESO REAL NOT NULL,"+
                "PESOM REAL NOT NULL,"+
                "ANULADO INTEGER NOT NULL,"+
                "PRIMARY KEY ([CODIGO_STOCK])"+
                ");";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {

            sql="CREATE TABLE [P_producto_imagen] ("+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "IMAGEN BLOB,"+
                    "PRIMARY KEY ([CODIGO_PRODUCTO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_linea_imagen] ("+
                    "CODIGO_LINEA INTEGER NOT NULL,"+
                    "IMAGEN BLOB,"+
                    "PRIMARY KEY ([CODIGO_LINEA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_empresa_imagen] ("+
                    "CODIGO_EMPRESA INTEGER NOT NULL,"+
                    "IMAGEN BLOB,"+
                    "PRIMARY KEY ([CODIGO_EMPRESA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_vendedor_imagen] ("+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "IMAGEN BLOB,"+
                    "PRIMARY KEY ([CODIGO_VENDEDOR])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_frase_sucursal] ("+
                    "CODIGO_FRASE_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_FRASE_FEL INTEGER NOT NULL,"+
                    "TEXTO TEXT NOT NULL,"+
                    "ES_FRASE_ISR INTEGER NOT NULL,"+
                    "ES_FRASE_IVA INTEGER NOT NULL,"+
                    "USUARIO_AGREGO INTEGER NOT NULL,"+
                    "FECHA_AGREGADO INTEGER NOT NULL,"+
                    "USUARIO_MODIFICO INTEGER NOT NULL,"+
                    "FECHA_MODIFICADO INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_FRASE_SUCURSAL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_costo] ("+
                    "COREL TEXT NOT NULL,"+
                    "CODIGO_COSTO INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "COSTO REAL NOT NULL,"+
                    "CODIGO_PROVEEDOR INTEGER NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL],[CODIGO_COSTO])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_costo_idx1 ON T_costo(CODIGO_PRODUCTO)";
            db.execSQL(sql);

            sql="CREATE INDEX T_costo_idx2 ON T_costo(FECHA)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_regla_costo] ("+
                    "CODIGO_EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_TIPO INTEGER NOT NULL,"+
                    "DIAS INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_EMPRESA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_mesero_grupo] ("+
                    "CODIGO_MESERO INTEGER NOT NULL,"+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MESERO],[CODIGO_GRUPO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_modo_emergencia] ("+
                    "CODIGO_OPCION INTEGER NOT NULL,"+
                    "VALOR TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_OPCION])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_orden_compra] ("+
                    "CODIGO_COMPRA TEXT NOT NULL,"+
                    "CODIGO_EMPRESA INTEGER NOT NULL,"+
                    "ANULADO INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "ESTATUS TEXT NOT NULL,"+
                    "FECHA_EMISION INTEGER NOT NULL,"+
                    "FECHA_RECEPCION INTEGER NOT NULL,"+
                    "CODIGO_USUARIO INTEGER NOT NULL,"+
                    "CODIGO_PROVEEDOR INTEGER NOT NULL,"+
                    "CODIGO_ALMACEN INTEGER NOT NULL,"+
                    "SERIE TEXT NOT NULL,"+
                    "NUMERO INTEGER NOT NULL,"+
                    "CORRELATIVO INTEGER NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "CREADO_EN_BOF INTEGER NOT NULL,"+
                    "ENVIADO INTEGER NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_COMPRA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_orden_compra_idx1 ON D_orden_compra(ESTATUS)";
            db.execSQL(sql);

            sql="CREATE INDEX D_orden_compra_idx2 ON D_orden_compra(ENVIADO)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_orden_compra_detalle] ("+
                    "CODIGO_COMPRA TEXT NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CANTIDAD REAL NOT NULL,"+
                    "UNIDAD_MEDIDA TEXT NOT NULL,"+
                    "COSTO REAL NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "ANULADO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_COMPRA],[CODIGO_PRODUCTO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {
            sql="CREATE TABLE [D_orden_compra_recepcion] ("+
                    "CODIGO_COMPRA TEXT NOT NULL,"+
                    "CORRELATIVO INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CANTIDAD REAL NOT NULL,"+
                    "FECHA_RECEPCION INTEGER NOT NULL,"+
                    "CORREL_D_MOVD TEXT NOT NULL,"+
                    "REFERENCIA TEXT NOT NULL,"+
                    "CODIGO_ALMACEN INTEGER NOT NULL,"+
                    "BANDERA INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_COMPRA],[CORRELATIVO])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_mesa_nombre] ("+
                    "CODIGO_MESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MESA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_pedidoorden] ("+
                    "COREL TEXT NOT NULL,"+
                    "ORDEN TEXT NOT NULL,"+
                    "TIPO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_modificador] ("+
                    "CODIGO_MODIF INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_MODIF])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_modificador_grupo] ("+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_GRUPO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_prodmodificador] ("+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_MODIF INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_PRODUCTO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_pedidocom] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "CODIGO_RUTA INTEGER NOT NULL,"+
                    "COREL_PEDIDO TEXT NOT NULL,"+
                    "COREL_LINEA INTEGER NOT NULL,"+
                    "COMANDA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}


        try {

            db.execSQL("ALTER TABLE P_PRODUCTO ADD CLASIFICACION INTEGER DEFAULT 0");

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_ordenpend] ("+
                    "GODIGO_ORDEN TEXT NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TIPO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GODIGO_ORDEN])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_ordenerror] ("+
                    "GODIGO_ORDEN TEXT NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TIPO INTEGER NOT NULL,"+
                    "ERROR TEXT NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GODIGO_ORDEN],[FECHA])"+
                    ");";

            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_orden_log] ("+
                    "COREL INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "METODO TEXT NOT NULL,"+
                    "ERROR TEXT NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";

            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_prodclasifmodif] ("+
                    "CODIGO_CLASIFICACION INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_GRUPO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CLASIFICACION])"+
                    ");";

            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_orden_mod] ("+
                    "COREL TEXT NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "IDMOD INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL],[ID],[IDMOD])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_orden_mod_idx1 ON T_orden_mod(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_orden_ing] ("+
                    "CODIGO_ING INTEGER NOT NULL,"+
                    "COREL TEXT NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "IDING INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PUID INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ING])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_orden_ing_idx1 ON T_orden_ing(CODIGO_ING)";
            db.execSQL(sql);

            sql="CREATE INDEX T_orden_ing_idx2 ON T_orden_ing(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_venta_ing] ("+
                    "CODIGO_ING INTEGER NOT NULL,"+
                    "ID INTEGER NOT NULL,"+
                    "IDING INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PUID INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_ING])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_venta_ing_idx1 ON T_venta_ing(CODIGO_ING)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {
            sql="CREATE TABLE [T_venta_mod] ("+
                    "ID INTEGER NOT NULL,"+
                    "IDMOD INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID],[IDMOD])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [T_pedidod] ("+
                    "COREL TEXT NOT NULL,"+
                    "COREL_DET INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "UMVENTA TEXT NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "CODIGO_TIPO_PRODUCTO TEXT NOT NULL,"+
                    "PRIMARY KEY ([COREL_DET])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX T_pedidod_idx1 ON T_pedidod(COREL)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [D_barril] ("+
                    "CODIGO_BARRIL TEXT NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_TIPO INTEGER NOT NULL,"+
                    "CODIGO_INTERNO TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "LOTE TEXT NOT NULL,"+
                    "FECHA_INICIO INTEGER NOT NULL,"+
                    "FECHA_CIERRE INTEGER NOT NULL,"+
                    "FECHA_VENCE INTEGER NOT NULL,"+
                    "FECHA_ENTRADA INTEGER NOT NULL,"+
                    "FECHA_SALIDA INTEGER NOT NULL,"+
                    "USUARIO_INICIO INTEGER NOT NULL,"+
                    "USUARIO_CIERRE INTEGER NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_BARRIL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_idx1 ON D_barril(CODIGO_INTERNO)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_idx2 ON D_barril(ACTIVO)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_idx3 ON D_barril(CODIGO_PRODUCTO)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_idx4 ON D_barril(STATCOM)";
            db.execSQL(sql);

        } catch (Exception e) {

        }

        try {

            sql="CREATE TABLE [D_barril_trans] ("+
                    "CODIGO_TRANS INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "FECHAHORA INTEGER NOT NULL,"+
                    "CODIGO_BARRIL TEXT NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CANTIDAD REAL NOT NULL,"+
                    "UM TEXT NOT NULL,"+
                    "MESERO INTEGER NOT NULL,"+
                    "TIPO_MOV INTEGER NOT NULL,"+
                    "IDTRANS TEXT NOT NULL,"+
                    "STATCOM INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_TRANS])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_trans_idx1 ON D_barril_trans(CODIGO_TRANS)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_trans_idx2 ON D_barril_trans(CODIGO_BARRIL)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_trans_idx3 ON D_barril_trans(CODIGO_PRODUCTO)";
            db.execSQL(sql);

            sql="CREATE INDEX D_barril_trans_idx4 ON D_barril_trans(STATCOM)";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {
            sql="CREATE TABLE [P_barril_tipo] ("+
                    "CODIGO_TIPO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "DESCRIPCION TEXT NOT NULL,"+
                    "CAPACIDAD REAL NOT NULL,"+
                    "MERMAMIN REAL NOT NULL,"+
                    "MERMAMAX REAL NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_TIPO])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {

            sql="CREATE TABLE [P_barril_barra] ("+
                    "CODIGO_BARRA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "BARRA TEXT NOT NULL,"+
                    "CODIGO_TIPO INTEGER NOT NULL,"+
                    "CODIGO_INTERNO TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_BARRA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_barril_barra_idx1 ON P_barril_barra(BARRA)";
            db.execSQL(sql);

        } catch (Exception e) {}


        try {

            sql="CREATE TABLE [P_cortesia] ("+
                    "CODIGO_CORTESIA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_CORTESIA])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) {}

        try {
            sql="ALTER TABLE P_cortesia ADD COLUMN CLAVE TEXT;";
            db.execSQL(sql);
        } catch (Exception e) {  }

        try {
            sql="DROP INDEX IX_P_CAJACIERRE ";
            db.execSQL(sql);
        } catch (Exception e) { }


        try {
            sql="CREATE TABLE [T_cierre_cred] ("+
                    "ID INTEGER NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "TOTAL REAL NOT NULL,"+
                    "CAJA REAL NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_vendedor_rol] ("+
                    "CODIGO_VENDEDOR_ROL INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "CODIGO_ROL INTEGER NOT NULL,"+
                    "fec_agr INTEGER NOT NULL,"+
                    "user_agr INTEGER NOT NULL,"+
                    "fec_mod INTEGER NOT NULL,"+
                    "user_mod INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_VENDEDOR_ROL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_vendedor_rol_idx1 ON P_vendedor_rol(CODIGO_VENDEDOR)";db.execSQL(sql);

        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_vendedor_sucursal] ("+
                    "CODIGO_VENDEDOR_SUCURSAL INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_SUCURSAL INTEGER NOT NULL,"+
                    "CODIGO_VENDEDOR INTEGER NOT NULL,"+
                    "fec_agr INTEGER NOT NULL,"+
                    "user_agr INTEGER NOT NULL,"+
                    "fec_mod INTEGER NOT NULL,"+
                    "user_mod INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_VENDEDOR_SUCURSAL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_vendedor_sucursal_idx1 ON P_vendedor_sucursal(CODIGO_VENDEDOR)";db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [D_cierre] ("+
                    "ID INTEGER NOT NULL,"+
                    "CIERRE INTEGER NOT NULL,"+
                    "FECHA INTEGER NOT NULL,"+
                    "TEXT TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_cierre_idx1 ON D_cierre(CIERRE)";db.execSQL(sql);
            sql="CREATE INDEX D_cierre_idx2 ON D_cierre(FECHA)";db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [D_facturahn] ("+
                    "COREL TEXT NOT NULL,"+
                    "SUBTOTAL REAL NOT NULL,"+
                    "EXON REAL NOT NULL,"+
                    "EXENTO REAL NOT NULL,"+
                    "GRAVADO REAL NOT NULL,"+
                    "IMP1 REAL NOT NULL,"+
                    "IMP2 REAL NOT NULL,"+
                    "VAL1 REAL NOT NULL,"+
                    "VAL2 REAL NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_stock_inv] ("+
                    "CODIGO_INVENTARIO_ENC INTEGER NOT NULL,"+
                    "TIPO TEXT NOT NULL,"+
                    "ESTADO INTEGER NOT NULL,"+
                    "CODIGO_ALMACEN INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_INVENTARIO_ENC])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_stock_inv_det] ("+
                    "CODIGO_INVENTARIO_ENC INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "UNIDADMEDIDA TEXT NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "COSTO REAL NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_INVENTARIO_ENC],[CODIGO_PRODUCTO])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_stock_inv_err] ("+
                    "CODIGO_INVENTARIO_ENC INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "NOTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_INVENTARIO_ENC],[CODIGO_PRODUCTO])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_tiponeg] ("+
                    "CODIGO_TIPO_NEGOCIO INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "DESCRIPCION TEXT NOT NULL,"+
                    "ACTIVO INTEGER NOT NULL,"+
                    "fec_agr INTEGER NOT NULL,"+
                    "user_agr INTEGER NOT NULL,"+
                    "fec_mod INTEGER NOT NULL,"+
                    "user_mod INTEGER NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_TIPO_NEGOCIO])"+
                    ");";
            db.execSQL(sql);
        } catch (Exception e) { }


        try {
            sql="CREATE TABLE [T_sv_gcont] ("+
                    "ID INTEGER NOT NULL,"+
                    "IDDEP TEXT NOT NULL,"+
                    "IDMUNI TEXT NOT NULL,"+
                    "IDNEG INTEGER NOT NULL,"+
                    "DEP TEXT NOT NULL,"+
                    "MUNI TEXT NOT NULL,"+
                    "NEG TEXT NOT NULL,"+
                    "PRIMARY KEY ([ID])"+
                    ");";
            db.execSQL(sql);

        } catch (Exception e) { }

        try {
            db.execSQL("ALTER TABLE D_FACTURA ADD CODIGO_TIPO_FACTURA INT NULL;");
        } catch (Exception e) { }



        try {
            sql="DROP TABLE D_facturat";
            db.execSQL(sql);


        } catch (Exception e) { }



        try {
            sql="CREATE TABLE [D_factura_sv] ("+
                    "COREL TEXT NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_TIPO_FACTURA INTEGER NOT NULL,"+
                    "CODIGO_DEPARTAMENTO TEXT NOT NULL,"+
                    "CODIGO_MUNICIPIO TEXT NOT NULL,"+
                    "CODIGO_TIPO_NEGOCIO INTEGER NOT NULL,"+
                    "PRIMARY KEY ([COREL])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX D_factura_sv_idx1 ON D_factura_sv(COREL)";db.execSQL(sql);
        } catch (Exception e) { }

        try {
            sql="CREATE TABLE [P_prodlista] ("+
                    "CODIGO_PRODLISTA INTEGER NOT NULL,"+
                    "CODIGO_LISTA INTEGER NOT NULL,"+
                    "EMPRESA INTEGER NOT NULL,"+
                    "CODIGO_PRODUCTO INTEGER NOT NULL,"+
                    "CANT REAL NOT NULL,"+
                    "CANT_MIN REAL NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO_PRODLISTA])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE INDEX P_prodlista_idx1 ON P_prodlista(CODIGO_LISTA)";db.execSQL(sql);
        } catch (Exception e) { }


        try {

        } catch (Exception e) { }

        try {

        } catch (Exception e) { }

    }

	private boolean update01() {

		try {

			db.beginTransaction();

            sql="CREATE TABLE [P_usgrupo] ("+
                    "CODIGO TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "CUENTA TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE TABLE [P_usgrupoopc] ("+
                    "GRUPO TEXT NOT NULL,"+
                    "OPCION INTEGER NOT NULL,"+
                    "PRIMARY KEY ([GRUPO],[OPCION])"+
                    ");";
            db.execSQL(sql);

            sql="CREATE TABLE [P_usopcion] ("+
                    "CODIGO INTEGER NOT NULL,"+
                    "MENUGROUP TEXT NOT NULL,"+
                    "NOMBRE TEXT NOT NULL,"+
                    "PRIMARY KEY ([CODIGO])"+
                    ");";
            db.execSQL(sql);

			db.setTransactionSuccessful();
			db.endTransaction();
		
		} catch (Exception e) {
			db.endTransaction();
		}

        flag=false;

        try {
            sql="SELECT * FROM P_usgrupo";
            dt=Con.OpenDT(sql);
            flag=dt.getCount()==0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

        if (!flag) return true;

        try {

            db.beginTransaction();
            db.execSQL("DELETE FROM P_usgrupo");
            db.execSQL("DELETE FROM P_usgrupoopc");
            db.execSQL("DELETE FROM P_usopcion");

            db.execSQL("INSERT INTO P_usgrupo VALUES (1,'Cajero','S')");
            db.execSQL("INSERT INTO P_usgrupo VALUES (2,'Supervisor','S')");
            db.execSQL("INSERT INTO P_usgrupo VALUES (3,'Gerente','S')");

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

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage()+"\n"+sql);return false;
        }

        return true;
		
	}

    private void update99() {

        try {

            db.beginTransaction();
            db.execSQL("UPDATE D_FACTURA SET STATCOM='S' WHERE COREL='28_201125082101'");
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
        }

    }

	//region Aux
	
 	private void msgbox(String msg) {
        ExDialog dialog = new ExDialog(cont);
		dialog.setMessage(msg);
					
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {  }
    	});
		dialog.show();
	
	}   		

	//endregion

}
