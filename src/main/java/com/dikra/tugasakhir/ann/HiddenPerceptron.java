package com.dikra.tugasakhir.ann;

import java.util.List;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class HiddenPerceptron extends Perceptron {

    public HiddenPerceptron(){
        super();
    }

    @Override
    public void computeOutput() {
        output = 0.;
        for (int i = 0; i < perceptrons_in.length; i++){
            if (perceptrons_in[i] == null) System.out.println("p_in - " + i + " = null");
            if (weights_in[i] == null) System.out.println("w_in - " + i + " = null");
            output += perceptrons_in[i].getOutput() * weights_in[i].getValue();
        }
        output = threshold(output);
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
