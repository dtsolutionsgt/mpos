package com.dtsgt.classesws;


import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_LINEA_ESTACIONList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_LINEA_ESTACION> items;
}