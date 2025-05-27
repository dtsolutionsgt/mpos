package com.dtsgt.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {
	
	public DateUtils() {}
	
	public String sfecha(long f) {
		long vy,vm,vd;
		String s;

		if (f==0) return "";
		
		vy=(long) f/100000000;
		f=f % 100000000;
		vm=(long) f/1000000;
		f=f % 1000000;
		vd=(long) f/10000;
		f=f % 10000;
		
		s="";
		if (vd>9) { s=s+String.valueOf(vd)+"/";} else {s=s+"0"+String.valueOf(vd)+"/";}  
		if (vm>9) { s=s+String.valueOf(vm)+"/20";} else {s=s+"0"+String.valueOf(vm)+"/20";}  
		if (vy>9) { s=s+String.valueOf(vy);} else {s=s+"0"+String.valueOf(vy);} 
		
		return s;
	}

	public String sfechash(long f) {

		long vy,vm,vd;
		String s;

		if (f==0) return "";

		vy=(long) f/100000000;
		f=f % 100000000;
		vm=(long) f/1000000;
		f=f % 1000000;
		vd=(long) f/10000;
		f=f % 10000;

		s="";
		if (vd>9) s=s+String.valueOf(vd)+"/"; else s=s+"0"+String.valueOf(vd)+"/";
		if (vm>9) s=s+String.valueOf(vm); else s=s+"0"+String.valueOf(vm);

		return s;
	}

	public String shora(long vValue) {

		int h,m;
		String sh,sm;
			
		h=(int) (vValue % 10000);
		m=h % 100;if (m>9) {sm=String.valueOf(m);} else {sm="0"+String.valueOf(m);}
		h=(int) h/100;if (h>9) {sh=String.valueOf(h);} else {sh="0"+String.valueOf(h);}
			
		return sh+":"+sm;
	}

	public String geActTimeStr(){

		long f,ch,cm,cs;
		String s,ss;
		
		final Calendar c = Calendar.getInstance();
		
		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);
		cs = c.get(Calendar.SECOND);
		
		s=""+ch;if (ch<10) s="0"+s;
		ss=""+cm;if (cm<10) ss="0"+ss;s=s+":"+ss;
		ss=""+cs;if (cs<10) ss="0"+ss;s=s+":"+ss;
		
		return s;
	}

    public long addDays(long f,int days){

        long cyear,cmonth,cday;

        final Calendar c = Calendar.getInstance();

		int año = 0;
		int mes = 0;
		int dia = 0;

		String saño = getyear(f)+"";
		String smes = getmonth(f)-1+"";
		String sdia = getday(f)+"";

		try {
			//#EJC20220102: Si el mes es enero, la resta devuelve 0, debe devolver diciembre (12)
			if (smes.equals("0")){
				smes ="12";
				saño = (Integer.parseInt(saño) - 1) + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			año = Integer.parseInt(saño);
			mes = Integer.parseInt(smes);
			dia = Integer.parseInt(sdia);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//c.set(getyear(f), getmonth(f)-1, getday(f));
		c.set(año,mes,dia);
        c.add(Calendar.DATE, days);
        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH)+1;
        cday = c.get(Calendar.DAY_OF_MONTH);

        f=cfecha(cyear,cmonth,cday);

        return f;
    }

	public long addHours(int hours){
		long f,cyear,cmonth,cday,chour,cmin;

		final Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, hours);

		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		chour = c.get(Calendar.HOUR_OF_DAY);
		cmin = c.get(Calendar.MINUTE);

		f=cfecha(cyear,cmonth,cday)+100*chour+cmin;

		return f;
	}

	public String sSecond(){

		long cs;
		String sss;
		
		final Calendar c = Calendar.getInstance();
		cs=c.get(Calendar.SECOND);
				
		sss=""+cs;
		if (cs<10) sss="0"+sss;
		
		return sss;
	}
	
	public String univfecha(long f) {

		long vy,vm,vd,m,h;
		String s;

		vy= (long) f/100000000;f=f % 100000000;
		vm= (long) f/1000000;f=f % 1000000;
		vd= (long) f/10000;f=f % 10000;
		h=  (long) f/100;
		m=  (long) f % 100;
		
		s="20";
		if (vy>9) s=s+vy; else s=s+"0"+vy; 
		if (vm>9) s=s+vm; else s=s+"0"+vm;
		if (vd>9) s=s+vd; else s=s+"0"+vd;  
		s=s+" ";
		s=s+"00:00:00";

		return s;
	}

	public String univfechahora(long f) {

        long vy,vm,vd,m,h;
        String s;

        vy= f/100000000;f=f % 100000000;
        vm= f/1000000;f=f % 1000000;
        vd= f/10000;f=f % 10000;
        h=  f/100;
        m=  f % 100;

        s="20";
        if (vy>9) s=s+vy; else s=s+"0"+vy;
        if (vm>9) s=s+vm; else s=s+"0"+vm;
        if (vd>9) s=s+vd; else s=s+"0"+vd;
        s=s+" ";
        if (h>9)  s=s+h;  else s=s+"0"+h;
        s=s+":";
        if (m>9)  s=s+m;  else s=s+"0"+m;
        s=s+":00";

        return s;
    }

    public String univfecha_vb_net(long f) {

        long vy,vm,vd,m,h;
        String s;

        vy=(int) f/100000000;f=f % 100000000;
        vm=(int) f/1000000;f=f % 1000000;
        vd=(int) f/10000;f=f % 10000;
        h= (int) f/100;
        m= f % 100;

        s="20";
        if (vy>9) s=s+vy; else s=s+"0"+vy;s=s+"-";
        if (vm>9) s=s+vm; else s=s+"0"+vm;s=s+"-";
        if (vd>9) s=s+vd; else s=s+"0"+vd;
        s=s+" ";
        if (h>9)  s=s+h;  else s=s+"0"+h;
        s=s+":";
        if (m>9)  s=s+m;  else s=s+"0"+m;
        s=s+":00";

        return s;
    }

	public String univfecha_vb_net_sinhora(long f) {
		long vy,vm,vd;
		String s;

		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		vd=(long) f/10000;f=f % 10000;

		s="20";
		if (vy>9) s=s+vy; else s=s+"0"+vy;s=s+"-";
		if (vm>9) s=s+vm; else s=s+"0"+vm;s=s+"-";
		if (vd>9) s=s+vd; else s=s+"0"+vd;

		return s;
	}

	public String univfechasinhora(long f) {

		int vy,vm,vd;
		String s;

		vy=(int) f/10000;f=f % 10000;
		vm=(int) f/100;f=f % 100;
		vd=(int) f;

		s=""+vy;
		if (vm>9) s=s+vm; else s=s+"0"+vm;
		if (vd>9) s=s+vd; else s=s+"0"+vd;
		s=vy+" "+vm+":"+vd;

		return s;
	}
	
	public String univfechaseg() {

		long f,fecha, vy,vm,vd;;
		int cyear,cmonth,cday,ch,cm, cs;
		String s;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);
		cs=c.get(Calendar.SECOND);

		vy=cyear;
		vm=cmonth;
		vd=cday;
			
		s=""+vy; 
		if (vm>9) s=s+vm; else s=s+"0"+vm;
		if (vd>9) s=s+vd; else s=s+"0"+vd;
		s = s + " ";
		if (ch>9) s=s+ch; else s=s+"0"+ch; s=s+ ":";
		if (cm>9) s=s+cm; else s=s+"0"+cm; s=s+ ":";
		if (cs>9) s=s+cs; else s=s+"0"+cs;

		return s;
	}

	public String univfechalong(long f) {

		long vy,vm,vd;
		String s;

		vy=(long) f/10000;f=f % 10000;
		vm=(long) f/100;f=f % 100;
		vd=(long) f;

		s="20"+vy;
		if (vm>9) s=s+vm; else s=s+"0"+vm;
		if (vd>9) s=s+vd; else s=s+"0"+vd;
        s="20"+vy+" "+vm+":"+vd+":00";

		return s;
	}

    public String univfechaext(long f) {

        long vy,vm,vd;
        String s;

        vy=(long) f/10000;f=f % 10000;
        vm=(long) f/100;f=f % 100;
        vd=(long) f;

        s=""+vy;
        if (vm>9) s=s+vm; else s=s+"0"+vm;
        if (vd>9) s=s+vd; else s=s+"0"+vd;
        s=vy+" "+vm+":"+vd+":00";

        return s;
    }

	public String univfechasql(long f) {

		long vy,vm,vd;
		String sy,sm,sd;

		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		vd=(long) f/10000;f=f % 10000;

		if (vy>9) sy="20"+vy; else sy="200"+vy;
		if (vm>9) sm=""+vm; else sm="0"+vm;
		if (vd>9) sd=""+vd; else sd="0"+vd;

		return sy+sm+sd;
	}

	public String univfechaReport(long f) {

		long vy,vm,vd;
		String sy,sm,sd;

		vy=(long) f/100000000;
		f=f % 100000000;
		vm=(long) f/1000000;
		f=f % 1000000;
		vd=(long) f/10000;
		f=f % 10000;

		if(vy<100){
			if (vy>9) sy="20"+vy; else sy="200"+vy;
		}else {
			sy = "" + vy;
		}

		if (vm>9) sm=""+vm; else sm="0"+vm;
		if (vd>9) sd=""+vd; else sd="0"+vd;

		return sd+"/"+sm+"/"+sy;
	}

    public long ffecha00(long f) {
		f=(long) f/10000;
		f=f*10000;
		return f;
	}
	
	public long ffecha24(long f) {
		f=(long) f/10000;
		f=f*10000+2359;
		return f;
	}

	public long cfechaSinHora(int year,int month, int day) {
		long c;
		c=year % 100;
		c=c*10000+month*100+day;
		return c;
	}

	public long cfechaDesc(long year, long month, long day) {

		long c;

		String d,mes,dia,ano;

		int siglo;

		if(year<2000){
			siglo = 0;
		}else {
			siglo = 2000;
		}

		year = year - siglo;
		ano = Long.toString(year);
		mes = Long.toString(month);
		dia = Long.toString(day);

		if (dia.length()<2) {
			dia = "0" + day;
		}

		if (mes.length()<2) {
			mes = "0" + month;
		}

		d= ano + mes + dia + "0000";

		c=Long.parseLong(d);

		return c;
	}

	public long cfechaRep(int year,int month, int day, boolean inicial) {

		long c;

		String d,mes,dia,ano;

		int siglo;

		if(year<2000){
			siglo = 0;
		}else {
			siglo = 2000;
		}

		year = year - siglo;
		ano = Integer.toString(year);
		mes = Integer.toString(month);
		dia = Integer.toString(day);

		if (dia.length()<2) {
			dia = "0" + day;
		}

		if (mes.length()<2) {
			mes = "0" + month;
		}

		if (inicial){
			d= ano + mes + dia + "0000";
		}else{
			d= ano + mes + dia + "2359";
		}

		c=Long.parseLong(d);

		return c;
	}

	public long parsedate(int date,int hour,int min) {
		long f;
		f=date+100*hour+min;
		return f;
	}
		
	public long getyear(long f) {

		long vy;
				
		vy=(long) f/100000000;f=f % 100000000;
		vy=vy+2000;
		
		return vy;
	}
	
	public long getmonth(long f) {
		long vy,vm;
		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		return vm;
	}
	
	public long getday(long f) {
		long vy,vm,vd;
		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		vd=(long) f/10000;f=f % 10000;
		return vd;
	}
	
	public long LastDay(int year,int month) {

		long m,y,ld;
		
		m=month % 2;
		if (m==1) {
			ld=31;
		} else {
			ld=30;
		}
		
		if (month==2) {
			ld=28;
			if (year % 4==0) {ld=29;}
		}

		return ld;
		
	}

	public long timeDiff(long v1,long v2) {

		long h1,m1,h2,m2,vm1,vm2,dif;

		h1=v1 % 10000;m1=h1 % 100;h1=(int) h1/100;
		vm1=h1*60+m1;
		
		h2=v2 % 10000;m2=h2 % 100;h2=(int) h2/100;
		vm2=h2*60+m2;
		
		dif=vm1-vm2;
		if (dif<0) dif=-dif;
		
		return dif;
		
	}

	public String sTimeDiff(long v1,long v2) {
		String st;
		long h1,m1,h2,m2,vm1,vm2,dif;

		h1=v1 % 10000;m1=h1 % 100;h1=(int) h1/100;
		vm1=h1*60+m1;

		h2=v2 % 10000;m2=h2 % 100;h2=(int) h2/100;
		vm2=h2*60+m2;

		dif=vm1-vm2;
		if (dif<0) dif=-dif;

		h1=dif / 60; m1=dif % 60;
		st=h1+":";if (m1<10) st=st+"0";st=st+m1;

		return st;
	}

	public int dayofweek(long f) {

		int y,m,d,dw;
	     
		final Calendar c = Calendar.getInstance();

		int año = 0;
		int mes = 0;
		int dia = 0;

		String saño = getyear(f)+"";
		String smes = getmonth(f)-1+"";
		String sdia = getday(f)+"";

		try {
			if (smes.equals("0")){
				smes ="12";
				saño = (Integer.parseInt(saño) - 1) + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			año = Integer.parseInt(saño);
			mes = Integer.parseInt(smes);
			dia = Integer.parseInt(sdia);

		} catch (Exception e) {
			e.printStackTrace();
		}

		c.set(año,mes,dia);

	    dw=c.get(Calendar.DAY_OF_WEEK);
	    
	    if (dw==1) dw=7;else dw=dw-1;
	    
	    return dw;
	}

	public long getFechaActual(){

		long f,fecha;
		String fechaS;
		int cyear,cmonth,cday,ch,cm;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f=cfechaSinHora(cyear,cmonth,cday);
		fechaS=f+"0000";
		fecha= Long.parseLong(fechaS);

		return fecha;
	}

	public long getFechaActualReport(){

		long f,fecha;
		int cyear,cmonth,cday,ch,cm;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f=cfechaSinHora(cyear,cmonth,cday);
		fecha=f*10000;

		return fecha;
	}

	public long getFechaActualReport(boolean inicial){

		long f,fecha;
		int cyear,cmonth,cday,ch,cm;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f=cfechaSinHora(cyear,cmonth,cday);

		if (inicial){
			fecha=f*10000;
		}else{
			String sFecha;
			sFecha=Long.toString(f);
			sFecha=sFecha+"2359";
			fecha=Long.parseLong(sFecha);
		}

		return fecha;
	}

	public long getFechaActualSinHora(){

		long f;
		int cyear,cmonth,cday;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f=cfechaSinHora(cyear,cmonth,cday);

		return f;
	}

	public long getActDateTime(){

		long f,fecha;
		int cyear,cmonth,cday,ch,cm;
		
		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);

		f=cfecha(cyear,cmonth,cday);
		fecha=f+ch*100+cm;
		
		return fecha;
	}

	public long getActDate(){

		long f;
		int cyear,cmonth,cday;

		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f=cfecha(cyear,cmonth,cday);

		return f;
	}

	public int getActHour(){
		final Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public long cfecha(long year, long month, long day) {
		long c;
		c=year % 100;
		c=c*10000+month*100+day;
		return c*10000;
	}

	public String getActDateStr(){

		long f;
		int cyear,cmonth,cday;
		
		final Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		
		f=cfecha(cyear,cmonth,cday);
		
		return sfecha(f);
	}
	
	public long getCorelBase(){

		long f;
		int cyear,cmonth,cday,ch,cm,cs,vd,vh;
		
		final Calendar c = Calendar.getInstance();
		
		cyear = c.get(Calendar.YEAR);cyear=cyear % 10;
		cmonth = c.get(Calendar.MONTH)+1;
		cday = c.get(Calendar.DAY_OF_MONTH);
		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);
		cs=c.get(Calendar.SECOND);
				
		vd=cyear*384+cmonth*32+cday;
		vh=ch*3600+cm*60+cs;
		
		f=vd*100000+vh;
		f=f*100;
		
		return f;
	}

	public String getCorelBaseLong(long f) {

		long vy,vm,vd,m,h,sec;
		String s;

		final Calendar c = Calendar.getInstance();

		vy = c.get(Calendar.YEAR);
		vm = c.get(Calendar.MONTH)+1;
		vd = c.get(Calendar.DAY_OF_MONTH);
		h=c.get(Calendar.HOUR_OF_DAY);
		m=c.get(Calendar.MINUTE);
		sec=c.get(Calendar.SECOND);

		s=""+vy;
		if (vm>9) s=s+vm; else s=s+"0"+vm;
		if (vd>9) s=s+vd; else s=s+"0"+vd;
		if (h>9)  s=s+h;  else s=s+"0"+h;
		if (m>9)  s=s+m;  else s=s+"0"+m;
		if (sec>9)  s=s+sec;  else s=s+"0"+sec;

		return s;
	}

    public String getCorelBaseLong() {

        long vy,vm,vd,m,h,sec;
        String s;

        final Calendar c = Calendar.getInstance();

        vy = c.get(Calendar.YEAR);
        vm = c.get(Calendar.MONTH)+1;
        vd = c.get(Calendar.DAY_OF_MONTH);
        h=c.get(Calendar.HOUR_OF_DAY);
        m=c.get(Calendar.MINUTE);
        sec=c.get(Calendar.SECOND);

        s=""+vy;
        if (vm>9) s=s+vm; else s=s+"0"+vm;
        if (vd>9) s=s+vd; else s=s+"0"+vd;
        if (h>9)  s=s+h;  else s=s+"0"+h;
        if (m>9)  s=s+m;  else s=s+"0"+m;
        if (sec>9)  s=s+sec;  else s=s+"0"+sec;

        return s;
    }

	public int getOrdenCorel(int idruta){
		int oc,ch,cm,cs;
		final Calendar c = Calendar.getInstance();

		idruta=idruta % 1000;

		ch=c.get(Calendar.HOUR_OF_DAY);
		cm=c.get(Calendar.MINUTE);
		cs=c.get(Calendar.SECOND);

		oc=ch*10000+cm*100+cs;
		oc=idruta*1000000+oc;

		return oc;
	}

	public int actDate()  {
		int cyear,cmonth,cday,f;

		Calendar c = Calendar.getInstance();
		cyear = c.get(Calendar.YEAR);
		cyear = cyear % 100;
		cmonth = c.get(Calendar.MONTH) + 1;
		cday = c.get(Calendar.DAY_OF_MONTH);

		f = cyear*10000 + cmonth * 100 + cday;
		return f;
	}



	//region Fecha larga

    public long fechalarga(int year,int month, int day) {
        long c;
        c=year % 10000;
        c=c*10000+month*100+day;
        return c;
    }

    public String sfechaLarga(long f) {

        long vy,vm,vd;
        String sy,sm,sd;

        if (f==0) return "--/--/--";

        vy=(long) f/10000;
        f=f % 10000;
        vm=(long) f/100;
        vd=f % 100;

        sy=""+vy;
        if (vm>9) sm=""+vm; else sm="0"+vm;
        if (vd>9) sd=""+vd; else sd="0"+vd;

        return sd+"/"+sm+"/"+sy;
    }

	//endregion

	// ********************

	public boolean fechaNIT_SV(String fs) {
		String yy,mm,dd;
		int y,m,d;
		long ld;

		try {
			dd=fs.substring(0,2);d=Integer.parseInt(dd);
			mm=fs.substring(2,4);m=Integer.parseInt(mm);if (m>12) return false;
			yy=fs.substring(4,6);y=Integer.parseInt(yy);
		} catch (Exception e) {
			return false;
		}

		try {
			ld=LastDay(1900+y,m);
			if (d<=ld) return true;

			ld=LastDay(2000+y,m);
			if (d<=ld) return true;
		} catch (Exception e) {	}

		return false;
	}

}