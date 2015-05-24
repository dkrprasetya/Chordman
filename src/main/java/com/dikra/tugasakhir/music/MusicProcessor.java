package com.dikra.tugasakhir.music;

import com.dikra.tugasakhir.music.model.*;
import com.dikra.tugasakhir.parser.json.MusicJSON;
import com.dikra.tugasakhir.parser.xml.MusicXML;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class MusicProcessor {
    Music music;
    private List<double[]> inputs;
    private List<double[]> outputs;
    private int active_part;

    public MusicProcessor(String path){
        music = new Music();
        if (path.substring(path.length()-4).equals(".xml")){
            System.out.println("path is xml");
            MusicXML musicXML = new MusicXML(path);
            music.setParts(musicXML.getParts());

        } else {
            System.out.println("path is json");

            music = new MusicJSON(path);
            MusicJSON musicJSON = new MusicJSON(path);
            music.setParts(musicJSON.getParts());
        }


        inputs = null;
        outputs = null;
        active_part = 0;
    }

    public Part getPartAt(int id){
        return music.getParts()[id];
    }

    public void setActivePart(int id){
        active_part = id;
    }

    public void generateInputOutput(){
        inputs = new ArrayList<double[]>();
        outputs = new ArrayList<double[]>();

        List<Note> all_notes = new ArrayList<Note>();
        List<Chord> all_chords = new ArrayList<Chord>();

        for (Measure measure : music.getParts()[active_part].getMeasures()){
            for (Note note : measure.getNotes()){
                all_notes.add(note);
            }
            for (Chord chord : measure.getChords()){
                all_chords.add(chord);
            }
        }

        Chord last_chord = null;
        for (int i = 0, j =0; i < all_chords.size(); i++){
            Chord chord = all_chords.get(i);
            int chord_id = chord.getPitchProgression();
            int chord_duration = chord.getDuration();
            int total_duration = 0;

            double[] input = new double[24];
            double[] output = new double[12];

            for (int k = 0; k < input.length; k++){
                input[k] = 0.;
            }

            if (last_chord != null){
                input[last_chord.getPitchProgression()] += 1.;
            }

            for (; j < all_notes.size() && total_duration + all_notes.get(j).getDuration() < chord_duration; j++){
                Note note = all_notes.get(j);

                int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
                total_duration += note.getDuration();

                input[12+pitch_id] += 1.;
            }


            for (int k = 0; k < output.length; k++){
                output[k] = (k == chord_id)? 1. : 0.;
            }

            inputs.add(input);
            outputs.add(output);
        }
    }

    public List<double[]> getInputs(){
        if (inputs == null) generateInputOutput();
        return inputs;
    }

    public List<double[]> getOutputs(){
        if (outputs == null) generateInputOutput();
        return outputs;
    }

    public static int getPitchId(String pitch_step, int pitch_alter){
        int id = 0;
        if (pitch_step.equals("C")) id = 0;
        if (pitch_step.equals("D")) id = 2;
        if (pitch_step.equals("E")) id = 4;
        if (pitch_step.equals("F")) id = 5;
        if (pitch_step.equals("G")) id = 7;
        if (pitch_step.equals("A")) id = 9;
        if (pitch_step.equals("B")) id = 11;

        id += pitch_alter;

        id = (id+12) % 12;

        return id;
    }

    public String getMusicJSON(){
        if (music == null) return "";
        return music.getMusicJSON();
    }

}
