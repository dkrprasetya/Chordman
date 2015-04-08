package com.dikra.tugasakhir.musicxml;

import org.dom4j.Node;

/**
 * Created by DIKRA on 4/8/2015.
 */
public class Note {
    private String pitch_step;
    private int pitch_octave;
    private int duration;

    public Note(Node node){
        Node pitch_node = node.selectSingleNode("pitch");

        pitch_step = pitch_node.selectSingleNode("step").getText();
        pitch_octave = Integer.parseInt(pitch_node.selectSingleNode("octave").getText());
        duration = Integer.parseInt(node.selectSingleNode("duration").getText());
    }

    public String getPitchStep() {
        return pitch_step;
    }

    public int getPitchOctave() {
        return pitch_octave;
    }

    public int getDuration() {
        return duration;
    }
}
