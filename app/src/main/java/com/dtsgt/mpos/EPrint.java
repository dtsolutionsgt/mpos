package com.dtsgt.mpos;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

public class EPrint extends PBase implements  ReceiveListener {

    //"BT:00:01:90:85:0D:8C"

    private String BT;

    // Epson

    private static final int REQUEST_PERMISSION = 100;
    public static final int Printer_TM_M30=1;
    public static final int Printer_MODEL_ANK = 0;

    private Context mContext = null;
    private Printer mPrinter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eprint);

        super.InitBase();

        mContext = this;

        BT="BT:00:01:90:85:0D:8C";

    }

    //region Events

    public void doPrint(View view) {
        try {
            runPrintReceiptSequence();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {

        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                msgbox(makeErrorMessage(status));
                dispPrinterWarnings(status);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });

    }

    //endregion

    //region Main

    private boolean runPrintReceiptSequence() {
        if (!initializeObject()) return false;

        if (!createReceiptData()) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) return false;

        if (!connectPrinter()) return false;

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            msgbox(makeErrorMessage(status));
            try {
                mPrinter.disconnect();
            }  catch (Exception ex) {}
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            msgbox(e.getMessage()+" sendData");
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }


    private boolean createReceiptData() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("THE STORE 123 (555) 555 – 5555\n");
            textData.append("STORE DIRECTOR – John Smith\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
            textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
            textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
            textData.append("------------------------------\n");
            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOTAL    99.81\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addFeedLine";
            mPrinter.addFeedLine(2);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            msgbox(method+" : "+e.getMessage());return false;
        }

        textData = null;

        return true;
    }

    //endregion

    //region Aux

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(0,0,this);
        }  catch (Exception e) {
            msgbox(e.getMessage());
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) return;

        mPrinter.clearCommandBuffer();
        mPrinter.setReceiveEventListener(null);
        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) return false;

        try {
            mPrinter.connect(BT, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            msgbox(e.getMessage()+" connect");return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            msgbox(e.getMessage()+ " beginTransaction");
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }  catch (Epos2Exception e) {
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) return;

        try {
            mPrinter.endTransaction();
        }   catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    msgbox(e.getMessage()+" end transaction");
                }
            });
        }

        try {
            mPrinter.disconnect();
        }   catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    msgbox(e.getMessage()+"disconnect");
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) return false;

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }   else if (status.getOnline() == Printer.FALSE) {
            return false;
        }  else {
            ;//print available
        }

        return true;
    }

    //endregion

    //region Messages

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) msg += "Printer is offline.\n";
        if (status.getConnection() == Printer.FALSE) msg +="Please check the connection of the printer and the mobile terminal.\n";
        if (status.getCoverOpen() == Printer.TRUE)  msg += "Please close roll paper cover.\n";
        if (status.getPaper() == Printer.PAPER_EMPTY) msg += "Please check roll paper.\n";
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON)
            msg +="Please release a paper feed switch.\n";
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += "Please remove jammed paper and close roll paper cover.\nRemove any jammed paper or foreign substances in the printer, and then turn the printer off and turn the printer on again.\n";
            msg += "Then, If the printer doesn't recover from error, please cycle the power switch.\n";
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += "Please cycle the power switch of the printer.\nIf same errors occurred even power cycled, the printer may out of order.\n";
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += "Please wait until error LED of the printer turns off.\n";
                msg += "Print head of printer is hot.\n";
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += "Please wait until error LED of the printer turns off.\n";
                msg += "Motor Driver IC of printer is hot.\n";
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += "Please wait until error LED of the printer turns off.\n";
                msg += "Battery of printer is hot.\n";
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += "Please set correct roll paper.\n";
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += "Please connect AC adapter or change the battery.\nBattery of printer is almost empty.\n";
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) return;

        if (status.getPaper() == Printer.PAPER_NEAR_END) warningsMsg+="Roll paper is nearly end.\n";
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) warningsMsg+="Battery level of printer is low.\n";

        msgbox(warningsMsg);
    }

    //endregion

    //region Activity Events


    //endregion




}
