package com.dikra.tugasakhir.ann;

import com.dikra.tugasakhir.music.MusicProcessor;

import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class NeuralNetwork {

    //region Variables

    private int inputCount;
    private int[] hiddenCount;
    private int outputCount;
    private int layerSize;
    private int perceptronSize;
    private double learnRate;
    private double momentum;
    private NeuralLayer[] neuralLayers;
    private static BiasPerceptron bias;

    //endregion

    //region Constructors + Initialization Methods

    /*** Constructor 1 ***/
    public NeuralNetwork(int inputCount, int[] hiddenCount, int outputCount, double learnRate, double momentum) {
        initialize(inputCount, hiddenCount, outputCount, learnRate, momentum);
    }

    /*** Constructor 2 -- Hidden layer node-size initialized with empirical calculation ***/
    /*** Hidden-node-size = (input_size + output_size) * 2/3 ***/
    public NeuralNetwork(int inputCount, int hiddenLayCount, int outputCount, double learnRate, double momentum){
        int[] hiddenCount = new int[hiddenLayCount];
        int nodeCount = Math.round((inputCount+outputCount) * 2.f / 3.f);
        for (int i = 0; i < hiddenLayCount; i++) hiddenCount[i] = nodeCount;

        initialize(inputCount, hiddenCount, outputCount, learnRate, momentum);
    }

    /*** Initialize network configuration ***/
    private void initialize(int inputCount, int[] hiddenCount, int outputCount, double learnRate, double momentum) {
        // Initialize constant variables
        this.inputCount = inputCount;
        this.hiddenCount = hiddenCount;
        this.outputCount = outputCount;
        this.learnRate = learnRate;
        this.momentum = momentum;

        // Initialize layers
        layerSize = hiddenCount.length+2;
        neuralLayers = new NeuralLayer[layerSize];
        neuralLayers[0] = new InputLayer(inputCount);
        for (int i = 0; i < hiddenCount.length; i++){
            neuralLayers[i+1] = new HiddenLayer(hiddenCount[i]);
        }
        neuralLayers[layerSize-1] = new OutputLayer(outputCount);



        // Initialize perceptrons & weights
        bias = new BiasPerceptron();
        for (int i = 0; i < layerSize; i++){
            neuralLayers[i].initialize();
        }


        for (int i = 1; i < layerSize; i++){
            initializeWeights(neuralLayers[i - 1], neuralLayers[i]);
        }
    }

    /*** Intitialize weights by connecting layer A to layer B ***/
    private void initializeWeights(NeuralLayer layerA, NeuralLayer layerB){
        Perceptron[] perceptronsA = layerA.getPerceptrons();
        Perceptron[] perceptronsB = layerB.getPerceptrons();

        int sizeA = layerA.getSize();
        int sizeB = layerB.getSize();

        Weight[][] weights_in = new Weight[sizeB][sizeA+1];
        Weight[][] weights_out = new Weight[sizeA+1][sizeB];

        double sqr_inp = Math.sqrt(perceptronsA.length+1);

        for (int i = 0; i < sizeA+1; i++){
            for  (int j = 0; j < sizeB; j++){
                Weight w = new Weight(perceptronsA[i], perceptronsB[j]);
                w.randomizeValue(-sqr_inp, sqr_inp);

                weights_out[i][j] = w;
                weights_in[j][i] = w;
            }
        }

        for (int i = 0; i < sizeA; i++){
            perceptronsA[i].setWeightsOut(weights_out[i]);
            perceptronsA[i].setPerceptronsOut(perceptronsB);
        }

        for (int i = 0; i < sizeB; i++){
            perceptronsB[i].setWeightsIn(weights_in[i]);
            perceptronsB[i].setPerceptronsIn(perceptronsA);
        }
    }


    //endregion

    //region Trained Weight Load-Save Methods

    /*** Save trained ANN weights to memory ***/
    public void saveWeightToMemory(){
        try {
            PrintWriter pw = new PrintWriter(new File("ann_weights" + MusicProcessor.experimentId  +".in"));

            for (int k = 1; k < layerSize; k++){
                NeuralLayer layerA = neuralLayers[k-1];
                NeuralLayer layerB = neuralLayers[k];
                Perceptron[] perceptronsA = layerA.getPerceptrons();
                Perceptron[] perceptronsB = layerB.getPerceptrons();

                int sizeA = layerA.getSize();
                int sizeB = layerB.getSize();

                for (int i = 0; i < sizeA+1; i++){
                    for  (int j = 0; j < sizeB; j++){
                        Weight w = perceptronsB[j].getWeightsIn()[i];
                        pw.println(w.getValue());
                    }
                }
            }

            System.out.println("Trained weights saved to ann_weights" + MusicProcessor.experimentId  +".in");
            pw.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Save file error!");
        }

    }

    /*** Load trained ANN weights from memory ***/
    public void loadFromMemory(){
        try {
            Scanner sc = new Scanner(new File("ann_weights"+ MusicProcessor.experimentId + ".in"));

            System.out.println("Read from " + "ann_weights"+ MusicProcessor.experimentId + ".in");

            for (int k = 1; k < layerSize; k++){
                NeuralLayer layerA = neuralLayers[k-1];
                NeuralLayer layerB = neuralLayers[k];

                Perceptron[] perceptronsA = layerA.getPerceptrons();
                Perceptron[] perceptronsB = layerB.getPerceptrons();

                int sizeA = layerA.getSize();
                int sizeB = layerB.getSize();

                Weight[][] weights_in = new Weight[sizeB][sizeA+1];
                Weight[][] weights_out = new Weight[sizeA+1][sizeB];

                for (int i = 0; i < sizeA+1; i++){
                    for  (int j = 0; j < sizeB; j++){
                        Weight w = new Weight(perceptronsA[i], perceptronsB[j]);

                        w.setValue(sc.nextDouble());

                        weights_out[i][j] = w;
                        weights_in[j][i] = w;
                    }
                }

                for (int i = 0; i < sizeA; i++){
                    perceptronsA[i].setWeightsOut(weights_out[i]);
                    perceptronsA[i].setPerceptronsOut(perceptronsB);
                }

                for (int i = 0; i < sizeB; i++){
                    perceptronsB[i].setWeightsIn(weights_in[i]);
                    perceptronsB[i].setPerceptronsIn(perceptronsA);
                }
            }

            sc.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Load file error!");
        }
    }

    //endregion

    //region Getter-Setter Methods

    /*** Bias perceptron getter method ***/
    public static BiasPerceptron getBias(){
        return bias;
    }

    /*** ANN output values getter method ***/
    public double[] getOutputs(){
        return ((OutputLayer)neuralLayers[layerSize-1]).getOutputs();
    }

    //endregion

    //region Main Calculation Methods

    /*** Compute output of each nodes starting from the inner layer ***/
    public void computeOutput(double[] inputs){
        ((InputLayer)neuralLayers[0]).setInput(inputs);
        for (int i = 0; i < layerSize; i++){
            neuralLayers[i].computeOutput();
        }
    }

    /*** Compute errors starting from the outer layer ***/
    private void computeError(double[] ideals){
        ((OutputLayer)neuralLayers[layerSize-1]).setIdealOutputs(ideals);
        for (int i = layerSize-1; i >= 1; i--){
            neuralLayers[i].computeError();
        }
    }

    /*** Update all weight ***/
    private void updateWeights(){
        for (int i = 1; i < layerSize; i++){
            Perceptron[] perceptrons = neuralLayers[i].getPerceptrons();
            int size = neuralLayers[i].getSize();

            for (int j = 0; j < size; j++){
                Weight[] weights = perceptrons[j].getWeightsIn();
                for (Weight w : weights){
                    if (w != null){
                        w.update(learnRate, momentum);
                    }
                }
            }
        }
    }

    /*** BackPropagation training ***/
    public void trainBackPropagation(double[] inputs, double[] ideals){
        computeOutput(inputs);
        computeError(ideals);
        updateWeights();
    }

    /*** Balancing dataset by compressing identic set to power of 2 ***/
    public static void balanceDataSet(List<DataSet> dataSets){
        Integer[] identic_count = new Integer[dataSets.size()];

        for (int i = 0; i < dataSets.size(); ++i){
            double[] cur_input = dataSets.get(i).inputs;
            double[] cur_output = dataSets.get(i).outputs;

            identic_count[i] = 1;

            for (int j = dataSets.size()-1; j > i; --j){
                boolean is_identic = true;
                double[] check_input = dataSets.get(j).inputs;
                double[] check_output = dataSets.get(j).outputs;

                for (int k = 0; k < check_input.length && is_identic; ++k){
                    if (check_input[k] != cur_input[k])
                        is_identic = false;
                }

                for (int k = 0; k < check_output.length && is_identic; ++k){
                    if (check_output[k] != cur_output[k])
                        is_identic = false;
                }

                if (is_identic){
                    dataSets.remove(j);
                    ++identic_count[i];
                }
            }
        }

        int sz = dataSets.size();
        for (int i = 0; i < sz; ++i){
            while (identic_count[i] > 0){
                dataSets.add(dataSets.get(i));
                identic_count[i] /= 2;
            }
        }
    }


    //endregion
}
