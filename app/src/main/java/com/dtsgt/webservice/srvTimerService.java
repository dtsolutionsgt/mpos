package com.dtsgt.webservice;

import android.annotation.SuppressLint;
import android.os.Environment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class srvTimerService extends srvBaseJob {

    private wsOrdenImport wsoi;
    private wsOrdenesRecibidos wsor;
    private wsPedidosImport wspi;
    private wsPedidosRecibidos wspr;

    private Runnable rnOrdenesNuevos;
    private Runnable rnPedidosNuevos;

    public ArrayList<String> itemso=new ArrayList<String>();
    public ArrayList<String> itemsp=new ArrayList<String>();

    private int idempresa,idsucursal;
    private String params;

    private String dirord= Environment.getExternalStorageDirectory().getPath() + "/mposordcaja";
    private String dirped= Environment.getExternalStorageDirectory().getPath() + "/mposordser";

    @Override
    public void execute() {

        itemso.clear();itemsp.clear();

        rnOrdenesNuevos = new Runnable() {
            public void run() {
                procesaOrdenes();
                //startOrdenImport.startService(getApplicationContext(),params);
            }
        };

        rnPedidosNuevos = new Runnable() {
            public void run() {
                procesaPedidos();
                //startPedidosImport.startService(getApplicationContext(),params);
            }
        };


        wsoi =new wsOrdenImport(URL,idempresa,idsucursal);
        wsoi.execute(rnOrdenesNuevos);

        /*
        wspi=new wsPedidosImport(URL,idempresa,idsucursal);
        wspi.execute(rnPedidosNuevos);
        */

        startMainTimer.startService(getApplicationContext(),params);

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
                    corel = s.substring(4);fname = dirord + "/" + corel + ".txt";
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
                        itemso.add(corel);
                    }
                } else if (ppe == 0) {
                    if (flag) {
                        try {
                            writer.close();writer = null;wfile = null;
                            itemso.add(corel);
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

        if (itemso.size()>0) {
            ordenessRecibidos();
            notifyorden(itemso.size());
        }
    }

    private void ordenessRecibidos() {
        String cor="";

        try {
            for (int i = 0; i <itemso.size(); i++) {
                cor+=itemso.get(i);
                if (i<itemso.size()-1) cor+=";";
            }

            wsor=new wsOrdenesRecibidos(URL,null);
            wsor.execute(cor);
        } catch (Exception e) {
            notification("MPos error : "+e.getMessage());
        }
    }

    private void procesaPedidos() {
        FileWriter wfile=null;
        BufferedWriter writer=null;
        File file;
        String s="",corel="",fname;
        int pp,ppe;
        boolean flag=false;

        if (wspi.items.size()==0) {
            return;
        }

        try {

            for (int i = 0; i <wspi.items.size(); i++) {

                s=wspi.items.get(i);
                pp=s.indexOf("FILE");ppe=s.indexOf("ENDFILE");

                if (pp == 0) {
                    corel = s.substring(5);fname = dirped + "/" + corel + ".txt";
                    file = new File(fname);flag = !file.exists();

                    if (flag) {
                        try {
                            wfile = new FileWriter(fname, false);
                            writer = new BufferedWriter(wfile);
                        } catch (IOException e) {
                            notification("MPos error : "+e.getMessage());flag=false;
                        }
                    } else {
                        itemsp.add(corel);
                    }
                } else if (ppe == 0) {
                    if (flag) {
                        try {
                            writer.close();writer = null;wfile = null;
                            itemsp.add(corel);
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

        if (itemsp.size()>0) {
            pedidosRecibidos();
            notifynew(itemsp.size());
        }

    }

    private void pedidosRecibidos() {
        String cor="";

        try {
            for (int i = 0; i <itemsp.size(); i++) {
                cor+=itemsp.get(i);
                if (i<itemsp.size()-1) cor+=";";
            }

            wspr=new wsPedidosRecibidos(URL,null);
            wspr.execute(cor);
        } catch (Exception e) {
            notification("MPos error : "+e.getMessage());
        }
    }

}
