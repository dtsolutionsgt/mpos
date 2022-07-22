package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_MODIFICADORList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_MODIFICADOR> items;
}
