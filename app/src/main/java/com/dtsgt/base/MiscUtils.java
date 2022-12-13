package com.dtsgt.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.text.DecimalFormat;
import java.util.Calendar;

public class MiscUtils {
		
	private Context cCont;
	private DecimalFormat ffrmdec,ffrmint,ffrmint2,ffrmdec2,ffrmgps;
	public String curr;

	private PBase pB;
	
	public MiscUtils(Context context,String currsymb) {

		cCont=context; 
		curr=currsymb;
		
		ffrmdec = new DecimalFormat("#,##0.00"); 
		ffrmint = new DecimalFormat("#,##0");
        ffrmint2 = new DecimalFormat("###0");
        ffrmdec2 = new DecimalFormat("#,##0.##");
		ffrmgps = new DecimalFormat("##0.0000000");

	}
	
	public MiscUtils(Context context) {

		cCont=context; 
		curr="";
		
		ffrmdec = new DecimalFormat("#,##0.00"); 
		ffrmint = new DecimalFormat("#,##0");
        ffrmint2 = new DecimalFormat("###0");
		ffrmdec2 = new DecimalFormat("#,##0.##"); 
		ffrmgps = new DecimalFormat("##0.0000000");
	}

    public void currsymb(String currsym) {
        curr=currsym;
    }
	
	public int CInt(String s) {
		return Integer.parseInt(s);
	}
	
	public String CStr(int v){
		return String.valueOf(v);
	}
	
	public String frmcur(double val) {
		return curr+ffrmdec.format(val);
	}

	//#CKFK 20190226 Agregué esta función porque necesito el formato sin moneda
	public String frmcur_sm(double val) {
		return ffrmdec.format(val);
	}
	
	public String frmval(double val) {
		return ffrmdec.format(val);
	}
	
	public String frmdec(double val) {
		return ffrmdec.format(val);
	}
	
	public String frmdec(int val) {
		return ffrmdec.format(val);
	}
	
	public String frmdecno(double val) {
		return ffrmdec2.format(val);
	}
	
	public String frmint(int val) {
		return ffrmint.format(val);
	}
	
	public String frmint(double val) {
		return ffrmint.format(val);
	}

    public String frmint2(int val) {
        return ffrmint2.format(val);
    }

    public String frmgps(double val) {
		return ffrmgps.format(val);
	}
	
	public String frmdecimal(double val,int ndec) {

		String ss="",ff="#,##0.";
		
		if (ndec<=0) {		
			ss=frmint((int) val);return ss;
		}
		
		for (int i = 1; i <ndec+1; i++) {
			ff=ff+"0";
		}
		
		DecimalFormat decim = new DecimalFormat(ff);
		ss=decim.format(val);
		
		return ss;
	}
	
	public double round2(double val){

	int ival;
		
		val=(double) (100*val);
		double rslt=Math.round(val);
		rslt=Math.floor(rslt);
		
		ival=(int) rslt;
		rslt=(double) ival;
		
		return (double) (rslt/100);
	}
	
	public boolean emptystr(String s){
		if (s==null || s.isEmpty()) {
			return true;
		} else{
			return false;
		}
	}

	public int bool(Boolean value) {
	    if (value) return 1;else return 0;
    }

	public int dayofweek() {

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		if (day==1) {
			day=7;			
		} else {	
			day=day-1;
		}
		
		return day;
	}
	
	public String getCorelBase(){

		int cyear,cmonth,cday,ch,cm,cs,vd,vh;
		String s;
		
		final Calendar c = Calendar.getInstance();
		
		cyear = c.get(Calendar.YEAR);cyear=cyear % 100;s=frm2num(cyear);
		cmonth = c.get(Calendar.MONTH)+1;s+=frm2num(cmonth);
		cday = c.get(Calendar.DAY_OF_MONTH);s+=frm2num(cday);
		ch=c.get(Calendar.HOUR_OF_DAY);s+=frm2num(ch);
		cm=c.get(Calendar.MINUTE);s+=frm2num(cm);
		cs=c.get(Calendar.SECOND);s+=frm2num(cs);
				
		return s;
	}
	
	public String frm2num(int n) {
		if (n>9) { return String.valueOf(n);} else {return "0"+String.valueOf(n);}	
	}
	
	@SuppressLint("SuspiciousIndentation")
	public void msgbox(String msg) {

	    if (msg.isEmpty()) return;
		msgbox2(msg);

	}

    public void msgbox2(String msg) {

        if (msg.isEmpty()) return;

        try {

            AlertDialog.Builder dialog = new AlertDialog.Builder(cCont);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setCancelable(false);
            dialog.setNeutralButton("OK", (dialog1, which) -> {});
            dialog.show();

        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    public void msgbox(int v) {
	    msgbox(String.valueOf(v));
	}   
	
	public void msgbox(double v) {
        msgbox(String.valueOf(v));
	}   

	public void toast(String msg) {
		Toast.makeText(cCont,msg, Toast.LENGTH_SHORT).show();
	}

	public int roundtoint(double val) {
		val=val*100;
		int ival=(int) Math.floor(val);
		int tval=ival % 100;
		int rval=ival-tval;
		if (tval>=50) rval=rval+100;
		rval=(int) rval/100;
		return rval;
	}

	public double round(double val,int ndec) {

		double v,pw;
		
		if (ndec>10)return val;
		
		if (ndec<0) ndec=0;
		v=val;
		pw=Math.pow(10,ndec);
		v=v*pw;
		v=Math.floor(v);
		v=v/pw;
	
		return v;
	}

	public double roundr(double val,int ndec) {

		double v,pw;

		if (ndec>10)return val;

		if (ndec<0) ndec=0;
		v=val;
		pw=Math.pow(10,ndec);
		v=v*pw;
		v=Math.round(v);
		v=v/pw;

		return v;
	}

	public double trunc(double val) {

		double v,pw;

		v=val;
		v=Math.floor(v);

		return v;
	}

	public Bitmap scaleBitmap(Bitmap bm, int size1, int size2) {
		Bitmap bms;
		Matrix matrix;
		int imw,imh;
		double bmw,bmh,z1,z2,z3,z4,zm1,zm2,zm;

		try {
			bmw=bm.getWidth();
			bmh=bm.getHeight();

			z1=bmw/size1;
			z2=bmh/size2;
			zm1=Math.max(z1,z2);
			z3=bmw/size2;
			z4=bmh/size1;
			zm2=Math.max(z3,z4);
			zm=Math.min(zm1,zm2);

			imw=(int) (bmw/zm);
			imh=(int) (bmh/zm);

			Bitmap scaledBitmap = Bitmap.createBitmap(imw,imh,Bitmap.Config.RGB_565);

			float scaleX = imw / (float) bm.getWidth();
			float scaleY = imh / (float) bm.getHeight();
			float pivotX = 0;
			float pivotY = 0;

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bm, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

			return scaledBitmap;
		} catch (Exception e) {
			String ee=e.getMessage();
			return null;
		}

	}

	public String Capitalize(String words) {
		String str="";
		boolean isCap=false;

		for (int i=0; i<words.length(); i++) {
			if (isCap){
				str+=words.toUpperCase().charAt(i);
			} else {
				if(i==0){
					str+=words.toUpperCase().charAt(i);
				} else {
					str+=words.toLowerCase().charAt(i);
				}
			}

			if (words.charAt(i)==' ') isCap=true;else isCap=false;
		}

		return str;
	}


}

