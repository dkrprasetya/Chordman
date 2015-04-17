package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class InputLayer extends NeuralLayer {

    public InputLayer(int size){
        super(size);
    }

    @Override
    public void initialize(){
        for (int i = 0; i < size; i++){
            perceptrons[i] = new InputPerceptron();
        }
        super.initialize();

    }

    public void setInput(double[] inputs){
        for (int i = 0; i < size; i++){
            ((InputPerceptron)perceptrons[i]).setInput(inputs[i]);
        }
    }
}
