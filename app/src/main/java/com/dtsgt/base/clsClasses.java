package com.dtsgt.base;

public class clsClasses {

	public class clsP_almacen {
		public int  codigo;
		public String nombre;
		public int  activo;
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
		public String codigo;
		public String tipo;
		public String nombre;
		public String cuenta;
		public String empresa;
		public int activo;
	}

    public class clsP_cajacierre {
        public String sucursal;
        public String ruta;
        public int  corel;
        public int  estado;
        public int  fecha;
        public String vendedor;
		public int codpago;
		public double fondocaja;
        public double montoini;
        public double montofin;
        public double montodif;
        public String statcom;
    }

    public class clsP_cajapagos {
        public String empresa;
        public String sucursal;
        public String ruta;
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
        public String vendedor;
        public String statcom;
    }

    public class clsP_cajareporte {
        public String sucursal;
        public String ruta;
        public int  corel;
        public int  linea;
        public String texto;
        public String statcom;
    }

    public class clsP_cliente {
		public String codigo;
		public String nombre;
		public String bloqueado;
		public String tiponeg;
		public String tipo;
		public String subtipo;
		public String canal;
		public String subcanal;
		public int  nivelprecio;
		public String mediapago;
		public double limitecredito;
		public int  diacredito;
		public String descuento;
		public String bonificacion;
		public long ultvisita;
		public double impspec;
		public String invtipo;
		public String invequipo;
		public String inv1;
		public String inv2;
		public String inv3;
		public String nit;
		public String mensaje;
		public String email;
		public String eservice;
		public String telefono;
		public String dirtipo;
		public String direccion;
		public String region;
		public String sucursal;
		public String municipio;
		public String ciudad;
		public int  zona;
		public String colonia;
		public String avenida;
		public String calle;
		public String numero;
		public String cartografico;
		public double coorx;
		public double coory;
		public String bodega;
		public String cod_pais;
		public String firmadig;
		public String codbarra;
		public String validacredito;
		public String fact_vs_fact;
		public String chequepost;
		public String precio_estrategico;
		public String nombre_propietario;
		public String nombre_representante;
		public double percepcion;
		public String tipo_contribuyente;
		public int  id_despacho;
		public int  id_facturacion;
		public int  modif_precio;
	}

    public class clsP_corel {
        public String resol;
        public String serie;
        public int  corelini;
        public int  corelfin;
        public int  corelult;
        public int  fechares;
        public String ruta;
        public int  fechavig;
        public int  resguardo;
        public int  valor1;
    }

    public class clsP_descuento {
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
		public String empresa;
		public String nombre;
		public String initpath;
		public String ftppath;
		public int  numreimpres;
		public String moddesc;
		public String usarpeso;
		public String devconprec;
		public String acumdesc;
		public double descmax;
		public double bonvoltol;
		public String cod_pais;
		public int  boleta_deposito;
		public int  editar_direccion;
		public int  deposito_parcial;
		public int  col_imp;
		public int  inv_enlinea;
		public int  fin_dia;
		public int  presentacion_multiple;
		public int  precios_especiales;
		public int  autoriz_modif_descbon;
		public int  cambio_por_cambio;
		public int  devolucion_mercancia;
		public int  cobros_sin_referencia;
		public double porcentaje_nc;
		public double porc_merma;
		public String producto_error_suma;
		public String unidad_medida_peso;
		public String lote_por_defecto;
		public int  incidencia_no_lectura;
	}

    public class clsP_factorconv {
        public String producto;
        public String unidadsuperior;
        public double factorconversion;
        public String unidadminima;
    }

	public class clsP_impuesto {
		public int  codigo;
		public double valor;
		public int activo;
	}

	public class clsP_linea {
		public String codigo;
		public String marca;
		public String nombre;
		public int activo;
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

    public class clsP_prodprecio {
        public String codigo;
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
        public String codigo;
        public int  item;
        public String nombre;
        public int  idopcion;
        public int  cant;
        public int  orden;
        public int  bandera;
        public String nota;
    }

    public class clsP_prodopc {
        public int  id;
        public String nombre;
        public int  activo;
    }

    public class clsP_prodopclist {
        public int  id;
        public String producto,nombre;
        public double cant;
        public int  idreceta;
    }

    public class clsP_producto {
		public String codigo;
		public String tipo;
		public String linea;
		public String sublinea;
		public String empresa;
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
		public int  codigo;
		public String nombre;
		public int  activo;
	}

	public class clsP_sucursal {
		public String codigo;
		public String empresa;
		public String descripcion;
		public String nombre;
		public String direccion;
		public String telefono;
		public String nit;
		public String texto;
		public int  activo;
	}

	public class clsP_ruta {
		public String codigo;
		public String nombre;
		public String activo;
		public String vendedor;
		public String venta;
		public String forania;
		public String sucursal;
		public String tipo;
		public String subtipo;
		public String bodega;
		public String subbodega;
		public String descuento;
		public String bonif;
		public String kilometraje;
		public String impresion;
		public String recibopropio;
		public String celular;
		public String rentabil;
		public String oferta;
		public double percrent;
		public String pasarcredito;
		public String teclado;
		public String editdevprec;
		public String editdesc;
		public String params;
		public int  semana;
		public int  objano;
		public int  objmes;
		public String syncfold;
		public String wlfold;
		public String ftpfold;
		public String email;
		public int  lastimp;
		public int  lastcom;
		public int  lastexp;
		public String impstat;
		public String expstat;
		public String comstat;
		public String param1;
		public String param2;
		public double pesolim;
		public int  intervalo_max;
		public int  lecturas_valid;
		public int  intentos_lect;
		public int  hora_ini;
		public int  hora_fin;
		public int  aplicacion_usa;
		public int  puerto_gps;
		public int  es_ruta_oficina;
		public int  diluir_bon;
		public int  preimpresion_factura;
		public int  modificar_media_pago;
		public String idimpresora;
		public String numversion;
		public int  fechaversion;
		public String arquitectura;
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
        public String codigo;
        public String nombre;
        public String cuenta;
    }

    public class clsP_usgrupoopc {
        public String grupo;
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
		public int Bandera,Cobro,ppend;
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
		public int ID,Icon;
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
        public int ID,bandera,listid;
        public String Name,Cod,Descrip;
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
