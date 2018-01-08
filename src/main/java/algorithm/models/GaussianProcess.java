package algorithm.models;

import org.apache.log4j.*;
import java.util.*;
import algorithm.utils.Normalization;
import org.apache.logging.log4j.core.util.ArrayUtils;
import utils.Pair;
import smile.math.kernel.MercerKernel;

public class GaussianProcess extends BaseModel{
    private Logger logger = Logger.getLogger(GaussianProcess.class);

    private MercerKernel<double[]> _kernel;
    private double _noise;
    private boolean _normalize_output;
    private boolean _normalize_input;
    private boolean _is_trained;
    private double[] _lower;
    private double[] _upper;
    private double _mean;
    private double _std;
    private GaussianProcessRegression<double[]> _gpr;

    /**
     * Interface to the GP library.
     *
     * @param kernel MercerKernel Object
     *               Specifies the kernel that is used for all Gaussian Process
     * @param noise float
     *              Noise term that is added to the diagonal of the covariance matrix
     *              for the Cholesky decomposition.
     * @param normalize_output bool
     *                         Zero mean unit variance normalization of the output values
     * @param normalize_input bool
     *                        Normalize all inputs to be in [0, 1]. This is important to define good priors for the
     *                        length scales.
     * @param lower Lower bound of the input space which is used for the input space normalization
     * @param upper Upper bound of the input space which is used for the input space normalization
     */
    public GaussianProcess(MercerKernel<double[]>kernel, double noise, boolean normalize_output,
                            boolean normalize_input, double[] lower, double[] upper){
        this._kernel = kernel;
        this._noise = noise;
        this._normalize_output = normalize_output;
        this._normalize_input = normalize_input;
        this._X = new ArrayList<Double[]>();
        this._y = new ArrayList<Double>();
        this._is_trained = false;
        this._lower = lower;
        this._upper = upper;
    }

    /**
     * Computes the Cholesky decomposition of the covariance of X and
     * estimates the GP hyperparameters by optimizing the marginal
     * loglikelihood. The prior mean of the GP is set to the empirical
     * mean of X.
     *
     * @param X (N, D)
     *          Input data points. The dimensionality of X is (N, D),
     *          with N as the number of points and D is the number of input dimensions.
     * @param y (N)
     *          The corresponding target values.
     * @throws Exception throws exception
     */
    @Override
    public void train(List<Double[]> X, List<Double> y) throws Exception{
        if (_normalize_input) {
            this._X = Normalization.zero_one_normalization(X, _lower, _upper);
        }
        else{
            this._X = X;
        }

        double sum = 0;
        for (double v : y){
            sum += v;
        }
        _mean = sum / y.size();
        _std = 0;
        for (double v : y){
            _std += (v - _mean) * (v - _mean);
        }
        _std = Math.sqrt(_std);

        if (_normalize_output){
            if (_std == 0){
                throw new Exception("Cannot normalize output. All targets have the same value!");
            }
            this._y = Normalization.zero_mean_unit_var_normalization(y, _mean, _std);
        }
        else{
            this._y = y;
        }

        int n = X.size(), m = X.get(0).length;
        double[][] X_array = new double[n][m];
        double[] y_array = new double[n];

        for (int i = 0; i < n; i ++ ){
            for (int j = 0; j < m; j ++ ){
                X_array[i][j] = this._X.get(i)[j];
            }
        }

        for (int i = 0; i < n; i ++ ){
            y_array[i] = this._y.get(i);
        }

        while (true) {
            boolean is_break = true;
            try {
                _gpr = new GaussianProcessRegression<double[]>(X_array, y_array, _kernel, _noise);
            } catch (Exception e) {
                if (this._noise == 0){
                    this._noise = 0.1;
                    logger.info("Noise of Gaussian Process += 0.1");
                }
                else{
                    this._noise *= Math.sqrt(10);
                    logger.info("Noise of Gaussian Process *= sqrt(10)");
                }
                is_break = false;
            }
            if (is_break){
                break;
            }
        }

        _is_trained = true;
    }

    public void update(List<Double[]> X, List<Double> y) throws Exception{
        if (this._X.size() > 0){
            if (this._normalize_input){
                List<Double[]> _X_new = new ArrayList<Double[]>();
                for (Double[] _x : this._X){
                    _X_new.add(Normalization.zero_one_unnormalization(_x, this._lower, this._upper));
                }
                this._X = _X_new;
            }
            if (this._normalize_output){
                List<Double> _y_new = new ArrayList<Double>();
                for (Double _v : this._y){
                    _y_new.add(Normalization.zero_mean_unit_var_unnormalization(_v, this._mean, this._std));
                }
                this._y = _y_new;
            }
        }

        this._X.addAll(X);
        this._y.addAll(y);
        train(this._X, this._y);
    }

    public double get_noise(){
        return _noise;
    }

    /**
     * Returns the predictive mean and variance of the objective function at
     * the given test points.
     *
     * @param X (N, D)
     *          Input test points
     * @return (mean, variance)
     * @throws Exception throws exception
     */
    public List<Pair<Double, Double>> predict(List<Double[]> X) throws Exception{
        if (!_is_trained){
            throw new Exception("Model has to be trained first!");
        }
        if (this._normalize_input){
            X = Normalization.zero_one_normalization(X, _lower, _upper);
        }
        List<Pair<Double, Double>> result = new ArrayList<Pair<Double, Double>>();
        for (Double[] x : X){
            int length = x.length;
            double[] x_array = new double[length];
            for (int i = 0; i < length; i ++ ){
                x_array[i] = x[i];
            }
            double mean = this._gpr.predict_mean(x_array);
            double variance = this._gpr.predict_variance(x_array);

            if (this._normalize_output){
                mean = Normalization.zero_mean_unit_var_unnormalization(mean, this._mean, this._std);
                variance *= this._std * this._std;
            }

            result.add(new Pair(mean, variance));
        }
        return result;
    }

    @Override
    public Pair<Double[], Double> get_incumbent() throws Exception{
        Pair<Double[], Double> result = super.get_incumbent();

        if (_normalize_input){
            result.setFirst(Normalization.zero_one_unnormalization(result.first(), _lower, _upper));
        }

        if (_normalize_output){
            result.setSecond(Normalization.zero_mean_unit_var_unnormalization(result.second(), _mean, _std));
        }
        return result;
    }
}
