package com.dikra.tugasakhir.ann;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class NeuralNetwork {

    private int inputCount;
    private int[] hiddenCount;
    private int outputCount;
    private int layerSize;
    private int perceptronSize;
    private double learnRate;
    private double momentum;
    private NeuralLayer[] neuralLayers;

    private static BiasPerceptron bias;


    public NeuralNetwork(int inputCount, int[] hiddenCount, int outputCount, double learnRate, double momentum) {
        initialize(inputCount, hiddenCount, outputCount, learnRate, momentum);
    }

    public NeuralNetwork(int inputCount, int hiddenLayCount, int outputCount, double learnRate, double momentum){
        int[] hiddenCount = new int[hiddenLayCount];
        int nodeCount = Math.round((inputCount+outputCount) * 2.f / 3.f);
        for (int i = 0; i < hiddenLayCount; i++) hiddenCount[i] = nodeCount;

        initialize(inputCount, hiddenCount, outputCount, learnRate, momentum);
    }

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

    public static BiasPerceptron getBias(){
        return bias;
    }

    public void computeOutput(double[] inputs){
        ((InputLayer)neuralLayers[0]).setInput(inputs);
        for (int i = 0; i < layerSize; i++){
            neuralLayers[i].computeOutput();
        }
    }

    public double[] getOutputs(){
        return ((OutputLayer)neuralLayers[layerSize-1]).getOutputs();
    }


    public void computeError(double[] ideals){
        ((OutputLayer)neuralLayers[layerSize-1]).setIdealOutputs(ideals);
        for (int i = layerSize-1; i >= 1; i--){
            neuralLayers[i].computeError();
        }
    }

    public void updateWeights(){
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

    public void trainBackpropagation(double[] inputs, double[] ideals){
        computeOutput(inputs);
        computeError(ideals);
        updateWeights();
    }
}
