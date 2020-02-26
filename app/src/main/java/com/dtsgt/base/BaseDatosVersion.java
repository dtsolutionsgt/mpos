package com.dtsgt.base;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.dtsgt.mpos.R;

public class BaseDatosVersion {

	public ArrayList<String> items=new ArrayList<String>();
	
	private Context cont;
	private SQLiteDatabase db;
	private BaseDatos Con;
	private String sql;	
	
	private int aver,pver;
	
	public BaseDatosVersion(Context context,SQLiteDatabase dbase,BaseDatos dbCon) {
		cont=context;
		db=dbase;
		Con=dbCon;
	}


    public void update(){
        if (!update01()) return;
    }
	

	// Private


	
	private boolean update01() {

		try {

			db.beginTransaction();


			db.execSQL(sql);


			db.setTransactionSuccessful();
			db.endTransaction();
		
		} catch (Exception e) {
			db.endTransaction();
			msgbox(e.getMessage()+"\n"+sql);return false;
		}	
		
		return true;		
		
	}
	

	// Aux
	
 	private void msgbox(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(cont);
    	
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(msg);
					
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {  }
    	});
		dialog.show();
	
	}   		
	
}
