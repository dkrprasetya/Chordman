package com.dikra.tugasakhir.musicxml;

import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIKRA on 4/8/2015.
 */
public class Measure {

    private int number;
    private Note[] notes;

    public Measure(Node node){
        number = Integer.parseInt(node.valueOf("@number"));
        List<Node> note_nodes = node.selectNodes("note");

        notes = new Note[note_nodes.size()];

        for (int i = 0; i < notes.length; i++){
            notes[i] = new Note(note_nodes.get(i));
        }

        System.out.println("Measure " + number + ", consisting " + getNotesLength() + " note(s), has been constructed.");
    }

    public int getNumber(){
        return number;
    }

    public Note[] getNotes(){
        return notes;
    }

    public int getNotesLength(){
        return notes.length;
    }
    public Note getNoteAt(int id){
        return notes[id];
    }
}