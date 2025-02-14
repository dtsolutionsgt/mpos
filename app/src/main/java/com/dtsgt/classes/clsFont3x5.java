package com.dtsgt.classes;

public class clsFont3x5 {

    public String L1,L2,L3,L4,L5;

    private String s;
    private int pw;

    public clsFont3x5(int pwidth) {
        pw=pwidth;
    }

    public  void get(int nnum) throws Exception {
        try {
            nnum=nnum % 100000;

            init();

            String ss=""+nnum;

            for (int i = 0; i <ss.length(); i++) {
                s=ss.substring(i,i+1);
                processNum(s);
            }

            center();

            L1+="";
            L2+="";
            L3+="";
            L4+="";
            L5+="";

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //region Private

    private void init() {
        L1="";L2="";L3="";L4="";L5="";

        for (int i = 0; i <5; i++) {
            L1+=nn[0][i];
            L2+=nn[1][i];
            L3+=nn[2][i];
            L4+=nn[3][i];
            L5+=nn[4][i];
        }

        L1+="    ";L2+="    ";L3+="    ";L4+="    ";L5+="    ";
    }

    private void processNum(String sn) {
        if (sn.equalsIgnoreCase("0")) addNum(n0);
        if (sn.equalsIgnoreCase("1")) addNum(n1);
        if (sn.equalsIgnoreCase("2")) addNum(n2);
        if (sn.equalsIgnoreCase("3")) addNum(n3);
        if (sn.equalsIgnoreCase("4")) addNum(n4);
        if (sn.equalsIgnoreCase("5")) addNum(n5);
        if (sn.equalsIgnoreCase("6")) addNum(n6);
        if (sn.equalsIgnoreCase("7")) addNum(n7);
        if (sn.equalsIgnoreCase("8")) addNum(n8);
        if (sn.equalsIgnoreCase("9")) addNum(n9);
    }

    private void center() {
        int ls=pw-L1.length();
        double lsh=ls;
        lsh=lsh*0.5;
        ls=(int) lsh;

        s="";
        for (int i = 0; i <ls; i++) {
            s+=" ";
        }

        L1=s+L1;
        L2=s+L2;
        L3=s+L3;
        L4=s+L4;
        L5=s+L5;
    }

    private void addNum(String[][] ni) {
        L1+="  ";L2+="  ";L3+="  ";L4+="  ";L5+="  ";

        for (int i = 0; i <4; i++) {
            L1+=ni[0][i];
            L2+=ni[1][i];
            L3+=ni[2][i];
            L4+=ni[3][i];
            L5+=ni[4][i];
        }
    }


    //endregion

    //region Chars

    private String[][] n1 = {
            {" "," ","#","#"},
            {" ","#"," ","#"},
            {"#"," "," ","#"},
            {" "," "," ","#"},
            {" "," "," ","#"}
    };

    private String[][] n2 = {
            {"#","#","#","#"},
            {" "," "," ","#"},
            {" ","#","#"," "},
            {"#"," "," "," "},
            {"#","#","#","#"}
    };

    private String[][] n3 = {
            {"#","#","#","#"},
            {" "," "," ","#"},
            {" ","#","#"," "},
            {" "," "," ","#"},
            {"#","#","#","#"}
    };

    private String[][] n4 = {
            {"#"," "," ","#"},
            {"#"," "," ","#"},
            {" ","#","#","#"},
            {" "," "," ","#"},
            {" "," "," ","#"}
    };

    private String[][] n5 = {
            {"#","#","#","#"},
            {"#"," "," "," "},
            {"#","#","#","#"},
            {" "," "," ","#"},
            {"#","#","#","#"}
    };

    private String[][] n6 = {
            {" ","#","#","#"},
            {"#"," "," "," "},
            {"#","#","#","#"},
            {"#"," "," ","#"},
            {" ","#","#"," "}
    };

    private String[][] n7 = {
            {"#","#","#","#"},
            {"#"," "," ","#"},
            {" "," "," ","#"},
            {" "," ","#"," "},
            {" ","#"," "," "}
    };

    private String[][] n8 = {
            {" ","#","#"," "},
            {"#"," "," ","#"},
            {" ","#","#"," "},
            {"#"," "," ","#"},
            {" ","#","#"," "}
    };

    private String[][] n9 = {
            {" ","#","#"," "},
            {"#"," "," ","#"},
            {" ","#","#","#"},
            {" "," "," ","#"},
            {" "," ","#"," "}
    };

    private String[][] n0 = {
            {" ","#","#"," "},
            {"#"," "," ","#"},
            {"#"," "," ","#"},
            {"#"," "," ","#"},
            {" ","#","#"," "}
    };

    private String[][] nn = {
            {" "," "," "," "," "},
            {" "," "," "," "," "},
            {" ","#"," ","#"," "},
            {"#","#","#","#","#"},
            {" ","#"," ","#"," "}
    };


    //endregion

}
