package algorithm.maximizers;

import algorithm.acquisition_functions.BaseAcquisitionFunction;
import algorithm.initial_design.InitRandomUniform;
import utils.ArrayUtils;
import java.util.Random;

public class RandomSampling extends BaseMaximizer{

    private int _nSamples;
    private Random _random;

    /**
     * Samples candidates uniformly at random and returns the point with the highest objective value.
     *
     * @param obectiveFunction acquisition function
     *                         The acquisition function which will be maximized
     * @param lower (D)
     *              Lower bounds of the input space
     * @param upper (D)
     *              Upper bounds of the input space
     * @param nSamples int
     *                 Number of candidates that are samples
     */
    public RandomSampling(BaseAcquisitionFunction obectiveFunction, double[] lower, double[] upper, int nSamples) throws Exception{
        super(obectiveFunction, lower, upper);

        if (nSamples == 0){
            throw new Exception("nSample(value = 0) is invalid in RandomSampling.RandomSampling().");
        }

        this._nSamples = nSamples;
        this._random = new Random();
    }

    /**
     * Maximizes the given acquisition function.
     *
     * @return (D)
     *      Point with highest acquisition value.
     * @throws Exception throws exception
     */
    public double[] maximize() throws Exception{
        double[][] rand = InitRandomUniform.get(this._lower, this._upper, (int)(_nSamples * 0.7));
        Double[] loc = this._objectiveFunction.get_model().get_incumbent().first();

        int n = this._nSamples - (int)(this._nSamples * 0.7);
        int m = this._lower.length;
        double[][] rand_incs = new double[n][m];
        for (int i = 0; i < n; i ++ ){
            for (int j = 0; j < m; j ++ ){
                double v = loc[j] + this._random.nextGaussian() * 0.1;
                v = Math.max(v, this._lower[j]);
                v = Math.min(v, this._upper[j]);
                rand_incs[i][j] = v;
            }
        }
        double[][] X = ArrayUtils.concatenate(rand, rand_incs);
        double[] y = new double[X.length];

        for (int i = 0; i < X.length; i ++ ){
            y[i] = this._objectiveFunction.compute(ArrayUtils.double2Double(X[i]));
        }

        int x_star = ArrayUtils.argmax(y);
        return X[x_star];
    }
}
