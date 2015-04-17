package com.dikra.tugasakhir;

import com.dikra.tugasakhir.ann.NeuralNetwork;
import com.dikra.tugasakhir.ann.old.NeuralNetwork2;
import com.dikra.tugasakhir.musicxml.MusicXML;

/**
 * Created by DIKRA on 4/5/2015.
 */
public class Main {

    public static void main(String[] args){
        System.out.println("Go lulus Juli!");

        testAnn();
    }

    public static void testAnn(){
        int[] hid = { 10, 10, 10, 10, 10 };
        double[][] inputs = { {0., 0.}, {1., 0.}, {0., 1.}, {1., 1.}};
        double[][] outputs = { {0.}, {1.}, {1.}, {0.} };

        NeuralNetwork network = new NeuralNetwork(2, hid, 1, 0.1, 0.0);

        for (int epoch = 0; epoch < 100000; epoch++){
            for (int i = 0; i < 4; i++){
                network.trainBackpropagation(inputs[i], outputs[i]);
            }
        }

        for (int i = 0; i < 4; i++){
            network.computeOutput(inputs[i]);
            System.out.format("{ %.2f, %.2f } = %.8f -- ideal = %.8f\n", inputs[i][0], inputs[i][1], network.getOutputs()[0], outputs[i][0]);
        }
    }

    public static void testXORann(){
        int[] hid = { 2 };
        NeuralNetwork2 network = new NeuralNetwork2(2, hid, 1, 0.5, 0.0, false, false);

        double[][] inputs = { {0., 0.}, {1., 0.}, {0., 1.}, {1., 1.}};

        double[][] outputs = { {0.}, {1.}, {1.}, {0.} };

        for (int epoch = 0; epoch < 1000; epoch++){
            for (int i = 0; i < 4; i++){
                network.training(inputs[i], outputs[i], true);
                System.out.println("{ " + inputs[i][0] + ", " + inputs[i][1] + "} = " + network.getOutputs()[0] + " -- ideal = " + outputs[i][0]);
                System.out.println();
            }

        }

        System.out.println("\n=========================");
        for (int i = 0; i < 4; i++){
            network.calculateOutputs(inputs[i]);
            System.out.println("{ " + inputs[i][0] + ", " + inputs[i][1] +"} = " + network.getOutputs()[0] + " -- ideal = " + outputs[i][0]);
        }
    }

    public static void testMusicXML(){
        long startTime = System.nanoTime();

        MusicXML musicXML = new MusicXML("res/twinkle.xml");

        long endTime = System.nanoTime();

        System.out.format("finished... time taken: %.6f s\n", (endTime - startTime) / 1000000000.);
    }
}
