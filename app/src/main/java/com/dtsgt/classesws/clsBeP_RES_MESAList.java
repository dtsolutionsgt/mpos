package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_RES_MESAList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_RES_MESA> items;
}
