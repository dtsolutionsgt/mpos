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

import java.text.DecimalFormat;

public class CliPos extends PBase {

	private EditText txtNIT,txtNom,txtRef;

	private String snit,sname,sdir;
	private boolean consFinal=false;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cli_pos);

		super.InitBase();
		addlog("CliPos",""+du.getActDateTime(),String.valueOf(gl.vend));

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
    	consFinal=true;
		if (agregaCliente("C.F.","Consumidor final","Ciudad")) procesaCF() ;
	}

	public void clienteNIT(View view) {

		try{

			if (!existeCliente()){

				snit=txtNIT.getText().toString();
				sname=txtNom.getText().toString();
				sdir=txtRef.getText().toString();

				if (!validaNIT(snit)) {
					toast("NIT incorrecto");return;
				}

				if (mu.emptystr(sname)) {
					toast("Nombre incorrecto");return;
				}

				if (agregaCliente(snit,sname,sdir)){
					procesaNIT(snit) ;
				}

			}

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

            /*txtNIT.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                     buscaCliente();
                }
            });*/

			txtNIT.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {

					if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
							(event.getAction() == KeyEvent.ACTION_DOWN)) {

						if (!existeCliente()){
							txtNom.requestFocus();
						}

						return true;
					} else {
						return false;
					}
				}
			});

			txtNIT.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				public void onFocusChange(View v, boolean hasFocus) {

					if(!hasFocus)
					{

						if (!txtNIT.getText().toString().isEmpty()){
							if (!existeCliente()){
								txtNom.requestFocus();
							}
						}

					}
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

			gl.codigo_cliente = 0;
			gl.rutatipo="V";
            gl.cliente="0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente=gl.cliente;

            gl.fnombre="Consumidor final";
            gl.fnit="CF";
            gl.fdir="Ciudad";
            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			consFinal=false;
			limpiaCampos();

            finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void procesaNIT(String snit) {

        int codigo=nitnum(snit);

		try {
			gl.rutatipo="V";

			gl.cliente=""+codigo;
			gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

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

			int nnit=nitnum(NIT);

			sql="SELECT Nombre,Direccion FROM P_CLIENTE " +
				"WHERE  NIT = '"+NIT+"'";

			DT=Con.OpenDT(sql);
			DT.moveToFirst();

			if (nnit==0) {
				throw new Exception("NIT no es válido");
			}

			txtNom.setText(DT.getString(0));
			txtRef.setText(DT.getString(1));

			procesaNIT(NIT);

		} catch (Exception e){
		    txtNom.setText("");txtRef.setText("");
		}

	}

	private boolean existeCliente() {
		Cursor DT;
        boolean resultado=false;

		try{

			String NIT=txtNIT.getText().toString();

			if (mu.emptystr(NIT)) {
				txtNIT.requestFocus();
				resultado=false;
			} else {

				sql="SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO,TIPO_CONTRIBUYENTE,CODIGO_CLIENTE FROM P_CLIENTE " +
					"WHERE NIT = '" + NIT + "'";
				DT=Con.OpenDT(sql);

				if (DT != null){

					if (DT.getCount()>0){
						DT.moveToFirst();

						txtNom.setText(DT.getString(1));
						txtRef.setText(DT.getString(2));

						gl.rutatipo="V";
                        gl.cliente=DT.getString(0);
						gl.nivel=gl.nivel_sucursal;
						gl.percepcion=0;
						gl.contrib=DT.getString(6);;
						gl.scancliente = gl.cliente;
						gl.fnombre=txtNom.getText().toString();
						gl.fnit=NIT;
						gl.fdir=DT.getString(4);

						gl.media=DT.getInt(5);
						gl.codigo_cliente=DT.getInt(5);

						limpiaCampos();
						finish();

						resultado=true;
					}
				}
			}

		} catch (Exception e){
			mu.toast("Ocurrió un error buscando al cliente");
			resultado=false;
		}
		return resultado;
	}

	private boolean agregaCliente(String NIT,String Nom,String dir) {

        int codigo=nitnum(NIT);

		try {

			if (!consFinal) {
				if (codigo==0){
					toast("NIT no es válido");
					return false;
				}
			}

			ins.init("P_CLIENTE");

            ins.add("CODIGO_CLIENTE",codigo);

            ins.add("CODIGO",""+codigo);
            ins.add("NOMBRE",Nom);
            ins.add("BLOQUEADO","N");
            ins.add("NIVELPRECIO",1);
            ins.add("MEDIAPAGO","1");
            ins.add("LIMITECREDITO",0);
            ins.add("DIACREDITO",0);
            ins.add("DESCUENTO","S");
            ins.add("BONIFICACION","S");
            ins.add("ULTVISITA",du.getActDate());
            ins.add("IMPSPEC",0);
            ins.add("NIT",NIT.toUpperCase());
            ins.add("EMAIL","");
            ins.add("ESERVICE","N"); // estado envio
            ins.add("TELEFONO"," ");
            ins.add("DIRECCION",dir);
            ins.add("COORX",0);
            ins.add("COORY",0);
            ins.add("BODEGA",""+gl.sucur);
            ins.add("COD_PAIS","");
            ins.add("CODBARRA","");
  			ins.add("PERCEPCION",0);
			ins.add("TIPO_CONTRIBUYENTE","");

			db.execSQL(ins.sql());

			return true;

		} catch (Exception e) {

			try {

				upd.init("P_CLIENTE");
				upd.add("NOMBRE",Nom);
				upd.add("NIT",NIT);
                upd.add("DIRECCION",dir);
                upd.add("ESERVICE","N");
				upd.add("CODIGO","0");
				upd.Where("CODIGO_CLIENTE="+codigo);

				db.execSQL(upd.sql());

				return true;

			} catch (SQLException e1) {
				mu.msgbox(e1.getMessage());return false;
			}

		}

	}

	private int nitnum(String nit) {
        int pp;

        try {
            nit=nit.toUpperCase();
            pp=nit.indexOf("-");
            if (pp<0) return 0;

            int A=(int) nit.charAt(pp+1);
            String snit=nit.substring(0,pp)+A;
            int nnit=Integer.parseInt(snit);

            return nnit;
        } catch (Exception e) {
            return -1;
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
