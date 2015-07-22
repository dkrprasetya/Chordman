package com.dikra.tugasakhir;

import com.dikra.tugasakhir.music.model.Measure;
import com.dikra.tugasakhir.music.model.Part;
import com.dikra.tugasakhir.parser.xml.MusicXML;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class MusicXMLTestCase extends TestCase {
    @Test
    public void testMusicXML() {
        MusicXML musicXML = new MusicXML("res/twinkle.xml");

        for (Part part : musicXML.getParts()){
            assert (part.getMeasuresLength() > 0);
            for (Measure measure : part.getMeasures()){
                assert (measure.getNotesLength() > 0);
            }
        }
    }
}
