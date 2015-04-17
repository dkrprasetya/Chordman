package com.dikra.tugasakhir.ann;

import java.util.Random;

/**
 * Created by DIKRA on 4/16/2015.
 */
public class Weight {
    private Perceptron src;
    private Perceptron dest;
    private double value;
    private double last_update;

    public Weight(Perceptron src, Perceptron dest){
        this.src = src;
        this.dest = dest;
        this.value = 0.;
        last_update = 0.;
    }

    public Perceptron getSource(){
        return src;
    }

    public Perceptron getDestination(){
        return dest;
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public void randomizeValue(double range_min, double range_max){
        this.value = range_min + (range_max - range_min) * (new Random()).nextDouble();
    }

    public void update(double learnRate){
        update(learnRate, 0.);
    }

    public void update(double learnRate, double momentum){
        double upd = learnRate * src.getOutput() * dest.getError() + momentum * last_update;
        value += upd;
        last_update = upd;
    }
}
