package com.dikra.tugasakhir.music.model;

import org.dom4j.Node;

/**
 * Created by DIKRA on 4/9/2015.
 */
public class MeasureAttributes {
    private int divisions;
    private int key_fifths;
    private int key_mode;
    private int time_beats;
    private int time_beat_type;
    private char clef_sign;
    private int clef_line;

    public MeasureAttributes(){
    }

    public void initFromXML(Node att_node){
        Node key_node = att_node.selectSingleNode("key");
        Node time_node = att_node.selectSingleNode("time");
        Node clef_node = att_node.selectSingleNode("clef");

        divisions = Integer.parseInt(att_node.selectSingleNode("divisions").getText());
        key_fifths = Integer.parseInt(key_node.selectSingleNode("fifths").getText());
        key_mode = (key_node.selectSingleNode("fifths").getText() == "major")? 1 : 0;
        time_beats = Integer.parseInt(time_node.selectSingleNode("beats").getText());
        time_beat_type = Integer.parseInt(time_node.selectSingleNode("beat-type").getText());
        clef_sign = clef_node.selectSingleNode("sign").getText().charAt(0);
        clef_line = Integer.parseInt(clef_node.selectSingleNode("line").getText());
    }

    public int getDivisions(){
        return divisions;
    }

    public int getKeyFifths(){
        return key_fifths;
    }

    public int getKeyMode(){
        return key_mode;
    }

    public int getTimeBeats() {
        return time_beats;
    }

    public int getTimeBeatType() {
        return time_beat_type;
    }

    public char getClefSign() {
        return clef_sign;
    }

    public int getClefLine() {
        return clef_line;
    }


}
