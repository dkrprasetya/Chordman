package com.dikra.tugasakhir.music.model;

import org.dom4j.Node;

import java.util.List;

/**
 * Created by DIKRA on 4/8/2015.
 */
public class Measure {

    private int number;
    private Note[] notes;
    private Chord[] chords;

    public Measure(){
    }

    public void initFromXML(Node node){
        number = Integer.parseInt(node.valueOf("@number"));
        List<Node> note_nodes = node.selectNodes("note");

        notes = new Note[note_nodes.size()];

        for (int i = 0; i < notes.length; i++){
            notes[i] = new Note();
            notes[i].initFromXML(note_nodes.get(i));
        }

        System.out.println("Measure " + number + ", consisting " + getNotesLength() + " note(s), has been constructed.");
    }

    public int getNumber(){
        return number;
    }

    public Note[] getNotes(){
        return notes;
    }

    public Chord[] getChords(){
        return chords;
    }

    public int getNotesLength(){
        return notes.length;
    }

    public int getChordsLength(){
        return chords.length;
    }

    public Note getNoteAt(int id){
        return notes[id];
    }

    public Chord getChordAt(int id){ return chords[id]; }

    public void setChords(Chord[] _chords){
        chords = _chords;
    }
}