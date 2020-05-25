package com.dtsgt.base;

public class clsClasses {

    public class clsD_factura {
        public int  empresa;
        public String corel;
        public String anulado;
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
        public String calcobj;
        public String serie;
        public int  corelativo;
        public int  impres;
        public String add1;
        public String add2;
        public String add3;
        public String depos;
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
        public String anulado;
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
    }

    public class clsD_facturap {
        public int  empresa;
        public String corel;
        public int  item;
        public String anulado;
        public int  codpago;
        public String tipo;
        public double valor;
        public String desc1;
        public String desc2;
        public String desc3;
        public String depos;
    }

    public class clsD_facturas {
        public String corel;
        public int  id;
        public int  producto;
        public double cant;
        public String umstock;
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
        public String codigo;
        public String nombre;
        public String bloqueado;
        public int  nivelprecio;
        public int  mediapago;
        public double limitecredito;
        public int  diacredito;
        public String descuento;
        public String bonificacion;
        public long  ultvisita;
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
        public int  codigo_cliente;
        public String imagen;
    }

    public class clsP_corel {
        public int codigo_corel ;
        public String resol;
        public String serie;
        public int  corelini;
        public int  corelfin;
        public int  corelult;
        public long  fechares;
        public int ruta;
        public long  fechavig;
        public int  resguardo;
        public int  valor1;
        public String activa;
    }

    public class clsP_descuento {
        public int  codigo_descuento;
        public String cliente;
        public int  ctipo;
        public String producto;
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

	public class clsP_impuesto {
        public  int codigo_impuesto;
		public int  codigo;
		public double valor;
		public int activo;
	}

    public class clsP_linea   {
        public String codigo;
        public String marca;
        public String nombre;
        public int  activo;
        public String imagen;
        public int  codigo_linea;
    }

    public class clsP_mediapago {
		public int  codigo;
		public String nombre;
		public String activo;
		public int  nivel;
		public String porcobro;
	}

    public class clsP_nivelprecio {
        public int codigo;
        public String nombre;
        public int activo;
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
        public int linea;
        public int empresa;
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
        public String descuento;
        public String bonificacion;
        public double imp1;
        public double imp2;
        public double imp3;
        public String vencomp;
        public String devol;
        public String ofrecer;
        public String rentab;
        public String descmax;
        public String iva;
        public String codbarra2;
        public int  cbconv;
        public String bodega;
        public String subbodega;
        public double peso_promedio;
        public int  modif_precio;
        public String imagen;
        public String video;
        public int  venta_por_peso;
        public int  es_prod_barra;
        public String unid_inv;
        public int  venta_por_paquete;
        public int  venta_por_factor_conv;
        public int  es_serializado;
        public int  param_caducidad;
        public String producto_padre;
        public double factor_padre;
        public int  tiene_inv;
        public int  tiene_vineta_o_tubo;
        public double precio_vineta_o_tubo;
        public int  es_vendible;
        public double unigrasap;
        public String um_salida;
        public int  activo;
    }

    public class clsP_proveedor {
        public int  codigo_proveedor;
        public String codigo;
        public String nombre;
        public int  activo;
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
        public String nit;
        public String texto;
        public int  activo;
        public String pet_prefijo;
        public String pet_llave;
        public String pet_alias_pfx;
        public String pet_pfx_llave;
        public int  codigo_escenario_isr;
        public int  codigo_escenario_iva;
        public String codigo_municipio;
    }

    public class clsP_ruta {
        public String codigo;
        public String sucursal;
        public String nombre;
        public int  codigo_ruta;
        public String activo;
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
        public String ruta;
        public int  nivel;
        public double nivelprecio;
        public String bodega;
        public String subbodega;
        public int  activo;
        public int  codigo_vendedor;
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
	}
	
	public class clsCDB {
		public String Cod,Desc,Adds,nit,email;
		public int Bandera,Cobro,ppend, CodNum;
		public double valor,coorx,coory;
		public long Date;
	}
	
	public class clsCFDV {
		public int id;
		public String Cod,Desc,Fecha,Valor,Sid;
		public double val;
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
		public int ID,Icon,icod;
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
        public int codigo_menu_opcion,bandera,orden,cod,unid;
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

    public class clsT_combo {
        public int  codigo_menu;
        public int  idcombo;
        public int  unid;
        public int  cant;
        public int  idseleccion;
        public int  orden;
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
