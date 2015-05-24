package com.dikra.tugasakhir.music.model;

import org.dom4j.Node;

/**
 * Created by DIKRA on 4/8/2015.
 */
public class Note {
    private String pitch_step;
    private int pitch_octave;
    private int pitch_alter;
    private int duration;

    public Note(){
    }

    public void initFromXML(Node node){
        Node pitch_node = node.selectSingleNode("pitch");

        pitch_step = pitch_node.selectSingleNode("step").getText();
        pitch_octave = Integer.parseInt(pitch_node.selectSingleNode("octave").getText());

        if (pitch_node.selectSingleNode("alter") != null){
            pitch_alter = Integer.parseInt(pitch_node.selectSingleNode("alter").getText());
        } else {
            pitch_alter = 0;
        }

        duration = Integer.parseInt(node.selectSingleNode("duration").getText());
    }

    public String getPitchStep() {
        return pitch_step;
    }

    public int getPitchOctave() {
        return pitch_octave;
    }

    public int getPitchAlter(){
        return pitch_alter;
    }

    public int getDuration() {
        return duration;
    }
}
