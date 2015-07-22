package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class OutputPerceptron extends Perceptron {

    private double ideal;

    public OutputPerceptron(){
        super();
        ideal = 0.;
    }

    public void setIdeal(double ideal){
        this.ideal = ideal;
    }

    @Override
    public void computeOutput() {
        output = 0.;
        for (int i = 0; i < perceptrons_in.length; i++){
            output += perceptrons_in[i].getOutput() * weights_in[i].getValue();
        }
        output = threshold(output);
    }

    @Override
    public void computeError() {
        error = output * (1. - output) * (ideal - output);
    }
}
