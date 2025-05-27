package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Nowifi extends PBase {

    private TextView l1,l2,l3,l4,l5,l6,l7,l8;

    private String s1,s2,s3,s4,s5,s6,s7,s8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_nowifi);

            super.InitBase();

            l1 = findViewById(R.id.textView212);
            l2 = findViewById(R.id.textView253);
            l3 = findViewById(R.id.textView315);
            l4 = findViewById(R.id.textView316);
            l5 = findViewById(R.id.textView317);
            l6 = findViewById(R.id.textView318);
            l7 = findViewById(R.id.textView319);
            l8 = findViewById(R.id.textView322);

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            if (gl.peRest) {
                s1="Si el restaurante usa más que un equipo, deben dejar de usar todos los equipos, menos una caja.";
                s2="Si se usa más de una caja, debe usarse solo una caja. ";
                s3="Todas las ventas se realizan en la única caja hasta que la conexión se recupera. ";
                s4="Antes de imprimir precuenta, asegurarse que en ningún equipo no se ingresó " +
                    "ningún artículo que no está incluido en la caja";
                s5="Si un artículo está en el equipo de mesero, pero NO ESTÁ en la caja, " +
                    "eliminar el artículo en el equipo de mesero e ingresarlo de nuevo en la caja.";
                s6="Si la mesa no aparece abierta en la caja, pero está abierta en el equipo de mesero," +
                   "abrirla en la caja e ingrese de nuevo todos los artículos. " +
                   "Luego con la opción 'Borrar cuentas' en el equipo de mesero borre la cuenta.";
                s7="Al momento de recuperar conexión, aparece un mensaje con la información";
                s8="Esperar UN MINUTO y luego continuar trabajando en todos los equipos";
            } else {
                s1="La perdida de conexión a internet afectará únicamente las existencias.";
                s2="Durante trabajo sin conexión no se van a poder ver las existencias actualizadas. ";
                s3="Al momento de recuperar conexión, aparece un mensaje con la información";
                s4="Esperar UN MINUTO y luego continuar trabajando el todos los equipos";
                s5="";s6="";s7="";s8="";
                l5.setVisibility(View.INVISIBLE);l6.setVisibility(View.INVISIBLE);
                l7.setVisibility(View.INVISIBLE);l8.setVisibility(View.INVISIBLE);
            }

            l1.setText(s1);l2.setText(s2);l3.setText(s3);l4.setText(s4);
            l5.setText(s5);l6.setText(s6);l7.setText(s7);l8.setText(s8);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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