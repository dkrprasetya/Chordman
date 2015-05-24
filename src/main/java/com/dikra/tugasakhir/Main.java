package com.dikra.tugasakhir;

import com.dikra.tugasakhir.music.MusicProcessor;
import com.dikra.tugasakhir.music.model.Music;
import com.dikra.tugasakhir.parser.xml.MusicXML;

/**
 * Created by DIKRA on 4/5/2015.
 */
public class Main {

    public static void main(String[] args){
        System.out.println("Go lulus Oktober!");

        MusicProcessor musicProcessor = new MusicProcessor("res/twinkle.json");
        musicProcessor.generateInputOutput();
    }
}
