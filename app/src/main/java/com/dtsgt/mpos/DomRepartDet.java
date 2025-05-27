package com.dtsgt.mpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_repartidorObj;
import com.dtsgt.classes.extListDlg;

public class DomRepartDet extends PBase {

    private EditText txtnom, txtplaca;
    private TextView lblemp, lblstat;

    clsP_repartidorObj P_repartidorObj;

    clsClasses.clsP_repartidor item;

    int id;
    boolean newitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_repart_det);

            super.InitBase();

            txtnom =  findViewById(R.id.editTextNumber4);txtnom.requestFocus();
            txtplaca =  findViewById(R.id.editTextNumber9);
            lblemp =  findViewById(R.id.textView336);
            lblstat =  findViewById(R.id.textView4);

            id=gl.repartidor_codigo;

            P_repartidorObj=new clsP_repartidorObj(this,Con,db);

            if (id==0) newItem(); else loadItem();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doSave(View view) {
        msgask(0,"Guardar repartidor?");
    }

    public void doStatus(View view) {
        if (item.activo==1) {
            msgask(1,"Borrar registro");
        } else {
            msgask(2,"Activar registro");
        }
    }

    public void doEmpresa(View view) {
        listaEmpresas();
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            newitem=false;
            P_repartidorObj.fill("WHERE (CODIGO="+id+")");
            item=P_repartidorObj.first();

            showItem();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        try {
            lblstat.setVisibility(View.INVISIBLE);
            int newid=P_repartidorObj.newID("SELECT MAX(CODIGO) FROM P_repartidor");

            newitem=true;
            item=clsCls.new clsP_repartidor();

            item.codigo=newid;
            item.activo=1;
            item.nombre="";
            item.placa="";
            item.codigo_empresa=1;

            showItem();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showItem() {
        try {

            txtnom.setText(""+item.nombre);
            txtplaca.setText(""+item.placa);
            setCombo();
            if (item.activo==1) lblstat.setText("Borrar"); else lblstat.setText("Activar");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void saveItem() {
        try {
            if (newitem) {
                P_repartidorObj.add(item);
            } else {
                P_repartidorObj.update(item);
            }
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void statItem(int stat) {
        try {
            item.activo=stat;
            P_repartidorObj.update(item);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    if (checkValues()) saveItem();break;
                case 1:
                    statItem(0);msgexit("Registro borrado");
                    break;
                case 2:
                    statItem(1);msgexit("Registro activado");
                    break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void msgexit(String msg) {

        try{
            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);

            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Aux

    private void setCombo() {
        try {
            for (clsClasses.clsP_empresa_trans itm: gl.emptrans ) {
                if (itm.codigo==item.codigo_empresa) {
                    lblemp.setText(itm.nombre);break;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean checkValues() {
        try {
           String val=txtnom.getText().toString();
            val=capitalizeName(val);

           if (val.isEmpty() || val.length()<3 ) throw new Exception("Nombre incorrecto.");

           item.nombre=val;
           item.placa=txtplaca.getText().toString();

           return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    public void listaEmpresas() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(DomRepartDet.this,"Empresa transporte");

            for (int i = 0; i <gl.emptrans.size(); i++) {
                listdlg.add(gl.emptrans.get(i).codigo,gl.emptrans.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        item.codigo_empresa=listdlg.getCodigoInt(position);
                        lblemp.setText(listdlg.getText(position));

                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    public String capitalizeName(String str) {
        String[] words = str.split("\\s+");  // Split the string into words
        StringBuilder cname = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                cname.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return cname.toString().trim();
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            P_repartidorObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion


}