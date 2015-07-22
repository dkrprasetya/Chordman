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
    private static int hiddenSize[];

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

        inputSize = 24 + (12 * id);
        outputSize = 12;
        hiddenSize = new int[]{ ((inputSize+outputSize) * 2 / 3) };
    }

    /*** Current experiment's ANN input node size getter method ***/
    public static int getInputSize(){
        return inputSize;
    }

    /*** Current experiment's ANN hidden layers node size getter method ***/
    public static int[] getHiddenSize(){
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

    //endregion

    // region ANN InputOutput Helper Methods
    private static void setInputChord0(double[] inp, int id, double val){
        inp[id+24] = val;
    }

    private static void setInputChord1(double[] inp, int id, double val){
        inp[id+36] = val;
    }

    private static void setInputOcc(double[] inp, int id, double val){
        inp[id] = val;
    }

    private static void addInputFreq(double[] inp, int id, double val){
        inp[id+12] += val;
    }

    public void generateInputOutput(){
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

        for (int i = 0, j =0; i < all_chords.size(); i++){
            Chord[] chord = new Chord[3];
            chord[2] = all_chords.get(i);
            chord[1] = (i >= 1)? all_chords.get(i-1) : Chord.getChordFromId(7);
            chord[0] = (i >= 2)? all_chords.get(i-2) : Chord.getChordFromId(7);


            int chord_duration = chord[2].getDuration();
            int total_duration = 0;

            double[] input = new double[inputSize];
            double[] output = new double[outputSize];

            for (int k = 0; k < input.length; k++){
                input[k] = 0.;
            }
            for (int k = 0; k < output.length; k++){
                output[k] = 0.;
            }

            if (chord[1] != null){
                if (experimentId >= 1)
                    setInputChord0(input, getPitchId(chord[1].getPitchStep(), chord[1].getPitchAlter()), 1.0);
            }
            if (chord[0] != null){
                if (experimentId >= 2)
                setInputChord1(input, getPitchId(chord[0].getPitchStep(), chord[0].getPitchAlter()), 1.0);
            }

            for (; j < all_notes.size() && total_duration + all_notes.get(j).getDuration() <= chord_duration; j++){
                Note note = all_notes.get(j);
                total_duration += note.getDuration();

                if (!note.isRest()){
                    int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
                    setInputOcc(input, pitch_id, 1.0);
                    addInputFreq(input, pitch_id, 1.0);
                }
            }

            if (chord[2] != null){
                output[getPitchId(chord[2].getPitchStep(), chord[2].getPitchAlter())] = 1.0;
                System.out.println("set output on " + getPitchId(chord[2].getPitchStep(), chord[2].getPitchAlter()));
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

    public static List<DataSet> getBasicChordsDataSet(){
        List<DataSet> dataSets = new ArrayList<DataSet>();
        double[] input;
        double[] output;

        int notes[] = { 0, 2, 4, 5, 7, 9, 11};

        // Main Triads
        for (int i = 0; i < notes.length; ++i){
            int id =  notes[i];

            int t = (id == 7)? 128  : (id == 5 || id == 2)? 32 : 8;

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
            setInputOcc(input, notes[(i+4)%notes.length], 1.0);
            output[id] = 1.0;

            for (int j = 0; j < t; ++j){
                dataSets.add(new DataSet(input, output));
            }

            input = new double[inputSize];
            output = new double[outputSize];

            for (int k = 0; k < input.length; k++){
                input[k] = 0.;
            }
            for (int k = 0; k < output.length; k++){
                output[k] = 0.;
            }
            setInputOcc(input, notes[i], 1.0);
            setInputOcc(input, notes[(i+4)%notes.length], 1.0);
            output[id] = 1.0;

            for (int j = 0; j < t; ++j){
                dataSets.add(new DataSet(input, output));
            }
        }

        if (experimentId < 1)
            return dataSets;

        // Chord Progressions by theory
        // 1. V -> I
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[4], 1.0);
        output[notes[0]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 2. vii -> I
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[6], 1.0);
        output[notes[0]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 3. ii -> V
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[1], 1.0);
        output[notes[4]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 4. ii -> vii
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[1], 1.0);
        output[notes[6]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 5. IV -> V
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[3], 1.0);
        output[notes[4]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 6. IV -> vii
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[3], 1.0);
        output[notes[6]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 7. vi -> ii
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[5], 1.0);
        output[notes[6]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 8. vi -> IV
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[5], 1.0);
        output[notes[3]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }

        // 9. vii -> iii
        input = new double[inputSize];
        output = new double[outputSize];
        for (int k = 0; k < input.length; k++){
            input[k] = 0.;
        }
        for (int k = 0; k < output.length; k++){
            output[k] = 0.;
        }
        setInputChord0(input, notes[6], 1.0);
        output[notes[2]] = 1.0;
        for (int j = 0; j < 512; ++j){
            dataSets.add(new DataSet(input, output));
        }


        return dataSets;
    }

    // endregion

    //region Determining Chords Methods

    /*** Assign chords using ANN ***/
    public void determineChords(NeuralNetwork ann){
        try {
            Measure[] measures = music.getParts()[active_part].getMeasures();
            MeasureAttributes[] attributes = music.getParts()[active_part].getMeasureAttributes();

            Chord[] chord = new Chord[2];
            chord[1] = null;
            chord[0] = null;

            for (int i = 0, j = 0; i < measures.length; ++i){
                Note[] notes = measures[i].getNotes();

                int chord_div_per_measure = (attributes[i].getTimeBeats() / 2);
                int tot_duration = attributes[i].getTimeBeats() * attributes[i].getDivisions();
                int chord_duration = attributes[i].getDivisions() * chord_div_per_measure;

                Chord[] res_chords = new Chord[tot_duration/chord_div_per_measure];

                for (int b = 0; b < tot_duration; b += chord_duration){
                    int total_duration = 0;

                    double[] input = new double[48];

                    for (int k = 0; k < input.length; k++){
                        input[k] = 0.;
                    }

                    if (chord[1] != null){
                        setInputChord1(input, getPitchId(chord[1].getPitchStep(), chord[1].getPitchAlter()), 1.0);
                    }
                    if (chord[0] != null){
                        setInputChord0(input, getPitchId(chord[0].getPitchStep(), chord[0].getPitchAlter()), 1.0);
                    }

                    for (; j < notes.length && total_duration + notes[j].getDuration() <= chord_duration; j++){
                        Note note = notes[j];
                        total_duration += note.getDuration();

                        if (!note.isRest()){
                            int pitch_id = getPitchId(note.getPitchStep(), note.getPitchAlter());
                            setInputOcc(input, pitch_id, 1.0);
                            addInputFreq(input, pitch_id, 1.0);
                        }
                    }

                    ann.computeOutput(input);

                    Chord output_chord = getChordFromOutput(ann.getOutputs());
                    output_chord.setDuration(chord_duration);

                    res_chords[b/chord_duration] = output_chord;

                    chord[0] = chord[1];
                    chord[1] = output_chord;
                }

                measures[i].setChords(res_chords);
            }

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
        List<Integer> candidates = new ArrayList<Integer>();
        for (double lim = 1.; candidates.size() == 0; lim -= 0.2) {
            for (int i = 0; i < output.length; ++i) {
                if (output[i] >= lim) candidates.add(i);
            }
        }
        Random rand = new Random();
        int chosen = candidates.get(rand.nextInt(candidates.size()));

//        System.out.print("candidates: ");
//        for (int i = 0; i < candidates.size(); ++i) System.out.print(" " + candidates.get(i));
//        System.out.println();

        return Chord.getChordFromId(chosen);
    }

    //endregion
}
