package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.appGlobals;

public class DescBon extends PBase {

	private TextView lblDesc;
	private EditText txtDesc;
	
	private String prodid;
	private double cant,desc;
	private int prommodo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desc_bon);
		
		super.InitBase();
		addlog("DescBon",""+du.getActDateTime(),String.valueOf(gl.vend));
		
		lblDesc= (TextView) findViewById(R.id.lblFecha);
		txtDesc = (EditText) findViewById(R.id.txtDesc);txtDesc.setEnabled(false);
		
		prodid=gl.promprod;
		cant=gl.promcant;
		desc=gl.promdesc;
		prommodo=gl.prommodo;
		
		gl.promapl=false;
		
		setHandlers();
		
		showProm();
	}
	
	private void showProm(){
		String ss;

		try{
			if (prommodo==0) ss=" %"; else ss=" (monto) ";

			lblDesc.setText("Máximo : "+mu.frmdecno(desc)+ss);
            lblDesc.setText("");
			txtDesc.setText(mu.frmdecno(desc));
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void setHandlers(){

		try{
			txtDesc.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
						aplProm(v);
						return true;
					}
					return false;
				}
			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}
	
	public void aplProm(View view){
		String svd;
		double vd;

		try{
			try {
				svd=txtDesc.getText().toString();
				svd=svd.replace(",",".");
				vd=Double.parseDouble(svd);

				if (vd<0) throw new Exception() ;
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
				mu.msgbox("Valor de descuento incorrecto");return;
			}

			if (vd>desc) {
				mu.msgbox("Valor de descuento es mayor que máximo");return;
			}

			desc=vd;
			gl.promprod=prodid;
			if (prommodo==0) {
				gl.promdesc=desc;
				gl.prommdesc=0;
			} else {
				gl.promdesc=0;
				gl.prommdesc=desc;
			}
			gl.promapl=true;

			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	
	// Activity Events

	@Override
	public void onBackPressed() {
		try{

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
		//super.onBackPressed();
	}	

}
