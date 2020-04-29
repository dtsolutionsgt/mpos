package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_USOPCIONList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_USOPCION> items;
}