package com.dtsgt.base;

public class clsClasses {

    public class clsD_factura {
        public int  empresa;
        public String corel;
        public boolean anulado;
        public long fecha;
        public int  ruta;
        public int  vendedor;
        public int  cliente;
        public double kilometraje;
        public long fechaentr;
        public String factlink;
        public double total;
        public double desmonto;
        public double impmonto;
        public double peso;
        public String bandera;
        public String statcom;
        public boolean calcobj;
        public String serie;
        public int  corelativo;
        public int  impres;
        public String add1;
        public String add2;
        public String add3;
        public boolean depos;
        public String pedcorel;
        public String referencia;
        public String asignacion;
        public String supervisor;
        public String ayudante;
        public String vehiculo;
        public int  codigoliquidacion;
        public String razon_anulacion;
        public String feelserie;
        public String feelnumero;
        public String feeluuid;
        public long feelfechaprocesado;
        public String feelcontingencia;
    }

    public class clsD_facturad {
        public int  empresa;
        public String corel;
        public int  producto;
        public String umpeso;
        public boolean anulado;
        public double cant;
        public double precio;
        public double imp;
        public double des;
        public double desmon;
        public double total;
        public double preciodoc;
        public double peso;
        public double val1;
        public String val2;
        public String umventa;
        public double factor;
        public String umstock;
    }

    public class clsD_facturaf {
        public String corel;
        public String nombre;
        public String nit;
        public String direccion;
        public String correo;
    }

    public class clsD_facturap {
        public int  empresa;
        public String corel;
        public int  item;
        public boolean anulado;
        public int  codpago;
        public String tipo;
        public double valor;
        public String desc1;
        public String desc2;
        public String desc3;
        public boolean depos;
    }

    public class clsD_facturas {
        public String corel;
        public int  id;
        public String  producto;
        public double cant;
        public String umstock;
    }

    public class clsD_fel_error {
        public int  empresa;
        public String corel;
        public int  item;
        public long  fecha;
        public int  nivel;
        public String error;
        public int  enviado;
    }

    public class clsD_Mov {
        public String COREL;
        public int RUTA;
        public int ANULADO;
        public long FECHA;
        public String TIPO;
        public int USUARIO;
        public String REFERENCIA;
        public String STATCOM;
        public int IMPRES;
        public int CODIGOLIQUIDACION;
        public int CODIGO_PROVEEDOR;
        public double TOTAL;
    }

    public class clsD_MovD {
        public String corel;
        public int  producto;
        public double cant;
        public double cantm;
        public double peso;
        public double pesom;
        public String lote;
        public int  codigoliquidacion;
        public String unidadmedida;
        public int  coreldet;
        public double precio;
        public int  motivo_ajuste;
    }

    public class clsD_pedido {
        public int  empresa;
        public String corel;
        public long  fecha_sistema;
        public long  fecha_pedido;
        public long  fecha_recepcion_suc;
        public long  fecha_salida_suc;
        public long  fecha_entrega;
        public int  codigo_cliente;
        public int  firma_cliente;
        public int  codigo_direccion;
        public int  codigo_sucursal;
        public double total;
        public int  codigo_estado;
        public int  codigo_usuario_creo;
        public int  codigo_usuario_proceso;
        public int  codigo_usuario_entrego;
        public int  anulado;
    }

    public class clsD_pedidod {
        public String corel;
        public int  corel_det;
        public int  codigo_producto;
        public String umventa;
        public double cant;
        public double total;
        public String nota;
        public String codigo_tipo_producto;
    }

    public class clsD_pedidocombo {
        public int  corel_det;
        public int  corel_combo;
        public int  seleccion;
        public int  codigo_producto;
        public int  cant;
        public String nota;
    }

    public class clsD_pedidoc {
        public String corel;
        public String nombre;
        public String telefono;
        public String direccion;
        public String referencia;
        public String nit;
    }

    public class clsP_archivoconf {
        public String ruta;
        public String tipo_hh;
        public String idioma;
        public String tipo_impresora;
        public String serial_hh;
        public String modif_peso;
        public String puerto_impresion;
        public String lbs_o_kgs;
        public int  nota_credito;
        public int codigo_archivoconf;
    }

    public class clsP_banco {
        public int codigo_banco;
		public String codigo;
		public String tipo;
		public String nombre;
		public String cuenta;
		public int empresa;
		public int activo;
	}

    public class clsP_bonif {
        public String cliente;
        public int  ctipo;
        public String producto;
        public int  ptipo;
        public int  tiporuta;
        public String tipobon;
        public double rangoini;
        public double rangofin;
        public int  tipolista;
        public String tipocant;
        public double valor;
        public String lista;
        public String cantexact;
        public String globbon;
        public String porcant;
        public long  fechaini;
        public long  fechafin;
        public int  coddesc;
        public String nombre;
        public String emp;
        public String umproducto;
        public String umbonificacion;
    }

    public class clsP_cajacierre {
        public int empresa;
        public int sucursal;
        public int ruta;
        public int  corel;
        public int  estado;
        public int  fecha;
        public int vendedor;
		public int codpago;
		public double fondocaja;
        public double montoini;
        public double montofin;
        public double montodif;
        public String statcom;
        public String codigo_cajacierre;
    }

    public class clsP_cajapagos {
        public int empresa;
        public int sucursal;
        public int ruta;
        public int  corel;
        public int  item;
        public int  anulado;
        public long  fecha;
        public int  tipo;
        public int  proveedor;
        public double monto;
        public String nodocumento;
        public String referencia;
        public String observacion;
        public int vendedor;
        public String statcom;
        public String codigo_cajapagos;
    }

    public class clsP_cajareporte {
        public int empresa;
        public int sucursal;
        public int ruta;
        public int  corel;
        public int  linea;
        public String texto;
        public String statcom;
        public String codigo_cajareporte;
    }

    public class clsP_cliente {
        public int  codigo_cliente;
        public String codigo;
        public int  empresa;
        public String nombre;
        public int  bloqueado;
        public int  nivelprecio;
        public int  mediapago;
        public double limitecredito;
        public int  diacredito;
        public int  descuento;
        public int  bonificacion;
        public long ultvisita;
        public double impspec;
        public String nit;
        public String email;
        public String eservice;
        public String telefono;
        public String direccion;
        public double coorx;
        public double coory;
        public String bodega;
        public String cod_pais;
        public String codbarra;
        public double percepcion;
        public String tipo_contribuyente;
        public String imagen;
    }

    public class clsP_corel {
        public int  codigo_corel;
        public int  empresa;
        public String resol;
        public String serie;
        public int  corelini;
        public int  corelfin;
        public int  corelult;
        public long fechares;
        public int  ruta;
        public int  activa;
        public String handheld;
        public long fechavig;
        public int  resguardo;
        public int  valor1;
    }

    public class clsP_descuento {
        public int  codigo_descuento;
        public int cliente;
        public int  ctipo;
        public int producto;
        public int  ptipo;
        public int  tiporuta;
        public double rangoini;
        public double rangofin;
        public String desctipo;
        public double valor;
        public String globdesc;
        public String porcant;
        public int  fechaini;
        public int  fechafin;
        public int  coddesc;
        public String nombre;
        public int activo;
    }

    public class clsP_empresa {
        public int empresa;
        public String nombre;
        public int  col_imp;
        public String  logo;
        public String razon_social;
        public String identificacion_tributaria;
        public String telefono;
        public String cod_pais;
        public String nombre_contacto;
        public String apellido_contacto;
        public String direccion;
        public String correo;
        public String codigo_activacion;
        public int  cod_cant_emp;
        public int  cantidad_puntos_venta;
        public String  clave;
    }

    public class clsP_encabezado_reporteshh {
        public int  codigo;
        public String texto;
        public String sucursal;
    }

    public class clsP_factorconv {
        public int producto;
        public String unidadsuperior;
        public double factorconversion;
        public String unidadminima;
        public int codigo_factorconv;
    }

    public class clsP_frase {
        public int  codigo_frase;
        public String texto;
    }

    public class clsP_impuesto {
        public  int codigo_impuesto;
		public int  codigo;
		public double valor;
		public boolean activo;
	}

    public class clsP_linea   {
        public String codigo;
        public String marca;
        public String nombre;
        public int  activo;
        public String imagen;
        public int  codigo_linea;
    }

    public class clsP_Producto_Tipo   {
        public String codigo_tipo_producto;
        public String nombre;
        public boolean  utiliza_stock;
    }

    public class clsP_departamento   {
        public String codigo;
        public String codigo_area;
        public String nombre;
    }

    public class clsP_municipio   {
        public String codigo;
        public String codigo_departamento;
        public String nombre;
    }

    public class clsP_mediapago {
        public int  codigo;
        public int  empresa;
        public String nombre;
        public int  activo;
        public int  nivel;
        public int  porcobro;
    }

    public class clsP_nivelprecio {
        public int codigo;
        public String nombre;
        public int activo;
    }

    public class clsP_motivoajuste {
        public int codigo_motivo_ajuste;
        public int empresa;
        public String nombre;
        public boolean activo;
    }

	public class clsP_conceptopago {
		public int codigo;
		public String nombre;
		public int activo;
	}

    public class clsP_moneda {
		public int  codigo;
		public String nombre;
		public int  activo;
		public String symbolo;
		public double cambio;
	}

    public class clsP_paramext {
        public int  id;
        public String nombre;
        public String valor;
        public String ruta;
    }

    public class clsP_prodcombo {
        public int codigo_combo;
        public String codigo;
        public String producto;
        public String tipo;
        public double cantmin;
        public double canttot;
    }

    public class clsP_prodprecio {
        public int  codigo_precio;
        public int  empresa;
        public int codigo_producto;
        public int  nivel;
        public double precio;
        public String unidadmedida;
    }

    public class clsP_nivelpreciolist {
        public String codigo;
        public int  nivel;
        public String nombre;
        public double precio;
        public String unidadmedida;
	}

    public class clsP_prodmenu {
        public int  codigo_menu;
        public int  empresa;
        public int  codigo_producto;
        public int  opcion_lista;
        public int  opcion_producto;
        public int  orden;
        public String nombre;
        public String nota;
        public int unid;

    }

    public class clsP_prodmenuopc {
        public int  codigo_menu_opcion;
        public int codigo_menu;
        public double cant;
        public int orden;
        public String nombre;
    }

    public class clsp_prodmenuopc_det {
        public int codigo_menuopc_det;
        public int codigo_menu_opcion;
        public int codigo_producto;
    }

    public class clsP_producto {
        public int  codigo_producto;
        public String codigo;
        public String codigo_tipo;
        public int  linea;
        public int  empresa;
        public String marca;
        public String codbarra;
        public String desccorta;
        public String desclarga;
        public double costo;
        public double factorconv;
        public String unidbas;
        public String unidmed;
        public double unimedfact;
        public String unigra;
        public double unigrafact;
        public int  descuento;
        public int  bonificacion;
        public double imp1;
        public double imp2;
        public double imp3;
        public String vencomp;
        public int  devol;
        public int  ofrecer;
        public int  rentab;
        public int  descmax;
        public String iva;
        public String codbarra2;
        public int  cbconv;
        public String bodega;
        public String subbodega;
        public double peso_promedio;
        public int  modif_precio;
        public String video;
        public int  venta_por_peso;
        public int  es_prod_barra;
        public String unid_inv;
        public int  venta_por_paquete;
        public int  venta_por_factor_conv;
        public int  es_serializado;
        public int  param_caducidad;
        public int  producto_padre;
        public double factor_padre;
        public int  tiene_inv;
        public int  tiene_vineta_o_tubo;
        public double precio_vineta_o_tubo;
        public int  es_vendible;
        public double unigrasap;
        public String um_salida;
        public int  activo;
        public String imagen;
        public float tiempo_preparacion;
    }

    public class clsP_proveedor {
        public int  codigo_proveedor;
        public String codigo;
        public String nombre;
        public Boolean  activo;
    }

    public class clsP_stock {
        public int  codigo;
        public double cant;
        public double cantm;
        public double peso;
        public double plibra;
        public String lote;
        public String documento;
        public int  fecha;
        public int  anulado;
        public String centro;
        public String status;
        public int  enviado;
        public int  codigoliquidacion;
        public String corel_d_mov;
        public String unidadmedida;
    }

    public class clsP_stock_update {
        public int  codigo_stock;
        public int  empresa;
        public int  sucursal;
        public int  ruta;
        public int  codigo_producto;
        public double cantidad;
        public String unidad_medida;
        public String referencia;
        public long fecha_transaccion;
        public long fecha_procesado;
        public int  procesado;
    }

    public class clsP_sucursal {
        public int  codigo_sucursal;
        public String codigo;
        public int  empresa;
        public int  codigo_nivel_precio;
        public String descripcion;
        public String nombre;
        public String direccion;
        public String telefono;
        public String correo;
        public String nit;
        public String texto;
        public int  activo;
        public String fel_codigo_establecimiento;
        public String fel_usuario_firma;
        public String fel_llave_certificacion;
        public String fel_llave_firma;
        public String fel_afiliacion_iva;
        public String fel_usuario_certificacion;
        public String codigo_postal;
        public int  codigo_escenario_isr;
        public int  codigo_escenario_iva;
        public String codigo_municipio;
        public int codigo_proveedor;
    }

    public class clsP_ruta {
        public String codigo;
        public String sucursal;
        public String nombre;
        public int  codigo_ruta;
        public boolean activo;
    }

    public class clsP_usuario {
		public String codigo;
		public String nombre;
		public String cod_grupo;
		public String sucursal;
		public String clave;
		public String empresa;
		public int  cod_rol;
	}

    public class clsP_usgrupo {
        public int codigo;
        public String nombre;
        public String cuenta;
    }

    public class clsP_usgrupoopc {
        public int grupo;
        public int  opcion;
    }

    public class clsP_usopcion {
        public int  codigo;
        public String menugroup;
        public String nombre;
    }

    public class clsVendedores {
        public String codigo;
        public String nombre;
        public String clave;
        public int ruta;
        public int  nivel;
        public double nivelprecio;
        public String bodega;
        public String subbodega;
        public int  activo;
        public int  codigo_vendedor;
        public String imagen;
        public long fecha_inicio_labores;
        public long fecha_fin_labores;
    }

    public class clsT_prodmenu {
        public int  id;
        public int  idsess;
        public int  iditem;
        public String codigo;
        public String nombre;
        public String descrip;
        public String nota;
        public int  bandera;
        public int  idlista;
        public int  cant;
    }

    public class clsT_combo {
        public int  codigo_menu;
        public int  idcombo;
        public int  unid;
        public int  cant;
        public int  idseleccion;
        public int  orden;
    }

    public class clsT_movd {
        public int  coreldet;
        public String corel;
        public int  producto;
        public double cant;
        public double cantm;
        public double peso;
        public double pesom;
        public String lote;
        public int  codigoliquidacion;
        public String unidadmedida;
        public double precio;
        public int  razon;
    }

    public class clsT_movr {
        public int  coreldet;
        public String corel;
        public int  producto;
        public double cant;
        public double cantm;
        public double peso;
        public double pesom;
        public String lote;
        public int  codigoliquidacion;
        public String unidadmedida;
        public double precio;
        public int  razon;
    }

    public class clsT_venta {
        public String producto;
        public String empresa;
        public String um;
        public double cant;
        public String umstock;
        public double factor;
        public double precio;
        public double imp;
        public double des;
        public double desmon;
        public double total;
        public double preciodoc;
        public double peso;
        public double val1;
        public String val2;
        public double val3;
        public String val4;
        public double percep;
    }

    //

	public class clsLista {
		public int  pk;
		public String f1;
		public String f2;
		public String f3;
		public String f4;
		public String f5;
		public String f6;
		public String f7;
		public String f8;
	}

	//region Aux

	public class clsCD {
		public String Cod,Desc,Text,um,prec;
		public int codInt;
		public double costo;
	}
	
	public class clsCDB {
		public String Cod,Desc,Adds,nit,email;
		public int Bandera,Cobro,ppend, CodNum;
		public double valor,coorx,coory;
		public long Date;
	}
	
	public class clsCFDV {
		public int id;
		public String Cod,Desc,Fecha,Valor,Sid, UUID, FechaFactura;
		public double val, precio;
	}
	
	public class clsExist {
		public String Cod,Desc,Fecha,Valor,ValorM,ValorT,Peso,PesoM,PesoT,Lote,Doc,Centro,Stat;
		public double cant,cantm;
		public int id,flag,items;
	}

	public class clsDevCan {
		public String Cod,Desc,Fecha,Valor,ValorM,ValorT,Peso,PesoM,PesoT,Lote,Doc,Centro,Stat;
		public double cant,cantm;
		public int id,flag,items;
	}

	public class clsMenu {
		public int ID,Icon,icod,cant;
		public String Name,Cod;
	}	
	
	public class clsVenta {
		public String Cod,Nombre,um,val,valp,sdesc,emp;
		public double Cant,Peso,Prec,Desc,Total,imp,percep;
		
	}
	
	public class clsPromoItem {
		public String Prod,Nombre,Bon,Tipo;
		public double RIni,RFin,Valor;
		public boolean Porrango,Porprod,Porcant;
	}
	
	public class clsObj {
		public String Nombre,Cod,Meta,Acum,Falta,Perc;
	}
	
	public class clsDepos {
		public String Nombre,Cod,Tipo,Banco;
		public double Valor,Total,Efect,Chec;
		public int Bandera,NChec;
	}
	
	public class clsCobro {
		public String Factura,Tipo,fini,ffin;
		public double Valor,Saldo,Pago;
		public int id,flag;
	}
	
	public class clsPago {
		public String Tipo,Num,Valor;
		public int id;
	}
	
	public class clsEnvio {
		public String Nombre;
		public int id,env,pend;
	}
	
	public class clsBonifItem {
		public String prodid,lista,cantexact,globbon,tipocant,porcant;
		public int tipolista;
		public double valor,mul,monto;
	}	
	
	public class clsBonifProd {
		public String id,nombre,prstr;
		public int flag;
		public double cant,cantmin,disp,precio,costo;
	}	
	
	public class clsDemoDlg {
		public int tipo,flag;
		public String Cod,Desc;
		public double val;
	}

	public class clsFinDiaItems {
		public int id, corel, val1, val2, val3, val4, val5, val6, val7, val8;
	}

	public class clsRepes {
		public int id,bol;
		public double peso,can;
		public String sid,speso,sbol,scan,stot;
	}

	public class  clsAyudante{
		public String idayudante;
		public String nombreayudante;
	}

	public class clsVehiculo{
		public String idVehiculo,marca,placa;
	}

	public class clsBarras{
		public String barra,peso;
	}

    public class clsOpcion {
        public int codigo_menu_opcion,bandera,orden,cod,unid,cant;
        public String Name;
    }

    public class clsReport{
        public String codProd, descrip, corel, serie, um;
        public int correl,cant,tipo;
        public double total,imp;
        public long fecha;
    }

	public class clsP_correlativos{
		public String empresa,ruta,serie,tipo,enviado;
		public int inicial,fin,actual;
	}



    //endregion

    public class clsView {
        public int  pk;
        public String f1;
        public String f2;
        public String f3;
        public String f4;
        public String f5;
        public String f6;
        public String f7;
        public String f8;
    }

}
