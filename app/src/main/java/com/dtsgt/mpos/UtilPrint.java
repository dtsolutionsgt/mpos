package com.dtsgt.mpos;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_archivoconfObj;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class UtilPrint extends PBase {
	
	private Spinner  spinPrint,spinNum;
	private EditText txtPar;
	private TextView lbl1;

	private ArrayList<String> spincode= new ArrayList<String>();
    private ArrayList<String> prnnum= new ArrayList<String>();
    private ArrayList<String> prnmac= new ArrayList<String>();
    private ArrayList<Integer> prntipo= new ArrayList<Integer>();

    private String prtipo,prpar;
    private int pridx,prnum=0, tipoimp;

	private AppMethods app;
    private printer prn;
    private Runnable printclose,printcallback;

    // Epson TM - "00:01:90:85:0D:8C"
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_util_print);
		
		super.InitBase();
		addlog("UtilPrint",""+du.getActDateTime(),gl.vend);
		
		spinPrint = (Spinner) findViewById(R.id.spinner1);
        spinNum = (Spinner) findViewById(R.id.spinner19);
		txtPar = (EditText) findViewById(R.id.txtFilter);
        lbl1 = (TextView) findViewById(R.id.textView209);lbl1.setText("");

		app = new AppMethods(this, gl, Con, db);

		setHandlers();

        fillSpinnerNum();

		//loadItem();
		//fillSpinnerTipos();
		
		buildFile();

		printcallback= new Runnable() {
		    public void run() { }
		};
				
		printclose= new Runnable() {
		    public void run() {
		    	//UtilPrint.super.finish();
		    }
		};
		
		prn=new printer(this,printclose,true);

		
	}

	// Events
	
	public void doApply(View view) {
		try{
			updateItem();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	public void doTestPrint(View view) {

		try{
			if (prtipo.equalsIgnoreCase("SIN IMPRESORA")) return;

			prpar=txtPar.getText().toString().trim();

			if (mu.emptystr(prpar)) {
				mu.msgbox("Parametro de impresion incorrecto");return;
			}

			if (buildFile()) {
			   app.doPrint(tipoimp);
            }

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}
		
	private void setHandlers(){
		    
		spinPrint.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	TextView spinlabel;
		       	
		    	try {
		    		spinlabel=(TextView)parentView.getChildAt(0);
			    	spinlabel.setTextColor(Color.BLACK);
			    	spinlabel.setPadding(5, 0, 0, 0);
			    	spinlabel.setTextSize(18);
				    
			    	prtipo=spincode.get(position);
				    pridx=position;	

		        } catch (Exception e) {
					addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
				   	mu.msgbox( e.getMessage());
		        }			    	
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        return;
		    }

		});

        spinNum.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TextView spinlabel;

                try {
                    spinlabel=(TextView)parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);
                    spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(18);

                    prnum=Integer.parseInt(prnnum.get(position));
                    tipoimp =prntipo.get(position);

                    if (tipoimp ==0)  lbl1.setText("Impresora facturación");else  lbl1.setText("Impresora cosina");
                    loadItem();

                } catch (Exception e) {
                    mu.msgbox( e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }
        });
	}
	
	// Main
	
	private void loadItem() {
		Cursor DT;

        prtipo="";prpar="";

		try {

			sql="SELECT TIPO_IMPRESORA,PUERTO_IMPRESION FROM P_ARCHIVOCONF WHERE CODIGO_ARCHIVOCONF="+prnum;
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0) {
                DT.moveToFirst();
				prtipo=DT.getString(0);
				prpar=DT.getString(1);
			} else {
                //addItem();
            }

            fillSpinnerTipos();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			prtipo="";prpar="";
		   	mu.msgbox( e.getMessage());
	    }	
		
		txtPar.setText(prpar);
        if (gl.debug) {
            if (prpar.isEmpty()) txtPar.setText("00:01:90:85:0D:8C");
        }

	}
	
	private void updateItem() {
		
		try {
			
			prpar=txtPar.getText().toString().trim();

			if(gl.impresora.equalsIgnoreCase("S")){
				if(prtipo.equalsIgnoreCase("SIN IMPRESORA")){
					msgbox("Debe seleccionar una impresora.");
				} else {
					sql="UPDATE P_ARCHIVOCONF SET TIPO_IMPRESORA='"+prtipo+"',PUERTO_IMPRESION='"+prpar+"' WHERE CODIGO_ARCHIVOCONF="+prnum;
					db.execSQL(sql);

					Toast.makeText(this,"Configuración guardada.", Toast.LENGTH_SHORT).show();
					super.finish();
				}
			} else {
				sql="UPDATE P_ARCHIVOCONF SET TIPO_IMPRESORA='"+prtipo+"',PUERTO_IMPRESION='"+prpar+"' WHERE CODIGO_ARCHIVOCONF="+prnum;
				db.execSQL(sql);
                msgbox("Configuración guardada.");
			}
		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox( e.getMessage());
		}
		
	}

	private void addItem() {

        try {
            clsP_archivoconfObj arch=new clsP_archivoconfObj(this,Con,db);
            clsClasses.clsP_archivoconf item = clsCls.new clsP_archivoconf();

            item.ruta = gl.caja;
            item.tipo_hh="";
            item.idioma="";
            item.tipo_impresora="SIN IMPRESORA";
            item.serial_hh="";
            item.modif_peso="";
            item.puerto_impresion="";
            item.lbs_o_kgs="";
            item.nota_credito=0;

            arch.add(item);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

	// Aux

    private void fillSpinnerNum() 	{
        Cursor dt;

        try {

            prnnum.clear();prnmac.clear();prntipo.clear();prnum=0;

            sql="SELECT CODIGO_ARCHIVOCONF,PUERTO_IMPRESION,NOTA_CREDITO FROM P_ARCHIVOCONF WHERE RUTA="+gl.codigo_ruta;
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {
                    prnnum.add(dt.getString(0));
                    prnmac.add(dt.getString(0)+" - ( MAC : "+dt.getString(1)+" )");
                    prntipo.add(dt.getInt(2));

                    dt.moveToNext();
                }
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, prnmac);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinNum.setAdapter(dataAdapter);
            spinNum.setSelection(0);

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            Log.d("e",e.getMessage());
        }
    }

    private void fillSpinnerTipos() 	{
		int sp=0;
	
		try {

			spincode.clear();
			
			s="SIN IMPRESORA";spincode.add(s); if (prtipo.equalsIgnoreCase(s)) sp=0;
			s="EPSON TM BlueTooth";spincode.add(s); if (prtipo.equalsIgnoreCase(s)) sp=1;
            s="HP Engage USB";spincode.add(s); if (prtipo.equalsIgnoreCase(s)) sp=2;

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spincode);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
			spinPrint.setAdapter(dataAdapter);
			spinPrint.setSelection(sp);

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			Log.d("e",e.getMessage());
		}
	}	
	
	private boolean buildFile() {

		BufferedWriter writer = null;
		FileWriter wfile;
		String fname;	
		
		fname = Environment.getExternalStorageDirectory()+"/"+"print.txt";
		
		try {

			wfile=new FileWriter(fname,false);
			writer = new BufferedWriter(wfile);
			
			writer.write(" ");writer.write("\r\n");
			writer.write("Impresion linea 1");writer.write("\r\n");
			writer.write("Impresion linea 2");writer.write("\r\n");
			writer.write("Impresion linea 3");writer.write("\r\n");
			writer.write(" ");writer.write("\r\n");
			writer.write(" ");writer.write("\r\n");
			
		    writer.close();

		    return true;
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("No se puede crear archivo de impresion : "+e.getMessage());return false;
		}
		
	}

}
