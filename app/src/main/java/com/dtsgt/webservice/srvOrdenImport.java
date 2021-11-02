package com.dtsgt.webservice;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class srvOrdenImport extends srvBaseJob {

    private wsOrdenImport wsoi;
    private wsOrdenesRecibidos wsor;
    private Runnable rnOrdenesNuevos;

    public ArrayList<String> items=new ArrayList<String>();

    private int idempresa,idsucursal;
    private String params;
    private String orddir= Environment.getExternalStorageDirectory().getPath() + "/mposordcaja";

    @Override
    public void execute() {

        items.clear();

        rnOrdenesNuevos = new Runnable() {
            public void run() {
                procesaOrdenes();
                startOrdenImport.startService(getApplicationContext(),params);
            }
        };

        wsoi =new wsOrdenImport(URL,idempresa,idsucursal);
        wsoi.execute(rnOrdenesNuevos);
    }

    @Override
    public boolean loadParams(String paramstr) {
        params=paramstr;

        try {
            String[] sp = params.split("#");

            URL=sp[0];
            idempresa=Integer.parseInt(sp[1]);
            idsucursal=Integer.parseInt(sp[2]);

            return true;
        } catch (Exception e) {
            URL="";idempresa=0;idsucursal=0;
            error=e.getMessage();return false;
        }
    }

    private void procesaOrdenes() {
        FileWriter wfile=null;
        BufferedWriter writer=null;
        File file;
        String s="",corel="",fname;
        int pp,ppe;
        boolean flag=false;

        if (wsoi.items.size()==0) {
            return;
        }

        try {

            for (int i = 0; i < wsoi.items.size(); i++) {

                s= wsoi.items.get(i);
                pp=s.indexOf("FILE");ppe=s.indexOf("ENDFILE");

                if (pp == 0) {
                    corel = s.substring(4);fname = orddir + "/" + corel + ".txt";
                    file = new File(fname);flag = !file.exists();

                    if (flag) {

                        try {
                            wfile = new FileWriter(fname, false);
                            writer = new BufferedWriter(wfile);

                            s="DELETE FROM P_RES_SESION WHERE ID='"+corel+"'";
                            writer.write(s);writer.write("\r\n");
                            s="DELETE FROM T_ORDEN WHERE COREL='"+corel+"'";
                            writer.write(s);writer.write("\r\n");
                            s="DELETE FROM T_ORDENCUENTA WHERE COREL='"+corel+"'";
                            writer.write(s);writer.write("\r\n");

                        } catch (IOException e) {
                            notification("MPos error : "+e.getMessage());flag=false;
                        }
                    } else {
                        items.add(corel);
                    }
                } else if (ppe == 0) {
                    if (flag) {
                        try {
                            writer.close();writer = null;wfile = null;
                            items.add(corel);
                        } catch (IOException e) {
                            notification("MPos error : "+e.getMessage());
                        }
                    }
                } else {
                    if (flag) {
                        try {
                            writer.write(s);writer.write("\r\n");
                        } catch (IOException e) {
                            notification("MPos error : "+e.getMessage());flag=false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            notification("MPos error : "+e.getMessage());
        }

        if (items.size()>0) {
            ordenessRecibidos();
            notifyorden(items.size());
        }
    }

    private void ordenessRecibidos() {
        String cor="";

        try {
            for (int i = 0; i <items.size(); i++) {
                cor+=items.get(i);
                if (i<items.size()-1) cor+=";";
            }

            wsor=new wsOrdenesRecibidos(URL,null);
            wsor.execute(cor);
        } catch (Exception e) {
            notification("MPos error : "+e.getMessage());
        }
    }

}
