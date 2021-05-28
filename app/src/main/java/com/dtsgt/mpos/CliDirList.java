package com.dtsgt.mpos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.webservice.wsOpenDT;

public class CliDirList extends PBase {

    private TextView lblNom;
    private ProgressBar pbar;

    private wsOpenDT ws;

    private Runnable rnAfterOpen;

    private int cliid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cli_dir_list);

        super.InitBase();

        lblNom=findViewById(R.id.lblDescrip2);
        pbar=findViewById(R.id.progressBar6);pbar.setVisibility(View.VISIBLE);

        ws=new wsOpenDT(gl.wsurl);

        rnAfterOpen = new Runnable() {
            public void run() {
                procesaDirecciones();
            }
        };

        cliid=gl.codigo_cliente;
        cliid=17638;
        lblNom.setText(gl.gstr);

        loadDirs();
    }


    //region Events


    //endregion

    //region Main


    private void loadDirs() {
        try {
            sql="SELECT CODIGO_DIRECCION, DIRECCION, REFERENCIA, TELEFONO " +
                "FROM P_CLIENTE_DIR WHERE (CODIGO_CLIENTE="+cliid+")";
            ws.execute(sql,rnAfterOpen);
        } catch (Exception e) {
            toast("No se pudo obtener lista de direcciones");
        }
    }

    private void procesaDirecciones() {
        try {
            toastlong("Direcciones : "+ws.openDTCursor.getCount());
            pbar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            toast("No se pudo procesar lista de direcciones");
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion

}