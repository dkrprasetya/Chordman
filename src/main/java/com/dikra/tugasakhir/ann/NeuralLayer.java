package com.dikra.tugasakhir.ann;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIKRA on 4/16/2015.
 */
public abstract class NeuralLayer {

    protected Perceptron[] perceptrons;
    protected final int size;

    public NeuralLayer(int size) {
        this.size = size;
        perceptrons = new Perceptron[size + 1];
    }

    public Perceptron[] getPerceptrons() {
        return perceptrons;
    }

    public int getSize() {
        return size;
    }

    public void computeOutput() {
        for (int i = 0; i < size; i++) {
            perceptrons[i].computeOutput();
        }


//        CompOutputThread[] threads = new CompOutputThread[size];
//
//        for (int i = 0; i < size; i++) {
//            threads[i] = new CompOutputThread(perceptrons[i]);
//        }
//
//        compute(threads);
    }

    public void computeError() {
        for (int i = 0; i < size; i++){
            perceptrons[i].computeError();
        }


//        CompErrorThread[] threads = new CompErrorThread[size];
//
//        for (int i = 0; i < size; i++) {
//            threads[i] = new CompErrorThread(perceptrons[i]);
//        }
//
//        compute(threads);
    }

    private void compute(Thread[] threads) {
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        perceptrons[size] = NeuralNetwork.getBias();
    }
}