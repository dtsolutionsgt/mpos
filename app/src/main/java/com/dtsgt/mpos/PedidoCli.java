package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocObj;
import com.dtsgt.webservice.srvPedidoDir;

public class PedidoCli extends PBase {

    private EditText txtnit,txttel,txtnom,txtdir,txtref;

    private clsD_pedidocObj D_pedidocObj;
    private clsClasses.clsD_pedidoc item=clsCls.new clsD_pedidoc();

    private String pedid,nit,dir;
    private int idcli;
    private boolean changed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_pedido_cli);
        } else {
            setContentView(R.layout.activity_pedido_cli_ver);
        }

        super.InitBase();

        txtnit=findViewById(R.id.txt1);
        txttel=findViewById(R.id.txt15);
        txtnom=findViewById(R.id.editText2);
        txtdir=findViewById(R.id.editText1);
        txtref=findViewById(R.id.txtCorreo);

        pedid=gl.pedid;

        D_pedidocObj=new clsD_pedidocObj(this,Con,db);

        setHandlers();

        loadItem();
    }

    //region Events

    public void doSave(View view) {
        if (!checkItem()) return;
        if (nit.isEmpty() | nit.length()<8) {
            msgAskCF("Aplicar como CONSUMIDOR FINAL");return;
        }
        msgAskSave("Aplicar");
    }

    private void setHandlers() {

        txtnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                changed=true;
            }
        });

        txttel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                changed=true;
            }
        });

        txtnom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                changed=true;
            }
        });

        txtdir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                changed=true;
            }
        });

        txtref.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                changed=true;
            }
        });

    }

    public void doExit(View view) {
        if (changed) msgAskExit("Salir sin guardar"); else finish();
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            D_pedidocObj.fill("WHERE (corel='"+pedid+"')");
            item=D_pedidocObj.first();

        } catch (Exception e) {
            item.corel="";item.nit="";item.telefono="";item.nombre="";item.direccion="";item.referencia="";
        }

        txtnit.setText(""+item.nit);
        txttel.setText(""+item.telefono);
        txtnom.setText(""+item.nombre);
        txtdir.setText(""+item.direccion);dir=item.direccion;
        txtref.setText(""+item.referencia);txtref.requestFocus();

        changed=false;
    }

    private void saveItem() {
        try {
            if (item.corel.isEmpty() | item.corel.length()<2) {
                D_pedidocObj.add(item);
            } else {
                D_pedidocObj.update(item);
            }

            if (!item.direccion.equalsIgnoreCase(dir)) {

                clsD_pedidoObj D_pedidoObj=new clsD_pedidoObj(this,Con,db);
                D_pedidoObj.fill("WHERE (corel='"+pedid+"')");
                idcli=D_pedidoObj.first().codigo_cliente;

                if (idcli!=gl.emp*10) {
                    Intent intent = new Intent(PedidoCli.this, srvPedidoDir.class);

                    intent.putExtra("URL",gl.wsurl);
                    intent.putExtra("codigo",idcli);
                    intent.putExtra("dir",item.direccion);

                    startService(intent);
                }
            }

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private boolean checkItem() {
        String ss;

        try {
            nit=txtnit.getText().toString();

            ss=txttel.getText().toString();
            if (ss.isEmpty()) {
                toast("Falta telefono");txttel.requestFocus();return false;
            }
            ss=txtnom.getText().toString();
            if (ss.isEmpty()) {
                toast("Falta nombre");txtnom.requestFocus();return false;
            }
            ss=txtdir.getText().toString();
            if (ss.isEmpty()) {
                toast("Falta direccion");txtdir.requestFocus();return false;
            }

            item.nit=""+nit;
            item.telefono=txttel.getText().toString();
            item.nombre=txtnom.getText().toString();
            item.direccion=txtdir.getText().toString();
            item.referencia=""+txtref.getText().toString();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
        return true;
    }

    private boolean isEmpty() {
        String ss;

        try {
            ss=txtnit.getText().toString();if (!ss.isEmpty()) return false;
            ss=txttel.getText().toString();if (!ss.isEmpty()) return false;
            ss=txtnom.getText().toString();if (!ss.isEmpty()) return false;
            ss=txtdir.getText().toString();if (!ss.isEmpty()) return false;
            ss=txtref.getText().toString();if (!ss.isEmpty()) return false;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return true;
    }

    private void aplicaCF() {
        int cc=gl.emp*10;

        try {

            item.nit="CF";
            item.telefono=txttel.getText().toString();
            item.nombre=txtnom.getText().toString();
            item.direccion=txtdir.getText().toString();
            item.referencia=""+txtref.getText().toString();

            sql="UPDATE D_PEDIDO SET CODIGO_CLIENTE="+cc+" WHERE Corel='"+pedid+"'";
            db.execSQL(sql);

            saveItem();
        } catch (Exception e) {
             msgbox(e.getMessage());
        }

    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Title");
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

    private void msgAskCF(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Title");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aplicaCF();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskSave(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Title");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveItem();
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
            D_pedidocObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}