package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class CompOutputThread extends Thread {

    Perceptron perceptron;

    public CompOutputThread(Perceptron perceptron){
        super();
        this.perceptron = perceptron;
    }

    @Override
    public void run() {
        super.run();

        perceptron.computeOutput();
    }
}
