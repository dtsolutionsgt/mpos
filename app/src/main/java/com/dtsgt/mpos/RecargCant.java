package com.dtsgt.mpos;

import java.text.DecimalFormat;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.appGlobals;

public class RecargCant extends PBase {

	private EditText txtCant, txtPrecio;
	private RelativeLayout rlCant;
	private TextView lblDesc,lblPrec,lblBU;
		
	private String prodid,estado,raz;
	private double cant,icant, precio, iprecio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recarg_cant);
		
		super.InitBase();
		addlog("RecargCant",""+du.getActDateTime(),gl.vend);
		
		setControls();
		
		prodid=((appGlobals) vApp).prod;
		estado=((appGlobals) vApp).devtipo;
		raz=((appGlobals) vApp).gstr;
		precio = gl.precio_recarga;
	
		setHandlers();
		
		((appGlobals) vApp).dval=-1;

		showData();

		if (gl.peDecCant==0) {
			txtCant.setInputType(InputType.TYPE_CLASS_NUMBER );
		} else {
			txtCant.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}

		txtCant.requestFocus();

		//txtCant.setText(((appGlobals) vApp).gstr2);
	}
	

	// Events
	
	public void sendCant(View view) {
		aplicaCant();
	}

	private void aplicaCant(){
		try{

			setCant();

			if (cant<0){
				mu.msgbox("Cantidad incorrecta");
				txtCant.requestFocus();
				return;
			}

			setPrecio();

			if (precio<=0){
				mu.msgbox("Precio incorrecto");
				txtPrecio.requestFocus();
				return;
			}

			((appGlobals) vApp).dval=cant;
			((appGlobals) vApp).precio_recarga=precio;
			((appGlobals) vApp).devrazon="0";

			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	// Main
	
	private void setHandlers(){

		try{

			txtCant.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						txtPrecio.requestFocus();
						txtPrecio.setText("");

						return true;
					}
					return false;
				}
			});

			txtPrecio.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						aplicaCant();
						return true;
					}
					return false;
				}
			});

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

		
	}
	
	private void showData() {
		Cursor DT;
		String ubas;
		double costo;
		
		try {
			sql="SELECT UNIDBAS,UNIDMED,UNIMEDFACT,UNIGRA,UNIGRAFACT,DESCCORTA,IMAGEN,DESCLARGA,COSTO "+
				 "FROM P_PRODUCTO WHERE CODIGO='"+prodid+"'";
           	DT=Con.OpenDT(sql);
			DT.moveToFirst();
							  
			ubas=DT.getString(0);
			
			lblBU.setText(ubas);((appGlobals) vApp).ubas=ubas;
			lblDesc.setText(DT.getString(7));
			costo=DT.getDouble(8);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		   mu.msgbox("1-"+ e.getMessage());costo=0;
	    }

		gl.costo=costo;
		lblPrec.setText(mu.frmdec(costo));
		txtPrecio.setText(mu.frmdec(costo));

		try {
			sql="SELECT CANT, PRECIO FROM T_CxCD WHERE CODIGO='"+prodid+"' AND CODDEV='"+raz+"'";
           	DT=Con.OpenDT(sql);

			if (DT != null) {
				if(DT.getCount()>0){
					DT.moveToFirst();
					icant=DT.getDouble(0);
					iprecio=DT.getDouble(1);

					if (icant>0) parseCant(icant);
					if (iprecio>0) parsePrecio(icant);

				}
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			icant=0;
			iprecio=0;
	    }	

	}
	
	private void setCant(){
		try {
			cant=Double.parseDouble(txtCant.getText().toString());			
			cant=mu.round(cant,gl.peDecCant);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			cant=-1; 
		}
	}

	private void setPrecio(){
		try {
			precio=Double.parseDouble(txtPrecio.getText().toString());
			precio=mu.round(precio,gl.peDecCant);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			precio=0;
		}
	}
	
	private void parseCant(double c) {
		try{
			DecimalFormat frmdec = new DecimalFormat("#.####");
			double ub;

			ub=c;
			if (ub>0) txtCant.setText(frmdec.format(ub));
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	private void parsePrecio(double c) {
		try{
			DecimalFormat frmdec = new DecimalFormat("#.####");
			double ub;

			ub=c;
			if (ub>0) txtPrecio.setText(frmdec.format(ub));
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	// Aux

	private void setControls() {
		try{
			txtCant= (EditText) findViewById(R.id.txtFilter);
			txtPrecio= (EditText) findViewById(R.id.txtFilterPrec);
			rlCant= (RelativeLayout) findViewById(R.id.rlCant);
			lblDesc=(TextView) findViewById(R.id.lblFecha);
			lblPrec=(TextView) findViewById(R.id.lblPNum);
			lblBU=(TextView) findViewById(R.id.lblBU);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}	
	
}
