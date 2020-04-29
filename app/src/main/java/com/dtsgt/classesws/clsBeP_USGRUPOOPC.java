package com.dtsgt.classesws;


import org.simpleframework.xml.Element;

public class clsBeP_USGRUPOOPC {

    @Element(required=false) public int GRUPO;
    @Element(required=false) public int OPCION;


    public clsBeP_USGRUPOOPC() {
    }

    public clsBeP_USGRUPOOPC(int GRUPO,int OPCION) {

        this.GRUPO=GRUPO;
        this.OPCION=OPCION;

    }


    public int getGRUPO() {
        return GRUPO;
    }
    public void setGRUPO(int value) {
        GRUPO=value;
    }
    public int getOPCION() {
        return OPCION;
    }
    public void setOPCION(int value) {
        OPCION=value;
    }

}

