package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class OutputLayer extends NeuralLayer {

    private double[] outputs;

    public OutputLayer(int size){
        super(size);
        outputs = new double[size];
    }

    @Override
    public void initialize(){
        for (int i = 0; i < size; i++){
            perceptrons[i] = new OutputPerceptron();
        }
    }

    public double[] getOutputs(){
        for (int i = 0; i < size; i++){
            outputs[i] = perceptrons[i].getOutput();
        }
        return outputs;
    }

    public void setIdealOutputs(double[] ideals){
        for (int i = 0; i < size; i++){
            ((OutputPerceptron)perceptrons[i]).setIdeal(ideals[i]);
        }
    }
}
