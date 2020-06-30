package com.dtsgt.classes;

import com.dtsgt.mpos.PBase;

import java.util.ArrayList;

class clsInvActual {

    public String  error="";

    private ArrayList<String> results=new ArrayList<String>();

    private String URL,sql;
    private int empresa,sucursal,caja;
    private boolean errflag;

    private final String NAMESPACE ="http://tempuri.org/";
    private String METHOD_NAME;

    public clsInvActual(String Url) {
         URL=Url;
    }

}
