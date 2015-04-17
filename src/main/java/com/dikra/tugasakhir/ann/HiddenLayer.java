package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class HiddenLayer extends NeuralLayer {

    public HiddenLayer(int size){
        super(size);
    }

    @Override
    public void initialize(){
        for (int i = 0; i < size; i++){
            perceptrons[i] = new HiddenPerceptron();
        }
        super.initialize();
    }
}
