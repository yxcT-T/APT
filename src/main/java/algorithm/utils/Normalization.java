package algorithm.utils;

import java.util.*;

public class Normalization {

    public static List<Double[]> zero_one_normalization(List<Double[]> X, double[] lower, double[] upper){
        List<Double[]> result = new ArrayList<Double[]>();
        int dim = lower.length;
        for (Double[] line : X){
            Double[] line_normalized = new Double[dim];
            for (int i = 0; i < dim; i ++ ){
                line_normalized[i] = (line[i] - lower[i]) / (upper[i] - lower[i]);
            }
            result.add(line_normalized);
        }
        return result;
    }

    public static Double[] zero_one_unnormalization(Double[] X, double[] lower, double[] upper){
        int dim = lower.length;
        Double[] result = new Double[dim];
        for (int i = 0; i < dim; i ++ )
            result[i] = lower[i] + (upper[i] - lower[i]) * X[i];
        return result;
    }

    public static List<Double> zero_mean_unit_var_normalization(List<Double> y, double mean, double std)
    {
        List<Double> result = new ArrayList<Double>();
        for (double v : y){
            result.add((v - mean) / std);
        }
        return result;
    }

    public static Double zero_mean_unit_var_unnormalization(Double y, double mean, double std){
        return y * std + mean;
    }
}
