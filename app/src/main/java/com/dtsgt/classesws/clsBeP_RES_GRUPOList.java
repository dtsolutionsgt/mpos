package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_RES_GRUPOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_RES_GRUPO> items;
}
