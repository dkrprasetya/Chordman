package com.dikra.tugasakhir.ann.old;

import java.util.Random;

/**
 * Created by DIKRA on 4/10/2015.
 */
public class Perceptron2 {

    private final int inputCount;
    private final boolean biasFlag;

    private double[] weight;
    private double output;
    private double error;
    private double[] weight_update;


    public Perceptron2(int inputCount, boolean biasFlag, boolean randomizeFlag){
        this.inputCount = inputCount;
        this.biasFlag = biasFlag;

        weight = new double[inputCount+((biasFlag)?1:0)]; // +1 if used bias
        weight_update = new double[weight.length];

        for (int i = 0; i < weight_update.length; i++){
            weight_update[i] = 0.;
        }

        if (randomizeFlag){
            randomizeWeights();
        } else {
            weight[0] = 0;
            weight[1] = 1;
        }
    }

    private void randomizeWeights(){
        Random rand = new Random();
        double rangeMin = -1.;
        double rangeMax = 1.;
        for (int i = 0; i < weight.length; i++){
            weight[i] = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
        }
    }

    /* Calculate output by input */
    public void calculateOutput(double[] input){
        // Reset to zero
        output = 0.;

        // Calculate from input
        for (int i = 0; i < inputCount; i++){
            output += input[i] * weight[i];
        }

        // Calculate from bias
        if (biasFlag){
            output += weight[inputCount];
        }

        // Apply activation function
        output = threshold(output);
    }

    public double getOutput(){
        return output;
    }

    public void calculateError(double error_out[], double weight_out[]){
        double output_err = 0.;
        for (int i = 0; i < error_out.length; i++){
            output_err += error_out[i] * weight_out[i];
        }

        error = output * (1. - output) * output_err;
    }

    public void calculateErrorOutput(double ideal){
        error = output * (1. - output) * (ideal - output);
    }

    public double getError(){
        return error;
    }

    public double getWeightAt(int id){
        return weight[id];
    }

    public double[] getWeights(){
        return weight;
    }

    /* Activation function: sigmoid */
    public double threshold(double sum){
        return 1. / (1. + Math.exp(-sum));
//        return sum;
    }
    public void updateWeight(double[] inputs, double learnRate, double momentum){
        for (int i = 0; i < weight.length; i++){
            double x = 1.;

            if (i < inputs.length){
                x = inputs[i];
            }

            double update = learnRate * error * x + momentum * weight_update[i];

            weight[i] += update;

            weight_update[i] = update;
        }
    }

}
