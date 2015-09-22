package com.dikra.tugasakhir.music;

import com.dikra.tugasakhir.ann.DataSet;
import com.dikra.tugasakhir.ann.NeuralNetwork;
import com.dikra.tugasakhir.music.model.*;
import com.dikra.tugasakhir.parser.json.MusicJSON;
import com.dikra.tugasakhir.parser.xml.MusicXML;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class MusicProcessor {

    //region Variables

    public static int experimentId = -1;
    private static int inputSize;
    private static int outputSize;
    private static int hiddenSize;

    private Music music;
    private List<double[]> inputs;
    private List<double[]> outputs;
    private int active_part;

    //endregion

    //region Constructors

    /*** Constructor -- Assigning music-file path to parsed-models ***/
    public MusicProcessor(String path){
        music = new Music();
        if (path.substring(path.length()-4).equals(".xml")){
            MusicXML musicXML = new MusicXML(path);
            music.setParts(musicXML.getParts());
        } else {
            music = new MusicJSON(path);
            MusicJSON musicJSON = new MusicJSON(path);
            music.setParts(musicJSON.getParts());
        }


        inputs = null;
        outputs = null;
        active_part = 0;
        if (experimentId == -1) setExperimentId(0);
    }

    //endregion

    //region Experiment Defaults Helper Methods

    /*** Set active experiment id ***/
    /*** 1 - Note occurences + frequencies as input param (24 nodes) ***/
    /*** 2 - Note occurences + frequencies and 1/2 measure time series as input param (36 nodes) ***/
    /*** 3 - Note occurences + frequencies and 1/2 measure time series as input param (48 nodes) ***/
    public static void setExperimentId(int id){
        experimentId = id;

        inputSize = 50 + (12 * id);
        outputSize = 12;
        hiddenSize = 3;
    }

    /*** Current experiment's ANN input node size getter method ***/
    public static int getInputSize(){
        return inputSize;
    }

    /*** Current experiment's ANN hidden layers node size getter method ***/
    public static int getHiddenSize(){
        return hiddenSize;
    }

    /*** Current experiment's ANN output layer node size getter method ***/
    public static int getOutputSize(){
        return outputSize;
    }

    //endregion

    //region Music Model Helper Methods

    /*** Part getter method ***/
    public Part getPartAt(int id){
        return music.getParts()[id];
    }

    /*** Set active music partiture ***/
    public void setActivePart(int id){
        active_part = id;
    }

    /*** Get active part ***/
    public Part getActivePart(){
        return getPartAt(active_part);
    }

    /*** Get pitch id from step and alter number ***/
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

    /*** Convert current models to MusicJSON ***/
    public String getMusicJSON(){
        if (music == null) return "";
        return music.getMusicJSON();
    }

    public String getMusicJSON(boolean prettify){
        if (music == null) return "";
        return music.getMusicJSON(prettify);
    }

    //endregion

    // region ANN InputOutput Helper Methods
    private static void setInputNextChord0(double[] inp, int id, double val){
        inp[id+24] = val;
    }

    private static void setInputIsEnding0(double[] inp, double val){
        inp[36] = 1;
    }

    private static void setInputNextChord1(double[] inp, int id, double val){
        inp[id+37] = val;
    }

    private static void setInputIsEnding1(double[] inp, double val){
        inp[49] = 1;
    }

    private static void setInputChord0(double[] inp, int id, double val){
        inp[id+50] = val;
    }

    private static void setInputChord1(double[] inp, int id, double val){
        inp[id+62] = val;
    }

    private static void setInputOcc(double[] inp, int id, double val){
        inp[id] = val;
    }

    private static void addInputFreq(double[] inp, int id, double val){
        inp[id+12] += val;
    }

    public void generateInputOutput(){
        System.out.println("Generating output...");
        inputs = new ArrayList<double[]>();
        outputs = new ArrayList<double[]>();

        List<Note> all_notes = new ArrayList<Note>();
        List<Chord> all_chords = new ArrayList<Chord>();

        for (Measure measure : music.getParts()[active_part].getMeasures()){
            if (measure == null){
                System.out.println("false broh");
                assert(false);
            }
            for (Note note : measure.getNotes()){
                all_notes.add(note);
            }
            for (Chord chord : measure.getChords()){
                if (chord == null){
                    System.out.println("waini gaswat");
                    assert(false);
                }
                all_chords.add(chord);
            }
        }

        double[][] next_chord = new double[all_chords.size()][];

        int total_duration = 0;
        for (int i = 0, j =0; i < all_chords.size(); i++){
            Chord[] chord = new Chord[3];
            chord[2] = all_chords.get(i);
            chord[1] = (i >= 1)? all_chords.get(i-1) : Chord.getChordFromId(7);
            chord[0] = (i >= 2)? all_chords.get(i-2) : Chord.getChordFromId(7);


            int chord_duration = chord[2].getDuration();

            double[] input = new double[inputSize];
            double[] output = new double[outputSize];

            for (int k = 0; k < input.length; k++){
                input[k] = 0.;
            }
            for (int k = 0; k < output.length; k++){
                output[k] = 0.;
            }

            if (chord[1] != null){
                if (experimentId >= 1) {
                    setInputChord0(input, getPitchId(chord[1].getPitchStep(), chord[1].getPitchAlter()), 1.0);
                }
            }
            if (chord[0] != null){
                if (experimentId >= 2){
                    setInputChord1(input, getPitchId(chord[0].getPitchStep(), chord[0].getPitchAlter()), 1.0);
                }

            }

//            if (next_chord[0] != null){
//                setInputNextChord0(input, getPitchId(next_chord[0].getPitchStep(), next_chord[0].getPitchAlter()), 1.0);
//            } else {
//                setInputIsEnding0(input, 1.0);
//            }
//
//            if (next_chord[1] != null){
//                setInputNextChord1(input, getPitchId(next_chord[1].getPitchStep(), next_chord[1].getPitchAlter()), 1.0);
//            } else {
//                setInputIsEnding1(input, 1.0);
//            }

            double[] freq_note = new double[12];
            for (int k = 0; k < 12; ++k) freq_note[k] = 0.;

            double sum_freq = 0.;
            for (; j < all_notes.size(); ++j){
                Note note = all_notes.get(j);

                if (!note.isRest()){
                    int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
                    setInputOcc(input, pitch_id, 1.0);
                    freq_note[pitch_id]+=1.0;
                    sum_freq += 1.0;
                }

                if (total_duration + note.getDuration() == chord_duration){
                    ++j;
                    total_duration += note.getDuration();
                    break;
                } else
                if (total_duration + note.getDuration() > chord_duration){
                    break;
                }

                total_duration += note.getDuration();
            }

            if (sum_freq > 0.){
                for (int k = 0; k < 12; ++k){
                    addInputFreq(input, k, freq_note[k]/sum_freq);
                }
            }

            total_duration -= chord_duration;

            next_chord[i] = getChordProbFromNotes(freq_note);

            if (chord[2] != null){
                output[getPitchId(chord[2].getPitchStep(), chord[2].getPitchAlter())] = 1.0;

                Chord relative = chord[2].getRelativeChord();


                int id_relative = getPitchId(relative.getPitchStep(), relative.getPitchAlter());

                if (input[id_relative] > 0.) output[id_relative] = 0.1;
            }

            inputs.add(input);
            outputs.add(output);

            System.out.print("Played notes (" + sum_freq + "): ");
            for (int it = 0; it < freq_note.length; it++){
                if (freq_note[it] > 0) {
                    System.out.print(it + " ("+freq_note[it] + ") ");
                }
            }
            System.out.println();

            System.out.print("Inputs: ");
            for (int it = 0; it < input.length; it++){
                System.out.print(input[it] + " ");
            }
            System.out.println();

            System.out.print("Outputs: ");
            for (int it = 0; it < output.length; it++){
                System.out.print(output[it] + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < inputs.size(); ++i){
            double[] input = inputs.get(i);

            if (i+1 >= inputs.size()){
                setInputIsEnding0(input, 1);
            }
            else {
//                for (int j = 0; j < 12; ++j){
//                    setInputNextChord0(input, j, next_chord[i+1][j]);
//                }
            }

            if (i+2 >= inputs.size()){
                setInputIsEnding1(input, 1);
            }
            else {
//                for (int j = 0; j < 12; ++j){
//                    setInputNextChord1(input, j, next_chord[i + 2][j]);
//                }
            }
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

    public static double[] getChordInputFromId(int idNote){
        double[] input = new double[12];
        int notes[] = { 0, 2, 4, 5, 7, 9, 11};

        for (int i = 0; i < 12; ++i) input[i] = 0.;

        setInputOcc(input, notes[idNote], 1.0);
        setInputOcc(input, notes[(idNote+2)%notes.length], 1.0);
        setInputOcc(input, notes[(idNote + 4) % notes.length], 1.0);
        addInputFreq(input, notes[idNote], 1.0/3.);
        addInputFreq(input, notes[(idNote + 2) % notes.length], 1.0/3.);
        addInputFreq(input, notes[(idNote + 4) % notes.length], 1.0/3.);

        return input;
    }


    public static List<DataSet> getBasicChordsDataSet(){
        List<DataSet> dataSets = new ArrayList<DataSet>();
        double[] input;
        double[] output;

        int notes[] = { 0, 2, 4, 5, 7, 9, 11};

        // Main Triads
        for (int i = 1; i < notes.length; ++i){
            int id =  notes[i];

            int t = (id == 7)? 128  : (id == 4 || id == 5 || id == 2 || id == 7 || id == 9)? 128 : 128;

            input = new double[inputSize];
            output = new double[outputSize];

            for (int k = 0; k < input.length; k++){
                input[k] = 0.;
            }
            for (int k = 0; k < output.length; k++){
                output[k] = 0.;
            }
            setInputOcc(input, notes[i], 1.0);
            setInputOcc(input, notes[(i+2)%notes.length], 1.0);
            setInputOcc(input, notes[(i + 4) % notes.length], 1.0);
            addInputFreq(input, notes[i], 1.0/3.);
            addInputFreq(input, notes[(i + 2) % notes.length], 1.0/3.);
            addInputFreq(input, notes[(i + 4) % notes.length], 1.0/3.);
            output[id] = 1.0;

            for (int j = 0; j < t; ++j){
                dataSets.add(new DataSet(input, output));
            }

        }

        if (experimentId < 1000000) // we're skipping the next rows of code
            return dataSets;

        // Chord Progressions by theory
        // 1. V -> I
        input = getChordInputFromId(0);
        output = new double[outputSize];
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }

        setInputChord0(input, notes[4], 1.0);
        output[notes[0]] = 1.0;

        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 2. vii -> { I, iii }
        input = getChordInputFromId(0);
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k){
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0/12.);
        }
        setInputChord0(input, notes[6], 1.0);
        output[notes[0]] = 1.0;
        output[notes[2]] = 1.0;
        output[notes[6]] = 1.0;
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 3. ii --> { V, vi }
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k){
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0/12.);
        }
        setInputChord0(input, notes[1], 1.0);
        output[notes[4]] = 1.0;
        output[notes[1]] = 1.0;
        output[notes[5]] = 1.0;
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 4. IV -> { V, vii }
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k){
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0/12.);
        }
        setInputChord0(input, notes[3], 1.0);
        output[notes[4]] = 1.0;
        output[notes[6]] = 1.0;
        output[notes[1]] = 1.0;
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 5. vi -> ii
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k){
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0/12.);
        }
        setInputChord0(input, notes[5], 1.0);
        output[notes[1]] = 1.0;
        output[notes[5]] = 1.0;
        output[notes[3]] = 1.0;
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 6. I to all
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k) {
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0 / 12.);
            output[notes[k]] = 1.;
        }
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }



        // 7. iii -> vi
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        for (int k =0; k < notes.length; ++k){
            setInputOcc(input, notes[k], 1.0);
            addInputFreq(input, notes[k], 1.0/12.);
        }
        setInputChord0(input, notes[5], 1.0);
        output[notes[2]] = 1.0;
        output[notes[5]] = 1.0;
        for (int j = 0; j < 64; ++j){
            dataSets.add(new DataSet(input, output));
        }


        return dataSets;
    }

    // endregion

    //region Determining Chords Methods

    /*** Assign chords using ANN ***/
    public void determineChords(NeuralNetwork ann){
        try {
            List<Note> all_notes = new ArrayList<Note>();
            List<Chord> all_chords = new ArrayList<Chord>();

            for (Measure measure : music.getParts()[active_part].getMeasures()){
                if (measure == null){
                    System.out.println("false broh");
                    assert(false);
                }
                for (Note note : measure.getNotes()){
                    all_notes.add(note);
                }
                for (Chord chord : measure.getChords()){
                    if (chord == null){
                        System.out.println("waini gaswat");
                        assert(false);
                    }
                    all_chords.add(chord);
                }
            }

            double[][] next_chord = new double[all_chords.size()][];

            int total_duration = 0;
            for (int i = 0, j =0; i < all_chords.size(); i++){
                Chord[] chord = new Chord[3];
                chord[2] = all_chords.get(i);
                chord[1] = (i >= 1)? all_chords.get(i-1) : Chord.getChordFromId(7);
                chord[0] = (i >= 2)? all_chords.get(i-2) : Chord.getChordFromId(7);

                int chord_duration = chord[2].getDuration();

                double[] input = new double[inputSize];
                //double[] output = new double[outputSize];

                for (int k = 0; k < input.length; k++){
                    input[k] = 0.;
                }

                if (chord[1] != null){
                    if (experimentId >= 1) {
                        setInputChord0(input, getPitchId(chord[1].getPitchStep(), chord[1].getPitchAlter()), 1.0);
                    }
                }
                if (chord[0] != null){
                    if (experimentId >= 2){
                        setInputChord1(input, getPitchId(chord[0].getPitchStep(), chord[0].getPitchAlter()), 1.0);
                    }

                }

//            if (next_chord[0] != null){
//                setInputNextChord0(input, getPitchId(next_chord[0].getPitchStep(), next_chord[0].getPitchAlter()), 1.0);
//            } else {
//                setInputIsEnding0(input, 1.0);
//            }
//
//            if (next_chord[1] != null){
//                setInputNextChord1(input, getPitchId(next_chord[1].getPitchStep(), next_chord[1].getPitchAlter()), 1.0);
//            } else {
//                setInputIsEnding1(input, 1.0);
//            }

                double[] freq_note = new double[12];
                for (int k = 0; k < 12; ++k) freq_note[k] = 0.;

                double sum_freq = 0.;
                for (; j < all_notes.size(); ++j){
                    Note note = all_notes.get(j);

                    if (!note.isRest()){
                        int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
                        setInputOcc(input, pitch_id, 1.0);
                        freq_note[pitch_id]+=1.0;
                        sum_freq += 1.0;
                    }

                    if (total_duration + note.getDuration() == chord_duration){
                        ++j;
                        total_duration += note.getDuration();
                        break;
                    } else
                    if (total_duration + note.getDuration() > chord_duration){
                        break;
                    }

                    total_duration += note.getDuration();
                }

                if (sum_freq > 0.){
                    for (int k = 0; k < 12; ++k){
                        addInputFreq(input, k, freq_note[k]/sum_freq);
                    }
                }

                total_duration -= chord_duration;

                //next_chord[i] = getChordProbFromNotes(freq_note);

                if (i+1 >= all_chords.size()){
                    setInputIsEnding0(input, 1.);
                }

                if (i+2 >= all_chords.size()){
                    setInputIsEnding1(input, 1.);
                }

                ann.computeOutput(input);

                double[] output = ann.getOutputs();

                //for (int it = 0; it < 12; ++it) output[it] *= next_chord_prob[cur_chord_id][it];

                System.out.print("Played notes (" + sum_freq + "): ");
                for (int it = 0; it < freq_note.length; it++){
                    if (freq_note[it] > 0) {
                        System.out.print(it + " ("+freq_note[it] + ") ");
                    }
                }
                System.out.println();

                System.out.print("Inputs: ");
                for (int it = 0; it < input.length; it++){
                    System.out.print(input[it] + " ");
                }
                System.out.println();

                Chord output_chord = getChordFromOutput(output);
                output_chord.setDuration(chord_duration);

                all_chords.get(i).setDuration(output_chord.getDuration());
                all_chords.get(i).setPitchAlter(output_chord.getPitchAlter());
                all_chords.get(i).setPitchStep(output_chord.getPitchStep());

                chord[0] = chord[1];
                chord[1] = output_chord;

                System.out.print("Played notes (" + sum_freq + "): ");
                for (int it = 0; it < freq_note.length; it++){
                    if (freq_note[it] > 0) {
                        System.out.print(it + " ("+freq_note[it] + ") ");
                    }
                }
                System.out.println();

                System.out.print("Inputs: ");
                for (int it = 0; it < input.length; it++){
                    System.out.print(input[it] + " ");
                }
                System.out.println();

                System.out.print("Outputs: ");
                for (int it = 0; it < output.length; it++){
                    System.out.print(output[it] + " ");
                }
                System.out.println();
            }
//
//            for (int i = 0; i < inputs.size(); ++i){
//                double[] input = inputs.get(i);
//
//                if (i+1 >= inputs.size()){
//                    setInputIsEnding0(input, 1);
//                }
//                else {
////                for (int j = 0; j < 12; ++j){
////                    setInputNextChord0(input, j, next_chord[i+1][j]);
////                }
//                }
//
//                if (i+2 >= inputs.size()){
//                    setInputIsEnding1(input, 1);
//                }
//                else {
////                for (int j = 0; j < 12; ++j){
////                    setInputNextChord1(input, j, next_chord[i + 2][j]);
////                }
//                }
//            }
//
//
//            Measure[] measures = music.getParts()[active_part].getMeasures();
//            MeasureAttributes[] attributes = music.getParts()[active_part].getMeasureAttributes();
//
//            Chord[] chord = new Chord[2];
//            chord[1] = null;
//            chord[0] = null;
//
//
//            double next_chord_prob[][] = new double[measures.length*2][];
//
//            for (int i = 0, j = 0; i < measures.length; ++i){
//                Note[] notes = measures[i].getNotes();
//
//                int chord_div_per_measure = (attributes[i].getTimeBeats() / 2);
//                int chord_duration = attributes[i].getDivisions() * chord_div_per_measure;
//
//                int total_duration = 0;
//                for (int b = 0; b < chord_div_per_measure; b++){
//                    double[] freq_note = new double[12];
//
//                    for (; j < notes.length; ++j){
//                        Note note = notes[j];
//                        total_duration += note.getDuration();
//
//                        if (!note.isRest()){
//                            int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
//                            freq_note[pitch_id] += 1.0;
//                        }
//
//                        if (total_duration + note.getDuration() == chord_duration){
//                            ++j;
//                            break;
//                        } else
//                        if (total_duration + note.getDuration() > chord_duration){
//                            total_duration -= note.getDuration();
//                            break;
//                        }
//                    }
//                    total_duration -= chord_duration;
//
//                    next_chord_prob[i*2 + b] = getChordProbFromNotes(freq_note);
//                }
//            }
//
//            for (int i = 0, j = 0; i < measures.length; ++i){
//                Note[] notes = measures[i].getNotes();
//
//                int chord_div_per_measure = (attributes[i].getTimeBeats() / 2);
//                int tot_duration = attributes[i].getTimeBeats() * attributes[i].getDivisions();
//                int chord_duration = attributes[i].getDivisions() * chord_div_per_measure;
//
//                Chord[] res_chords = new Chord[chord_div_per_measure];
//
//                int total_duration = 0;
//
//                for (int b = 0; b < chord_div_per_measure; b++){
//
//                    double[] input = new double[getInputSize()];
//
//                    for (int k = 0; k < input.length; k++){
//                        input[k] = 0.;
//                    }
//
//                    if (chord[1] != null){
//                        if (experimentId >= 1)
//                        setInputChord0(input, getPitchId(chord[1].getPitchStep(), chord[1].getPitchAlter()), 1.0);
//                    }
//                    if (chord[0] != null){
//                        if (experimentId >= 2)
//                        setInputChord1(input, getPitchId(chord[0].getPitchStep(), chord[0].getPitchAlter()), 1.0);
//                    }
//
//                    double freq_note[] = new double[12];
//                    for (int it = 0; it < 12; ++it) freq_note[it] = 0.;
//                    double sum_freq = 0.;
//                    for (; j < notes.length; ++j){
//                        Note note = notes[j];
//                        total_duration += note.getDuration();
//
//                        if (!note.isRest()){
//                            int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
//                            setInputOcc(input, pitch_id, 1.0);
//                            freq_note[pitch_id] += 1.0;
//                            sum_freq += 1.0;
//                        }
//
//                        if (total_duration + note.getDuration() == chord_duration){
//                            ++j;
//                            break;
//                        } else
//                        if (total_duration + note.getDuration() > chord_duration){
//                            total_duration -= note.getDuration();
//                            break;
//                        }
//                    }
//                    total_duration -= chord_duration;
//
//                    if (sum_freq > 0.){
//                        for (int it = 0; it < 12; ++it){
//                            addInputFreq(input, it, freq_note[it]/sum_freq);
//                        }
//                    }
//
//                    int cur_chord_id = i*2+b;
//                    if (cur_chord_id+1 >= next_chord_prob.length){
//                        setInputIsEnding0(input, 1);
//                    } else {
////                        for (int it = 0; it < 12; ++it){
////                            setInputNextChord0(input, it, next_chord_prob[cur_chord_id+1][it]);
////                        }
//                    }
//                    if (cur_chord_id+2 >= next_chord_prob.length){
//                        setInputIsEnding1(input, 1);
//                    } else {
////                        for (int it = 0; it < 12; ++it){
////                            setInputNextChord1(input, it, next_chord_prob[cur_chord_id+2][it]);
////                        }
//                    }
//
//                    ann.computeOutput(input);
//
//                    double[] output = ann.getOutputs();
//
//                    //for (int it = 0; it < 12; ++it) output[it] *= next_chord_prob[cur_chord_id][it];
//
//                    System.out.print("Played notes (" + sum_freq + "): ");
//                    for (int it = 0; it < freq_note.length; it++){
//                        if (freq_note[it] > 0) {
//                            System.out.print(it + " ("+freq_note[it] + ") ");
//                        }
//                    }
//                    System.out.println();
//
//                    System.out.print("Inputs: ");
//                    for (int it = 0; it < input.length; it++){
//                        System.out.print(input[it] + " ");
//                    }
//                    System.out.println();
//
//                    Chord output_chord = getChordFromOutput(output);
//                    output_chord.setDuration(chord_duration);
//
//                    res_chords[b] = output_chord;
//
//                    chord[0] = chord[1];
//                    chord[1] = output_chord;
//                }
//
//                measures[i].setChords(res_chords);
//            }

            PrintWriter pw = new PrintWriter(new File("output.json"));
            pw.println(music.getMusicJSON(true));

            System.out.println("\nPenentuan chord berhasil\nKomposisi chord disimpan pada <output.json>");

            pw.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("\nPenentuan chord gagal!");
        }

    }


    /*** Pick chords from ANN output value fuzzily ***/
    Chord getChordFromOutput(double[] output){
        double tot = 0.;
        double lim = 0.75;

        //output[0] += 0.125;
        //output[0] *= 2.;
        //output[11] *= 1/2.;
        //output[7] *= 2./5.;
        while (lim >= 0.){
            tot = 0.;
            for (int i = 0; i < output.length; ++i) {
                if (output[i] >= lim) tot += output[i];
            }

            if (tot > 0) break;

            lim -= 0.75;
        }

        if (lim < 0.) return Chord.getChordFromId(0);

        System.out.print("candidates: ");
        for (int i = 0; i < output.length; ++i){
            if (output[i] == 0.) continue;
            if (output[i] >= lim) System.out.format(" %d (%.3f)", i, output[i]);
        }
        System.out.println();

        Random rand = new Random();
        double prob = rand.nextDouble() * tot;

        tot = 0.;
        for (int i = 0; i < output.length; ++i){
            if (output[i] >= lim) tot += output[i];

            if (tot >= prob){
                System.out.println("pick " + i);
                return Chord.getChordFromId(i);
            }
        }
        System.out.println("pick " + 0);
        return Chord.getChordFromId(0);
    }

    Chord getChordFromOutput2(double[] output){
        double tot = 0.;

        for (int i = 0; i < output.length; ++i) {
            tot += output[i];
        }

        System.out.print("candidates: ");
        for (int i = 0; i < output.length; ++i){
            System.out.format(" %d (%.3f)", i, output[i]);
        }
        System.out.println();

        Random rand = new Random();
        double prob = rand.nextDouble() * tot;

        tot = 0.;
        for (int i = 0; i < output.length; ++i){
            tot += output[i];

            if (tot >= prob){
                System.out.println("pick " + i);
                return Chord.getChordFromId(i);
            }
        }
        System.out.println("pick " + 0);
        return Chord.getChordFromId(0);
    }

    Chord getChordFromOutputGreedy(double[] output){
        double tot = 0.;
        double lim = 0.5;
        while (true){
            tot = 0.;
            for (int i = 0; i < output.length; ++i) {
                if (output[i] >= lim) tot += output[i];
            }

            if (tot > 0.) break;

            lim -= 0.15;
        }

        System.out.print("candidates: ");
        for (int i = 0; i < output.length; ++i){
            if (output[i] >= lim) System.out.format(" %d (%.3f)", i, output[i]);
        }
        System.out.println();

        double maxprob = 0.;
        int ret = -1;

        for (int i = 0; i < output.length; ++i){
            if (output[i] > maxprob){
                maxprob = output[i];
                ret = i;
            }
        }
        return Chord.getChordFromId(ret);
    }

    /** Get chords probability from notes **/
    double[] getChordProbFromNotes(double[] freq){
        int p[] = { 0, 2, 4, 5, 7, 9, 11};
        double[] ret = new double[12];
        double tot = 0.;

        for (int i = 0; i < p.length; ++i){
            ret[i] = 0.;
            //tot += 3*freq[p[i]];
        }

        ret[0] = 0.5;

//        if (tot == 0.) {
//            return ret;
//        }

        for (int i = 0; i < p.length; ++i){
//            ret[(p[i]-2+p.length)%p.length] += freq[p[i]]/tot;
//            ret[p[i]] += freq[p[i]]/tot;
//            ret[(p[i]+2)%p.length] += freq[p[i]]/tot;

            ret[(p[i]-2+p.length)%p.length] = 1.;
            ret[p[i]] = 1.;
            ret[(p[i]+2)%p.length] = 1.;
        }

        int prog[][] = { {0, 2, 4, 5, 7, 9, 10, 11}, {0, 2, 3, 5, 7, 8, 10, 11}, {0, 2, 3, 5, 6, 8, 9, 10, 11} };

        for (int i = 0; i < p.length; ++i){
            int scale;
            if (p[i] == 0 || p[i] == 5 || p[i] == 7) scale = 0;
            else if (p[i] == 2 || p[i] == 4 || p[i] == 9) scale = 1;
            else if (p[i] == 11) scale = 2;
            else scale = 0;

            int flag[] = new int[12];
            for (int j = 0; j < 12; ++j) flag[j] = 0;

            for (int j = 0; j < prog[scale].length; ++j) flag[(prog[scale][j] + p[i]) % 12] = 1;

            for (int j = 0; j < 12; ++j){
                if (freq[j] > 0. && flag[j] == 0){
                    ret[p[i]] = 0.;
                    break;
                }
            }

        }


        return ret;
    }

    //endregion

    //region Evaluation Methods

    public double evaluate(MusicProcessor mp){
        List<Chord> origin_chords = new ArrayList<Chord>();

        for (Measure measure : music.getParts()[active_part].getMeasures()){
            if (measure == null){
                System.out.println("false broh");
                assert(false);
            }
            for (Chord chord : measure.getChords()){
                if (chord == null){
                    System.out.println("waini gaswat");
                    assert(false);
                }
                origin_chords.add(chord);
            }
        }

        List<Chord> output_chords = new ArrayList<Chord>();

        for (Measure measure : mp.getActivePart().getMeasures()){
            if (measure == null){
                System.out.println("false broh");
                assert(false);
            }
            for (Chord chord : measure.getChords()){
                if (chord == null){
                    System.out.println("waini gaswat");
                    assert(false);
                }
                output_chords.add(chord);
            }
        }

        int prog[][] = { {0, 2, 4, 5, 7, 9, 10, 11}, {0, 2, 3, 5, 7, 8, 10, 11}, {0, 2, 3, 5, 6, 8, 9, 10, 11} };

        double score = 0.;
        for (int i = 0; i < origin_chords.size(); ++i){
            int pid_a = getPitchId(origin_chords.get(i).getPitchStep(), origin_chords.get(i).getPitchAlter());
            int pid_b = getPitchId(output_chords.get(i).getPitchStep(), output_chords.get(i).getPitchAlter());

            if (pid_b == pid_a){
                score += 1.;
                continue;
            }

            if (pid_a == 2 || pid_a == 4 || pid_a == 9){
                if (pid_b == (pid_a+3)%12){
                    score += 0.8;
                    continue;
                }
            } else {
                if (pid_b == (pid_a-3+12)%12){
                    score += 0.8;
                    continue;
                }
            }

            int scale_a;
            if (pid_a == 0 || pid_a == 5 || pid_a == 7) scale_a = 0;
            else if (pid_a == 2 || pid_a == 4 || pid_a == 9) scale_a = 1;
            else if (pid_a == 11) scale_a = 2;
            else scale_a = 0;

            int flag_a[] = new int[12];
            for (int j = 0; j < 12; ++j) flag_a[j] = 0;

            for (int j = 0; j < prog[scale_a].length; ++j) flag_a[(prog[scale_a][j] + pid_a) % 12] = 1;

            int scale_b;
            if (pid_b == 0 || pid_b == 5 || pid_b == 7) scale_b = 0;
            else if (pid_b == 2 || pid_b == 4 || pid_b == 9) scale_b = 1;
            else if (pid_b == 11) scale_b = 2;
            else scale_b = 0;

            int flag_b[] = new int[12];
            for (int j = 0; j < 12; ++j) flag_b[j] = 0;

            for (int j = 0; j < prog[scale_b].length; ++j) flag_b[(prog[scale_b][j] + pid_b) % 12] = 1;

            double add = 0.;
            for (int j = 0; j < 12; ++j){
                if (flag_a[j] == flag_b[j]) add += 1.;
            }

            add /= 12.;
            add *= 3. / 5.;

            score += add;
        }
        score /= (double)(origin_chords.size());

        return score;
    }

    //endregion
}
