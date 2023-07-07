package com.dtsgt.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.Fragment;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;

public class fragBase extends Fragment {

    public Context cont;
    public PBase owner;

    public appGlobals gl;

    public BaseDatos Con;
    public SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    public clsClasses clsCls = new clsClasses();

    public int callback;

    public fragBase(PBase own) {
        owner=own;
        cont=owner;
        gl=own.gl;

        Con=own.Con;
        ins=Con.Ins;upd=Con.Upd;
        db=own.db;
   }

    public void afterCreate() { }

    public void onResume(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db=dbase;
    }

    @Override
    public void onResume() {
        super.onResume();
        onResume(owner.Con, owner.db);
    }

    public void callBack() { }

}