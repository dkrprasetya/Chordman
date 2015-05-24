package com.dikra.tugasakhir;

import com.dikra.tugasakhir.ann.NeuralNetwork;
import junit.framework.TestCase;
import org.junit.*;

/**
 * Created by DIKRA on 5/24/2015.
 */
public class ANNTestCase extends TestCase {
    @Test
    public void testANN(){
        double EPS = 0.05; // error 5* 10^-2
        double[][] inputs = { {0., 0.}, {1., 0.}, {0., 1.}, {1., 1.}};
        double[][] outputs = { {0.}, {1.}, {1.}, {1.} };

        NeuralNetwork network = new NeuralNetwork(2, 1, 1, 0.3, 0.9);

        for (int epoch = 0; epoch < 100000; epoch++){
            for (int i = 0; i < 4; i++){
                network.trainBackpropagation(inputs[i], outputs[i]);
            }
        }

        for (int i = 0; i < 4; i++){
            network.computeOutput(inputs[i]);
            //System.out.format("{ %.2f, %.2f } = %.8f -- ideal = %.8f\n", inputs[i][0], inputs[i][1], network.getOutputs()[0], outputs[i][0]);
            assert (Math.abs(network.getOutputs()[0] - outputs[i][0]) < EPS);
        }
    }

}
