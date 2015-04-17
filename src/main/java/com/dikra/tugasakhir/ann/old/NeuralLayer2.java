package com.dikra.tugasakhir.ann.old;

/**
 * Created by DIKRA on 4/10/2015.
 */
public class NeuralLayer2 {
    protected Perceptron2[] nodes;
    protected final int nodeCount;
    protected final int weightCount;
    protected final boolean biasFlag;

    public NeuralLayer2(int nodeCount, int weightCount, boolean biasFlag, boolean randomizeFlag){
        this.nodeCount = nodeCount;
        this.weightCount = weightCount;
        this.biasFlag = biasFlag;
        nodes = new Perceptron2[nodeCount];
        for (int i = 0; i < nodeCount; i++){
            nodes[i] = new Perceptron2(weightCount, biasFlag, randomizeFlag);
        }
    }

    /* Calculate outputs for each perceptron by input */
    public void calculateOutputs(double input[]){
        for (int i = 0; i < nodeCount; i++) {
            nodes[i].calculateOutput(input);
        }
    }

    /* Get all outputs numbers from nodes */
    public double[] getOutputs(){
        double[] output = new double[nodeCount];
        for (int i = 0; i < nodeCount; i++){
            output[i] = nodes[i].getOutput();
        }
        return output;
    }

    /* Calculate errors for each perceptron by forward layer */
    public void calculateErrors(NeuralLayer2 nextlayer){
        for (int i = 0; i < nodeCount; i++){
            nodes[i].calculateError(nextlayer.getErrors(), nextlayer.getWeights(i));
        }
    }

    /* Calculate errors for output layer */
    public void calculateErrorsOutput(double ideal[]){
        for (int i = 0; i < nodeCount; i++){
            nodes[i].calculateErrorOutput(ideal[i]);
        }
    }

    /* Get all error numbers from nodes */
    public double[] getErrors(){
        double[] error = new double[nodeCount];
        for (int i = 0; i < nodeCount; i++){
            error[i] = nodes[i].getError();
        }
        return error;
    }

    /* Get all weight numbers for input_node[id] */
    public double[] getWeights(int id){
        double[] weight = new double[nodeCount];
        for (int i = 0; i < nodeCount; i++){
            weight[i] = nodes[i].getWeightAt(id);
        }
        return weight;
    }

    /* Update weight of nodes */
    public void updateWeight(double[] inputs, double learnRate, double momentum){
        for (int i = 0; i < nodeCount; i++){
            nodes[i].updateWeight(inputs, learnRate, momentum);
        }
    }

    public void printWeights(){
        System.out.print(">> ");
        for (int i = 0; i < nodeCount; i++){
            double[] weights = nodes[i].getWeights();

            System.out.print("{ ");
            for (int j = 0; j < weights.length; j++){
                System.out.print(weights[j] + ", ");
            }
            System.out.print("} ");

        }
        System.out.println("");
    }
}
