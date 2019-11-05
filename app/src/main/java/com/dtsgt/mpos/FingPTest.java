package com.dtsgt.mpos;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dtsgt.classes.Globals;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;

public class FingPTest extends PBase {

    private ImageView img1,img2;

   /* private Reader m_reader = null;
    private Engine m_engine = null;
    private Fmd m_fmd = null;
    private Reader.CaptureResult cap_result = null;*/
    private int m_DPI = 0;
    private String m_deviceName = "$01$/dev/bus/usb/001/002";

    private Bitmap bmImg;

    private String imgfold;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fing_ptest);

        super.InitBase();

        img1 = (ImageView) findViewById(R.id.imageView40);
        img2 = (ImageView) findViewById(R.id.imageView41);

        //db.execSQL("create table FPrint ( ID TEXT, image BLOB)");
        imgfold= Environment.getExternalStorageDirectory()+ "/RoadFotos/";

        //initializeReader();

        try {
            String prodimg = imgfold+"fprint.png";
            File file = new File(prodimg);
            if (file.exists()) {
                bmImg = BitmapFactory.decodeFile(prodimg);
                img1.setImageBitmap(bmImg);
            }
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        String qu = "SELECT * FROM FPrint ";
        Cursor cur = db.rawQuery(qu, new String[]{});
        id=cur.getCount();
        toast("regfs : "+id);
    }

    //region Events

    public void doSave(View view) {
        try {
            if (!save()) return;
            Bitmap bm=load();
            img2.setImageBitmap(bm);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public void doEmpty(View view) {
        try {
            db.execSQL("DELETE FROM FPrint ");
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Image database Save/Load

    private boolean save() {
        try {
            id++;
            insertImage(""+id,bmImg);
            return true;
        } catch (Exception e) {
            msgbox(e.getMessage());return false;
        }
    }

    private Bitmap load() {
        Bitmap bm=null;

        bm=getImage();
        return bm;
    }

    public Bitmap getImage() {

        String qu = "SELECT * FROM FPrint WHERE ID="+id;
        Cursor cur = null;

        cur = db.rawQuery(qu, new String[]{});

        if (cur != null) {

            if (cur.moveToFirst()) {

                byte[] imgByte = cur.getBlob(1);
                cur.close();
                msgbox("Size 2 : "+imgByte.length);

                Bitmap bmp=BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

                return bmp;
            }

            try {
                if (cur != null && !cur.isClosed()) cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public void insertImage(String name, Bitmap img) {
        Bitmap storedBitmap = null;
        String sql = "INSERT INTO FPrint (ID,image) VALUES(?,?)";

        SQLiteStatement insertStmt = db.compileStatement(sql);

        byte[] imgByte = getBitmapAsByteArray(img);
        msgbox("Size 1 : "+imgByte.length);
        //storedBitmap = getImage(name);

        //if (storedBitmap == null) {
            insertStmt.bindString(1, name);
            insertStmt.bindBlob(2, imgByte);

        try {
            insertStmt.executeInsert();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        //}
     }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    //endregion

    //region DPUaU

    private boolean initializeReader() {
        try {
            /*Globals.DefaultImageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT;

            //Context applContext = getApplicationContext();
            //m_reader = Globals.getInstance().getReader(m_deviceName, applContext);

            m_reader = Globals.getInstance().getReader(m_deviceName, this);
            m_reader.Open(Reader.Priority.COOPERATIVE);
            m_DPI = Globals.GetFirstDPI(m_reader);
            m_engine = UareUGlobal.GetEngine();*/
            return true;
        } catch (Exception e) {
            m_deviceName = "";
            String ss=e.getMessage();
            toastlong("No se logro conectar con lector : "+e.getMessage());
            return false;
        }
    }

    //endregion

    //region DPUaU Globals

    //endregion

}
