package com.dikra.tugasakhir.ann.old;

/**
 * Created by DIKRA on 4/9/2015.
 */
public class NeuralNetwork2 {

    /* CONSTANT CONFIGURTIONS */
    private final int inputCount;
    private final int[] hiddenCount;
    private final int outputCount;
    private final double learnRate;
    private final double momentum;
    private final boolean inputHasWeight;

    /* NEURAL NETWORK LAYERS */
    private NeuralLayer2[] hiddenLayer;
    private NeuralLayer2 outputLayer;

    /* CONFIGURABLE ATTRIBUTES */


    public NeuralNetwork2(int inputCount, int[] hiddenCount, int outputCount, double learnRate, double momentum, boolean inputHasWeight, boolean biasFlag){
        // Initialize constant variables
        this.inputCount = inputCount;
        this.hiddenCount = hiddenCount;
        this.outputCount = outputCount;
        this.learnRate = learnRate;
        this.momentum = momentum;
        this.inputHasWeight = inputHasWeight;

        System.out.println(inputCount + " - " + hiddenCount[0] + " - " + outputCount);


        // Instansiate hidden layers
        hiddenLayer = new NeuralLayer2[hiddenCount.length];
        for (int i = 0; i < hiddenCount.length; i++){
            if (i == 0){
                hiddenLayer[i] = new NeuralLayer2(hiddenCount[i], inputCount, true, false);
            } else {
                hiddenLayer[i] = new NeuralLayer2(hiddenCount[i], hiddenCount[i-1], true, false);
            }
        }

        // Instansiate output layers
        if (hiddenCount.length > 0){
            outputLayer = new NeuralLayer2(outputCount, hiddenCount[hiddenCount.length-1], false, false);
        } else {
            outputLayer = new NeuralLayer2(outputCount, inputCount, false, true);
        }

        System.out.print("Hidden: ");
        hiddenLayer[0].printWeights();
        System.out.print("Output: ");
        outputLayer.printWeights();
    }

    /* Compute output by input parameter */
    public void calculateOutputs(double input[]){
        double[] outputs;

        // INPUT LAYER
        outputs = input;

        // HIDDEN LAYER
        for (int i = 0; i < hiddenCount.length; i++){
            hiddenLayer[i].calculateOutputs(outputs);
            outputs = hiddenLayer[i].getOutputs();
        }

        // OUTPUT LAYER
        outputLayer.calculateOutputs(outputs);
    }

    /* Get outputs */
    public double[] getOutputs(){
        return outputLayer.getOutputs();
    }

    /* Learning algorithm: Backpropagation */
    public void training(double inputs[], double ideal[], boolean shouldCalculate){

        System.out.println("Training (first input, second input, ideal output): " + inputs[0] + ", " + inputs[1] + " -> " + ideal[0]);
        if (shouldCalculate){
            calculateOutputs(inputs);
        }
        System.out.println("Calculated output: " + getOutputs()[0]);

        // OUTPUT LAYER TRAINING
        outputLayer.calculateErrorsOutput(ideal);
        double[] errors = outputLayer.getErrors();

        if (hiddenCount.length > 0){
            outputLayer.updateWeight(hiddenLayer[hiddenCount.length-1].getOutputs(), learnRate, momentum);
        } else {
            outputLayer.updateWeight(inputs, learnRate, momentum);
        }

        // HIDDEN LAYER TRAINING
        for (int i = hiddenCount.length-1; i >= 0; i--){
            if (i == hiddenCount.length-1){
                hiddenLayer[i].calculateErrors(outputLayer);
            } else {
                hiddenLayer[i].calculateErrors(hiddenLayer[i + 1]);
            }
            errors = hiddenLayer[i].getErrors();

            if (i == 0){
                hiddenLayer[i].updateWeight(inputs, learnRate, momentum);
            } else {
                hiddenLayer[i].updateWeight(hiddenLayer[i - 1].getOutputs(), learnRate, momentum);
            }
        }

        System.out.println("\n=== AFTER ALL TRAINING =====");
        System.out.print("Input layer weights: ");
        hiddenLayer[0].printWeights();
        System.out.print("Hidden layer weights: ");
        outputLayer.printWeights();
    }
}
