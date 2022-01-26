package com.dtsgt.classesws;


import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_REGLA_COSTOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_REGLA_COSTO> items;
}

