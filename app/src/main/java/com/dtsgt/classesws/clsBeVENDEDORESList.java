package com.dtsgt.classesws;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeVENDEDORESList {
    @ElementList(inline=true,required=false)
    public List<clsBeVENDEDORES> items;
}
