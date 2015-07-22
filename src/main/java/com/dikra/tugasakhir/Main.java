package com.dikra.tugasakhir;

import com.dikra.tugasakhir.ann.DataSet;
import com.dikra.tugasakhir.ann.NeuralNetwork;
import com.dikra.tugasakhir.music.MusicProcessor;

import java.io.File;
import java.util.*;

/**
 * Created by DIKRA on 4/5/2015.
 */
public class Main {

    public static Scanner scanner = null;

    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }

    public static void training(){
        File file = new File("res/data.in");
        Scanner sc = null;
        List<DataSet> dataSets = new ArrayList<DataSet>();

        try {
            sc = new Scanner(file);

            while (sc.hasNext()){
                String inpName = sc.next();
                MusicProcessor mp = new MusicProcessor("res/songs/"+inpName);
                mp.generateInputOutput();

                List<double[]> _in = mp.getInputs();
                List<double[]> _out = mp.getOutputs();

                for (int i = 0; i < _in.size(); ++i){
                    dataSets.add(new DataSet(_in.get(i), _out.get(i)));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            sc.close();
        }

        dataSets.addAll(MusicProcessor.getBasicChordsDataSet());

        NeuralNetwork ann = new NeuralNetwork(MusicProcessor.getInputSize(), 1, MusicProcessor.getOutputSize(), 0.3, 0.9);

        NeuralNetwork.balanceDataSet(dataSets);


        for (int epoch = 0; epoch < 10000; ++epoch){
            System.out.println("\rTraining at epoch-" + epoch + "...");
            Collections.shuffle(dataSets);
            for (int i = 0; i < dataSets.size(); ++i){
                double[] ann_in = dataSets.get(i).inputs;
                double[] ann_out = dataSets.get(i).outputs;
                ann.trainBackPropagation(ann_in, ann_out);
            }
        }

        ann.saveWeightToMemory();
        System.out.println("\nTraining completed!\nWeights of network is saved at <ann_weights.in>!");
    }

    public static void testing(){
        System.out.println();
        System.out.println("Daftar lagu:");

        File file = new File("res/data.in");
        Scanner sc = null;
        List<String> titles = new ArrayList<String>();
        try {
            sc = new Scanner(file);

            for (int i  = 1; sc.hasNext(); ++i){
                String inpName = sc.next();
                System.out.println(i + ". " + inpName);
                titles.add(inpName);
            }


        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }

        System.out.print("Indeks lagu yang anda pilih: ");
        int id = scanner.nextInt();

        if (id < 1 || id > titles.size()){
            System.out.println("Nomor lagu di luar batas!");
        } else {
            MusicProcessor mp = new MusicProcessor("res/songs/"+titles.get(id-1));

            // further processing
            NeuralNetwork ann = new NeuralNetwork(MusicProcessor.getInputSize(), 1, MusicProcessor.getOutputSize(), 0.3, 0.9);
            ann.loadFromMemory();

            mp.determineChords(ann);
        }
    }

    public static void main(String[] args){

//        MusicProcessor mp = new MusicProcessor("res/songs/twinkle.xml");
//        try {
//            PrintWriter pw = new PrintWriter(new File("twinkle.txt"));
//            pw.println(mp.getMusicJSON());
//            pw.close();
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }

        int menu;
        boolean running = true;

        try {
            scanner = new Scanner(System.in);
            while (running){
                clearConsole();
                System.out.println();
                System.out.println("=========================================================");
                System.out.println("========               CHORDMAN                   =======");
                System.out.println("=========================================================");
                System.out.println("M Dikra Prasetya - Juni 2015");
                System.out.println("Wisuda Oktober 2015 amiin!");
                System.out.println();
                System.out.println("Pilih eksperimen yang ingin dilakukan:");
                System.out.println("1. Eksperimen 1 (Tanpa time series)");
                System.out.println("2. Eksperimen 2 (Time-series jarak 1/2 bar)");
                System.out.println("3. Eksperimen 3 (Time-series jarak 1 bar)");
                System.out.println("4. Keluar program");
                System.out.println();
                System.out.print("Masukan anda: ");
                menu = scanner.nextInt();

                if (1 <= menu && menu <= 3){
                    MusicProcessor.setExperimentId(menu-1);

                    clearConsole();
                    do {
                        System.out.println();
                        System.out.println("=========================================================");
                        System.out.println("========               CHORDMAN                   =======");
                        System.out.println("=========================================================");
                        System.out.println("M Dikra Prasetya - Juni 2015");
                        System.out.println("Wisuda Oktober 2015 amiin!");
                        System.out.println();
                        System.out.println("On experiment-" + (MusicProcessor.experimentId+1) + "...");
                        System.out.println("1. Pelatihan data");
                        System.out.println("2. Pengujian data");
                        System.out.println("3. Kembali ke pemilihan eksperimen");
                        System.out.println();
                        System.out.print("Masukan anda: ");
                        menu = scanner.nextInt();

                        switch (menu){
                            case 1 : training(); break;
                            case 2 : testing(); break;
                            default: break;
                        }
                    } while (menu != 3);
                } else
                if (menu == 4){
                    running = false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
        }
    }
}
