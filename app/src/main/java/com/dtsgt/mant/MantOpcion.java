package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_prodopcObj;
import com.dtsgt.classes.clsP_prodopclistObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.ladapt.LA_P_prodopclist;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MantOpcion extends PBase {

    private ImageView imgstat;
    private EditText txt1,txt2;
    private ListView listView;

    private clsP_prodopcObj holder;
    private clsP_prodopclistObj P_prodopclistObj;
    private clsClasses.clsP_prodopc item=clsCls.new clsP_prodopc();
    public ArrayList<clsClasses.clsP_prodopclist> items = new ArrayList<clsClasses.clsP_prodopclist>();

    private LA_P_prodopclist adapter;

    private int id;
    private String selid="",selname;
    private boolean newitem=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_opcion);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        listView = (ListView) findViewById(R.id.listView1);

        holder =new clsP_prodopcObj(this,Con,db);
        P_prodopclistObj=new clsP_prodopclistObj(this,Con,db);

        setHandlers();

        if (gl.gcods.isEmpty()) {
            newItem();
        } else {
            id=Integer.parseInt(gl.gcods);
            loadItem();
            listItems();
        }
    }

    //region Events

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    public void doStatus(View view) {
        if (item.activo==1) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doAddItem(View view) {
        browse=1;
        gl.listaedit=false;
        gl.mantid=8;
        startActivity(new Intent(this,Lista2.class));
    }

    public void doDelItem(View view) {
        msgAskDelete("Eliminar "+selname);
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_prodopclist item = (clsClasses.clsP_prodopclist)lvObj;

                adapter.setSelectedIndex(position);
                selidx=position;
                selid=item.producto;
                selname=item.nombre;
            };
        });
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE ID="+id);
            item=holder.first();

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.activo==1) {
                imgstat.setImageResource(R.drawable.delete_64);
            } else {
                imgstat.setImageResource(R.drawable.mas);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.id=0;
        item.nombre="";
        item.activo=1;

        showItem();
    }

    private void addItem() {
        try {

            db.beginTransaction();

            holder.add(item);

            for (int i = 0; i <items.size(); i++) {
                P_prodopclistObj.add(items.get(i));
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.gcods=""+item.id;
            finish();

        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());
        }
    }

    private void updateItem() {
        try {

            db.beginTransaction();

            holder.update(item);

            P_prodopclistObj.delete(id);

            for (int i = 0; i <items.size(); i++) {
                P_prodopclistObj.add(items.get(i));
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    private void addListItem() {
         if (gl.pickcode.isEmpty()) return;

        try {

            for (int i = 0; i <items.size(); i++) {
                if (items.get(i).producto.equalsIgnoreCase(gl.pickcode)) return;
            }

            clsClasses.clsP_prodopclist pitem=clsCls.new clsP_prodopclist();

            pitem.id= id;
            pitem.producto = gl.pickcode;
            pitem.cant=1;
            pitem.idreceta=0;
            pitem.nombre=gl.pickname;

            items.add(pitem);

            updateItems();

        } catch (Exception e) {
             msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void delListItem() {
        if (selidx==-1) return;

        try {
            items.remove(selidx);
            updateItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region List

    private void listItems() {
        clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
        clsClasses.clsP_prodopclist pitem;
        String idprod,nom;

         items.clear();

        try {
            P_prodopclistObj.fill("WHERE ID="+id);

            for (int i = 0; i <P_prodopclistObj.count; i++) {

                idprod=P_prodopclistObj.items.get(i).producto;
                P_productoObj.fill("WHERE CODIGO='"+idprod+"'");
                nom=P_productoObj.first().desclarga;

                pitem=clsCls.new clsP_prodopclist();

                pitem.id=id;
                pitem.producto = idprod;
                pitem.cant=1;
                pitem.idreceta=0;
                pitem.nombre=nom;

                items.add(pitem);
            }

            updateItems();
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void updateItems(){
        selid="";selidx=-1;

        try {
            Collections.sort(items, new ProdNameComparator());
            adapter=new LA_P_prodopclist(this,this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public class ProdNameComparator implements Comparator<clsClasses.clsP_prodopclist>  {
        public int compare(clsClasses.clsP_prodopclist left, clsClasses.clsP_prodopclist right) {
            return left.nombre.compareTo(right.nombre);
        }
    }

    //endregion

    //region Aux

    private void showItem() {
        if(newitem) txt1.setText(""); else txt1.setText(""+item.id);
        txt2.setText(item.nombre);
    }

    private boolean validaDatos() {
        String ss;

        try {

            if (newitem) {
                ss=txt1.getText().toString();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill("WHERE ID="+ss);
                if (holder.count>0) {
                    msgbox("¡Código ya existe!\n"+holder.first().nombre);return false;
                }

                item.id=Integer.parseInt(ss);
            }

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                item.nombre=ss;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskDelete(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                delListItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskUpdate(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskStatus(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (item.activo==1) {
                    item.activo=0;
                } else {
                    item.activo=1;
                };
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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
            holder.reconnect(Con,db);
            P_prodopclistObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            addListItem();return;
        }
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    //endregion

}
