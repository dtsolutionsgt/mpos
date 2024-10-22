package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbPedidoEnc extends fbBase {

    public clsClasses.clsD_domicilio_enc item;
    public ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();
    public ArrayList<String> doms= new ArrayList<String>();

    public fbPedidoEnc(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsD_domicilio_enc item) {
        fdt=fdb.getReference(root+item.corel);
        fdt.setValue(item);
    }

    public void updateStatCom(String node) {
        fdb.getReference(root+node).child("importado").setValue(1);
    }

    public void updateState(String node,int value) {
        fdb.getReference(root+node).child("estado").setValue(value);
    }

    public void listPending( Runnable rnCallback ) {
        try {

            fdb.getReference(root).orderByChild("importado").equalTo(0).
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String idcorel;

                            doms.clear();
                            if (task.isSuccessful()) {

                                DataSnapshot res=task.getResult();
                                if (res.exists()) {
                                    for (DataSnapshot snap : res.getChildren()) {
                                        idcorel=snap.child("corel").getValue(String.class);
                                        doms.add(idcorel);
                                    }
                                }
                                errflag=false;
                            } else {
                                errflag=true;
                            }

                            callBack=rnCallback;
                            runCallBack();
                        }
                    });
        } catch (Exception e) {
            errflag=true;
        }
    }

    public void getItem(String node,Runnable rnCallback) {

        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    try {
                        item=clsCls.new clsD_domicilio_enc();

                        item.corel=res.child("corel").getValue(String.class);
                        item.empresa=res.child("empresa").getValue(Integer.class);
                        item.codigo_sucursal=res.child("codigo_sucursal").getValue(Integer.class);
                        item.fecha_hora=res.child("fecha_hora").getValue(Long.class);
                        item.vendedor=res.child("vendedor").getValue(Integer.class);
                        item.codigo_cliente=res.child("codigo_cliente").getValue(Integer.class);
                        item.cliente_nombre=res.child("cliente_nombre").getValue(String.class);
                        item.direccion_text=res.child("direccion_text").getValue(String.class);
                        item.texto=res.child("texto").getValue(String.class);
                        item.telefono=res.child("telefono").getValue(String.class);
                        item.cambio=res.child("cambio").getValue(Integer.class);
                        item.forma_pago=res.child("forma_pago").getValue(Integer.class);
                        item.nit=res.child("nit").getValue(String.class);
                        item.iddireccion=res.child("iddireccion").getValue(Integer.class);
                        item.importado=res.child("importado").getValue(Integer.class);
                        item.estado=2;
                        item.idorden=0;

                        errflag=false;
                    } catch (Exception e) {
                        errflag=true;
                        error=e.getMessage();
                    }

                } else {
                    errflag=true;
                    error=task.getException().getMessage();
                }

                callBack=rnCallback;
                runCallBack();
            }
        });
    }

}
