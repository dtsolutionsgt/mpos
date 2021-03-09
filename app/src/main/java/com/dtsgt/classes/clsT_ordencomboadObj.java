package com.dtsgt.classes;
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencomboadObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencomboad";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencomboad> items= new ArrayList<clsClasses.clsT_ordencomboad>();

    public clsT_ordencomboadObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordencomboad item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencomboad item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencomboad item) {
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

    public clsClasses.clsT_ordencomboad first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencomboad item) {

        ins.init("T_ordencomboad");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CANT",item.cant);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencomboad item) {

        upd.init("T_ordencomboad");

        upd.add("NOMBRE",item.nombre);
        upd.add("CANT",item.cant);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencomboad item) {
        sql="DELETE FROM T_ordencomboad WHERE (ID="+item.id+") AND (COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ordencomboad WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordencomboad item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencomboad();

            item.id=dt.getInt(0);
            item.corel=dt.getString(1);
            item.idcombo=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.cant=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsT_ordencomboad item) {

        ins.init("T_ordencomboad");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CANT",item.cant);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencomboad item) {

        upd.init("T_ordencomboad");

        upd.add("NOMBRE",item.nombre);
        upd.add("CANT",item.cant);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

