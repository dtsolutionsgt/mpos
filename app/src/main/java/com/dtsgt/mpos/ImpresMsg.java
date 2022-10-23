package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ImpresMsg extends PBase {

    private TextView cboPrn;
    private EditText txt1;

    private String ss;
    private int idprinter=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impres_msg);

        super.InitBase();

        cboPrn = findViewById(R.id.textView1);
        txt1 = findViewById(R.id.editText2);

        gl.pelComandaBT=true;

        cargaImpresoras();

    }

    //region Events

    public void doSend(View view) {

        if (gl.pelComandaBT) idprinter=0;
        if (idprinter==-1) {
            msgbox("Falta seleccionar una impresora.");return;
        }

        ss=txt1.getText().toString();
        if (ss.isEmpty()) {
            msgbox("No se puede enviar un mensaje vacío.");return;
        }
    }

    public void doAdd(View view) {
        txt1.setText("AGREGAR al orden : ");txt1.setSelection(txt1.getText().length());
    }

    public void doRemove(View view) {
        txt1.setText("QUITAR de orden : ");txt1.setSelection(txt1.getText().length());
    }

    public void doDelay(View view) {
        txt1.setText("RETRASADO orden : ");txt1.setSelection(txt1.getText().length());
    }

    public void doVoid(View view) {
        txt1.setText("ANULAR PREPARACION orden : ");txt1.setSelection(txt1.getText().length());
    }

    public void doClear(View view) {
        txt1.setText("");
    }

    public void doSelect(View view) {
        if (gl.pelComandaBT) return;
    }

    //endregion

    //region Main


    //endregion

    //region Aux

    private void cargaImpresoras() {
       if (gl.pelComandaBT) {
           cboPrn.setText("IMPRESORA DE CAJA");return;
       }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion


}