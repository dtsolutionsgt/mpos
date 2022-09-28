package com.dtsgt.mant;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsP_barril_tipoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import com.dtsgt.webservice.wsCommit;

public class MantBarril extends PBase {

    private EditText txtid,txtlote;
    private TextView lblprod,lbltipo,lblvence,lblrec,lblabr,lblcier,lblsave,lbldevol;
    private ImageView imgsave, imgdevol;
    private RelativeLayout relitem;

    private clsP_productoObj P_productoObj;
    private clsP_barril_tipoObj P_barril_tipoObj;
    private clsD_barrilObj D_barrilObj;

    private clsClasses.clsD_barril item;

    private wsCommit wscom;

    private Runnable rnSetSentFlag;

    private int idprod,idtipo;
    private String idbarril,lote,pkid;
    private long fvence,frec,fabr,fcier;
    private boolean nuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_barril);

        try {

            super.InitBase();

            txtid = findViewById(R.id.editTextTextPersonName);
            txtlote = findViewById(R.id.editTextTextPersonName2);
            lblprod = findViewById(R.id.textView285);
            lbltipo = findViewById(R.id.textView286);
            lblvence = findViewById(R.id.textView287);
            lblrec = findViewById(R.id.textView288);
            lblabr = findViewById(R.id.textView289);
            lblcier = findViewById(R.id.textView290);
            lblsave = findViewById(R.id.textView299);
            lbldevol = findViewById(R.id.textView300);
            imgsave = findViewById(R.id.imgImg2);
            imgdevol = findViewById(R.id.imageView135);
            relitem = findViewById(R.id.relativeLayout1);

            P_productoObj = new clsP_productoObj(this, Con, db);
            P_barril_tipoObj = new clsP_barril_tipoObj(this, Con, db);
            D_barrilObj = new clsD_barrilObj(this, Con, db);

            item = clsCls.new clsD_barril();

            wscom = new wsCommit(gl.wsurl);

            rnSetSentFlag = new Runnable() {
                public void run() {
                    validaEnvio();
                }
            };

            if (gl.gcods.isEmpty()) newItem();else loadItem();

            relitem.setEnabled(item.activo==-1);

            imgsave.setVisibility(View.VISIBLE);lblsave.setVisibility(View.VISIBLE);
            imgdevol.setVisibility(View.VISIBLE);lbldevol.setVisibility(View.VISIBLE);

            if (item.activo==1) {  // Abierto
                imgsave.setVisibility(View.INVISIBLE);lblsave.setVisibility(View.INVISIBLE);
                imgdevol.setVisibility(View.INVISIBLE);lbldevol.setVisibility(View.INVISIBLE);
            }

            if (nuevo) {
                imgdevol.setVisibility(View.INVISIBLE);lbldevol.setVisibility(View.INVISIBLE);
            }

            if (item.fecha_inicio>0) {
                imgsave.setVisibility(View.INVISIBLE);
                lblsave.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doProd(View view) {
        if (item.activo==-1) listProd();
    }

    public void doTipo(View view) {
        if (item.activo==-1) listTipo();
    }

    public void doVence(View view) {
        obtenerFecha();
    }

    public void doSave(View view) {
        if (validaValores()) msgAskSave("Guardar registro");
    }

    public void doDel(View view) {
        msgAskDel("Eliminar registro");
    }

    public void doExit(View view) {
        msgAskExit("Salir sin guardar registro");
    }

    //endregion

    //region Main

    private void addItem() {
        try {
            item.codigo_producto=idprod;
            item.codigo_tipo=idtipo;
            item.codigo_interno=idbarril;
            item.lote=lote;
            item.fecha_vence=fvence;
            item.statcom=0;

            D_barrilObj.add(item);

            agregaRegistro();
            toast("Registro agregado");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            item.codigo_producto=idprod;
            item.codigo_tipo=idtipo;
            item.codigo_interno=idbarril;
            item.lote=lote;
            item.fecha_vence=fvence;
            item.statcom=0;

            D_barrilObj.update(item);

            actualizaRegistro();
            toast("Registro actualizado");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showItem() {

        if (item.activo==-1) {
            imgdevol.setVisibility(View.VISIBLE);lbldevol.setVisibility(View.VISIBLE);
        } else {
            imgdevol.setVisibility(View.INVISIBLE);lbldevol.setVisibility(View.INVISIBLE);
        }

        if (nuevo) {
            imgdevol.setVisibility(View.INVISIBLE);lbldevol.setVisibility(View.INVISIBLE);
        }

        try {
            lblprod.setText("");
            P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+idprod+")");
            if (P_productoObj.count>0) lblprod.setText(P_productoObj.first().desccorta);

            lbltipo.setText("");
            P_barril_tipoObj.fill("WHERE (CODIGO_TIPO="+idtipo+")");
            if (P_barril_tipoObj.count>0) lbltipo.setText(P_barril_tipoObj.first().descripcion);

            lblvence.setText(du.sfecha(fvence));
            lblrec.setText(du.sfecha(frec));
            lblabr.setText(du.sfecha(fabr));
            lblcier.setText(du.sfecha(fcier));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        nuevo=true;

        try {
            idprod=0;
            idtipo=0;
            idbarril="";
            lote="";
            fvence=du.getActDate();
            frec=du.getActDateTime();
            fabr=0;
            fcier=0;

            item.codigo_barril=gl.codigo_ruta+"_"+mu.getCorelBase();
            item.empresa=gl.emp;
            item.codigo_sucursal=gl.tienda;
            item.codigo_tipo=idtipo;
            item.codigo_interno=idbarril;
            item.activo=-1;
            item.codigo_producto=idprod;
            item.lote=lote;
            item.fecha_inicio=fabr;
            item.fecha_cierre=fcier;
            item.fecha_vence=fvence;
            item.fecha_entrada=frec;
            item.fecha_salida=0;
            item.usuario_inicio=0;
            item.usuario_cierre=0;
            item.statcom=-1;

            showItem();
            txtid.setText(idbarril);
            txtlote.setText(lote);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void loadItem() {
        nuevo=false;

        try {
            D_barrilObj.fill("WHERE (CODIGO_BARRIL='"+gl.gcods+"')");
            item=D_barrilObj.first();

            idtipo=item.codigo_tipo;
            idbarril=item.codigo_interno;
            idprod=item.codigo_producto;
            lote=item.lote;

            fvence=item.fecha_vence;
            fabr=item.fecha_inicio;
            fcier=item.fecha_cierre;
            frec=item.fecha_entrada;

            showItem();

            txtid.setText(idbarril);
            txtlote.setText(lote);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Envio

    private void agregaRegistro() {
        try {
            pkid=item.codigo_barril;
            sql= addItemSqlAndroid(item);
            wscom.execute(sql,rnSetSentFlag);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void actualizaRegistro() {
        try {
            pkid=item.codigo_barril;
            sql= updateItemSqlAndroid(item);
            wscom.execute(sql,rnSetSentFlag);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void deshabilitaRegistro() {
        try {
            pkid=item.codigo_barril;
            sql= disableItemSqlAndroid(item);
            db.execSQL(disableItemSql(item));
            wscom.execute(sql,rnSetSentFlag);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void validaEnvio() {
        try {
            if (!wscom.errflag) {
                try {
                    sql="UPDATE D_barril SET STATCOM=1 WHERE CODIGO_BARRIL='"+pkid+"'";
                    db.execSQL(sql);
                } catch (Exception e) {
                    toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            } else {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+wscom.error);
            }
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public String addItemSqlAndroid(clsClasses.clsD_barril item) {
        String fv=""+du.univfechahora(item.fecha_vence);
        String fe=""+du.univfechahora(item.fecha_entrada);

        ins.init("D_barril");

        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("LOTE",item.lote);
        //ins.add("FECHA_INICIO",item.fecha_inicio);
        //ins.add("FECHA_CIERRE",item.fecha_cierre);
        ins.add("FECHA_VENCE",fv);
        ins.add("FECHA_ENTRADA",fe);
        //ins.add("FECHA_SALIDA",item.fecha_salida);
        ins.add("USUARIO_INICIO",item.usuario_inicio);
        ins.add("USUARIO_CIERRE",item.usuario_cierre);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSqlAndroid(clsClasses.clsD_barril item) {
        String fv=""+du.univfechahora(item.fecha_vence);

        upd.init("D_barril");

        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("LOTE",item.lote);
        upd.add("FECHA_VENCE",fv);

        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");

        return upd.sql();

    }

    public String disableItemSqlAndroid(clsClasses.clsD_barril item) {
        String fc=""+du.univfechahora(du.getActDateTime());

        upd.init("D_barril");

        upd.add("ACTIVO",-99);
        upd.add("FECHA_CIERRE",fc);

        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");

        return upd.sql();
    }

    public String disableItemSql(clsClasses.clsD_barril item) {

        upd.init("D_barril");

        upd.add("ACTIVO",-99);
        upd.add("FECHA_CIERRE",du.getActDateTime());

        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");

        return upd.sql();
    }

    //endregion

    //region Aux

    private void listProd() {
        try {
            P_productoObj.fill("WHERE (CODIGO_TIPO='B') AND (ACTIVO=1) ORDER BY DESCCORTA");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(MantBarril.this,"Producto");

            for (int i = 0; i <P_productoObj.count; i++) {
                listdlg.add(P_productoObj.items.get(i).codigo_producto,P_productoObj.items.get(i).desccorta);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idprod=listdlg.getCodigoInt(position);
                        showItem();
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

    private void listTipo() {
        try {
            P_barril_tipoObj.fill("WHERE (ACTIVO=1) ORDER BY DESCRIPCION");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(MantBarril.this,"Tipo barril");

            for (int i = 0; i <P_barril_tipoObj.count; i++) {
                listdlg.add(P_barril_tipoObj.items.get(i).codigo_tipo,P_barril_tipoObj.items.get(i).descripcion);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idtipo=listdlg.getCodigoInt(position);
                        showItem();
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

    private void obtenerFecha(){
        try {
            DatePickerDialog datedlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    fvence = du.cfecha(year, month + 1, dayOfMonth);
                    lblvence.setText(du.sfecha(fvence));
                }
            }, (int) du.getyear(fvence), (int) (du.getmonth(fvence)-1), (int) du.getday(fvence));

            datedlg.show();
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean validaValores() {
        idbarril=txtid.getText().toString();
        lote=txtlote.getText().toString();

        if (idprod==0) {
            msgbox("Falta definir producto");return false;
        }
        if (idtipo==0) {
            msgbox("Falta definir tipo");return false;
        }
        if (idbarril.isEmpty()) {
            msgbox("Falta identificación de barril"); return false;
        }

        return true;
    }

    //endregion

    //region Dialogs

    private void msgAskSave(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (nuevo) {
                    addItem();
                } else {
                    updateItem();
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskDel(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deshabilitaRegistro();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
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
    public void onResume() {
        super.onResume();
        try {
            P_productoObj.reconnect(Con,db);
            P_barril_tipoObj.reconnect(Con,db);
            D_barrilObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}