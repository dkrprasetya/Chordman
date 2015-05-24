package com.dikra.tugasakhir.parser.xml;


import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * Created by DIKRA on 4/6/2015.
 */
public class XMLHelper {

    /*** Returns loaded Document, null if there's exception ***/
    public static Document load(String path) throws Exception {
        SAXReader saxReader = new SAXReader();

        return saxReader.read(new File(path));
    }
}
