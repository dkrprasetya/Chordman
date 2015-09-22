package com.dikra.tugasakhir;

import com.dikra.tugasakhir.ann.DataSet;
import com.dikra.tugasakhir.ann.NeuralNetwork;
import com.dikra.tugasakhir.music.MusicPlayer;
import com.dikra.tugasakhir.music.MusicProcessor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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
        File file = new File("res/training.in");
        Scanner sc = null;
        List<DataSet> dataSets = new ArrayList<DataSet>();

        try {
            sc = new Scanner(file);

            while (sc.hasNext()){
                String inpName = sc.next();
                MusicProcessor mp = new MusicProcessor("res/songs/"+inpName + ".json");

                System.out.println("\nGenerate input-output of " + inpName);
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

        List<DataSet> basicDataSets = MusicProcessor.getBasicChordsDataSet();
        int iteration_num = 10000;

        NeuralNetwork ann = new NeuralNetwork(MusicProcessor.getInputSize(), MusicProcessor.getHiddenSize(), MusicProcessor.getOutputSize(), 0.6, 0.9);

        //NeuralNetwork.balanceDataSet(dataSets);

        for (int epoch = 1; epoch <= iteration_num; ++epoch){
            Collections.shuffle(basicDataSets);
            for (int i = 0; i < basicDataSets.size(); ++i){
                double[] ann_in = basicDataSets.get(i).inputs;
                double[] ann_out = basicDataSets.get(i).outputs;
                ann.trainBackPropagation(ann_in, ann_out);
            }
            if (epoch % (iteration_num/100) == 0) System.out.println("\rBasic training at epoch (" + (epoch*100/iteration_num) + "%)");
        }

        //System.out.println("DataSet size: " + dataSets.size());
        //dataSets.addAll(MusicProcessor.getBasicChordsDataSet());

        //System.out.println("DataSet size: " + dataSets.size());

        iteration_num = 10000;
        for (int epoch = 1; epoch <= iteration_num; ++epoch){
            Collections.shuffle(dataSets);
            for (int i = 0; i < dataSets.size(); ++i){
                double[] ann_in = dataSets.get(i).inputs;
                double[] ann_out = dataSets.get(i).outputs;
                ann.trainBackPropagation(ann_in, ann_out);
            }
            if (epoch % (iteration_num/100) == 0) System.out.println("\rTraining at epoch (" + (epoch*100/iteration_num) + "%)");
        }

        ann.saveWeightToMemory();
        System.out.println("\nTraining completed! DataSet size: " + dataSets.size() + "\nWeights of network is saved at <ann_weights.in>!");
    }

    public static void testing(){
        System.out.println();
        System.out.println("Daftar lagu:");

        File file = new File("res/testing.in");
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

        System.out.println((titles.size()+1) + ". " + "Kembali ke menu utama" );

        System.out.print("Indeks lagu yang anda pilih: ");

        int id;
        do {
            id = scanner.nextInt();
            if (id < 1 || id > titles.size()) {
                System.out.println("Nomor lagu di luar batas!");
            }
        } while(id < 1 || id > titles.size()+1);

        if (id == titles.size()+1){
            return;
        } else {
            MusicProcessor mp = new MusicProcessor("res/songs/"+titles.get(id-1) + ".json");

            // further processing
            NeuralNetwork ann = new NeuralNetwork(MusicProcessor.getInputSize(), MusicProcessor.getHiddenSize(), MusicProcessor.getOutputSize(), 0.3, 0.9);
            ann.loadFromMemory();

            mp.determineChords(ann);

            System.out.println("Memainkan hasil komposisi chord untuk pengiringan...");


            try {
                String outputpath = "res/output/" + titles.get(id-1) + "-" + MusicProcessor.experimentId +  "-" + MusicProcessor.getHiddenSize() + ".json";
                Files.deleteIfExists( new File(outputpath).toPath() );
                Files.copy(new File("output.json").toPath(), new File(outputpath).toPath());
            } catch(Exception e){
                e.printStackTrace();
            }

            final MusicPlayer musicPlayer = new MusicPlayer(mp);

            musicPlayer.play();

            System.out.print("Tekan <Enter> untuk menghentikan musik.");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (musicPlayer.isPlaying()){
                musicPlayer.stop();
            }
        }
    }

    public static void viewing(){
        System.out.println();
        System.out.println("Daftar lagu:");

        File file = new File("res/allsong.in");
        Scanner sc = null;
        List<String> titles = new ArrayList<String>();
        try {
            sc = new Scanner(file);

            for (int i  = 1; sc.hasNext(); ++i){
                String inpName = sc.next();
                titles.add(inpName);
            }


        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }

        int id;
        do {
            for (int i = 1; i <= titles.size(); ++i){
                System.out.println(i + ". " + titles.get(i-1));
            }
            System.out.println((titles.size() + 1) + ". " + "Kembali ke menu utama");

            System.out.print("Indeks lagu yang anda pilih: ");
            id = scanner.nextInt();
            if (id < 1 || id > titles.size()) {
                System.out.println("Nomor lagu di luar batas!");
            }
        } while(id < 1 || id > titles.size()+1);

        if (id == titles.size()+1){
            return;
        } else {
            int opt;
            do {
                System.out.println();
                System.out.println("1. Mainkan melodi dan chord");
                System.out.println("2. Hanya mainkan melodi");
                System.out.print("Opsi memainkan lagu yang anda pilih: ");
                opt = scanner.nextInt();
            } while (opt < 1 || opt > 2);


            MusicProcessor mp = new MusicProcessor("res/songs/"+titles.get(id-1) + ".json");

            System.out.println("Memainkan hasil komposisi chord untuk pengiringan...");



            final MusicPlayer musicPlayer;
            if (opt == 1) musicPlayer= new MusicPlayer(mp);
            else musicPlayer= new MusicPlayer(mp, true);

            musicPlayer.play();

            System.out.print("Tekan <Enter> untuk menghentikan musik.");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (musicPlayer.isPlaying()){
                musicPlayer.stop();
            }
        }
    }


    public static void evaluating(){
        System.out.println();
        System.out.println("Evaluasi:");

        File file = new File("res/data.in");
        Scanner sc = null;
        List<String> titles = new ArrayList<String>();
        try {
            sc = new Scanner(file);

            for (int i  = 1; sc.hasNext(); ++i){
                String inpName = sc.next();
                titles.add(inpName);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }

        try {
            PrintWriter pw = new PrintWriter(new File("evaluation_log"+MusicProcessor.experimentId + "-" + MusicProcessor.getHiddenSize()+".txt"));

            double avgScore = 0.;
            for (int id = 1; id <= titles.size(); ++id){
                MusicProcessor mp = new MusicProcessor("res/songs/"+titles.get(id-1) + ".json");
                MusicProcessor mp_origin = new MusicProcessor("res/songs/"+titles.get(id-1) + ".json");

                // further processing
                NeuralNetwork ann = new NeuralNetwork(MusicProcessor.getInputSize(), MusicProcessor.getHiddenSize(), MusicProcessor.getOutputSize(), 0.3, 0.9);
                ann.loadFromMemory();

                double avgcurscore = 0.;

                for (int it = 0; it < 100; ++it){
                    mp.determineChords(ann);

                    double getScore = mp_origin.evaluate(mp);

                    avgcurscore += getScore;
                }

                avgcurscore /= (double)100;


                pw.printf("(%d) %.8f\n", id, avgcurscore);
                avgScore += avgcurscore;

                try {
                    String outputpath = "res/output/" + titles.get(id-1) + "-" + MusicProcessor.experimentId +  "-" + MusicProcessor.getHiddenSize() + ".json";
                    Files.deleteIfExists( new File(outputpath).toPath() );
                    Files.copy(new File("output.json").toPath(), new File(outputpath).toPath());
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            avgScore /= (double)titles.size();


            pw.printf("Average: %.8f\n", avgScore);

            pw.close();

        } catch (Exception e){
            e.printStackTrace();
        }




    }

    public static void main(String[] args){
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
                System.out.println("1. Lihat data latih");
                System.out.println("2. Eksperimen 1 (Tanpa time series)");
                System.out.println("3. Eksperimen 2 (Time-series jarak 1/2 bar)");
                System.out.println("4. Eksperimen 3 (Time-series jarak 1 bar)");
                System.out.println("5. Keluar program");
                System.out.println();
                System.out.print("Masukan anda: ");
                menu = scanner.nextInt();

                if (menu == 1){
                    viewing();
                } else
                if (2 <= menu && menu <= 4){
                    MusicProcessor.setExperimentId(menu-2);

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
                        System.out.println("3. Evaluasi");
                        System.out.println("4. Kembali ke pemilihan eksperimen");
                        System.out.println();
                        System.out.print("Masukan anda: ");
                        menu = scanner.nextInt();

                        switch (menu){
                            case 1 : training(); break;
                            case 2 : testing(); break;
                            case 3 : evaluating(); break;
                            default: break;
                        }
                    } while (menu != 4);
                } else
                if (menu == 5){
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
