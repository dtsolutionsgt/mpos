package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.dtsgt.base.appGlobals;

public class CliPos extends PBase {

	private EditText txtNIT,txtNom,txtRef;

	private String snit,sname,sdir;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cli_pos);

		super.InitBase();
		addlog("CliPos",""+du.getActDateTime(),gl.vend);

		txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.requestFocus();
		txtNom = (EditText) findViewById(R.id.editText2);
		txtRef = (EditText) findViewById(R.id.editText1);

		setHandlers();	

		txtNIT.setText("");
		txtNom.setText("");
		txtRef.setText("");

        //gl.cliposflag=true;
	}
	
	//region  Events

	public void consFinal(View view) {
		if (agregaCliente("C.F.","Consumidor final","Ciudad")) procesaCF() ;
	}

	public void clienteNIT(View view) {

		try{
			snit=txtNIT.getText().toString();
			sname=txtNom.getText().toString();
			sdir=txtRef.getText().toString();

			if (!validaNIT(snit)) {
				toast("NIT incorrecto");return;
			}
			if (mu.emptystr(sname)) {
				toast("Nombre incorrecto");return;
			}

			if (agregaCliente(snit,sname,sdir)) procesaNIT(snit) ;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void buscarCliente(View view) {
        gl.cliente="";
        browse=1;
        startActivity(new Intent(this,Clientes.class));
    }

	private void setHandlers() {

		try{

            txtNIT.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                     buscaCliente();
                }
            });


		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    //endregion

	//region Main

	private void procesaCF() {

		try{
			gl.rutatipo="V";
            gl.cliente="C.F.";
            gl.nivel=1;
            gl.percepcion=0;
            gl.contrib="";

            gl.fnombre="Consumidor final";
            gl.fnit="C.F.";
            gl.fdir="Ciudad";
            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			limpiaCampos();

            finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void procesaNIT(String snit) {

		try{
			gl.rutatipo="V";

			gl.cliente=snit;
            gl.nivel=1;
            gl.percepcion=0;
            gl.contrib="";

            gl.fnombre=sname;
            gl.fnit=snit;
            gl.fdir=sdir;

            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			limpiaCampos();

			finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    //endregion
	
	//region Aux

	private boolean validaNIT(String N)  {

		try{
			String P, C, s, NC;
			int[] v = {0,0,0,0,0,0,0,0,0,0};
			int j, mp, sum, d11, m11, r11, cn, ll;

			N=N.trim();
			N=N.replaceAll(" ","");
			if (mu.emptystr(N)) return false;



		/*
		N=N.toUpperCase();
		if (N.equalsIgnoreCase("CF")) N="C.F.";
		if (N.equalsIgnoreCase("C/F")) N="C.F.";
		if (N.equalsIgnoreCase("C.F")) N="C.F.";
		if (N.equalsIgnoreCase("CF.")) N="C.F.";

		if (N.equalsIgnoreCase("C.F.")) return true;

		ll = N.length();
		if (ll<5) return false;

		P = N.substring(0,ll-2);
		C = N.substring(ll-1, ll);

		ll = ll - 1;
		sum = 0;

		try {

			for (int i = 0; i <ll-1; i++) {
				s =P.substring( i, i+1);
				j=Integer.parseInt(s);
				mp = ll + 1 - i-1;
				sum = sum + j * mp;
			}

			d11 =(int) Math.floor(sum/11);
			m11 = d11 * 11;
			r11 = sum - m11;
			cn = 11 - r11;

			if (cn == 10) s = "K"; else s=""+cn;

			if (cn>10) {
				cn = cn % 11;
				s =""+cn;
			}

			NC = P+"-"+s;
			//mu.msgbox(NC);

			if (N.equalsIgnoreCase(NC)) return true; else return false;

		} catch (Exception e) {
			return false;
		}
		*/
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
		return true;

	}
	
	private void buscaCliente() {
		Cursor DT;

		try{
			String NIT=txtNIT.getText().toString();
			if (mu.emptystr(NIT)) {
				txtNIT.requestFocus();return;
			}

			sql="SELECT Nombre,Direccion FROM P_CLIENTE WHERE CODIGO='"+NIT+"'";
			DT=Con.OpenDT(sql);
			DT.moveToFirst();

			txtNom.setText(DT.getString(0));
			txtRef.setText(DT.getString(1));
		} catch (Exception e){
		    txtNom.setText("");txtRef.setText("");
		}

	}

	private boolean agregaCliente(String NIT,String Nom,String dir) {

		try {

			ins.init("P_CLIENTE");

			ins.add("CODIGO",NIT);
			ins.add("NOMBRE",Nom);
			ins.add("BLOQUEADO","N");
			ins.add("TIPONEG","");
			ins.add("TIPO","");
			ins.add("SUBTIPO","");
			ins.add("CANAL","");
			ins.add("SUBCANAL","");
			ins.add("NIVELPRECIO",1);

			ins.add("MEDIAPAGO","1");
			ins.add("LIMITECREDITO",0);
			ins.add("DIACREDITO",0);
			ins.add("DESCUENTO","N");
			ins.add("BONIFICACION","N");
			ins.add("ULTVISITA",fecha);

			ins.add("IMPSPEC",0);
			ins.add("INVTIPO","N");
			ins.add("INVEQUIPO","N");
			ins.add("INV1","N");
			ins.add("INV2","N");
			ins.add("INV3","N");

			ins.add("NIT",NIT);
			ins.add("MENSAJE","N");
            ins.add("EMAIL","");
            ins.add("ESERVICE","");
	        ins.add("TELEFONO"," ");
			ins.add("DIRTIPO","N");
			ins.add("DIRECCION",dir);
            ins.add("REGION"," ");
			ins.add("SUCURSAL","1");

            ins.add("MUNICIPIO"," ");
            ins.add("CIUDAD"," ");
            ins.add("ZONA","1");
            ins.add("COLONIA"," ");
            ins.add("AVENIDA"," ");
            ins.add("CALLE"," ");
            ins.add("NUMERO"," ");
            ins.add("CARTOGRAFICO"," ");

    		ins.add("COORX",0);
			ins.add("COORY",0);
            ins.add("BODEGA","");
            ins.add("COD_PAIS","");
			ins.add("FIRMADIG","N");
			ins.add("CODBARRA","");
			ins.add("VALIDACREDITO","N");
            ins.add("FACT_VS_FACT","0");
            ins.add("CHEQUEPOST","N");

			ins.add("PRECIO_ESTRATEGICO","N");
			ins.add("NOMBRE_PROPIETARIO","");
			ins.add("NOMBRE_REPRESENTANTE","");
			ins.add("PERCEPCION",0);
			ins.add("TIPO_CONTRIBUYENTE","");
			ins.add("ID_DESPACHO",0);
			ins.add("ID_FACTURACION",0);
			ins.add("MODIF_PRECIO",0);

			db.execSQL(ins.sql());

			return true;

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);

			//toast(e.getMessage());
			//MU.msgbox("E", e.getMessage());

			try {

				upd.init("P_CLIENTE");
				upd.add("NOMBRE",Nom);
                upd.add("DIRECCION",dir);
				upd.Where("CODIGO='"+NIT+"'");

				db.execSQL(upd.sql());

				return true;

			} catch (SQLException e1) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
				mu.msgbox(e1.getMessage());return false;
			}

		}

	}

	private void limpiaCampos() {
		try{
			txtNIT.setText("");
			txtNom.setText("");
			txtNIT.requestFocus();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    //endregion

    //region Activity Events

    protected void onResume() {
        try{
            super.onResume();

            if (browse==1) {
                browse=0;
                if (! gl.cliente.isEmpty()) finish();
                return;
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion
}
