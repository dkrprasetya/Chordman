package com.dikra.tugasakhir.music.model;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class Chord {
    private String pitch_step;
    private int pitch_alter;
    private int duration;

    public Chord(){
        pitch_alter = 0;
        duration = 0;
    }

    public static Chord getChordFromId(int id){
        Chord chord = new Chord();
        switch (id){
            case 0:
                chord.setPitchStep("C");
                break;
            case 1:
                chord.setPitchStep("C");
                chord.setPitchAlter(1);
                break;
            case 2:
                chord.setPitchStep("D");
                break;
            case 3:
                chord.setPitchStep("D");
                chord.setPitchAlter(1);
                break;
            case 4:
                chord.setPitchStep("E");
                break;
            case 5:
                chord.setPitchStep("F");
                break;
            case 6:
                chord.setPitchStep("F");
                chord.setPitchAlter(1);
                break;
            case 7:
                chord.setPitchStep("G");
                break;
            case 8:
                chord.setPitchStep("G");
                chord.setPitchAlter(1);
                break;
            case 9:
                chord.setPitchStep("A");
                break;
            case 10:
                chord.setPitchStep("A");
                chord.setPitchAlter(1);
                break;
            case 11:
                chord.setPitchStep("B");
                break;
            default:
                System.out.println("Error converting id to chord...");
                assert (false);
                break;
        }
        return chord;
    }

    public String getPitchStep(){ return pitch_step; }
    public int getPitchAlter(){ return pitch_alter; }
    public int getDuration(){
        return duration;
    }

    public void setPitchStep(String _pitch_step){
        pitch_step = _pitch_step;
    }

    public void setPitchAlter(int _pitch_alter){
        pitch_alter = _pitch_alter;
    }

    public void setDuration(int _duration){
        duration = _duration;
    }

}
