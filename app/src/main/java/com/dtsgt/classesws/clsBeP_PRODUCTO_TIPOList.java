package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_PRODUCTO_TIPOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_PRODUCTO_TIPO> items;
}