package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_CAJA_IMPRESORAList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_CAJA_IMPRESORA> items;
}
