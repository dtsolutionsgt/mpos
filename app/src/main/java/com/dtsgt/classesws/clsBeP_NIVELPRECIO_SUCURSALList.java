package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_NIVELPRECIO_SUCURSALList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_NIVELPRECIO_SUCURSAL> items;
}
