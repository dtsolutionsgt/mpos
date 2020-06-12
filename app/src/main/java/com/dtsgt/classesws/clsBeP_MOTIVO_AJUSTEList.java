package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_MOTIVO_AJUSTEList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_MOTIVO_AJUSTE> items;
}

