package com.dtsgt.classesws;


import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_RUTAList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_RUTA> items;
}