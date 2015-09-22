package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public abstract class Perceptron {

    protected double output;
    protected double error;

    protected Weight[] weights_out;
    protected Perceptron[] perceptrons_out;
    protected Weight[] weights_in;
    protected Perceptron[] perceptrons_in;

    public Perceptron(){
        output = 0.;
        error = 0.;

        weights_in = null;
        weights_out = null;
        perceptrons_in = null;
        perceptrons_out = null;
    }

    public double getOutput(){
        return output;
    }


    public double getError(){
        return error;
    }

    public void setWeightsOut(Weight[] weights_out){
        this.weights_out = weights_out;
    }

    public void setPerceptronsOut(Perceptron[] perceptrons_out){
        this.perceptrons_out = perceptrons_out;
    }

    public void setWeightsIn(Weight[] weights_in){
        this.weights_in = weights_in;
    }

    public void setPerceptronsIn(Perceptron[] perceptrons_in){
        this.perceptrons_in = perceptrons_in;
    }

    public Weight[] getWeightsIn(){
        return weights_in;
    }

    /* Activation function: sigmoid */
    public static double threshold(double sum){
        return 1. / (1. + Math.exp(-sum));
    }

    public abstract void computeOutput();

    public abstract void computeError();
}
