package com.dikra.tugasakhir.ann;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class CompErrorThread extends Thread {

    private Perceptron perceptron;

    public CompErrorThread(Perceptron perceptron){
        super();
        this.perceptron = perceptron;
    }

    @Override
    public void run() {
        super.run();
        perceptron.computeError();
    }
}