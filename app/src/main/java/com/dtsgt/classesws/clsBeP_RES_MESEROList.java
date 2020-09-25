package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_RES_MESEROList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_RES_MESERO> items;
}