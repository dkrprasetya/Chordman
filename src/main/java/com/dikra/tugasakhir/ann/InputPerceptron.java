package com.dikra.tugasakhir.ann;

import java.util.List;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class InputPerceptron extends Perceptron {

    private double input;

    public InputPerceptron(){
        super();
    }

    public void setInput(double input){
        this.input = input;
    }

    @Override
    public void computeOutput() {
        output = input;
    }

    @Override
    public void computeError() {
        double output_err = 0.;
        for (int i = 0; i < perceptrons_out.length-1; i++){
            output_err += perceptrons_out[i].getError() * weights_out[i].getValue();
        }
        error = output * (1. - output) * output_err;
    }
}
