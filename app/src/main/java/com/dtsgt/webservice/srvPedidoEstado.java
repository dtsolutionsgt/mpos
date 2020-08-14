package com.dtsgt.webservice;

import android.content.Intent;

public class srvPedidoEstado extends srvBase {

    private wsPedidosEstado wspe;

    private String correlativo;
    private int estado_pedido,valor_estado;

    @Override
    public void execute() {
        wspe=new wsPedidosEstado(URL,null);
        wspe.execute(correlativo,estado_pedido,valor_estado);
    }

    public void loadParams(Intent intent) {
        correlativo = intent.getStringExtra("correlativo");
        estado_pedido = intent.getIntExtra("estado_pedido",0);
        valor_estado = intent.getIntExtra("valor_estado",0);
    }

}
