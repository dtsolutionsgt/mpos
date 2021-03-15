package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_UNIDAD_CONVList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_UNIDAD_CONV> items;
}
