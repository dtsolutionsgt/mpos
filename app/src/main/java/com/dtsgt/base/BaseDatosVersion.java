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

            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";db.execSQL(sql);
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
            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";db.execSQL(sql);
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
            sql="CREATE INDEX T_ordencombo_idx1 ON T_ordencombo(COREL)";db.execSQL(sql);
        } catch (Exception e) {
        }

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

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage()+"\n"+sql);return false;
        }

        return true;
		
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
