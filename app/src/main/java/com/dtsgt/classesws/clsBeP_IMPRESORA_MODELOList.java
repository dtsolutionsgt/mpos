package com.dtsgt.classesws;


import org.simpleframework.xml.ElementList;

import java.util.List;

public class clsBeP_IMPRESORA_MODELOList {
    @ElementList(inline=true,required=false)
    public List<clsBeP_IMPRESORA_MODELO> items;
}