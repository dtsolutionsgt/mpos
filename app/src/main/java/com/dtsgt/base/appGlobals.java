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
	            banderaCobro, cliposflag, forcedclose, cierreDiario,invregular,checksuper,gNITcf;
	public int  mpago, corelZ,codigo_cliente,codigo_ruta,codigo_vendedor,codigo_proveedor,idmodgr,
			    emp, tienda,dias_anul,cod_prov_recarga,	timeout,produid,mesero_venta,mesacodigo,
                comensales, cliente_dom, idclidir, idalm, idalm2, idalmpred,mesa_grupo,
			    uidingrediente, idgrres, idgrsel, idgrpos, usuario_cortesia,bar_prod;
	public String  cliente,ruta,vend, caja, clave,nombre_proveedor,idmov,FELmsg, prndrvmsg,nocuenta_precuenta,
                codigo_pais,primesa,pricuenta,ordcorel,numero_orden,nombre_mesero,nombre_mesero_sel,
                corelmov, linea_sel="",mesa_alias,nummesapedido, nombre_cortesia,bar_um,bar_idbarril;
	public String dom_nit,dom_nom, dom_dir,dom_ref,dom_tel,dom_ddir,caja_est_pago,
			    nom_alm,nom_alm2,mesa_area;
	public double precio_recarga,total_pago,propina_valor,monto_final_ingresado,menuprecio,
			      dom_total,bar_cant;
	public boolean configCajaSuc = false,InvCompSend=false,pedlistcli,ventalock,
	               inicio_caja_correcto = false,inicia_caja_primera_vez = false,
                   recibir_automatico = false,meserodir,cerrarmesero,preimpresion,parallevar,paraentrega,
                   impresion_comanda, modo_domicilio, cf_domicilio, cierra_clave, mesero_lista ,
                   ingreso_mesero,after_login,modo_prec,mesero_precuenta,sin_propina,
			       modo_cortesia,modo_apertura;

	//Tamaño de pantalla
    public int scrx,scry,scrdim;
    public boolean scrhoriz;

	//#EJC20210804: Para pasar parámetro a appbundle print.
	public String QRCodeStr ="";

	//#CKFK 20210705
	public boolean pickup = false,delivery = false;

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
    public int peDec,peDecCant,peDecImp,peLimiteGPS,peMargenGPS,peVentaGps,peAvizoFEL,peNumImp,peLineaIngred;
    public Boolean peStockItf,peSolicInv,peAceptarCarga,peBotInv,peBotPrec,pePedidos;
	public Boolean peBotStock,peVehAyud,peEnvioParcial,peOrdPorNombre,peFotoBio,peInvCompart;
    public Boolean peImprFactCorrecta,peMCent,peImpOrdCos,peMImg,peMFact,peEnvio,peCajaRec,peRepVenCod;
    public Boolean peAnulSuper,peRest,peModifPed,pePropinaFija,peBotComanda,peEditTotCombo;
    public Boolean peAgregarCombo,peComboLimite,peComboDet,peFactSinPropina;
	public Boolean peVentaDomicilio,peVentaEntrega,peDomEntEnvio,peNoCerrarMesas,peActOrdenMesas;
    public double  pePropinaPerc,pePropinaCarta;

    // Parametros Extra Local
    public Boolean pelCaja,pelCajaRecep,pelDespacho, pelOrdenComanda;
    public Boolean pelClaveMes,pelClaveCaja,pelComandaBT, pelMeseroCaja;
    public String  pelPrefijoOrden;

	// Descuentos
	public String promprod;
	public double promcant,promdesc,prommdesc,descglob,descgtotal,desc_monto;
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

	public String felUsuarioCertificacion, felLlaveCertificacion;

	public ArrayList<String> peditems = new ArrayList<String>();

}
