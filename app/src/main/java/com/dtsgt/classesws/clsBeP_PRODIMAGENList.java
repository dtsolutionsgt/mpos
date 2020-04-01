package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_PRODIMAGENList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_PRODIMAGEN> items;
}
