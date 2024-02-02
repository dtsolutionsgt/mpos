package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_GIRO_NEGOCIOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_GIRO_NEGOCIO> items;
}
