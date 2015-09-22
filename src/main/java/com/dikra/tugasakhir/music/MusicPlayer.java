package com.dikra.tugasakhir.music;

import com.dikra.tugasakhir.music.model.*;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.jfugue.player.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by DIKRA on 7/25/2015.
 */
public class MusicPlayer{
    protected final MusicProcessor mp;
    protected final boolean onlyMelody;

    protected static Player player;
    protected static Pattern pattern;
    protected static Thread playerThread;
    protected static Thread saveMidiThread;

    public MusicPlayer(MusicProcessor _mp, boolean om){
        mp = _mp;
        player = new Player();
        onlyMelody = om;
        init();
    }

    public MusicPlayer(MusicProcessor _mp){
        mp = _mp;
        player = new Player();
        onlyMelody = false;
        init();
    }

    public void init(){
        Measure[] measures = mp.getActivePart().getMeasures();
        MeasureAttributes[] att = mp.getActivePart().getMeasureAttributes();

        String melodyStr = "V0 I[Piano]";
        String chordStr = "V1 I[Guitar]";

        for (int i = 0; i < measures.length; ++i) {
            Note[] notes = measures[i].getNotes();
            Chord[] chords = measures[i].getChords();
            int mdivision = att[i].getDivisions();

            for (int j = 0; j < notes.length; ++j){
                melodyStr += " " + noteToString(notes[j], mdivision);
            }

            for (int j = 0; j < chords.length; ++j){

                chordStr += " " + chordToString(chords[j], mdivision, 2);
            }

            if (i < measures.length-1){
                melodyStr += " |";
                chordStr += " |";
            }
        }

        pattern = new Pattern("T250");
        pattern.add(melodyStr);
        if (!onlyMelody) pattern.add(chordStr);
    }

    private String noteToString(Note note, int mdivision){
        String ret = "";

        if (note.isRest()){
            ret += "R";
        } else {
            ret += note.getPitchStep();

            if (note.getPitchAlter() == 1) {
                ret += "#";
            } else if (note.getPitchAlter() == -1) {
                ret += "b";
            }

            ret += (note.getPitchOctave() + 1);
        }

        float notedur = (float)note.getDuration() / (float)mdivision;
        ret += "/" + notedur;

        return ret;
    }

    private String chordToString(Chord chord, int mdivision, int nplay){
        String ret = "";

        ret += chord.getPitchStep();

        if (chord.getPitchAlter() == 1){
            ret += "#";
        } else
        if (chord.getPitchAlter() == -1){
            ret += "b";
        }

        if (chord.getPitchStep().equals("C") || chord.getPitchStep().equals("F") || chord.getPitchStep().equals("G")){
            ret += "maj";
        } else
        if (chord.getPitchStep().equals("D") || chord.getPitchStep().equals("E") || chord.getPitchStep().equals("A")){
            ret += "min";
        } else
        if (chord.getPitchStep().equals("B")){
            ret += "dim";
        } else {
            ret += "maj";
        }


        float notedur = (float)chord.getDuration() / (float)mdivision;

        notedur /= (float)nplay;

        ret += "/" + notedur;

        String rettt = "";

        for (int i = 0; i < nplay; ++i){
            if (i > 0) rettt += " ";
            rettt += ret;
        }

        return rettt;
    }

    public void play(){
        if (isPlaying()){
            player.getManagedPlayer().finish();
            playerThread.interrupt();
            playerThread = null;
        }

        playerThread = new Thread(new Runnable() {
            public void run() {
                player.play(pattern);
            }
        });
        playerThread.start();
    }

    public boolean isPlaying(){
        if (playerThread == null)
            return false;
        return playerThread.isAlive();
    }

    public void stop(){
        if (isPlaying()){
            player.getManagedPlayer().finish();
            playerThread.interrupt();
        }
    }

    public void run() {
        player.play(pattern);
    }

    public void saveMidi(final String fileName){
        saveMidiThread = new Thread(new Runnable() {
            public void run() {
                File midi = new File(fileName);
                try {
                    MidiFileManager manager = new MidiFileManager();
                    manager.savePatternToMidi(pattern, midi);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        saveMidiThread.start();
    }
}
