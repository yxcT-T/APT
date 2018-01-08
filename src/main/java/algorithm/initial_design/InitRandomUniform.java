package algorithm.initial_design;

public class InitRandomUniform {

    /**
     * Samples N data points uniformly.
     *
     * @param lower (D)
     *              Lower bounds of the input space
     * @param upper (D)
     *              Upper bounds of the input space
     * @param n_points int
     *                The number of initial data points
     * @return (N, D)
     *      The initial design data points
     */
    public static double[][] get(double[] lower, double[] upper, int n_points){
        int n_dims = lower.length;
        double[][] result = new double[n_points][n_dims];
        for (int i = 0; i < n_points; i ++ ){
            for (int j = 0; j < n_dims; j ++ ){
                result[i][j] = Math.random() * (upper[j] - lower[j]) + lower[j];
            }
        }
        return result;
    }
}
