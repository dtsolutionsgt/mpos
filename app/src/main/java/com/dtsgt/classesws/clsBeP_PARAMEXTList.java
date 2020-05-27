package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_PARAMEXTList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_PARAMEXT> items;
}

