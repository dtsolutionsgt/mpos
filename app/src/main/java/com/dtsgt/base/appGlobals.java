package com.dtsgt.base;

import android.app.Application;

import com.dtsgt.base.clsClasses.clsBonifItem;
import com.dtsgt.base.clsClasses.clsDemoDlg;
import com.epson.eposdevice.Device;
import com.epson.eposdevice.printer.Printer;

import java.security.PublicKey;
import java.util.ArrayList;

public class appGlobals extends Application {

	public String rutanom,sucur,rutatipo, rutatipog,  vendnom, gstr, gstr2, prod, um, umpres, umstock, clitipo;
	public String ubas, empnom,imgpath,umpeso,lotedf,impresora, tipoImpresora, codSupervisor, ayudante,
			ayudanteID, vehiculo, vehiculoID;
	public String bonprodid,bonbarid,bonbarprod,pprodname,contrib,ateninistr,tcorel,CodDev,tipoprodcod;
	public int prodcod,prodmenu,itemid,gint,tipo,nivel,nivel_sucursal,rol,prodtipo,prw,boldep,vnivel,vnivprec,media;
	public int autocom,pagomodo,filtrocli,prdlgmode,mantid,retcant,limcant,reportid,cajaid;
	public long nuevaFecha,atentini,lastDate;
	public double dval,dpeso,pagoval,pagolim,bonprodcant,percepcion,costo,credito,umfactor,prectemp,fondoCaja,FinMonto;
	public boolean CellCom,closeDevBod,modoinicial,newmenuitem,validDate,comquickrec;
	public String ref1,ref2,ref3,escaneo,corel_d_mov,barra,parVer,gcods,prtipo,prpar;
	public String gNITCliente, gDirCliente, gNombreCliente, gCorreoCliente,gTelCliente;
	public String felcorel,felserie,felnum,feluuid,prodid,pedid,pedcorel,idorden,mesanom;
	public String tiendanom,cajanom,urlglob,menuitemid,titReport,pickcode,pickname,wsurl;
	public int tiponcredito,validarCred,gpsdist,gcodi,savemantid,salaid,idmesero;
    public boolean vcredito,vcheque,vchequepost,validimp,dev,banco,disc,iniciaVenta,listaedit,exitflag;
	public boolean closeCliDet, closeVenta,closePedido, promapl, pagado, pagocobro, sinimp, rutapos, devol, modoadmin,
			    reportList, usarpeso, banderafindia, depparc, incNoLectura, cobroPendiente, findiaactivo,
	            banderaCobro, cliposflag, forcedclose, cierreDiario,invregular,checksuper,gNITcf;
	public int  mpago, corelZ,codigo_cliente,codigo_ruta,codigo_vendedor,codigo_proveedor,
			    emp, tienda,dias_anul,cod_prov_recarga,	timeout,produid,mesero_venta,comensales;
	public String  cliente,ruta,vend, caja, clave,nombre_proveedor,idmov,FELmsg, prndrvmsg,
                codigo_pais,primesa,pricuenta,ordcorel,numero_orden;
	public double precio_recarga,total_pago,propina_valor,monto_final_ingresado,menuprecio;
	public boolean configCajaSuc = false,InvCompSend=false,pedlistcli,ventalock,
	               inicio_caja_correcto = false,inicia_caja_primera_vez = false,
                   recibir_automatico = false,meserodir,cerrarmesero,preimpresion;


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
	public String peModal,peMon,peFormatoFactura,peMMod,peFEL,peComNoAplic;
    public int peDec,peDecCant,peDecImp,peLimiteGPS,peMargenGPS,peVentaGps,peAvizoFEL,peNumImp;
    public Boolean peStockItf,peSolicInv,peAceptarCarga,peBotInv,peBotPrec,pePedidos;
	public Boolean peBotStock,peVehAyud,peEnvioParcial,peOrdPorNombre,peFotoBio,peInvCompart;
    public Boolean peImprFactCorrecta,peMCent,peImpOrdCos,peMImg,peMFact,peEnvio,peCajaRec,peRepVenCod;
    public Boolean peAnulSuper,peRest,peModifPed,pePropinaFija,peBotComanda,peEditTotCombo;
    public Boolean peAgregarCombo,peComboLimite,peOrdenComanda;
    public double  pePropinaPerc;

    // Parametros Extra Local
    public Boolean pelCaja,pelCajaRecep,pelTablet,pelDespacho;
    public String  pelPrefijoOrden;

	// Descuentos
	public String promprod;
	public double promcant,promdesc,prommdesc,descglob,descgtotal;
	public int prommodo;
	
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

}
