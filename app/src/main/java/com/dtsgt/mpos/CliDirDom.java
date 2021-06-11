package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_cliente_dirObj;
import com.dtsgt.classes.clsP_cliente_dir_bitaObj;

public class CliDirDom extends PBase {

    private EditText txtdir,txtref,txttel;
    private TextView lblBorrar;

    private clsP_cliente_dirObj P_cliente_dirObj;
    private clsP_cliente_dir_bitaObj P_cliente_dir_bitaObj;

    private clsClasses.clsP_cliente_dir_bita bitem = clsCls.new clsP_cliente_dir_bita();

    private int iddir,newbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cli_dir_dom);

        super.InitBase();

        txtdir=findViewById(R.id.editText1);
        txtref=findViewById(R.id.txtCorreo);
        txttel=findViewById(R.id.txt15);
        lblBorrar=findViewById(R.id.textView242);

        iddir=gl.idclidir;

        P_cliente_dirObj=new clsP_cliente_dirObj(this,Con,db);
        P_cliente_dir_bitaObj=new clsP_cliente_dir_bitaObj(this,Con,db);

        if (iddir==0) newItem();else loadItem();

    }

    //region Events

    public void doSave(View view) {
        if (txtdir.getText().toString().isEmpty()) {
            msgbox("Falta dirección");return;
        }
        msgAskSave("Guardar la dirección");
    }

    public void doDelete(View view) {
        msgAskDel("Borrar la dirección");
    }

    public void doExit(View view) {
        msgAskExit("Salir sin guardar");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            P_cliente_dirObj.fill("WHERE (CODIGO_DIRECCION="+iddir+")");

            txtdir.setText(P_cliente_dirObj.first().direccion);
            txtref.setText(P_cliente_dirObj.first().referencia);
            txttel.setText(P_cliente_dirObj.first().telefono);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        try {
            txtdir.setText("");
            txtref.setText("");
            txttel.setText("");
            lblBorrar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addItem() {
        int newid;

        try {

            newid=P_cliente_dirObj.newID("SELECT MAX(CODIGO_DIRECCION) FROM P_cliente_dir ");
            if (newid<1000000000) newid=1000000000;
            newbt=P_cliente_dir_bitaObj.newID("SELECT MAX(CODIGO_BITACORA) FROM P_cliente_dir_bita ");

            db.beginTransaction();

            clsClasses.clsP_cliente_dir item = clsCls.new clsP_cliente_dir();

            item.codigo_direccion=newid;
            item.codigo_cliente=gl.cliente_dom;
            item.referencia=""+txtref.getText().toString();
            item.codigo_departamento="2";
            item.codigo_municipio="10";
            item.direccion=""+txtdir.getText().toString();
            item.zona_entrega=0;
            item.telefono=""+txttel.getText().toString();

            P_cliente_dirObj.add(item);


            bitem.codigo_bitacora=newbt;
            bitem.codigo_direccion=newid;
            bitem.estado=1;
            bitem.codigo_cliente=gl.cliente_dom;
            bitem.direccion=""+txtdir.getText().toString();
            bitem.referencia=""+txtref.getText().toString();
            bitem.telefono=""+txttel.getText().toString();
            bitem.statcom=0;

            P_cliente_dir_bitaObj.add(bitem);


            db.setTransactionSuccessful();
            db.endTransaction();

            toast("Registro agregado");finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void updateItem() {

        try {
            newbt=P_cliente_dir_bitaObj.newID("SELECT MAX(CODIGO_BITACORA) FROM P_cliente_dir_bita ");

            db.beginTransaction();

            clsClasses.clsP_cliente_dir item = clsCls.new clsP_cliente_dir();

            item.codigo_direccion=iddir;
            item.codigo_cliente=gl.cliente_dom;
            item.referencia=""+txtref.getText().toString();
            item.codigo_departamento="2";
            item.codigo_municipio="10";
            item.direccion= ""+txtdir.getText().toString();
            item.zona_entrega=0;
            item.telefono=""+txttel.getText().toString();

            P_cliente_dirObj.update(item);

            bitem.codigo_bitacora=newbt;
            bitem.codigo_direccion=iddir;
            bitem.estado=2;
            bitem.codigo_cliente=gl.cliente_dom;
            bitem.direccion=""+txtdir.getText().toString();
            bitem.referencia=""+txtref.getText().toString();
            bitem.telefono=""+txttel.getText().toString();
            bitem.statcom=0;

            P_cliente_dir_bitaObj.add(bitem);


            db.setTransactionSuccessful();
            db.endTransaction();

            toast("Registro actualizado");finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void deleteItem() {
        try {

            newbt=P_cliente_dir_bitaObj.newID("SELECT MAX(CODIGO_BITACORA) FROM P_cliente_dir_bita ");

            db.beginTransaction();

            P_cliente_dirObj.delete(iddir);

            bitem.codigo_bitacora=newbt;
            bitem.codigo_direccion=iddir;
            bitem.estado=3;
            bitem.codigo_cliente=gl.cliente_dom;
            bitem.direccion="";
            bitem.referencia="";
            bitem.telefono="";
            bitem.statcom=0;

            P_cliente_dir_bitaObj.add(bitem);


            db.setTransactionSuccessful();
            db.endTransaction();

            toast("Registro eliminado");finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs

    private void msgAskSave(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Direccion de cliente");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (iddir==0) addItem();else updateItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskDel(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Direccion de cliente");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Direccion de cliente");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_cliente_dirObj.reconnect(Con,db);
            P_cliente_dir_bitaObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}