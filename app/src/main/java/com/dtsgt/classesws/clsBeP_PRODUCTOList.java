package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_PRODUCTOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_PRODUCTO> items;
}