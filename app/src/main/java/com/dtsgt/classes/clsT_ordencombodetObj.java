package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencombodetObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencombodet";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencombodet> items= new ArrayList<clsClasses.clsT_ordencombodet>();

    public clsT_ordencombodetObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsT_ordencombodet item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencombodet item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencombodet item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel+ " "+specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsT_ordencombodet first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencombodet item) {

        ins.init("T_ordencombodet");

        ins.add("CODIGO_MENUOPC_DET",item.codigo_menuopc_det);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("COREL",item.corel);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencombodet item) {

        upd.init("T_ordencombodet");

        upd.add("IDCOMBO",item.idcombo);
        upd.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("COREL",item.corel);

        upd.Where("(CODIGO_MENUOPC_DET="+item.codigo_menuopc_det+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencombodet item) {
        sql="DELETE FROM T_ordencombodet WHERE (CODIGO_MENUOPC_DET="+item.codigo_menuopc_det+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ordencombodet WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordencombodet item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencombodet();

            item.codigo_menuopc_det=dt.getInt(0);
            item.idcombo=dt.getInt(1);
            item.codigo_menu_opcion=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.corel=dt.getString(4);

            items.add(item);

            dt.moveToNext();
        }

        if (dt!=null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt=null;
        int nid;

        try {
            dt=Con.OpenDT(idsql);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_ordencombodet item) {

        ins.init("T_ordencombodet");

        ins.add("CODIGO_MENUOPC_DET",item.codigo_menuopc_det);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("COREL",item.corel);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencombodet item) {

        upd.init("T_ordencombodet");

        upd.add("IDCOMBO",item.idcombo);
        upd.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("COREL",item.corel);

        upd.Where("(CODIGO_MENUOPC_DET="+item.codigo_menuopc_det+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

