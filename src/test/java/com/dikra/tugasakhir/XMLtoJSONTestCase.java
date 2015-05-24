package com.dikra.tugasakhir;

import com.dikra.tugasakhir.music.model.*;
import com.dikra.tugasakhir.parser.json.MusicJSON;
import com.dikra.tugasakhir.parser.xml.MusicXML;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class XMLtoJSONTestCase extends TestCase {
    @Test
    public void testXMLtoJSON() {
        MusicXML musicXML = new MusicXML("res/twinkle.xml");
        Music music = new Music();
        music.setParts(musicXML.getParts());

        try {
            PrintWriter writer = new PrintWriter("temp_twinkle.json", "UTF-8");
            writer.println(music.getMusicJSON());
            writer.close();
        } catch (Exception e){
            assert false;
        }

        MusicJSON musicJSON = new MusicJSON("temp_twinkle.json");

        assertEquals(music.getParts().length, musicJSON.getParts().length);

        for (int i = 0; i < music.getParts().length; i++){
            Part pa = music.getParts()[i];
            Part pb = musicJSON.getParts()[i];

            assertEquals(pa.getMeasuresLength(), pb.getMeasuresLength());

            for (int j = 0; j < pa.getMeasuresLength(); j++){
                MeasureAttributes ta = pa.getMeasureAttAt(j);
                MeasureAttributes tb = pb.getMeasureAttAt(j);

                assertEquals(ta.getClefLine(), tb.getClefLine());
                assertEquals(ta.getClefSign(), tb.getClefSign());
                assertEquals(ta.getDivisions(), tb.getDivisions());
                assertEquals(ta.getKeyFifths(), tb.getKeyFifths());
                assertEquals(ta.getKeyMode(), tb.getKeyMode());
                assertEquals(ta.getTimeBeats(), tb.getTimeBeats());
                assertEquals(ta.getTimeBeatType(), tb.getTimeBeatType());

                Measure ma = pa.getMeasureAtNumber(j+1);
                Measure mb = pb.getMeasureAtNumber(j+1);

                assertEquals(ma.getNotesLength(), mb.getNotesLength());
                //assertEquals(ma.getChordsLength(), mb.getChordsLength());

                for (int k = 0; k < ma.getNotesLength(); k++){
                    Note na = ma.getNotes()[k];
                    Note nb = mb.getNotes()[k];

                    assertEquals(na.getDuration(), nb.getDuration());
                    assertEquals(na.getPitchAlter(), nb.getPitchAlter());
                    assertEquals(na.getPitchOctave(), nb.getPitchOctave());
                    assertEquals(na.getPitchStep(), nb.getPitchStep());
                }

                /*
                for (int k = 0; k < ma.getChordsLength(); k++){
                    Chord ca = ma.getChordAt(k);
                    Chord cb = mb.getChordAt(k);

                    assertEquals(ca.getDuration(), cb.getDuration());
                    assertEquals(ca.getPitchProgression(), cb.getPitchProgression());
                }*/
            }
        }

        try{

            File file = new File("temp_twinkle.json");

            file.delete();

        }catch(Exception e){

            e.printStackTrace();

        }
    }
}
