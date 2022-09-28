package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_BARRIL_BARRAList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_BARRIL_BARRA> items;
}
