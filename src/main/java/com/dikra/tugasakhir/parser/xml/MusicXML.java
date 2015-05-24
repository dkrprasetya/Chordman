package com.dikra.tugasakhir.parser.xml;

import com.dikra.tugasakhir.music.model.Music;
import com.dikra.tugasakhir.music.model.Part;
import org.dom4j.Document;
import org.dom4j.Node;

import java.util.List;

/**
 * Created by DIKRA on 4/6/2015.
 */
public class MusicXML extends Music {
    private Document doc;

    public MusicXML(String path) {
        boolean success = false;
        try {
            init(path);
            success = true;
        } catch (Exception e){
            e.printStackTrace();

            // reset attributes to null
            doc = null;
            parts = null;
        } finally {
            //System.out.println("Parsing MusicXML success? " + success);
        }

    }

    /*** Initialize MusicXML class with .xml file ***/
    public void init(String path) throws Exception {
        doc = XMLHelper.load(path);

        List<Node> part_nodes = doc.selectNodes("/score-partwise/part");

        parts = new Part[part_nodes.size()];

        for (int i = 0; i < parts.length; i++){
            parts[i] = new Part();
            parts[i].initFromXML(part_nodes.get(i));
        }
    }

    public Document getXMLDoc(){
        return doc;
    }
}
