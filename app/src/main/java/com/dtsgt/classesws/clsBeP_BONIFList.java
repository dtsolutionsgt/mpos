package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_BONIFList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_BONIF> items;
}