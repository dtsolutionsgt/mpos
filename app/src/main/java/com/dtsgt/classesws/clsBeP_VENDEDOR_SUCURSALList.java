package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_VENDEDOR_SUCURSALList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_VENDEDOR_SUCURSAL> items;
}