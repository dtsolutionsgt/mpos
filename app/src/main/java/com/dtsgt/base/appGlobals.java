package com.dtsgt.base;

import android.app.Application;

import com.dtsgt.base.clsClasses.clsBonifItem;
import com.dtsgt.base.clsClasses.clsDemoDlg;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.printer.Printer;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class appGlobals extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			//FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		} catch (Exception e) {
			String ss=e.getMessage();
		}
	}


	public String rutanom,sucur,rutatipo, rutatipog,  vendnom, gstr, gstr2, prod, um, umpres, umstock, clitipo;
	public String ubas, empnom,imgpath,umpeso,lotedf,impresora, tipoImpresora, codSupervisor, ayudante,
			ayudanteID, vehiculo, vehiculoID;
	public String bonprodid,bonbarid,bonbarprod,pprodname,contrib,ateninistr,tcorel,CodDev,tipoprodcod;
	public int prodcod,prodmenu,itemid,gint,tipo,nivel,nivel_sucursal,rol,prodtipo,prw,boldep,vnivel,vnivprec,media;
	public boolean EsVentaDelivery,EsNivelPrecioDelivery = false;
	public int autocom,pagomodo,filtrocli,prdlgmode,mantid,retcant,limcant,reportid,cajaid;
	public long nuevaFecha,atentini,lastDate;
	public double dval,dpeso,pagoval,pagolim,bonprodcant,percepcion,costo,credito,umfactor,prectemp,fondoCaja,FinMonto;
	public boolean CellCom,closeDevBod,modoinicial,newmenuitem,validDate,comquickrec;
	public String ref1,ref2,ref3,escaneo,corel_d_mov,barra,parVer,gcods,prtipo,prpar;
	public String gNITCliente, gDirCliente, gNombreCliente, gCorreoCliente,gTelCliente;
	public String felcorel,felserie,felnum,feluuid,prodid,pedid,pedcorel,idorden,mesanom,nom_est;
	public String tiendanom,cajanom,urlglob,menuitemid,titReport,pickcode,pickname,wsurl;
	public int tiponcredito,validarCred,gpsdist,gcodi,savemantid,salaid,idmesero,modoclave;
    public boolean vcredito,vcheque,vchequepost,validimp,dev,banco,disc,iniciaVenta,listaedit,exitflag;
	public boolean closeCliDet, closeVenta,closePedido, promapl, pagado, pagocobro, sinimp, rutapos, devol, modoadmin,
			    reportList, usarpeso, banderafindia, depparc, incNoLectura, cobroPendiente, findiaactivo,
	            banderaCobro, cliposflag, forcedclose, cierreDiario,invregular,checksuper,gNITcf,
				inic_inv_auto;
	public int  mpago, corelZ,codigo_cliente,codigo_ruta,codigo_vendedor,codigo_proveedor,idmodgr,
			    emp, tienda,dias_anul,cod_prov_recarga,	timeout,produid,mesero_venta,mesacodigo,
                comensales, cliente_dom, idclidir, idalm, idalm2, idalmpred,mesa_grupo,
			    uidingrediente, idgrres, idgrsel, idgrpos, usuario_cortesia,bar_prod,cuenta_borrar,cuenta_pagar,
				mesa_vend,mesa_codigo,invcent_cod,sal_idneg,desc_tipo_apl,prcu_mesa,prcu_vend,precuenta_cuenta;
	public String  cliente,ruta,vend, caja, clave,nombre_proveedor,idmov,FELmsg, prndrvmsg,nocuenta_precuenta,
                codigo_pais,primesa,pricuenta,ordcorel,numero_orden,nombre_mesero,nombre_mesero_sel,
                corelmov, linea_sel="",mesa_alias,nummesapedido, nombre_cortesia,bar_um,bar_idbarril;
	public String dom_nit,dom_nom, dom_dir,dom_ref,dom_tel,dom_ddir,sal_iddep,sal_idmun,sal_neg,sal_mun,sal_dep,
			     prcu_corel,nom_alm,nom_alm2,mesa_area,nit_tipo,invcent_tipo;
	public double precio_recarga,total_pago,propina_valor,monto_final_ingresado,menuprecio,
			      dom_total,bar_cant,descadd,monto_propina;
	public boolean configCajaSuc = false,InvCompSend=false,pedlistcli,ventalock,
	               inicio_caja_correcto = false,inicia_caja_primera_vez = false,
                   recibir_automatico = false,meserodir,cerrarmesero,preimpresion,parallevar,paraentrega,
                   impresion_comanda, modo_domicilio, cf_domicilio, cierra_clave, mesero_lista ,
                   ingreso_mesero,after_login,modo_prec,mesero_precuenta,sin_propina,modo_upd_venta,
			       modo_cortesia,modo_apertura,imp_inventario,sal_NIT,sal_NRC,sal_PER;

	//FEL Identificacion
	public String felSIN="SIN FEL";
	public String felInfile="INFILE";
	public String felSal="FELSAL";

	//Tamaño de pantalla
    public int scrx,scry,scrdim;
    public boolean scrhoriz;

	//#EJC20210804: Para pasar parámetro a appbundle print.
	public String QRCodeStr ="";

	//#CKFK 20210705
	public boolean domicilio = false,delivery = false;

	//#CKFK 20190319 Para facilidades de desarrollo se debe colocar la variable debug en true, por defecto estará en false
	public boolean debug=true;

	//Devolución Cliente
	public String devtipo,devrazon,dvumventa,dvumstock,dvumpeso,dvlote,scancliente;
	public double dvfactor,dvpeso,dvprec,dvpreclista,dvtotal;
	public int dvbrowse=0,tienelote,facturaVen,brw=0;
    public boolean dvporpeso,devfindia,devprncierre,gpspass,climode,endPrint;
    public double dvdispventa;
    public String dvcorreld,dvcorrelnc,dvestado,dvactuald,dvactualnc;

	// Parametros Extra
	public String peModal,peMon,peFormatoFactura,peMMod,peFEL,peComNoAplic,peTextoPie;
	public String peFraseIVA,peFraseISR,peImpFactIP;
	public int peDec,peDecCant,peDecImp,peLimiteGPS,peMargenGPS,peVentaGps,peAvizoFEL;
	public int peCajaPricipal,peNumImp,peLineaIngred,pePorConsumo,peMaxOrden;
    public Boolean peStockItf,peSolicInv,peAceptarCarga,peBotInv,peBotPrec,pePedidos;
	public Boolean peBotStock,peVehAyud,peEnvioParcial,peOrdPorNombre,peFotoBio,peInvCompart;
    public Boolean peImprFactCorrecta,peMCent,peImpOrdCos,peMImg,peMFact,peEnvio,peCajaRec,peRepVenCod;
    public Boolean peAnulSuper,peRest,peModifPed,pePropinaFija,peBotComanda,peEditTotCombo;
    public Boolean peAgregarCombo,peComboLimite,peComboDet,peFactSinPropina,peRedondPropina;
	public Boolean peVentaDomicilio,peVentaEntrega,peDomEntEnvio,peNoCerrarMesas,peActOrdenMesas;
	public Boolean peCafeTicket,peNoEnviar,peUsaSoloBOF,peAcumDesc, peNumOrdCommandaVenta;
	public Boolean peImpFactBT,peImpFactLan,peImpFactUSB,peNumOrdCentral,peCajaMesasManual,peMesaAtenderTodos;
	public Boolean peFactPropinaAparte,pePrecu1015;
    public double  pePropinaPerc,pePropinaCarta,peDescMax;

    // Parametros Extra Local
    public Boolean pelCaja,pelCajaRecep,pelDespacho, pelOrdenComanda;
    public Boolean pelClaveMes,pelClaveCaja,pelComandaBT, pelMeseroCaja;
    public String  pelPrefijoOrden;

	// Descuentos
	public String promprod;
	public double promcant,promdesc,prommdesc,descglob,descgtotal,desc_monto;
	public int prommodo;
	public double total_factura_previo_descuento=0;
	// Bonificaciones
	public ArrayList<clsBonifItem> bonus = new ArrayList<clsBonifItem>();
	public clsDemoDlg clsDemo;
	
	// GPS
	public double gpspx,gpspy,gpscpx,gpscpy,gpscdist;
	
	//Id de Dispositivo Móvil
	public String deviceId,devicename;

	// Epson
	public Device mDevice=null;
	public Printer mPrinter=null;
	public boolean mPrinterSet=false;
	public String mPrinterIP;

	//Cobros
	public int escbro=0;

	//Desglose de efectivo
	public double totDep;

	//Comunicación con WS
	public int isOnWifi = 0;

	public String felUsuarioCertificacion, felLlaveCertificacion;

	public ArrayList<String> peditems = new ArrayList<String>();

	//#EJC202301020908AM: Parámetro para saber si se sincronizan o no
	//todos los clientes.
	public Boolean Sincronizar_Clientes = false;
	public double auxCantVenta = 0;
}